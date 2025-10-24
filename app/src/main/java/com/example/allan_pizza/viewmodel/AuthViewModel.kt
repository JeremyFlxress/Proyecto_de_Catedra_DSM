package com.example.allan_pizza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allan_pizza.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 1. Define un estado para la UI de los diálogos (Login/Register)
sealed interface AuthUiState {
    object Idle : AuthUiState      // Estado inicial
    object Loading : AuthUiState   // Cargando
    object Success : AuthUiState   // Éxito
    data class Error(val message: String) : AuthUiState // Error
}

// 2. Define el ViewModel
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // --- ESTADO PARA LOS DIÁLOGOS (Login/Register) ---
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    // --- ESTADO GENERAL DE LA APP ---
    // Guarda los datos del usuario logueado (de Firestore)
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser

    // Un simple booleano para saber si hay sesión activa o no
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // "Oyente" que reacciona a cambios de login/logout en tiempo real
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _isLoggedIn.value = user != null // Actualiza el estado
        if (user != null) {
            // Si hay un usuario, buscamos sus datos en Firestore
            fetchUserData(user.uid)
        } else {
            // Si no hay usuario (hizo logout), limpiamos los datos
            _currentUser.value = null
        }
    }

    // El bloque init se ejecuta cuando el ViewModel es creado
    init {
        // Empezamos a escuchar los cambios de autenticación
        auth.addAuthStateListener(authStateListener)
    }

    /**
     * Busca los datos de un usuario en Firestore por su UID
     */
    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                val userModel = snapshot.toObject(UserModel::class.java)
                _currentUser.value = userModel
            } catch (e: Exception) {
                _currentUser.value = null // Limpia si hay error
            }
        }
    }

    /**
     * Registra un nuevo usuario con email/contraseña y guarda sus datos en Firestore.
     */
    fun registerUser(
        nombre: String,
        direccion: String,
        telefono: String,
        email: String,
        contrasena: String
    ) {
        if (nombre.isBlank() || direccion.isBlank() || telefono.isBlank() || email.isBlank() || contrasena.isBlank()) {
            _uiState.value = AuthUiState.Error("Todos los campos son obligatorios")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Crear el usuario en Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(email, contrasena).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // 2. Crear el objeto de datos del usuario
                    val user = UserModel(
                        uid = firebaseUser.uid,
                        nombre = nombre,
                        direccion = direccion,
                        telefono = telefono,
                        email = email
                    )

                    // 3. Guardar el objeto de usuario en Firestore
                    db.collection("users").document(firebaseUser.uid)
                        .set(user)
                        .await()

                    // 4. Notificar a la UI del diálogo que todo fue exitoso
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error("No se pudo crear el usuario")
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.value = AuthUiState.Error("El correo electrónico ya está en uso")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Ocurrió un error desconocido")
            }
        }
    }

    /**
     * Inicia sesión de un usuario existente.
     */
    fun loginUser(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _uiState.value = AuthUiState.Error("Email y contraseña son obligatorios")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Iniciar sesión con Firebase Authentication
                auth.signInWithEmailAndPassword(email, contrasena).await()

                // 2. Notificar a la UI del diálogo que el login fue exitoso
                // El authStateListener se encargará de cargar los datos
                _uiState.value = AuthUiState.Success

            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Credenciales incorrectas")
            }
        }
    }

    /**
     * Resetea el estado de la UI (para los diálogos) a 'Idle'
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun logout() {
        auth.signOut() // Esto disparará al authStateListener
    }

    /**
     * Limpieza del listener cuando el ViewModel se destruye
     */
    override fun onCleared() {
        super.onCleared()
        // Es importante remover el "oyente" cuando el ViewModel se destruye
        // para evitar fugas de memoria.
        auth.removeAuthStateListener(authStateListener)
    }
}


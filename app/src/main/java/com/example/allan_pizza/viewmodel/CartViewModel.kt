package com.example.allan_pizza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allan_pizza.data.CartItem
import com.example.allan_pizza.data.FirestoreCartItem
import com.example.allan_pizza.data.Product
import com.example.allan_pizza.data.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel del Carrito, ahora conectado a Firestore y reactivo a la autenticación.
 */
class CartViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // Inyectamos (o instanciamos) el repositorio de productos
    // Lo necesitamos para "unir" los IDs del carrito con los datos del producto
    private val productRepository = ProductRepository()

    // --- Estado del Carrito (Firestore) ---
    // Este Flow interno solo contiene los IDs y cantidades de Firestore
    private val _firestoreCartItems = MutableStateFlow<List<FirestoreCartItem>>(emptyList())
    private var cartListener: ListenerRegistration? = null // Para poder "apagar" el oyente al hacer logout

    // --- Estado de la UI (Público) ---
    // Este Flow público combina el carrito de Firestore con los datos de los productos
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // --- Estado Agregado (Público) ---
    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        // Escucha cambios de autenticación
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Si el usuario inicia sesión, escucha su carrito
                listenToCart(user.uid)
            } else {
                // Si el usuario cierra sesión, limpia el carrito y detiene el oyente
                cartListener?.remove()
                _firestoreCartItems.value = emptyList()
            }
        }

        // Combina el flow de productos y el flow del carrito de Firestore
        // para crear el flow de la UI (CartItem con objeto Product completo)
        viewModelScope.launch {
            productRepository.productsFlow.combine(_firestoreCartItems) { products, firestoreCart ->
                // "Join" del lado del cliente
                val uiCartItems = firestoreCart.mapNotNull { cartItem ->
                    products.find { it.id == cartItem.productId }?.let { product ->
                        CartItem(product = product, quantity = cartItem.quantity)
                    }
                }

                // Actualiza los estados agregados
                _totalItems.value = uiCartItems.sumOf { it.quantity }
                _totalPrice.value = uiCartItems.sumOf { it.totalPrice }

                uiCartItems // Emite la lista combinada
            }.collect { combinedList ->
                _cartItems.value = combinedList // Actualiza el flow de la UI
            }
        }
    }

    /**
     * Se suscribe a la sub-colección "cart" del usuario en Firestore.
     */
    private fun listenToCart(userId: String) {
        // Detiene cualquier oyente anterior
        cartListener?.remove()

        val cartCollection = db.collection("users").document(userId).collection("cart")

        cartListener = cartCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error al escuchar el carrito: $error")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _firestoreCartItems.value = snapshot.toObjects()
            }
        }
    }

    /**
     * Añade un producto al carrito en Firestore.
     */
    fun addToCart(product: Product) {
        val userId = auth.currentUser?.uid ?: return // No hacer nada si no hay usuario

        val cartItemRef = db.collection("users").document(userId)
            .collection("cart").document(product.id) // El ID del producto es el ID del documento

        viewModelScope.launch {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(cartItemRef)
                if (snapshot.exists()) {
                    // Si ya existe, incrementa la cantidad
                    val currentQuantity = snapshot.getLong("quantity") ?: 0
                    transaction.update(cartItemRef, "quantity", currentQuantity + 1)
                } else {
                    // Si no existe, crea el documento
                    val newItem = FirestoreCartItem(productId = product.id, quantity = 1)
                    transaction.set(cartItemRef, newItem)
                }
            }.addOnFailureListener {
                println("Error al añadir al carrito: $it")
            }
        }
    }

    /**
     * Reduce la cantidad o elimina un item del carrito en Firestore.
     */
    fun removeFromCart(productId: String) {
        val userId = auth.currentUser?.uid ?: return

        val cartItemRef = db.collection("users").document(userId)
            .collection("cart").document(productId)

        viewModelScope.launch {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(cartItemRef)
                if (snapshot.exists()) {
                    val currentQuantity = snapshot.getLong("quantity") ?: 0
                    if (currentQuantity > 1) {
                        // Si hay más de 1, reduce la cantidad
                        transaction.update(cartItemRef, "quantity", currentQuantity - 1)
                    } else {
                        // Si solo hay 1, elimina el documento
                        transaction.delete(cartItemRef)
                    }
                }
            }.addOnFailureListener {
                println("Error al quitar del carrito: $it")
            }
        }
    }

    /**
     * Limpia todo el carrito en Firestore (no implementado en tu UI, pero útil)
     */
    fun clearCart() {
        val userId = auth.currentUser?.uid ?: return
        val cartCollection = db.collection("users").document(userId).collection("cart")

        viewModelScope.launch {
            cartCollection.get().await().documents.forEach {
                it.reference.delete()
            }
        }
    }

    // Limpia el oyente cuando el ViewModel se destruye
    override fun onCleared() {
        cartListener?.remove()
        super.onCleared()
    }
}


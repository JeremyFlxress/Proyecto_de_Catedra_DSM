package com.example.allan_pizza.data

/**
 * Modelo de datos para guardar la información del usuario en Firestore.
 */
data class UserModel(
    val uid: String = "",       // ID único de Firebase Auth
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val email: String = ""      // Guardamos el email para referencia
)
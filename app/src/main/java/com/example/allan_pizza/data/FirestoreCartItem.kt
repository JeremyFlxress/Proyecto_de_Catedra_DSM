package com.example.allan_pizza.data

/**
 * Representa la estructura de datos que guardamos en la sub-colección
 * "cart" de Firestore.
 */
data class FirestoreCartItem(
    val productId: String = "",
    val quantity: Int = 0
)
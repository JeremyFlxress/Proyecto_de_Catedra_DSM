package com.example.allan_pizza.data

/**
 * Representa la estructura de datos que guardamos en la sub-colección
 * "cart" de Firestore.
 *
 * Solo guardamos el ID del producto y la cantidad para evitar
 * duplicar datos y mantenerlo ligero.
 */
data class FirestoreCartItem(
    val productId: String = "",
    val quantity: Int = 0
)


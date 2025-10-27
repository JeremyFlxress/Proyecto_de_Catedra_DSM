package com.example.allan_pizza.data

import com.google.firebase.Timestamp

/**
 * Representa un item individual DENTRO de un pedido.
 * Guardamos los datos clave para no tener que buscarlos de nuevo.
 */
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "", // Guardamos la URL para mostrarla fácil
    val quantity: Int = 0,
    val unitPrice: Double = 0.0
)

/**
 * Representa el Pedido (Order) principal que se guarda en la colección "orders".
 */
data class Order(
    val id: String = "", // ID del documento de Firestore
    val userId: String = "",
    val userName: String = "",      // Datos del UserModel
    val userAddress: String = "",   // Datos del UserModel
    val userPhone: String = "",     // Datos del UserModel
    val items: List<OrderItem> = emptyList(), // La lista de productos
    val totalPrice: Double = 0.0,
    val status: String = "En preparación", // Estado inicial
    val paymentMethod: String = "Efectivo", // Asumido por tu UI
    val timestamp: Timestamp = Timestamp.now()
) {
    // Constructor vacío requerido por Firestore para .toObject()
    constructor() : this("", "", "", "", "", emptyList(), 0.0, "En preparación", "Efectivo", Timestamp.now())
}
package com.example.allan_pizza.data

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de datos para un Producto.
 *
 * CAMBIOS:
 * 1. @DocumentId: Le dice a Firestore que llene el campo 'id' automáticamente con el ID del documento.
 * 2. id: Cambiado de Int a String (los IDs de Firestore son Strings).
 * 3. imageResId -> imageUrl: Cambiado a String para cargar desde una URL (ej. Firebase Storage).
 * 4. Valores por defecto: Añadidos para que el deserializador de Firestore (toObject()) funcione sin problemas.
 */
data class Product(
    @DocumentId
    val id: String = "", // ID de Firestore
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // URL de la imagen
    val description: String = ""
)

/**
 * Modelo de datos para la UI del Carrito.
 *
 * CAMBIOS:
 * 1. Product.id ahora es un String, así que no hay cambios de lógica aquí,
 * pero es importante saber que el ID subyacente ha cambiado.
 */
data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}


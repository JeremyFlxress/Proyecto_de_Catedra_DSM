package com.example.allan_pizza.data

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String = "", // ID de Firestore
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // URL de la imagen
    val description: String = "",
    val category: String = "Producto"
)
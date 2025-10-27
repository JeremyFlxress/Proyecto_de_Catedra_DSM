package com.example.allan_pizza.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    // Un StateFlow privado que mantiene la lista actual de productos
    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())

    // Un Flow público inmutable para que la UI lo observe
    val productsFlow: StateFlow<List<Product>> = _productsFlow.asStateFlow()

    init {
        // Escucha cambios en la colección "products" en tiempo real
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error al escuchar productos: $error")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Convierte los documentos a objetos Product y los emite al flow
                    val products = snapshot.toObjects<Product>()
                    _productsFlow.value = products.sortedBy { it.price } // Ordenar por precio
                }
            }
    }

    // Función para obtener un solo producto por ID (útil para el carrito)
    // Usamos 'suspend' porque es una operación de una sola vez
    suspend fun getProductById(productId: String): Product? {
        return try {
            db.collection("products").document(productId).get().await().toObject(Product::class.java)
        } catch (e: Exception) {
            println("Error al obtener producto por ID: $e")
            null
        }
    }
}
package com.example.allan_pizza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allan_pizza.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // Listener para el pedido activo
    private var orderListener: ListenerRegistration? = null

    // --- NUEVO: Listener para el historial ---
    private var historyListener: ListenerRegistration? = null

    // Flow para el pedido activo
    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    // --- NUEVO: Flow para el historial de pedidos ---
    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory.asStateFlow()

    init {
        // Escucha cambios de autenticación
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Si el usuario inicia sesión, escucha su pedido activo
                listenToActiveOrder(user.uid)
                // --- NUEVO: Escucha también su historial ---
                listenToOrderHistory(user.uid)
            } else {
                // Si cierra sesión, limpia todo
                orderListener?.remove()
                _activeOrder.value = null

                // --- NUEVO: Limpia el historial ---
                historyListener?.remove()
                _orderHistory.value = emptyList()
            }
        }
    }

    /**
     * MODIFICADO: Escucha el pedido MÁS RECIENTE del usuario, sin importar su estado.
     * (Esta función estaba correcta en tu código)
     */
    private fun listenToActiveOrder(userId: String) {
        orderListener?.remove() // Limpia oyente anterior

        val query = db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING) // El más reciente
            .limit(1) // Solo queremos el último pedido

        orderListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error al escuchar pedido activo: $error")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val order = snapshot.documents.first().toObject(Order::class.java)
                _activeOrder.value = order?.copy(id = snapshot.documents.first().id)
            } else {
                // No hay pedidos
                _activeOrder.value = null
            }
        }
    }

    // --- NUEVA FUNCIÓN ---
    /**
     * Escucha TODOS los pedidos del usuario para el historial.
     */
    private fun listenToOrderHistory(userId: String) {
        historyListener?.remove() // Limpia oyente anterior

        val query = db.collection("orders")
            .whereEqualTo("userId", userId)
            // Ordenamos del más nuevo al más viejo
            .orderBy("timestamp", Query.Direction.DESCENDING)

        historyListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                println("Error al escuchar historial de pedidos: $error")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Convertimos todos los documentos a objetos Order
                val historyList = snapshot.toObjects(Order::class.java)
                _orderHistory.value = historyList
            }
        }
    }

    /**
     * Marca un pedido como completado (o cancelado).
     */
    fun completeActiveOrder() {
        val orderId = _activeOrder.value?.id ?: return
        viewModelScope.launch {
            try {
                db.collection("orders").document(orderId)
                    .update("status", "Completado")
                    .await()
                // El listener actualizará el estado automáticamente
            } catch (e: Exception) {
                println("Error al completar el pedido: $e")
            }
        }
    }


    override fun onCleared() {
        orderListener?.remove()
        // --- NUEVO: Limpia el listener del historial ---
        historyListener?.remove()
        super.onCleared()
    }
}
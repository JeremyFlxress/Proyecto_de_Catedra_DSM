package com.example.allan_pizza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allan_pizza.data.* // Importar los modelos
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

class CartViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val productRepository = ProductRepository()

    private val _firestoreCartItems = MutableStateFlow<List<FirestoreCartItem>>(emptyList())
    private var cartListener: ListenerRegistration? = null

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToCart(user.uid)
            } else {
                cartListener?.remove()
                _firestoreCartItems.value = emptyList()
            }
        }

        viewModelScope.launch {
            productRepository.productsFlow.combine(_firestoreCartItems) { products, firestoreCart ->
                val uiCartItems = firestoreCart.mapNotNull { cartItem ->
                    products.find { it.id == cartItem.productId }?.let { product ->
                        CartItem(product = product, quantity = cartItem.quantity)
                    }
                }

                _totalItems.value = uiCartItems.sumOf { it.quantity }
                _totalPrice.value = uiCartItems.sumOf { it.totalPrice }

                uiCartItems
            }.collect { combinedList ->
                _cartItems.value = combinedList
            }
        }
    }

    private fun listenToCart(userId: String) {
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

    fun addToCart(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        val cartItemRef = db.collection("users").document(userId)
            .collection("cart").document(product.id)

        viewModelScope.launch {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(cartItemRef)
                if (snapshot.exists()) {
                    val currentQuantity = snapshot.getLong("quantity") ?: 0
                    transaction.update(cartItemRef, "quantity", currentQuantity + 1)
                } else {
                    val newItem = FirestoreCartItem(productId = product.id, quantity = 1)
                    transaction.set(cartItemRef, newItem)
                }
            }.addOnFailureListener {
                println("Error al añadir al carrito: $it")
            }
        }
    }

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
                        transaction.update(cartItemRef, "quantity", currentQuantity - 1)
                    } else {
                        transaction.delete(cartItemRef)
                    }
                }
            }.addOnFailureListener {
                println("Error al quitar del carrito: $it")
            }
        }
    }

    /**
     * Crea un nuevo documento de "pedido" en Firestore y luego limpia el carrito.
     */
    fun createOrder(user: com.example.allan_pizza.data.UserModel) {
        val userId = auth.currentUser?.uid ?: return
        val currentCartItems = _cartItems.value
        val currentTotalPrice = _totalPrice.value

        if (currentCartItems.isEmpty()) {
            println("El carrito está vacío, no se puede crear el pedido.")
            return
        }

        viewModelScope.launch {
            try {
                // 1. Convierte CartItems a OrderItems
                val orderItems = currentCartItems.map { cartItem ->
                    OrderItem(
                        productId = cartItem.product.id,
                        productName = cartItem.product.name,
                        productImageUrl = cartItem.product.imageUrl,
                        quantity = cartItem.quantity,
                        unitPrice = cartItem.product.price
                    )
                }

                // 2. Crea el objeto Pedido
                val newOrder = Order(
                    userId = userId,
                    userName = user.nombre,
                    userAddress = user.direccion,
                    userPhone = user.telefono,
                    items = orderItems,
                    totalPrice = currentTotalPrice,
                    status = "En preparación", // Estado inicial
                    paymentMethod = "Efectivo",   // Asumido de tu UI
                    timestamp = com.google.firebase.Timestamp.now()
                )

                // 3. Guarda el pedido en Firestore
                db.collection("orders").add(newOrder).await()

                // 4. Si tiene éxito, limpia el carrito
                clearCart() // Llama a la función que ya tenías

            } catch (e: Exception) {
                println("Error al crear el pedido: $e")
            }
        }
    }

    /**
     * Limpia todo el carrito en Firestore.
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

    override fun onCleared() {
        cartListener?.remove()
        super.onCleared()
    }
}
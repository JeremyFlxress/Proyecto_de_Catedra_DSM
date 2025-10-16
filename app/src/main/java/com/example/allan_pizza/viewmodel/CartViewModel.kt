package com.example.allan_pizza.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.allan_pizza.data.CartItem
import com.example.allan_pizza.data.Product

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: List<CartItem> by _cartItems

    val totalItems: Int
        get() = _cartItems.value.sumOf { it.quantity }

    val totalPrice: Double
        get() = _cartItems.value.sumOf { it.totalPrice }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.product.id == product.id }
        
        if (existingItemIndex != -1) {
            // Si el producto ya existe, incrementar la cantidad
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Si es un producto nuevo, agregarlo al carrito
            currentItems.add(CartItem(product = product, quantity = 1))
        }
        
        _cartItems.value = currentItems
    }

    fun removeFromCart(productId: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.product.id == productId }
        
        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            if (existingItem.quantity > 1) {
                // Si hay más de 1, decrementar la cantidad
                currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                // Si solo hay 1, remover el item completamente
                currentItems.removeAt(existingItemIndex)
            }
        }
        
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}

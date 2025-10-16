package com.example.allan_pizza.data

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageResId: Int,
    val description: String = ""
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

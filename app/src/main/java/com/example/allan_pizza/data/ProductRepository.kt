package com.example.allan_pizza.data

import com.example.allan_pizza.R

object ProductRepository {
    val products = listOf(
        Product(
            id = 1,
            name = "Pizza Peperoni Extra Grande",
            price = 12.00,
            imageResId = R.drawable.pizza_peperoni,
            description = "Deliciosa pizza con peperoni y queso mozzarella"
        ),
        Product(
            id = 2,
            name = "Pizza Margherita",
            price = 10.50,
            imageResId = R.drawable.pizza_peperoni,
            description = "Pizza clásica con tomate, mozzarella y albahaca"
        ),
        Product(
            id = 3,
            name = "Pizza Hawaiana",
            price = 13.50,
            imageResId = R.drawable.pizza_peperoni,
            description = "Pizza con jamón, piña y queso mozzarella"
        ),
        Product(
            id = 4,
            name = "Pizza Cuatro Quesos",
            price = 14.00,
            imageResId = R.drawable.pizza_peperoni,
            description = "Pizza con cuatro tipos de queso diferentes"
        ),
        Product(
            id = 5,
            name = "Pizza Vegetariana",
            price = 11.00,
            imageResId = R.drawable.pizza_peperoni,
            description = "Pizza con vegetales frescos y queso mozzarella"
        ),
        Product(
            id = 6,
            name = "Pizza BBQ Chicken",
            price = 15.00,
            imageResId = R.drawable.pizza_peperoni,
            description = "Pizza con pollo a la barbacoa y cebolla"
        )
    ).sortedBy { it.price } // Ordenar por precio
}

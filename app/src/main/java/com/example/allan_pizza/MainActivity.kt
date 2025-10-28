package com.example.allan_pizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.allan_pizza.ui.screens.HomeScreen
import com.example.allan_pizza.ui.screens.OrderVerificationScreen
import com.example.allan_pizza.ui.screens.OrderHistoryScreen
import com.example.allan_pizza.ui.theme.Allan_PizzaTheme
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel
import com.example.allan_pizza.viewmodel.OrderViewModel
import com.example.allan_pizza.ui.screens.CheckoutScreen
import com.example.allan_pizza.ui.screens.ComboDetailScreen
import com.example.allan_pizza.data.Product

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Allan_PizzaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // Estado para controlar qué pantalla mostrar
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCombo by remember { mutableStateOf<Product?>(null) }

    // --- Los ViewModels (esto ya estaba perfecto) ---
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    // Obtenemos el historial (esto ya estaba perfecto)
    val orderHistory by orderViewModel.orderHistory.collectAsState()


    when (currentScreen) {
        "home" -> {
            HomeScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onNavigateToOrderVerification = {
                    currentScreen = "orderVerification"
                },
                onNavigateToOrderHistory = {
                    currentScreen = "orderHistory"
                },
                // --- 1. AÑADIMOS EL NUEVO NAVEGADOR ---
                onNavigateToCheckout = {
                    currentScreen = "checkout"
                },
                onComboClick = { product ->
                    selectedCombo = product // Guarda el combo
                    currentScreen = "comboDetail" // Cambia de pantalla
                }
            )
        }

        "comboDetail" -> {
            // Nos aseguramos de que el combo no sea nulo
            selectedCombo?.let { product ->
                ComboDetailScreen(
                    product = product,
                    onBack = {
                        currentScreen = "home"
                        selectedCombo = null // Limpia la selección
                    }
                )
            } ?: run {
                // Si es nulo por algún error, regresa a home
                currentScreen = "home"
            }
        }


        "checkout" -> {
            CheckoutScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onBack = {
                    // Si el usuario da "atrás", vuelve a home
                    currentScreen = "home"
                },
                onPlaceOrder = { method ->
                    val user = authViewModel.currentUser.value
                    if (user != null) {
                        orderViewModel.resetActiveOrder()
                        cartViewModel.createOrder(user, method)
                        currentScreen = "orderVerification"
                    }
                }
            )
        }

        "orderVerification" -> {
            OrderVerificationScreen(
                // Pasamos las MISMAS instancias
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel,
                onBackToHome = {
                    currentScreen = "home"
                },
                onCartClick = {
                    // TODO: Podrías hacer que esto muestre
                    // un diálogo de carrito si lo necesitas
                },
                onUserClick = {
                    // TODO: Podrías mostrar el perfil
                }
            )
        }
        "orderHistory" -> {
            OrderHistoryScreen(
                // Pasamos la lista real del historial
                orders = orderHistory,
                onBackToHome = {
                    currentScreen = "home"
                }
            )
        }

    }
}
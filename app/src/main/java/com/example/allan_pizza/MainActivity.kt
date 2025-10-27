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
// --- IMPORTACIONES CLAVE ---
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.allan_pizza.ui.screens.HomeScreen
import com.example.allan_pizza.ui.screens.OrderVerificationScreen
import com.example.allan_pizza.ui.screens.OrderHistoryScreen
import com.example.allan_pizza.ui.theme.Allan_PizzaTheme
// --- Importar los ViewModels ---
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel
import com.example.allan_pizza.viewmodel.OrderViewModel

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

    // --- ¡IMPORTANTE! ---
    // Creamos los ViewModels aquí, UNA SOLA VEZ.
    // Estas instancias se compartirán entre todas las pantallas.
    val authViewModel: AuthViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    // Obtenemos el historial (del OrderViewModel que acabamos de crear)
    val orderHistory by orderViewModel.orderHistory.collectAsState()


    when (currentScreen) {
        "home" -> {
            HomeScreen(
                // Pasamos las instancias
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onNavigateToOrderVerification = {
                    currentScreen = "orderVerification"
                },
                onNavigateToOrderHistory = {
                    currentScreen = "orderHistory"
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
                    // Podrías implementar que esto muestre el CartDialog
                },
                onUserClick = {
                    // Podrías implementar que esto muestre el ProfileDialog
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
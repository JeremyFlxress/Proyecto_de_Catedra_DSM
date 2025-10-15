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
import com.example.allan_pizza.ui.screens.HomeScreen
import com.example.allan_pizza.ui.screens.OrderVerificationScreen
import com.example.allan_pizza.ui.theme.Allan_PizzaTheme

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

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onNavigateToOrderVerification = {
                    currentScreen = "orderVerification"
                }
            )
        }
        "orderVerification" -> {
            OrderVerificationScreen(
                onBackToHome = {
                    currentScreen = "home"
                },
                onCartClick = {
                },
                onUserClick = {

                }
            )
        }
    }
}
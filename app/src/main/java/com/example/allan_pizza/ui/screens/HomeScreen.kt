package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Importado
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Importado
import com.example.allan_pizza.R
import com.example.allan_pizza.ui.components.ProductCard
import com.example.allan_pizza.ui.components.LoginDialog
import com.example.allan_pizza.ui.components.RegisterDialog
import com.example.allan_pizza.ui.components.CartDialog
import com.example.allan_pizza.ui.components.OptionsMenu
import com.example.allan_pizza.ui.components.ProfileDialog // Importado
import com.example.allan_pizza.data.ProductRepository
import com.example.allan_pizza.viewmodel.AuthViewModel // Importado
import com.example.allan_pizza.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class) // Añadido para TopAppBar
@Composable
fun HomeScreen(
    onNavigateToOrderVerification: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {}
) {
    // --- LÓGICA DE VIEWMODELS ---
    val cartViewModel = remember { CartViewModel() }
    val authViewModel: AuthViewModel = viewModel() // ViewModel de Autenticación

    // --- ESTADOS DE AUTENTICACIÓN ---
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // --- ESTADOS DE DIÁLOGOS ---
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showCartDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) } // Nuevo estado para perfil

    // Obtener productos del repositorio
    val products = ProductRepository.products

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔺 Header superior (SIN CAMBIOS DE ESTILO)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE53935))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // --- CAMBIO DE LÓGICA: Nombre de usuario ---
                // Muestra el nombre si está logueado, sino el título
                if (isLoggedIn && currentUser != null) {
                    Text(
                        text = "¡Hola, ${currentUser!!.nombre}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Allan Pizza",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón del carrito con badge (SIN CAMBIOS)
                    Box {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito de compras",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    showCartDialog = true
                                },
                            tint = Color.Black
                        )
                        if (cartViewModel.totalItems > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color.White, shape = RoundedCornerShape(50))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartViewModel.totalItems.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    // --- CAMBIO DE LÓGICA: Icono de Persona/Perfil ---
                    Icon(
                        // Cambia el ícono si está logueado
                        imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Filled.Person,
                        contentDescription = if (isLoggedIn) "Perfil" else "Usuario",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                // Muestra perfil si está logueado, sino login
                                if (isLoggedIn) {
                                    showProfileDialog = true
                                } else {
                                    showLoginDialog = true
                                }
                            },
                        tint = Color.Black
                    )

                    // --- CAMBIO DE LÓGICA: Icono de Menú ---
                    // Solo muestra el menú si el usuario ha iniciado sesión
                    if (isLoggedIn) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Opciones",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    showOptionsMenu = true
                                },
                            tint = Color.Black
                        )
                    }
                }
            }

            // 🟡 Estado de la orden (SIN CAMBIOS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ... (tu código de "Estado de tu orden" no se toca) ...
                Text(
                    text = "Estado de tu orden",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Button(
                    onClick = { onNavigateToOrderVerification() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Verificar",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 🖼️ Banner (SIN CAMBIOS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.combo_banner),
                    contentDescription = "Banner de promoción",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🍕 Título (SIN CAMBIOS)
            Text(
                text = "Menú",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 📋 Lista de productos (SIN CAMBIOS)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products.size) { index ->
                    val product = products[index]
                    ProductCard(
                        product = product,
                        onAddToCart = { cartViewModel.addToCart(product) }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        } // Fin de Column principal

        // --- MANEJO DE DIÁLOGOS (DENTRO DEL BOX) ---

        // ✅ Diálogo de inicio de sesión (CONECTADO)
        if (showLoginDialog) {
            LoginDialog(
                authViewModel = authViewModel, // <-- Conectado
                onDismiss = { showLoginDialog = false },
                onLoginSuccess = {
                    showLoginDialog = false
                },
                onRegisterClick = {
                    showLoginDialog = false
                    showRegisterDialog = true
                }
            )
        }

        // ✅ Diálogo de registro (CONECTADO)
        if (showRegisterDialog) {
            RegisterDialog(
                authViewModel = authViewModel, // <-- Conectado
                onDismiss = { showRegisterDialog = false },
                onRegisterSuccess = {
                    showRegisterDialog = false
                    showLoginDialog = true
                }
            )
        }

        // 🛒 Diálogo del carrito (SIN CAMBIOS)
        if (showCartDialog) {
            // ... (tu código del CartDialog no se toca) ...
            CartDialog(
                cartItems = cartViewModel.cartItems,
                onDismiss = { showCartDialog = false },
                onConfirmOrder = {
                    showCartDialog = false
                },
                onAddItem = { productId ->
                    val product = products.find { it.id == productId }
                    product?.let { cartViewModel.addToCart(it) }
                },
                onRemoveItem = { productId ->
                    cartViewModel.removeFromCart(productId)
                }
            )
        }

        // 📋 Menú de opciones (CONECTADO)
        OptionsMenu(
            isVisible = showOptionsMenu,
            onDismiss = { showOptionsMenu = false },
            onOrderHistoryClick = { onNavigateToOrderHistory() }, // Ya estaba bien
            onLogoutClick = {
                // --- CAMBIO DE LÓGICA: Logout ---
                authViewModel.logout()
                // El onDismiss se maneja dentro del OptionsMenu, así que no hace falta
            }
        )

        // 📇 (NUEVO) Diálogo de Perfil
        if (showProfileDialog) {
            currentUser?.let { user ->
                ProfileDialog(
                    user = user,
                    onDismiss = { showProfileDialog = false },
                    onLogout = {
                        authViewModel.logout() // Llama al logout
                        showProfileDialog = false // Cierra el diálogo
                    }
                )
            }
        }

    } // Fin de Box principal
}


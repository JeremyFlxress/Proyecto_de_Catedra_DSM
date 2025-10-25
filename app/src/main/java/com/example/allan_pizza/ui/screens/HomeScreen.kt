package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // CAMBIO: Importar 'items' correcto
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Importar Coil
import com.example.allan_pizza.R
// Asegúrate de importar el ProductCard real
import com.example.allan_pizza.ui.components.ProductCard
import com.example.allan_pizza.ui.components.LoginDialog
import com.example.allan_pizza.ui.components.RegisterDialog
import com.example.allan_pizza.ui.components.CartDialog
import com.example.allan_pizza.ui.components.OptionsMenu
import com.example.allan_pizza.ui.components.ProfileDialog
import com.example.allan_pizza.data.ProductRepository
import com.example.allan_pizza.data.Product // Importar Product
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToOrderVerification: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {}
) {
    // --- LÓGICA DE VIEWMODELS ---
    // CAMBIO: Instanciar el Repositorio y pasarlo al ViewModel (o usar Hilt/Koin para inyección)
    // Por simplicidad, lo instanciamos aquí.
    val productRepository = remember { ProductRepository() }
    val cartViewModel: CartViewModel = viewModel() // No necesita factory si instanciamos repo dentro
    val authViewModel: AuthViewModel = viewModel()

    // --- ESTADOS DE AUTENTICACIÓN ---
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // --- ESTADOS DEL CARRITO (desde el ViewModel) ---
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCartItems by cartViewModel.totalItems.collectAsState()
    val totalCartPrice by cartViewModel.totalPrice.collectAsState()

    // --- ESTADOS DE PRODUCTOS (desde el Repositorio) ---
    val products by productRepository.productsFlow.collectAsState()

    // --- ESTADOS DE DIÁLOGOS ---
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showCartDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔺 Header superior (Lógica actualizada)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE53935))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    // Botón del carrito con badge (Lógica actualizada)
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

                        // CAMBIO: Usar el estado del ViewModel
                        if (totalCartItems > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color.White, shape = RoundedCornerShape(50))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = totalCartItems.toString(), // CAMBIO
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    // Icono de Persona/Perfil (Lógica sin cambios)
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Filled.Person,
                        contentDescription = if (isLoggedIn) "Perfil" else "Usuario",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                if (isLoggedIn) {
                                    showProfileDialog = true
                                } else {
                                    showLoginDialog = true
                                }
                            },
                        tint = Color.Black
                    )

                    // Icono de menú (Lógica sin cambios)
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

            // 🟡 Estado de la orden (Sin cambios)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

            // 🖼️ Banner (Sin cambios)
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

            // 🍕 Título (Sin cambios)
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

            // 📋 Lista de productos (Lógica actualizada)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // CAMBIO: Usar 'items' con la lista del flow
                items(products) { product ->
                    ProductCard(
                        product = product,
                        // CAMBIO: Asumir que ProductCard usa Coil para 'imageUrl'
                        // y que 'onAddToCart' ahora solo necesita el producto.
                        onAddToCart = {
                            if (isLoggedIn) {
                                cartViewModel.addToCart(product)
                            } else {
                                // Si no está logueado, mostrar diálogo de login
                                showLoginDialog = true
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        // ✅ Diálogo de inicio de sesión (Sin cambios)
        if (showLoginDialog) {
            LoginDialog(
                authViewModel = authViewModel,
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

        // ✅ Diálogo de registro (Sin cambios)
        if (showRegisterDialog) {
            RegisterDialog(
                authViewModel = authViewModel,
                onDismiss = { showRegisterDialog = false },
                onRegisterSuccess = {
                    showRegisterDialog = false
                    showLoginDialog = true
                }
            )
        }

        // 🛒 Diálogo del carrito (Lógica actualizada)
        if (showCartDialog) {
            CartDialog(
                cartItems = cartItems, // CAMBIO: Usar 'cartItems' del flow
                totalPrice = totalCartPrice, // CAMBIO: Usar 'totalCartPrice' del flow
                onDismiss = { showCartDialog = false },
                onConfirmOrder = {
                    showCartDialog = false
                    // TODO: Navegar a la pantalla de verificación
                    onNavigateToOrderVerification()
                },
                onAddItem = { productId -> // ID ahora es String
                    // Encontramos el producto en la lista completa
                    val product = products.find { it.id == productId }
                    product?.let { cartViewModel.addToCart(it) }
                },
                onRemoveItem = { productId -> // ID ahora es String
                    cartViewModel.removeFromCart(productId)
                }
            )
        }

        // 📋 Menú de opciones (Sin cambios)
        OptionsMenu(
            isVisible = showOptionsMenu,
            onDismiss = { showOptionsMenu = false },
            onOrderHistoryClick = { onNavigateToOrderHistory() },
            onLogoutClick = {
                authViewModel.logout()
            }
        )

        // 📇 Diálogo de Perfil (Sin cambios)
        if (showProfileDialog) {
            currentUser?.let { user ->
                ProfileDialog(
                    user = user,
                    onDismiss = { showProfileDialog = false },
                    onLogout = {
                        authViewModel.logout()
                        showProfileDialog = false
                    }
                )
            }
        }

    } // Fin de Box principal
}


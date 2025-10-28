package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Sigue siendo necesario para el preview
import com.example.allan_pizza.R
import com.example.allan_pizza.ui.components.AutoCarousel
import com.example.allan_pizza.ui.components.BannerItem
import com.example.allan_pizza.ui.components.ProductCard
import com.example.allan_pizza.ui.components.LoginDialog
import com.example.allan_pizza.ui.components.RegisterDialog
import com.example.allan_pizza.ui.components.CartDialog
import com.example.allan_pizza.ui.components.OptionsMenu
import com.example.allan_pizza.ui.components.ProfileDialog
import com.example.allan_pizza.data.ProductRepository
import com.example.allan_pizza.data.Product
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.math.ceil


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onNavigateToOrderVerification: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToCheckout: () -> Unit
) {
    val productRepository = remember { ProductRepository() }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCartItems by cartViewModel.totalItems.collectAsState()
    val totalCartPrice by cartViewModel.totalPrice.collectAsState()
    val products by productRepository.productsFlow.collectAsState()
    var currentPage by remember {mutableStateOf(1)}
    val itemsPerPage = 5
    val allProducts by productRepository.productsFlow.collectAsState()
    val totalPages = if (allProducts.isEmpty()) {
        1
    }else {
        ceil(allProducts.size.toDouble() / itemsPerPage).toInt()
    }
    val paginatedProducts = remember(currentPage, allProducts) {
        if (allProducts.isNotEmpty()) {
            val startIndex = (currentPage - 1) * itemsPerPage // 'coerceAtMost' evita que el índice se pase del tamaño de la lista
            val endIndex = (startIndex + itemsPerPage).coerceAtMost(allProducts.size)
            allProducts.slice(startIndex until endIndex)
        } else {
            emptyList() // Lista vacía si no hay productos
        }
    }
    val banners = remember {
        listOf(
            BannerItem(
                imageRes = R.drawable.combo_banner,
                title = "¡Combo Especial!",
                subtitle = "2 Pizzas + Bebida por solo $25.99"
            ),
            BannerItem(
                imageRes = R.drawable.pizza_peperoni,
                title = "Pizza del Día",
                subtitle = "Pepperoni Extra Grande - $18.99"
            ),
            BannerItem(
                imageRes = R.drawable.combo_banner,
                title = "Delivery Gratis",
                subtitle = "En pedidos mayores a $30"
            ),
            BannerItem(
                imageRes = R.drawable.pizza_peperoni,
                title = "20% de Descuento",
                subtitle = "En tu primera orden"
            )
        )
    }

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

            //  Header superior
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
                    // Botón del carrito con badge
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

                        if (totalCartItems > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color.White, shape = RoundedCornerShape(50))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = totalCartItems.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    // Icono de Persona/Perfil
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

                    // Icono de menú
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

            // 🟡 Estado de la orden
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

            // 🖼️ Carrusel de Banners
            AutoCarousel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                banners = banners,
                autoScrollDelay = 3000L, // 3 segundos
                height = 120
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🍕 Título
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

            // 📋 Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(paginatedProducts) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            if (isLoggedIn) {
                                cartViewModel.addToCart(product)
                            } else {
                                showLoginDialog = true
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPrevious = {
                    if (currentPage > 1) currentPage--
                },
                onNext = {
                    if (currentPage < totalPages) currentPage++
                }
            )
        }

        // ✅ Diálogo de inicio de sesión
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

        // ✅ Diálogo de registro
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

        // 🛒 Diálogo del carrito
        if (showCartDialog) {
            CartDialog(
                cartItems = cartItems,
                totalPrice = totalCartPrice,
                onDismiss = { showCartDialog = false },
                onConfirmOrder = {
                    val user = authViewModel.currentUser.value
                    if (user != null) {
                        // 1. Cierra el diálogo
                        showCartDialog = false
                        // 2. Navega a la nueva pantalla de Checkout
                        onNavigateToCheckout()
                    } else {
                        // Si no está logueado, lo mandamos a loguear
                        showCartDialog = false // Cierra también el carrito
                        showLoginDialog = true
                    }
                    // --- FIN DE LA MODIFICACIÓN ---
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

        // 📋 Menú de opciones
        OptionsMenu(
            isVisible = showOptionsMenu,
            onDismiss = { showOptionsMenu = false },
            onOrderHistoryClick = { onNavigateToOrderHistory() },
            onLogoutClick = {
                authViewModel.logout()
            }
        )

        // 📇 Diálogo de Perfil
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

    }
}

@Composable
private fun PaginationControls(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    // No mostrar los controles si solo hay 1
    if (totalPages <= 1) {
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Botón "Anterior"
        IconButton(
            onClick = onPrevious,
            enabled = currentPage > 1, // Deshabilitado en la página 1
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color(0xFFE53935), // Rojo
                disabledContentColor = Color.Gray
            )
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Página anterior")
        }

        // Texto "Página X de Y"
        Text(
            text = "Página $currentPage de $totalPages",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Botón "Siguiente"
        IconButton(
            onClick = onNext,
            enabled = currentPage < totalPages, // Deshabilitado en la última página
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color(0xFFE53935), // Rojo
                disabledContentColor = Color.Gray
            )
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Página siguiente")
        }
    }
}
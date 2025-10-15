package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.allan_pizza.R
import com.example.allan_pizza.ui.components.ProductCard
import com.example.allan_pizza.ui.components.LoginDialog
import com.example.allan_pizza.ui.components.RegisterDialog
import com.example.allan_pizza.ui.components.CartDialog
import com.example.allan_pizza.ui.components.CartItem

@Preview(showBackground = true)
@Composable
fun HomeScreen(onNavigateToOrderVerification: () -> Unit = {}) {
    // Estados de los diálogos
    var showLoginDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showCartDialog by remember { mutableStateOf(false) }

    // Lista de items del carrito (puedes agregar más dinámicamente)
    val cartItems = remember {
        mutableStateListOf(
            CartItem(
                imageResId = R.drawable.pizza_peperoni,
                name = "Pizza Peperoni Extra Grande",
                price = 12.00,
                status = "Preparando"
            ),
            CartItem(
                imageResId = R.drawable.pizza_peperoni,
                name = "Pizza Peperoni Extra Grande",
                price = 12.00,
                status = "Preparando"
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔺 Header superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE53935))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Allan Pizza",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

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

                        // Badge con cantidad de items
                        if (cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color.White, shape = RoundedCornerShape(50))
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItems.size.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Usuario",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                showLoginDialog = true
                            },
                        tint = Color.Black
                    )
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

            // 🖼️ Banner
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(4) {
                    ProductCard(
                        imageResId = R.drawable.pizza_peperoni,
                        name = "Pizza Peperoni Extra Grande",
                        price = "$12.00"
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        // ✅ Diálogo de inicio de sesión
        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onLogin = { email, password ->
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
                onDismiss = { showCartDialog = false },
                onConfirmOrder = {
                    // Aquí puedes agregar la lógica para confirmar el pedido
                    showCartDialog = false
                    // Por ejemplo, mostrar un mensaje de confirmación
                }
            )
        }
    }
}
package com.example.allan_pizza.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Sigue siendo necesario para el preview
import coil.compose.AsyncImage // ¡Importante para la imagen!
import com.example.allan_pizza.R
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel
import com.example.allan_pizza.viewmodel.OrderViewModel
import kotlinx.coroutines.delay

@Composable
fun OrderVerificationScreen(
    // --- CAMBIO: Recibimos los ViewModels y quitamos los defaults (= viewModel()) ---
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    // ---
    onBackToHome: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onUserClick: () -> Unit = {}
) {
    // --- ESTADOS DE AUTENTICACIÓN (Leídos del ViewModel recibido) ---
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // --- ESTADOS DEL CARRITO (Leídos del ViewModel recibido) ---
    val totalCartItems by cartViewModel.totalItems.collectAsState()

    // --- ESTADO DEL PEDIDO (Leído del ViewModel recibido) ---
    val activeOrder by orderViewModel.activeOrder.collectAsState()

    // --- MANEJO DEL BOTÓN DE RETROCESO DEL DISPOSITIVO ---
    BackHandler {
        onBackToHome()
    }

    // --- LÓGICA DE AUTO-REGRESO ---
    // (Esta parte ya estaba bien)
    LaunchedEffect(activeOrder?.status) {
        if (activeOrder?.status == "Completado") {
            // Si el estado es "Completado", espera 3 segundos...
            delay(3000)
            // ...y luego navega de vuelta al Home (limpiando la pantalla)
            onBackToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔺 Header superior (Sin cambios)
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
                    text = currentUser?.nombre ?: "Allan Pizza",
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
                                .clickable { onCartClick() },
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
                                    text = totalCartItems.toString(), // Dinámico
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Filled.Person,
                        contentDescription = "Usuario",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onUserClick() },
                        tint = Color.Black
                    )
                }
            }

            // Contenido scrolleable
            if (activeOrder == null) {
                // Estado de carga o si no hay pedido
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // --- CAMBIO: Mejorar el mensaje de "no pedido" ---
                        Image(
                            painter = painterResource(id = R.drawable.pizza_peperoni), // Asume que tienes un drawable para esto
                            contentDescription = "No hay pedido",
                            modifier = Modifier.size(120.dp),
                            alpha = 0.7f
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes ningún pedido activo en este momento.",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Cuando el pedido se ha cargado
                val order = activeOrder!! // Sabemos que no es nulo aquí

                // (El resto de la lógica de UI no necesita cambios)
                val (backgroundColor, textColor) = when (order.status) {
                    "En preparación" -> Color(0xFFFDD835) to Color.Black // Amarillo
                    "En camino" -> Color(0xFF81D4FA) to Color.Black // Celeste
                    "Completado" -> Color(0xFFA5D6A7) to Color.Black // Verde
                    else -> Color.Gray to Color.White // Un color por defecto
                }

                // Formatea la lista de productos
                val productNames = order.items.joinToString("\n") {
                    "${it.quantity}x ${it.productName}"
                }
                // Coge la imagen del primer producto
                val imageUrl = order.items.firstOrNull()?.productImageUrl

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🖼️ Imagen de la pizza (dinámica)
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen del producto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.pizza_peperoni),
                        error = painterResource(id = R.drawable.pizza_peperoni)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 📝 Campo: Nombre (dinámico)
                    OrderInfoField(label = "Nombre", value = order.userName)

                    // 🍕 Título del producto (dinámico)
                    Text(
                        text = "Tu pedido:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    // Muestra los productos
                    Text(
                        text = productNames,
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )


                    // 💰 Campo: Total a pagar (dinámico)
                    OrderInfoField(
                        label = "Total a pagar: $",
                        value = "%.2f".format(order.totalPrice)
                    )

                    // 💳 Campo: Método de pago (dinámico)
                    OrderInfoField(label = "Método de pago:", value = order.paymentMethod)

                    // 📍 Campo: Lugar (dinámico)
                    OrderInfoField(label = "Lugar", value = order.userAddress)

                    // 🟡 Estado de la orden (dinámico)
                    Text(
                        text = "Estado de la orden:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor) // Color dinámico
                            .padding(PaddingValues(vertical = 10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.status, // Dinámico
                            color = textColor,   // Color dinámico
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔙 Botón para regresar al Home
                    Button(
                        onClick = {
                            onBackToHome()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Volver al Menú",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OrderInfoField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFAFAFA), // Un gris claro
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black // Asegura que el texto sea legible
            )
        )
    }
}
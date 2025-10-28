package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
// --- NUEVO: Importar Coil para imágenes ---
import coil.compose.AsyncImage
import com.example.allan_pizza.R // Asegúrate de que esta sea tu R
// ---
import com.example.allan_pizza.data.Order
import com.example.allan_pizza.data.OrderItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// --- CAMBIO: Formato de fecha para incluir la hora ---
private fun formatTimestamp(timestamp: Timestamp): String {
    // Formato: "27 oct 2025, 11:35 a.m."
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "ES"))
    return sdf.format(timestamp.toDate())
}

@Composable
fun OrderHistoryScreen(
    orders: List<Order>,
    onBackToHome: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar (Sin cambios)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE53935))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBackToHome() },
                    tint = Color.White
                )

                Text(
                    text = "Allan Pizza",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Espacio para mantener el título centrado
                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título "Historial de pedidos" (Sin cambios)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(
                            Color(0xFF424242),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Historial de pedidos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMBIO GRANDE: Lista de Pedidos Rediseñada ---
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes pedidos en tu historial.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    // Un solo 'item' por 'Order', con espacio entre ellos
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Iteramos sobre la lista de pedidos
                    items(orders) { order ->
                        // Usamos nuestro nuevo Composable
                        OrderHistoryCard(order = order)
                    }

                    // Espacio final
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// --- NUEVO: Composable para la Card de Historial Rediseñada ---
@Composable
fun OrderHistoryCard(order: Order) {
    // Obtenemos el primer item para la imagen y el resumen
    val firstItem = order.items.firstOrNull()
    val remainingItems = order.items.size - 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)), // Fondo gris claro
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Imagen del primer producto
            AsyncImage(
                model = firstItem?.productImageUrl,
                contentDescription = "Imagen del pedido",
                modifier = Modifier
                    .size(70.dp) // Tamaño fijo para la imagen
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop,
                // Placeholder genérico (el que usamos en OrderVerificationScreen)
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Columna de Información (Nombre, Hora, Total)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Fila para el nombre del producto y (+X más)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = firstItem?.productName ?: "Detalle del Pedido",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    // Muestra " (+1 más)" si hay más items
                    if (remainingItems > 0) {
                        Text(
                            text = " (+${remainingItems} más)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    }
                }

                // Fecha y Hora (¡NUEVO!)
                Text(
                    text = formatTimestamp(order.timestamp),
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                // Total del Pedido
                Text(
                    text = "Total: $%.2f".format(order.totalPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935) // Color rojo de tu marca
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Chip de Estado (Completado, En camino, etc.)
            val (backgroundColor, textColor) = when (order.status) {
                "En preparación" -> Color(0xFFFDD835) to Color.Black // Amarillo
                "En camino" -> Color(0xFF81D4FA) to Color.Black // Celeste
                "Completado" -> Color(0xFFA5D6A7) to Color.Black // Verde
                "Cancelado" -> Color(0xFFEF9A9A) to Color.Black // Rojo claro
                else -> Color.Gray to Color.White
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = order.status,
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

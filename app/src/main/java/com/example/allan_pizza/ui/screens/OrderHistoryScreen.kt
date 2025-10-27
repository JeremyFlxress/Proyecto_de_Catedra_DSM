package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- NUEVO: Importar los data models reales ---
import com.example.allan_pizza.data.Order
import com.example.allan_pizza.data.OrderItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// --- ELIMINADO: Ya no necesitamos las data class de ejemplo ---

// --- NUEVO: Función helper para formatear la fecha ---
private fun formatTimestamp(timestamp: Timestamp): String {
    // Formato: "martes, 26 de Agosto de 2025"
    val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return sdf.format(timestamp.toDate())
}

// --- CAMBIO: La firma de la función ahora acepta la lista real ---
@Composable
fun OrderHistoryScreen(
    orders: List<Order>, // <-- Acepta la lista real
    onBackToHome: () -> Unit = {}
) {

    // --- ELIMINADO: Ya no necesitamos los datos de ejemplo ---

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

            // --- CAMBIO: Lista de pedidos con datos reales ---
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Iteramos sobre la lista real de pedidos
                    orders.forEach { order ->
                        // Fecha
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    // Formateamos el Timestamp real
                                    text = formatTimestamp(order.timestamp),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Items de la orden
                        items(order.items) { item ->
                            // Pasamos los datos reales al OrderCard
                            OrderCard(
                                productName = item.productName,
                                quantity = item.quantity,
                                // Calculamos el total de este item
                                total = item.quantity * item.unitPrice
                            )
                        }
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

// --- CAMBIO: OrderCard ahora recibe un Double para el total ---
@Composable
fun OrderCard(
    productName: String,
    quantity: Int,
    total: Double // <-- Cambiado de String a Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                color = Color(0xFFFFA726),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = productName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cantidad: $quantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
            Text(
                // Formateamos el total a 2 decimales
                text = "Total: $%.2f".format(total),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center
            )
        }
    }
}


// --- AÑADIDO UN PREVIEW (opcional pero recomendado) ---
@Preview(showBackground = true)
@Composable
fun OrderHistoryScreenPreview() {
    // Creamos datos de ejemplo solo para el preview
    val previewOrders = listOf(
        Order(
            id = "1",
            timestamp = Timestamp.now(),
            items = listOf(
                OrderItem(productName = "Pizza Peperoni", quantity = 2, unitPrice = 12.0),
                OrderItem(productName = "Coca Cola", quantity = 1, unitPrice = 1.5)
            )
        ),
        Order(
            id = "2",
            timestamp = Timestamp.now(),
            items = listOf(
                OrderItem(productName = "Pizza Suprema", quantity = 1, unitPrice = 14.0)
            )
        )
    )
    OrderHistoryScreen(orders = previewOrders, onBackToHome = {})
}
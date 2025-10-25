package com.example.allan_pizza.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage // Importar AsyncImage de Coil
import com.example.allan_pizza.data.CartItem
import com.example.allan_pizza.R // Importar R para los placeholders

@Composable
fun CartDialog(
    cartItems: List<CartItem>,
    totalPrice: Double, // Recibir el total calculado desde el ViewModel
    onDismiss: () -> Unit,
    onConfirmOrder: () -> Unit,
    onAddItem: (String) -> Unit = {},    // CAMBIO: de Int a String
    onRemoveItem: (String) -> Unit = {}  // CAMBIO: de Int a String
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(0.dp) // Reducido para que el Card ocupe todo
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(20.dp) // Padding aplicado aquí dentro
                ) {
                    // Header del carrito (Sin cambios)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Carrito",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Detalles del pedido",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "¡Gracias por preferirnos!",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lista de productos en el carrito
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(
                                cartItem = item,
                                // CAMBIO: Pasa el ID como String
                                onAddItem = { onAddItem(item.product.id) },
                                onRemoveItem = { onRemoveItem(item.product.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de confirmar pedido con total
                    // CAMBIO: Usar el totalPrice del ViewModel
                    // val total = cartItems.sumOf { it.totalPrice } // -> Esta lógica ahora está en el VM

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onConfirmOrder,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "Confirmar\nPedido",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            // CAMBIO: Formatear el total recibido
                            text = "Total: $${"%.2f".format(totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CAMBIO: Cargar imagen desde URL con Coil
            AsyncImage(
                model = cartItem.product.imageUrl, // Carga la URL
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                // Placeholder por si falla la carga
                placeholder = painterResource(id = R.drawable.pizza_peperoni), // Asumiendo que tienes este drawable
                error = painterResource(id = R.drawable.pizza_peperoni) // Asumiendo que tienes este drawable
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información del producto (Sin cambios)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Precio unitario: $${String.format("%.2f", cartItem.product.price)}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Total: $${String.format("%.2f", cartItem.totalPrice)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935)
                )
            }

            // Controles de cantidad (Sin cambios)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onAddItem,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(0xFFE53935),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = cartItem.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                IconButton(
                    onClick = onRemoveItem,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(0xFF757575),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Quitar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


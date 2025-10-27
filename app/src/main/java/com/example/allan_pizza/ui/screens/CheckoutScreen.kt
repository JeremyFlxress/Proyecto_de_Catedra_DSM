// Contenido para CheckoutScreen.kt
package com.example.allan_pizza.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.allan_pizza.ui.components.CartItemCard // Reutilizamos tu Card!
import com.example.allan_pizza.viewmodel.AuthViewModel
import com.example.allan_pizza.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onPlaceOrder: (String) -> Unit,
    onBack: () -> Unit
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black, // Texto al escribir
        unfocusedTextColor = Color.Black, // Texto al estar en otro campo
        focusedBorderColor = Color(0xFFE53935), // Borde rojo al seleccionar
        unfocusedBorderColor = Color.Gray, // Borde gris normal
        focusedLabelColor = Color(0xFFE53935), // Etiqueta roja al seleccionar
        cursorColor = Color(0xFFE53935) // Cursor rojo
    )

    val paymentOptions = listOf("Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito")
    var selectedPaymentMethod by remember { mutableStateOf(paymentOptions[0]) }
    var isPaymentMenuExpanded by remember { mutableStateOf(false) }

    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Validar si el botón de pagar debe estar activo
    val isPaymentInfoValid = when (selectedPaymentMethod) {
        "Efectivo" -> true
        else -> cardNumber.length == 16 && expiryDate.length == 5 && cvv.length == 3
    }
    // Obtenemos los datos de los ViewModels
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCartPrice by cartViewModel.totalPrice.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Pedido", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53935),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Barra inferior con el total y el botón de pago
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Total a Pagar:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            "$${"%.2f".format(totalCartPrice)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Button(
                        onClick = { onPlaceOrder(selectedPaymentMethod)},
                        enabled = isPaymentInfoValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935), // Color normal
                            contentColor = Color.White,       // Texto normal
                            disabledContainerColor = Color(0xFFBDBDBD), // Fondo gris (deshabilitado)
                            disabledContentColor = Color(0xFFFAFAFA) // Texto blanco (deshabilitado)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            "Realizar Pedido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- 1. Detalles de Entrega ---
            item {
                CheckoutSectionCard(title = "Detalles de Entrega") {
                    InfoRow("Nombre:", currentUser?.nombre ?: "No disponible")
                    InfoRow("Dirección:", currentUser?.direccion ?: "No disponible")
                    InfoRow("Teléfono:", currentUser?.telefono ?: "No disponible")
                }
            }

            // --- 2. Metodo de Pago ---
            item {
                CheckoutSectionCard(title = "Método de Pago") {

                    // --- NUEVO: Dropdown de selección ---
                    ExposedDropdownMenuBox(
                        expanded = isPaymentMenuExpanded,
                        onExpandedChange = { isPaymentMenuExpanded = !isPaymentMenuExpanded },
                    ) {
                        TextField(
                            value = selectedPaymentMethod,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = isPaymentMenuExpanded
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    if (selectedPaymentMethod == "Efectivo") Icons.Default.Money else Icons.Default.CreditCard,
                                    contentDescription = null
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor() // Importante
                        )

                        ExposedDropdownMenu(
                            expanded = isPaymentMenuExpanded,
                            onDismissRequest = { isPaymentMenuExpanded = false }
                        ) {
                            paymentOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedPaymentMethod = option
                                        isPaymentMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // --- NUEVO: Campos condicionales para tarjeta ---
                    AnimatedVisibility(visible = selectedPaymentMethod != "Efectivo") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { if (it.length <= 16) cardNumber = it },
                                label = { Text("Número de Tarjeta (16 dígitos)") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                            Row(Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { if (it.length <= 5) expiryDate = it }, // TODO: Añadir formato XX/XX
                                    label = { Text("Exp (MM/YY)") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.weight(1f),
                                    colors = textFieldColors
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { if (it.length <= 3) cvv = it },
                                    label = { Text("CVV (3 dígitos)") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    modifier = Modifier.weight(1f),
                                    colors = textFieldColors
                                )
                            }
                        }
                    }
                    // --- FIN NUEVO ---
                }
            }

            // --- 3. Resumen del Pedido ---
            item {
                Text(
                    "Resumen del Pedido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            items(cartItems) { item ->
                // ¡Reutilizamos el Card del diálogo!
                // Lo hacemos "read-only" pasando lambdas vacías
                CartItemCard(
                    cartItem = item,
                    onAddItem = { /* No se puede editar aquí */ },
                    onRemoveItem = { /* No se puede editar aquí */ }
                )
            }
        }
    }
}

// Card genérico para las secciones
@Composable
private fun CheckoutSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// Fila simple para mostrar información
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}
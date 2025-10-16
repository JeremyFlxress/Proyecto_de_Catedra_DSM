package com.example.allan_pizza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
fun OptionsMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.TopEnd
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 60.dp, end = 16.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // Opción 1: Historial de pedidos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOrderHistoryClick()
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Historial de pedidos",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Historial de pedidos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    
                    // Opción 2: Salir de la cuenta
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLogoutClick()
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Salir de la cuenta",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Salir de la cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

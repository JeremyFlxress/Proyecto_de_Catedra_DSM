package com.example.allan_pizza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.allan_pizza.data.UserModel

@Composable
fun ProfileDialog(
    user: UserModel, // Recibe los datos del usuario para mostrarlos
    onDismiss: () -> Unit,
    onLogout: () -> Unit // Callback para cuando se presiona "Cerrar Sesión"
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE584), shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Mi Perfil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Mostramos la información
                InfoRow(icon = Icons.Default.Person, text = user.nombre)
                InfoRow(icon = Icons.Default.Email, text = user.email)
                InfoRow(icon = Icons.Default.Phone, text = user.telefono)
                InfoRow(icon = Icons.Default.Home, text = user.direccion)

                Spacer(modifier = Modifier.height(8.dp))
                

                // Botón de Cerrar Sesión
                Button(
                    onClick = {
                        onLogout() // Llama al callback
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Rojo
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}


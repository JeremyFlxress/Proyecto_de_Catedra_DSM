package com.example.allan_pizza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
// --- IMPORTACIÓN AÑADIDA ---
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.allan_pizza.viewmodel.AuthUiState
import com.example.allan_pizza.viewmodel.AuthViewModel

@Composable
fun RegisterDialog(
    onDismiss: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel() // Obtiene el ViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observamos el estado de la UI desde el ViewModel
    val uiState by authViewModel.uiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading

    // Efecto para reaccionar al éxito y resetear el estado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess() // Llama al callback (ej. para cerrar el diálogo)
            authViewModel.resetState() // Resetea el estado
        }
    }

    Dialog(onDismissRequest = {
        authViewModel.resetState() // Resetea también si se cierra manualmente
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFCC80), shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "¡Regístrate!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // --- CORRECCIÓN AQUÍ ---
                // Se añadió el nombre del parámetro 'onValueChange =' en todas las llamadas
                CustomTextField(
                    label = "Nombre completo",
                    value = nombre,
                    enabled = !isLoading,
                    onValueChange = { nombre = it }
                )
                CustomTextField(
                    label = "Dirección",
                    value = direccion,
                    enabled = !isLoading,
                    onValueChange = { direccion = it }
                )
                CustomTextField(
                    label = "Teléfono",
                    value = telefono,
                    enabled = !isLoading,
                    onValueChange = { telefono = it }
                )
                CustomTextField(
                    label = "Email",
                    value = email,
                    enabled = !isLoading,
                    onValueChange = { email = it }
                )

                // Campo de Contraseña
                CustomTextField(
                    label = "Contraseña",
                    value = password,
                    enabled = !isLoading,
                    onValueChange = { password = it },
                    isPassword = true
                )

                // Muestra un error si uiState es Error
                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón de registro
                Button(
                    onClick = {
                        // Llamamos al ViewModel para registrar
                        authViewModel.registerUser(nombre, direccion, telefono, email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading // Deshabilitado mientras carga
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// TextField reutilizable para este diálogo
@Composable
private fun CustomTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Black)
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            // --- CORRECCIÓN AQUÍ ---
            // Era PasswordVisualTransformation.None, se cambió a VisualTransformation.None
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}


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
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.allan_pizza.viewmodel.AuthUiState
import com.example.allan_pizza.viewmodel.AuthViewModel

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel() // Obtiene el ViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observa el estado de la UI del ViewModel
    val uiState by authViewModel.uiState.collectAsState()
    val isLoading = uiState is AuthUiState.Loading

    // Efecto para reaccionar al éxito del login y resetear el estado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess() // Cierra el diálogo
            authViewModel.resetState() // Resetea el estado para la próxima vez
        }
    }

    Dialog(onDismissRequest = {
        authViewModel.resetState() // Resetea también si se cierra manually
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFB74D), shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Campo de Email
                LoginTextField("Email", email, !isLoading) { email = it }

                // Campo de Contraseña
                LoginTextField("Contraseña", password, !isLoading, isPassword = true) { password = it }

                // Muestra de Errores
                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón de Ingresar
                Button(
                    onClick = {
                        authViewModel.loginUser(email, password)
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
                            text = "Ingresar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Enlace de registro
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable(enabled = !isLoading) { onRegisterClick() }
                )
            }
        }
    }
}

// TextField reutilizable para este diálogo
@Composable
private fun LoginTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
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


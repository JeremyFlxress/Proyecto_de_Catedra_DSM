// Contenido para ComboDetailScreen.kt
package com.example.allan_pizza.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import coil.compose.AsyncImage
import com.example.allan_pizza.R
import com.example.allan_pizza.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboDetailScreen(
    product: Product, // Recibimos el producto (combo) a mostrar
    onBack: () -> Unit  // Función para regresar
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53935), // Rojo
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Para que sea scrolleable si la descripción es larga
                .background(Color.White)
        ) {
            // Imagen del combo
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Imagen más grande
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título y Precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$%.2f".format(product.price),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }

                // Divider
                Divider(color = Color.Gray.copy(alpha = 0.3f))

                // Descripción (¡Lo más importante!)
                Text(
                    text = "Descripción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = product.description, // Muestra la descripción de Firebase
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    lineHeight = 24.sp // Espacio entre líneas
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para regresar
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC79100) // Amarillo mostaza oscuro
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Volver al Menú",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Texto blanco
                    )
                }
            }
        }
    }
}
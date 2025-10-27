package com.example.allan_pizza.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import kotlinx.coroutines.delay

data class BannerItem(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)

@Composable
fun AutoCarousel(
    modifier: Modifier = Modifier,
    banners: List<BannerItem>,
    autoScrollDelay: Long = 3000L, // 3 segundos
    height: Int = 100
) {
    if (banners.isEmpty()) return
    
    var currentIndex by remember { mutableStateOf(0) }
    
    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(autoScrollDelay)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val currentBanner = banners[currentIndex]
        
        // Background image/shape
        Image(
            painter = painterResource(id = currentBanner.imageRes),
            contentDescription = "Banner ${currentIndex + 1}",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay for better text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(12.dp))
        )
        
        // Text overlay
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentBanner.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = currentBanner.subtitle,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
        
        // Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            banners.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (index == currentIndex) Color.White else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RouteBook(
    val id: Int,
    val title: String,
    val distanceKm: Double,
    val elevationM: Int,
    val location: String,
    val tags: List<String>,
    val coverResId: Int,
    val coverImageUrl: String,
    val difficulty: String
)

@Composable
fun RouteCard(route: RouteBook, onDownload: (RouteBook) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = route.coverResId),
                        contentDescription = route.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(route.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(String.format("%.2f km · %d m · %s", route.distanceKm, route.elevationM, route.difficulty), fontSize = 13.sp, color = Color.Gray)
                    if (route.location.isNotBlank()) Text(route.location, fontSize = 12.sp, color = Color.Gray)
                    if (route.tags.isNotEmpty()) Text(route.tags.joinToString(" · "), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}


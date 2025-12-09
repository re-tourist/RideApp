package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.screens.DiscoverScreen
import coil.compose.AsyncImage
import com.example.rideflow.R
import com.example.rideflow.backend.DatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBookDetailScreen(navController: NavController, routeId: Int) {
    var title by remember { mutableStateOf("") }
    var distanceKm by remember { mutableStateOf(0.0) }
    var elevationM by remember { mutableStateOf(0) }
    var location by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("简单") }
    var coverUrl by remember { mutableStateOf<String?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var favoriteCount by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }
    val userId = 1 // 测试用户，可后续接入真实登录态

    LaunchedEffect(routeId) {
        Thread {
            DatabaseHelper.processQuery(
                "SELECT title, distance_km, elevation_m, location, difficulty, cover_image_url FROM routes WHERE route_id = ?",
                listOf(routeId)
            ) { rs ->
                if (rs.next()) {
                    val t = rs.getString(1)
                    val d = rs.getDouble(2)
                    val e = rs.getInt(3)
                    val l = rs.getString(4) ?: ""
                    val dif = rs.getString(5) ?: "简单"
                    val img = rs.getString(6)
                    androidx.core.os.HandlerCompat.createAsync(android.os.Looper.getMainLooper()).post {
                        title = t
                        distanceKm = d
                        elevationM = e
                        location = l
                        difficulty = dif
                        coverUrl = img
                    }
                }
                Unit
            }
            val tagList = mutableListOf<String>()
            DatabaseHelper.processQuery("SELECT tag_name FROM route_tags WHERE route_id = ?", listOf(routeId)) { trs ->
                while (trs.next()) tagList.add(trs.getString(1) ?: "")
                Unit
            }
            DatabaseHelper.processQuery("SELECT COUNT(*) FROM route_favorites WHERE route_id = ?", listOf(routeId)) { frs ->
                if (frs.next()) favoriteCount = frs.getInt(1)
                Unit
            }
            DatabaseHelper.processQuery("SELECT 1 FROM route_favorites WHERE route_id = ? AND user_id = ? LIMIT 1", listOf(routeId, userId)) { urs ->
                isFavorite = urs.next()
                Unit
            }
            androidx.core.os.HandlerCompat.createAsync(android.os.Looper.getMainLooper()).post { tags = tagList }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "路书详情") },
                navigationIcon = {
                    IconButton(onClick = {
                        try {
                            com.example.rideflow.ui.screens.DiscoverNavigatorState.openRouteBook = true
                        } catch (_: Exception) {}
                        navController.navigate("${AppRoutes.MAIN}?tab=discover") {
                            popUpTo(AppRoutes.MAIN) { inclusive = true }
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Thread {
                            if (isFavorite) {
                                DatabaseHelper.executeUpdate(
                                    "DELETE FROM route_favorites WHERE user_id = ? AND route_id = ?",
                                    listOf(userId, routeId)
                                )
                                androidx.core.os.HandlerCompat.createAsync(android.os.Looper.getMainLooper()).post {
                                    isFavorite = false
                                    favoriteCount = (favoriteCount - 1).coerceAtLeast(0)
                                }
                            } else {
                                DatabaseHelper.executeUpdate(
                                    "INSERT IGNORE INTO route_favorites (user_id, route_id, favorited_at) VALUES (?, ?, CURRENT_TIMESTAMP)",
                                    listOf(userId, routeId)
                                )
                                androidx.core.os.HandlerCompat.createAsync(android.os.Looper.getMainLooper()).post {
                                    isFavorite = true
                                    favoriteCount = favoriteCount + 1
                                }
                            }
                        }.start()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "收藏",
                            tint = if (isFavorite) Color(0xFFFFD54F) else Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                if (coverUrl != null) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    AssistChip(onClick = {}, label = { Text(text = difficulty) })
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "距离 ${String.format("%.2f", distanceKm)} km", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "爬升 ${elevationM} m", fontSize = 14.sp, color = Color.Gray)
                    Text(text = location, fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    tags.forEachIndexed { index, tag ->
                        Surface(color = Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small) {
                            Text(text = tag, fontSize = 12.sp, color = Color(0xFF007AFF), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        if (index < tags.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "已收藏 $favoriteCount", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

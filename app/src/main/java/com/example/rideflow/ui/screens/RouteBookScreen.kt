package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material3.ExperimentalMaterial3Api

data class RouteBook(
    val id: Int,
    val title: String,
    val distanceKm: Double,
    val elevationM: Int,
    val location: String,
    val tags: List<String>,
    val coverImage: Int,
    val difficulty: String
)

private val routeCategories = listOf("热门", "周边", "长距离", "爬坡", "休闲")

private val mockRoutes = listOf(
    RouteBook(1, "滨江环线", 32.5, 210, "上海市", listOf("骑行", "休闲"), R.drawable.ic_launcher_foreground, "简单"),
    RouteBook(2, "西郊爬坡挑战", 65.0, 980, "浙江省", listOf("骑行", "爬坡"), R.drawable.ic_launcher_foreground, "困难"),
    RouteBook(3, "城市夜骑", 18.3, 80, "上海市", listOf("骑行", "夜骑"), R.drawable.ic_launcher_foreground, "中等")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBookScreen(onBack: () -> Unit, onOpenMyRouteBook: () -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "路书") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(onClick = onOpenMyRouteBook) { Text(text = "我的路书") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(routeCategories.withIndex().toList()) { (index, title) ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = { Text(title) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            val filteredRoutes = remember(selectedCategory) {
                when (selectedCategory) {
                    0 -> mockRoutes
                    1 -> mockRoutes.filter { it.location.contains("上海") }
                    2 -> mockRoutes.filter { it.distanceKm >= 50 }
                    3 -> mockRoutes.filter { it.elevationM >= 500 }
                    else -> mockRoutes.filter { it.tags.contains("休闲") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filteredRoutes) { route ->
                    RouteCard(route = route)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(route: RouteBook) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                Image(
                    painter = painterResource(id = route.coverImage),
                    contentDescription = route.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(onClick = {}, label = { Text(text = route.difficulty) })
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                    ) {
                        Text(text = "导航")
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = route.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "距离 ${route.distanceKm} km", fontSize = 12.sp, color = Color.Gray)
                    Text(text = "爬升 ${route.elevationM} m", fontSize = 12.sp, color = Color.Gray)
                    Text(text = route.location, fontSize = 12.sp, color = Color.Gray)
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    route.tags.forEachIndexed { index, tag ->
                        Surface(color = Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = Color(0xFF007AFF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (index < route.tags.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = {}) { Text(text = "下载GPX") }
                    Text(text = "已收藏 0", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RouteBookScreenPreview() {
    RouteBookScreen(onBack = {})
}

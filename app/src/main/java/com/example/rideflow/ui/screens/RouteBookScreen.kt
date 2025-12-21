package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage

data class RouteBook(
    val id: Int,
    val title: String,
    val distanceKm: Double,
    val elevationM: Int,
    val location: String,
    val tags: List<String>,
    val coverImage: Int,
    val coverImageUrl: String? = null,
    val difficulty: String,
    val favoriteCount: Int = 0
)

private val routeCategories = listOf("热门", "周边", "长距离", "爬坡", "休闲")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBookScreen(onBack: () -> Unit, onOpenMyRouteBook: () -> Unit = {}, userId: String = "", navController: NavController? = null) {
    var selectedCategory by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    var routes by remember { mutableStateOf<List<RouteBook>>(emptyList()) }
    LaunchedEffect(Unit) {
        Thread {
            val list = mutableListOf<RouteBook>()
            val tagsMap = mutableMapOf<Int, MutableList<String>>()
            val favMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery(
                "SELECT route_id, title, distance_km, elevation_m, location, difficulty, cover_image_url FROM routes ORDER BY updated_at DESC LIMIT 200"
            ) { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val title = rs.getString(2) ?: ""
                    val dist = rs.getDouble(3)
                    val elev = rs.getInt(4)
                    val loc = rs.getString(5) ?: ""
                    val diff = rs.getString(6) ?: "简单"
                    val img = rs.getString(7)
                    list.add(RouteBook(id, title, dist, elev, loc, emptyList(), R.drawable.ic_launcher_foreground, img, diff))
                }
                Unit
            }
            DatabaseHelper.processQuery("SELECT route_id, tag_name FROM route_tags") { trs ->
                while (trs.next()) tagsMap.getOrPut(trs.getInt(1)) { mutableListOf() }.add(trs.getString(2) ?: "")
                Unit
            }
            DatabaseHelper.processQuery("SELECT route_id, COUNT(*) AS c FROM route_favorites GROUP BY route_id") { frs ->
                while (frs.next()) favMap[frs.getInt(1)] = frs.getInt(2)
                Unit
            }
            val merged = list.map { r -> r.copy(tags = tagsMap[r.id] ?: emptyList(), favoriteCount = favMap[r.id] ?: 0) }
            handler.post { routes = merged }
        }.start()
    }
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
            val filteredRoutes = remember(selectedCategory, routes) {
                when (selectedCategory) {
                    0 -> routes
                    1 -> routes.filter { it.location.contains("上海") }
                    2 -> routes.filter { it.distanceKm >= 50 }
                    3 -> routes.filter { it.elevationM >= 500 }
                    else -> routes.filter { it.tags.contains("休闲") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filteredRoutes) { route ->
                    RouteCard(route = route, onClick = {
                        navController?.navigate("${AppRoutes.ROUTE_DETAIL}/${route.id}")
                    })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCard(route: RouteBook, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                if (route.coverImageUrl != null) {
                    AsyncImage(
                        model = route.coverImageUrl,
                        contentDescription = route.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = route.coverImage),
                        contentDescription = route.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = route.difficulty) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFE8F5E9),
                            labelColor = Color(0xFF2E7D32)
                        )
                    )
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
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "已收藏 ${route.favoriteCount}", fontSize = 12.sp, color = Color.Gray)
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

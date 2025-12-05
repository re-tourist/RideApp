package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteBookScreen(onBack: () -> Unit, userId: String = "") {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("已下载", "我制作的", "我的收藏")
    val handler = Handler(Looper.getMainLooper())
    var favorites by remember { mutableStateOf<List<RouteBook>>(emptyList()) }
    var downloads by remember { mutableStateOf<List<RouteBook>>(emptyList()) }
    var created by remember { mutableStateOf<List<RouteBook>>(emptyList()) }
    LaunchedEffect(userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                val list = mutableListOf<RouteBook>()
                val tagsMap = mutableMapOf<Int, MutableList<String>>()
                DatabaseHelper.processQuery(
                    "SELECT r.route_id, r.title, r.distance_km, r.elevation_m, r.location, r.difficulty, r.cover_image_url FROM route_favorites rf JOIN routes r ON rf.route_id = r.route_id WHERE rf.user_id = ?",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val title = rs.getString(2)
                        val dist = rs.getDouble(3)
                        val elev = rs.getInt(4)
                        val loc = rs.getString(5) ?: ""
                        val diff = rs.getString(6) ?: "简单"
                        val img = rs.getString(7) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                        list.add(RouteBook(id, title, dist, elev, loc, emptyList(), R.drawable.ic_launcher_foreground, img, diff))
                    }
                    Unit
                }
                DatabaseHelper.processQuery("SELECT route_id, tag_name FROM route_tags") { trs ->
                    while (trs.next()) tagsMap.getOrPut(trs.getInt(1)) { mutableListOf() }.add(trs.getString(2) ?: "")
                    Unit
                }
                val merged = list.map { r -> r.copy(tags = tagsMap[r.id] ?: emptyList()) }
                handler.post { favorites = merged }
            }.start()

            Thread {
                val list = mutableListOf<RouteBook>()
                val tagsMap = mutableMapOf<Int, MutableList<String>>()
                DatabaseHelper.processQuery(
                    "SELECT r.route_id, r.title, r.distance_km, r.elevation_m, r.location, r.difficulty, r.cover_image_url FROM route_downloads rd JOIN routes r ON rd.route_id = r.route_id WHERE rd.user_id = ?",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val title = rs.getString(2)
                        val dist = rs.getDouble(3)
                        val elev = rs.getInt(4)
                        val loc = rs.getString(5) ?: ""
                        val diff = rs.getString(6) ?: "简单"
                        val img = rs.getString(7) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                        list.add(RouteBook(id, title, dist, elev, loc, emptyList(), R.drawable.ic_launcher_foreground, img, diff))
                    }
                    Unit
                }
                DatabaseHelper.processQuery("SELECT route_id, tag_name FROM route_tags") { trs ->
                    while (trs.next()) tagsMap.getOrPut(trs.getInt(1)) { mutableListOf() }.add(trs.getString(2) ?: "")
                    Unit
                }
                val merged = list.map { r -> r.copy(tags = tagsMap[r.id] ?: emptyList()) }
                handler.post { downloads = merged }
            }.start()

            Thread {
                val list = mutableListOf<RouteBook>()
                val tagsMap = mutableMapOf<Int, MutableList<String>>()
                DatabaseHelper.processQuery(
                    "SELECT r.route_id, r.title, r.distance_km, r.elevation_m, r.location, r.difficulty, r.cover_image_url FROM routes r WHERE r.creator_user_id = ?",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val title = rs.getString(2)
                        val dist = rs.getDouble(3)
                        val elev = rs.getInt(4)
                        val loc = rs.getString(5) ?: ""
                        val diff = rs.getString(6) ?: "简单"
                        val img = rs.getString(7) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                        list.add(RouteBook(id, title, dist, elev, loc, emptyList(), R.drawable.ic_launcher_foreground, img, diff))
                    }
                    Unit
                }
                DatabaseHelper.processQuery("SELECT route_id, tag_name FROM route_tags") { trs ->
                    while (trs.next()) tagsMap.getOrPut(trs.getInt(1)) { mutableListOf() }.add(trs.getString(2) ?: "")
                    Unit
                }
                val merged = list.map { r -> r.copy(tags = tagsMap[r.id] ?: emptyList()) }
                handler.post { created = merged }
            }.start()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "我的路书") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                    )
                }
            }
            val data = when (selectedTab) { 2 -> favorites; 0 -> downloads; else -> created }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(data) { route ->
                    RouteCard(route = route, onDownload = { _ -> })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyRouteBookScreenPreview() {
    MyRouteBookScreen(onBack = {})
}


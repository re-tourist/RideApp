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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteBookScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("已下载", "我制作的", "我的收藏")
    val all = listOf(
        RouteBook(10, "本地环线", 12.0, 120, "上海市", listOf("骑行", "休闲"), R.drawable.ic_launcher_foreground, "简单"),
        RouteBook(11, "山地挑战", 54.0, 820, "浙江省", listOf("骑行", "爬坡"), R.drawable.ic_launcher_foreground, "困难"),
        RouteBook(12, "夜骑精选", 20.0, 100, "上海市", listOf("夜骑"), R.drawable.ic_launcher_foreground, "中等")
    )
    val downloaded = all.take(2)
    val created = all.drop(1)
    val favorites = all

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
            val data = when (selectedTab) {
                0 -> downloaded
                1 -> created
                else -> favorites
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(data) { route ->
                    RouteCard(route = route)
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


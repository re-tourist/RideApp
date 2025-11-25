package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material3.ExperimentalMaterial3Api

data class Rider(
    val id: Int,
    val name: String,
    val city: String,
    val level: String,
    val avatarRes: Int
)

private val nearbyRiders = listOf(
    Rider(1, "shockman911", "上海", "金牌骑客", R.drawable.ic_launcher_foreground),
    Rider(2, "KCT江鹰", "杭州", "菜鸟骑迹", R.drawable.ic_launcher_foreground)
)

private val myRiders = listOf(
    Rider(3, "至若", "上海", "普通", R.drawable.ic_launcher_foreground)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑友") },
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
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(text = "附近") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(text = "我的") })
            }
            val data = if (selectedTab == 0) nearbyRiders else myRiders
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(data) { rider ->
                    RiderRow(rider)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun RiderRow(rider: Rider) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = rider.avatarRes),
                contentDescription = rider.name,
                modifier = Modifier.size(48.dp)
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = rider.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Row {
                    Text(text = rider.city, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = rider.level, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiderScreenPreview() {
    RiderScreen(onBack = {})
}


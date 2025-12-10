package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val tags: List<String>,
    val imageRes: Int,
    val isOpen: Boolean
)

private val categories = listOf("我的赛事", "骑行", "越野跑", "徒步")

private val mockEvents = listOf(
    Event(
        id = 1,
        title = "迎风织金季·GBA青年自行车线上赛",
        date = "时间：2025-11-08",
        location = "地点：任意地点",
        tags = listOf("骑行", "挑战"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Event(
        id = 2,
        title = "2025“环八娄”自行车爬坡联赛（娄城）",
        date = "时间：2025-11-29",
        location = "地点：浙江省娄城市",
        tags = listOf("骑行", "竞速"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Event(
        id = 3,
        title = "魔都乡见 崇明生态健康骑行线上赛",
        date = "时间：2025-12-14",
        location = "地点：上海市崇明区",
        tags = listOf("骑行", "休闲"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Event(
        id = 4,
        title = "越野跑周末挑战赛",
        date = "时间：2025-12-01",
        location = "地点：上海市郊区",
        tags = listOf("越野跑", "挑战"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Event(
        id = 5,
        title = "城市徒步健康行",
        date = "时间：2025-12-05",
        location = "地点：上海市中心",
        tags = listOf("徒步", "休闲"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(1) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "行者赛事") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
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
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(categories.withIndex().toList()) { (index, title) ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = { Text(title) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            val filteredEvents = remember(selectedCategory) {
                when (selectedCategory) {
                    0 -> mockEvents.filter { it.isOpen }
                    1 -> mockEvents.filter { it.tags.contains("骑行") }
                    2 -> mockEvents.filter { it.tags.contains("越野跑") }
                    else -> mockEvents.filter { it.tags.contains("徒步") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filteredEvents) { event ->
                    EventCard(event = event)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                Image(
                    painter = painterResource(id = event.imageRes),
                    contentDescription = event.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "行者报名") }
                    )
                    if (event.isOpen) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                        ) {
                            Text(text = "报名中")
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = event.date, fontSize = 12.sp, color = Color.Gray)
                    Text(text = event.location, fontSize = 12.sp, color = Color.Gray)
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    event.tags.forEachIndexed { index, tag ->
                        Surface(
                            color = Color(0xFFF0F0F0),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = Color(0xFF007AFF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (index < event.tags.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesScreenPreview() {
    ActivitiesScreen(onBack = {})
}

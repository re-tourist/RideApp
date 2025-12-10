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

data class Race(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val tags: List<String>,
    val imageRes: Int,
    val isOpen: Boolean
)

private val raceCategories = listOf("我的赛事", "骑行", "越野跑", "徒步")

private val mockRaces = listOf(
    Race(1, "城市半程骑行公开赛", "时间：2025-12-01", "地点：上海市中心", listOf("骑行", "竞速"), R.drawable.ic_launcher_foreground, true),
    Race(2, "越野跑挑战杯", "时间：2025-12-10", "地点：浙江省青山", listOf("越野跑", "挑战"), R.drawable.ic_launcher_foreground, true),
    Race(3, "冬季徒步联赛", "时间：2025-12-20", "地点：上海市郊区", listOf("徒步", "休闲"), R.drawable.ic_launcher_foreground, true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "赛事") },
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
                items(raceCategories.withIndex().toList()) { (index, title) ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = { Text(title) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            val filtered = remember(selectedCategory) {
                when (selectedCategory) {
                    0 -> mockRaces.filter { it.isOpen }
                    1 -> mockRaces.filter { it.tags.contains("骑行") }
                    2 -> mockRaces.filter { it.tags.contains("越野跑") }
                    else -> mockRaces.filter { it.tags.contains("徒步") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filtered) { race ->
                    RaceCard(race = race)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceCard(race: Race) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                Image(
                    painter = painterResource(id = race.imageRes),
                    contentDescription = race.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(onClick = {}, label = { Text(text = "赛事报名") })
                    if (race.isOpen) {
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
                Text(text = race.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = race.date, fontSize = 12.sp, color = Color.Gray)
                    Text(text = race.location, fontSize = 12.sp, color = Color.Gray)
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    race.tags.forEachIndexed { index, tag ->
                        Surface(color = Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small) {
                            Text(
                                text = tag,
                                fontSize = 12.sp,
                                color = Color(0xFF007AFF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (index < race.tags.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceScreenPreview() {
    RaceScreen(onBack = {})
}


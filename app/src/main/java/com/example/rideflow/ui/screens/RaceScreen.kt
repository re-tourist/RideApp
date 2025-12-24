package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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

data class Race(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val tags: List<String>,
    val imageRes: Int,
    val imageUrl: String? = null,
    val isOpen: Boolean,
    val isMine: Boolean
)

private val raceCategories = listOf("全部", "我的赛事", "娱乐赛", "竞速赛")

private fun loadRaces(handler: Handler, onLoaded: (List<Race>) -> Unit) {
    Thread {
        val list = mutableListOf<Race>()
        DatabaseHelper.processQuery("SELECT race_id, title, event_date, location, event_type, is_open, cover_image_url FROM races ORDER BY event_date DESC LIMIT 100") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val date = rs.getTimestamp(3)?.toString() ?: ""
                val loc = rs.getString(4) ?: ""
                val type = rs.getString(5) ?: "骑行"
                val open = rs.getBoolean(6)
                val coverUrl = rs.getString(7) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                val tags = mutableListOf<String>()
                DatabaseHelper.processQuery("SELECT tag_name FROM race_tags WHERE race_id = ?", listOf(id)) { trs ->
                    while (trs.next()) tags.add(trs.getString(1) ?: "")
                    Unit
                }
                var mine = false
                DatabaseHelper.processQuery(
                    "SELECT 1 FROM user_races WHERE user_id = ? AND race_id = ? AND relation IN ('registered','favorite') LIMIT 1",
                    listOf(1, id)
                ) { urs ->
                    mine = urs.next()
                    Unit
                }
                list.add(Race(id, title, "时间：" + (if (date.isNotEmpty()) date.substring(0, 10) else "待定"), "地点：" + loc, if (tags.isEmpty()) listOf(type) else tags, R.drawable.ic_launcher_foreground, coverUrl, open, mine))
            }
            handler.post { onLoaded(list) }
            Unit
        }
    }.start()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceScreen(onBack: () -> Unit, onCreateRace: () -> Unit = {}, navController: NavController) {
    var selectedCategory by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    var dbRaces by remember { mutableStateOf<List<Race>>(emptyList()) }
    LaunchedEffect(Unit) { loadRaces(handler) { list -> dbRaces = list } }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "赛事") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onCreateRace) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "创建赛事")
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
            val filtered = remember(selectedCategory, dbRaces) {
                when (selectedCategory) {
                    0 -> dbRaces
                    1 -> dbRaces.filter { it.isMine }
                    2 -> dbRaces.filter { it.tags.contains("娱乐赛") }
                    else -> dbRaces.filter { it.tags.contains("竞速赛") }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filtered) { race ->
                    RaceCard(race = race, onClick = { 
                        navController.navigate("${AppRoutes.RACE_DETAIL}/${race.id}") 
                    })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceCard(race: Race, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                if (race.imageUrl != null) {
                    AsyncImage(
                        model = race.imageUrl,
                        contentDescription = race.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = race.imageRes),
                        contentDescription = race.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
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
    RaceScreen(onBack = {}, navController = androidx.navigation.compose.rememberNavController())
}


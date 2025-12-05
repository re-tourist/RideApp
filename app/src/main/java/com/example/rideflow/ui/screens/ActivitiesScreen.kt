package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.navigation.AppRoutes
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage

data class Activity(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val tags: List<String>,
    val imageRes: Int,
    val imageUrl: String? = null,
    val isOpen: Boolean
)

private val activityCategories = listOf("我的活动", "骑行活动", "跑步活动", "徒步活动", "其他活动")

private val mockActivitiesList = listOf(
    Activity(
        id = 1,
        title = "周末骑行休闲游",
        date = "时间：2025-11-15",
        location = "地点：上海市浦东新区",
        tags = listOf("骑行", "休闲"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Activity(
        id = 2,
        title = "城市夜跑活动",
        date = "时间：2025-11-20",
        location = "地点：上海市黄浦区",
        tags = listOf("跑步", "夜跑"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Activity(
        id = 3,
        title = "秋季徒步旅行",
        date = "时间：2025-11-25",
        location = "地点：上海市松江区",
        tags = listOf("徒步", "户外"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Activity(
        id = 4,
        title = "骑行技术交流活动",
        date = "时间：2025-12-01",
        location = "地点：上海市宝山区",
        tags = listOf("骑行", "技术"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    ),
    Activity(
        id = 5,
        title = "亲子户外运动日",
        date = "时间：2025-12-05",
        location = "地点：上海市闵行区",
        tags = listOf("户外", "亲子"),
        imageRes = R.drawable.ic_launcher_foreground,
        isOpen = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(navController: NavController, onBack: () -> Unit, onCreateActivity: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    var dbActivities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    LaunchedEffect(Unit) {
        Thread {
            val list = mutableListOf<Activity>()
            DatabaseHelper.processQuery(
                "SELECT event_id, title, event_date, location, event_type, is_open, cover_image_url FROM events ORDER BY event_date DESC LIMIT 100"
            ) { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val title = rs.getString(2)
                    val date = rs.getTimestamp(3)?.toString() ?: ""
                    val loc = rs.getString(4) ?: ""
                    val type = rs.getString(5) ?: "骑行"
                    val open = rs.getBoolean(6)
                    val coverUrl = rs.getString(7) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                    val tags = mutableListOf<String>()
                    DatabaseHelper.processQuery(
                        "SELECT tag_name FROM event_tags WHERE event_id = ?",
                        listOf(id)
                    ) { trs ->
                        while (trs.next()) tags.add(trs.getString(1) ?: "")
                        Unit
                    }
                    val item = Activity(
                        id = id,
                        title = title,
                        date = "时间：" + (if (date.isNotEmpty()) date.substring(0, 16) else "待定"),
                        location = "地点：" + loc,
                        tags = if (tags.isEmpty()) listOf(type) else tags,
                        imageRes = R.drawable.ic_launcher_foreground,
                        imageUrl = coverUrl,
                        isOpen = open
                    )
                    list.add(item)
                }
                handler.post { dbActivities = list }
                Unit
            }
        }.start()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "活动") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onCreateActivity) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "创建活动")
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
                items(activityCategories.withIndex().toList()) { (index, title) ->
                    FilterChip(
                        selected = selectedCategory == index,
                        onClick = { selectedCategory = index },
                        label = { Text(title) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            val filteredActivities = remember(selectedCategory, dbActivities) {
                when (selectedCategory) {
                    0 -> dbActivities.filter { it.isOpen }
                    1 -> dbActivities.filter { it.tags.contains("骑行") }
                    2 -> dbActivities.filter { it.tags.contains("越野跑") || it.tags.contains("跑步") }
                    3 -> dbActivities.filter { it.tags.contains("徒步") }
                    else -> dbActivities
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(filteredActivities) { activity ->
                    ActivityItemCard(
                        activity = activity,
                        onClick = { navController.navigate("${AppRoutes.ACTIVITY_DETAIL}/${activity.id}") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityItemCard(activity: Activity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp)) {
                if (activity.imageUrl != null) {
                    AsyncImage(
                        model = activity.imageUrl,
                        contentDescription = activity.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = activity.imageRes),
                        contentDescription = activity.title,
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
                        label = { Text(text = "活动报名") }
                    )
                    if (activity.isOpen) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                        ) {
                            Text(text = "报名中")
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = activity.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = activity.date, fontSize = 12.sp, color = Color.Gray)
                    Text(text = activity.location, fontSize = 12.sp, color = Color.Gray)
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    activity.tags.forEachIndexed { index, tag ->
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
                        if (index < activity.tags.size - 1) Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview
@Composable
fun ActivitiesScreenPreview() {
    ActivitiesScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        onBack = {},
        onCreateActivity = {}
    )
}

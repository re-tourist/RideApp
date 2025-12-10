package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class ActivityItem(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val distance: String,
    val duration: String,
    val type: ActivityType,
    val status: ActivityStatus
)

enum class ActivityType { CYCLE, RUNNING, WALKING }
enum class ActivityStatus { COMPLETED, IN_PROGRESS, UPCOMING }

val mockActivities = listOf(
    ActivityItem(id = 1, title = "周末城市骑行", date = "2023-11-18 09:00", location = "市中心广场", distance = "25.6km", duration = "1小时30分钟", type = ActivityType.CYCLE, status = ActivityStatus.COMPLETED),
    ActivityItem(id = 2, title = "夜跑训练", date = "2023-11-20 19:00", location = "滨江跑道", distance = "8.5km", duration = "45分钟", type = ActivityType.RUNNING, status = ActivityStatus.IN_PROGRESS),
    ActivityItem(id = 3, title = "山地越野骑行", date = "2023-11-25 08:00", location = "郊外山地公园", distance = "42.3km", duration = "3小时", type = ActivityType.CYCLE, status = ActivityStatus.UPCOMING),
    ActivityItem(id = 4, title = "晨练散步", date = "2023-11-15 07:30", location = "社区公园", distance = "4.2km", duration = "30分钟", type = ActivityType.WALKING, status = ActivityStatus.COMPLETED)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyActivitiesScreen(navController: NavController, userId: String = "") {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "我的活动", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        val handler = Handler(Looper.getMainLooper())
        var activities by remember { mutableStateOf<List<ActivityItem>>(emptyList()) }
        var completedCount by remember { mutableStateOf(0) }
        var inProgressCount by remember { mutableStateOf(0) }
        var upcomingCount by remember { mutableStateOf(0) }
        var totalDistanceStr by remember { mutableStateOf("—") }
        LaunchedEffect(userId) {
            val uid = userId.toIntOrNull()
            if (uid != null) {
                Thread {
                    val list = mutableListOf<ActivityItem>()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    DatabaseHelper.processQuery(
                        "SELECT ue.user_event_id, e.title, e.event_date, e.location, e.event_type, ue.status, COALESCE(s.distance_km, 0), COALESCE(s.duration_seconds, 0) FROM user_events ue JOIN events e ON ue.event_id = e.event_id LEFT JOIN user_event_stats s ON s.user_id = ue.user_id AND s.event_id = ue.event_id WHERE ue.user_id = ? ORDER BY e.event_date DESC",
                        listOf(uid)
                    ) { rs ->
                        while (rs.next()) {
                            val id = rs.getInt(1)
                            val title = rs.getString(2) ?: "活动"
                            val ts = rs.getTimestamp(3)
                            val dateStr = if (ts != null) sdf.format(Date(ts.time)) else ""
                            val location = rs.getString(4) ?: ""
                            val eventType = rs.getString(5) ?: "其他"
                            val statusStr = rs.getString(6) ?: "upcoming"
                            val distKm = rs.getDouble(7)
                            val durSec = rs.getInt(8)
                            val type = when (eventType) {
                                "骑行" -> ActivityType.CYCLE
                                "越野跑" -> ActivityType.RUNNING
                                "徒步" -> ActivityType.WALKING
                                else -> ActivityType.WALKING
                            }
                            val status = when (statusStr) {
                                "completed" -> ActivityStatus.COMPLETED
                                "in_progress" -> ActivityStatus.IN_PROGRESS
                                "upcoming" -> ActivityStatus.UPCOMING
                                else -> ActivityStatus.UPCOMING
                            }
                            list.add(
                                ActivityItem(
                                    id = id,
                                    title = title,
                                    date = dateStr,
                                    location = location,
                                    distance = if (distKm > 0.0) String.format(Locale.getDefault(), "%.2f km", distKm) else "—",
                                    duration = formatDuration(durSec),
                                    type = type,
                                    status = status
                                )
                            )
                        }
                        handler.post {
                            activities = list
                            completedCount = list.count { it.status == ActivityStatus.COMPLETED }
                            inProgressCount = list.count { it.status == ActivityStatus.IN_PROGRESS }
                            upcomingCount = list.count { it.status == ActivityStatus.UPCOMING }
                            val total = list.filter { it.type == ActivityType.CYCLE && it.status == ActivityStatus.COMPLETED }
                                .mapNotNull { runCatching { it.distance.replace("km", "").trim().toDouble() }.getOrNull() }
                                .sum()
                            totalDistanceStr = if (total > 0.0) String.format(Locale.getDefault(), "%.2f km", total) else "—"
                        }
                        Unit
                    }
                }.start()
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding), contentPadding = PaddingValues(16.dp)) {
            item {
                ActivityStatsCard(completed = completedCount.toString(), inProgress = inProgressCount.toString(), upcoming = upcomingCount.toString(), totalDistance = totalDistanceStr)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(activities) { activity ->
                ActivityCard(activity)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "—"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}小时${m}分钟" else "${m}分钟"
}

@Composable
fun ActivityStatsCard(completed: String, inProgress: String, upcoming: String, totalDistance: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "活动统计", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ActivityStatItem("已完成", completed)
                ActivityStatItem("进行中", inProgress)
                ActivityStatItem("即将到来", upcoming)
                ActivityStatItem("总距离", totalDistance)
            }
        }
    }
}

@Composable
fun ActivityStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ActivityCard(activity: ActivityItem) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = activity.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                ActivityStatusBadge(activity.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ActivityInfoItem(activity.date)
                ActivityInfoItem(activity.location)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ActivityDataItem("距离", activity.distance)
                ActivityDataItem("时长", activity.duration)
                ActivityTypeItem(activity.type)
            }
        }
    }
}

@Composable
fun ActivityStatusBadge(status: ActivityStatus) {
    val (bgColor, textColor, text) = when (status) {
        ActivityStatus.COMPLETED -> Triple(Color(0xFF4CAF50), Color.White, "已完成")
        ActivityStatus.IN_PROGRESS -> Triple(Color(0xFF2196F3), Color.White, "进行中")
        ActivityStatus.UPCOMING -> Triple(Color(0xFFFFC107), Color.Black, "即将到来")
    }
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text = text, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun ActivityInfoItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = text, fontSize = 14.sp, color = Color(0xFF666666), modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun ActivityDataItem(label: String, value: String) {
    Column() {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun ActivityTypeItem(type: ActivityType) {
    val (text, color) = when (type) {
        ActivityType.CYCLE -> Pair("骑行", Color(0xFF4CAF50))
        ActivityType.RUNNING -> Pair("跑步", Color(0xFF2196F3))
        ActivityType.WALKING -> Pair("步行", Color(0xFFFFC107))
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = Color.Transparent, shape = CircleShape, modifier = Modifier.height(40.dp), contentColor = color) {
            Text(text = text, fontSize = 12.sp)
        }
        Text(text = text, fontSize = 12.sp, color = color, modifier = Modifier.padding(top = 4.dp))
    }
}

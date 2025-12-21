package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import kotlin.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rideflow.R
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.YearMonth

data class RideRecord(
    val id: Int,
    val date: String,
    val distance: Double,
    val duration: String,
    val avgSpeed: Double,
    val mapImage: Int
)

private val mockRideRecords = listOf(
    RideRecord(
        id = 1,
        date = "2025-10-16 21:59",
        distance = 0.58,
        duration = "00:07:55",
        avgSpeed = 4.4,
        mapImage = R.drawable.ic_launcher_foreground
    ),
    RideRecord(
        id = 2,
        date = "2025-10-16 21:45",
        distance = 0.55,
        duration = "00:07:38",
        avgSpeed = 4.4,
        mapImage = R.drawable.ic_launcher_foreground
    ),
    RideRecord(
        id = 3,
        date = "2025-10-16 21:33",
        distance = 0.40,
        duration = "00:05:44",
        avgSpeed = 4.3,
        mapImage = R.drawable.ic_launcher_foreground
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideRecordScreen(navController: NavController, userId: String) {
    var dbRecords by remember { mutableStateOf<List<RideRecord>>(emptyList()) }
    var monthlyDistanceKm by remember { mutableStateOf(0.0) }
    var monthlyDurationSec by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                val list = mutableListOf<RideRecord>()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                DatabaseHelper.processQuery(
                    "SELECT record_id, start_time, duration_sec, distance_km, avg_speed_kmh FROM user_ride_records WHERE user_id = ? ORDER BY start_time DESC",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val ts = rs.getTimestamp(2)
                        val durationSec = rs.getInt(3)
                        val distanceKm = rs.getDouble(4)
                        val avgSpeed = rs.getDouble(5)
                        val dateStr = if (ts != null) sdf.format(Date(ts.time)) else ""
                        list.add(
                            RideRecord(
                                id = id,
                                date = dateStr,
                                distance = distanceKm,
                                duration = formatDuration(durationSec),
                                avgSpeed = avgSpeed,
                                mapImage = R.drawable.ic_launcher_foreground
                            )
                        )
                    }
                    Unit
                }
                val ym = YearMonth.now()
                val start = ym.atDay(1).toString() + " 00:00:00"
                val end = ym.plusMonths(1).atDay(1).toString() + " 00:00:00"
                DatabaseHelper.processQuery(
                    "SELECT SUM(distance_km), SUM(duration_sec) FROM user_ride_records WHERE user_id = ? AND start_time >= ? AND start_time < ?",
                    listOf(uid, start, end)
                ) { rs ->
                    var d = 0.0
                    var t = 0
                    if (rs.next()) {
                        d = rs.getDouble(1)
                        t = rs.getInt(2)
                    }
                    handler.post {
                        dbRecords = list
                        monthlyDistanceKm = d
                        monthlyDurationSec = t
                    }
                    Unit
                }
            }.start()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑行记录") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 统计卡片区域
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "本月统计",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RideDataItem(label = "总里程", value = String.format(Locale.getDefault(), "%.2f km", monthlyDistanceKm))
                        RideDataItem(label = "总时长", value = String.format(Locale.getDefault(), "%.1f 小时", monthlyDurationSec / 3600.0))
                        RideDataItem(label = "平均速度", value = String.format(Locale.getDefault(), "%.2f km/h", if (monthlyDurationSec > 0) monthlyDistanceKm / (monthlyDurationSec / 3600.0) else 0.0))
                    }
                }
            }

            // 骑行历史记录
            Text(
                text = "骑行历史",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(dbRecords) {
                    RideHistoryItem(record = it)
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDataItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryItem(record: RideRecord) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = record.date, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(text = "${record.distance} km", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "${record.avgSpeed} km/h", fontSize = 14.sp, color = Color.Gray)
            }
            Text(text = "用时: ${record.duration}", fontSize = 14.sp, color = Color.Gray)
        }
        Image(
            painter = painterResource(id = record.mapImage),
            contentDescription = "Map preview",
            modifier = Modifier.size(80.dp, 60.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideRecordScreenPreview() {
    val navController = rememberNavController()
    RideRecordScreen(navController = navController, userId = "1")
}

private fun formatDuration(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

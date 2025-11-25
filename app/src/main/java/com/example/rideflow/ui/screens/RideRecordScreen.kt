package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.rideflow.R

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
fun RideRecordScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑行记录") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
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
                        RideDataItem(label = "总里程", value = "15.8 km")
                        RideDataItem(label = "总时长", value = "3.2 小时")
                        RideDataItem(label = "平均速度", value = "12.5 km/h")
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
                items(mockRideRecords) {
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
    RideRecordScreen()
}
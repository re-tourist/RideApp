package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width

sealed class RideStatus {
    object NotStarted : RideStatus()
    object InProgress : RideStatus()
    object Paused : RideStatus()
}

data class RideHistory(
    val date: String,
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val calories: String
)

data class RideReport(
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val calories: String,
    val elevation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(navController: androidx.navigation.NavController) {
    var rideStatus = remember { mutableStateOf<RideStatus>(RideStatus.NotStarted) }
    var showReportDialog = remember { mutableStateOf(false) }

    var rideDuration = remember { mutableStateOf("00:05:24") }
    var rideDistance = remember { mutableStateOf("0.42") }
    var currentSpeed = remember { mutableStateOf("4.3") }
    var avgSpeed = remember { mutableStateOf("4.4") }
    var calories = remember { mutableStateOf("25") }

    val historyList = listOf(
        RideHistory(
            date = "2025-10-18 15:41",
            duration = "00:08:08",
            distance = "3.25 km",
            avgSpeed = "24.1 km/h",
            calories = "156 kcal"
        ),
        RideHistory(
            date = "2025-10-17 22:33",
            duration = "00:15:03",
            distance = "5.67 km",
            avgSpeed = "22.6 km/h",
            calories = "210 kcal"
        )
    )

    val rideReport = RideReport(
        duration = rideDuration.value,
        distance = "${rideDistance.value} km",
        avgSpeed = "${avgSpeed.value} km/h",
        maxSpeed = "28.5 km/h",
        calories = "${calories.value} kcal",
        elevation = "45 m"
    )

    val onStartClick = { rideStatus.value = RideStatus.InProgress }
    val onPauseClick = { rideStatus.value = RideStatus.Paused }
    val onResumeClick = { rideStatus.value = RideStatus.InProgress }
    val onStopClick = {
        rideStatus.value = RideStatus.NotStarted
        showReportDialog.value = true
    }
    val onCloseReport = { showReportDialog.value = false }

    if (showReportDialog.value) {
        AlertDialog(
            onDismissRequest = onCloseReport,
            confirmButton = {
                Button(onClick = onCloseReport) { Text("关闭") }
            },
            title = { Text("骑行报告") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("用时: ${rideReport.duration}")
                    Text("距离: ${rideReport.distance}")
                    Text("均速: ${rideReport.avgSpeed}")
                    Text("最高速: ${rideReport.maxSpeed}")
                    Text("卡路里: ${rideReport.calories}")
                    Text("爬升: ${rideReport.elevation}")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            when (rideStatus.value) {
                is RideStatus.NotStarted -> {
                    TopAppBar(
                        title = { Text("骑迹", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    )
                }
                is RideStatus.InProgress -> {
                    TopAppBar(
                        title = { Text("正在运动", color = Color.White) },
                        navigationIcon = {
                            Button(
                                onClick = onStopClick,
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text("结束", color = Color.White, fontSize = 14.sp) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF))
                    )
                }
                is RideStatus.Paused -> {
                    TopAppBar(
                        title = { Text("已暂停", color = Color.White) },
                        navigationIcon = {
                            Button(
                                onClick = onStopClick,
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text("结束", color = Color.White, fontSize = 14.sp) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF))
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (rideStatus.value) {
                RideStatus.NotStarted -> NotStartedContent(historyList = historyList, onStartClick = onStartClick)
                RideStatus.InProgress -> InProgressContent(
                    duration = rideDuration.value,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    calories = calories.value,
                    onPauseClick = onPauseClick,
                    onStopClick = onStopClick
                )
                RideStatus.Paused -> PausedContent(
                    duration = rideDuration.value,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    calories = calories.value,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick
                )
            }
        }
    }
}

@Composable
private fun NotStartedContent(
    historyList: List<RideHistory>,
    onStartClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) { Text("地图显示区域", color = Color.Gray, fontSize = 16.sp) }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("经度: 120.394605", fontSize = 14.sp, color = Color.Gray)
                    Text("纬度: 30.311664", fontSize = 14.sp, color = Color.Gray)
                    Text("精度: 30.0m", fontSize = 14.sp, color = Color.Gray)
                    Text("浙江省杭州市钱塘区2号大街48-64号靠近工商大学云滨(地铁站)", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item {
            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) { Text("开始运动", fontSize = 18.sp, color = Color.White) }
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("最近的运动", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), elevation = ButtonDefaults.buttonElevation(0.dp)) { Text("查看全部", fontSize = 14.sp, color = Color(0xFF007AFF)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(historyList) { history ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(history.date, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("用时: ${history.duration}", fontSize = 14.sp)
                            Text("距离: ${history.distance}", fontSize = 14.sp)
                            Text("均速: ${history.avgSpeed}", fontSize = 14.sp)
                            Text("卡路里: ${history.calories}", fontSize = 14.sp)
                        }
                        Box(
                            modifier = Modifier.size(60.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("路线", fontSize = 12.sp, color = Color.Gray) }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun InProgressContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    calories: String,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 上半部分：骑行数据和地图
        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(duration, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF007AFF), modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("距离(km)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("卡路里", fontSize = 12.sp, color = Color.Gray) }
                    }
                }
            }
            // 地图区域使用具体高度
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text("骑行地图（含路线轨迹）", color = Color.Gray, fontSize = 16.sp) }
        }
        
        // 下半部分：按钮区域，固定在导航栏上方
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)) {
            Row(modifier = Modifier.padding(horizontal = 24.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onPauseClick, modifier = Modifier.size(80.dp, 50.dp), shape = RoundedCornerShape(25.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500))) { Text("暂停", color = Color.White, fontSize = 14.sp) }
                Spacer(modifier = Modifier.width(40.dp))
                Button(onClick = onStopClick, modifier = Modifier.size(80.dp, 50.dp), shape = RoundedCornerShape(25.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))) { Text("结束", color = Color.White, fontSize = 14.sp) }
            }
        }
    }
}

@Composable
private fun PausedContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    calories: String,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(duration, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9500), modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("已暂停", fontSize = 16.sp, color = Color(0xFFFF9500), modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("距离(km)", fontSize = 12.sp, color = Color.Gray) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray) }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("卡路里", fontSize = 12.sp, color = Color.Gray) }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).padding(16.dp),
            contentAlignment = Alignment.Center
        ) { Text("骑行地图（暂停）", color = Color.Gray, fontSize = 16.sp) }
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onResumeClick, modifier = Modifier.size(64.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))) { Text("继续", color = Color.White, fontSize = 14.sp) }
            Spacer(modifier = Modifier.width(40.dp))
            Button(onClick = onStopClick, modifier = Modifier.size(64.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))) { Text("结束", color = Color.White, fontSize = 14.sp) }
        }
    }
}

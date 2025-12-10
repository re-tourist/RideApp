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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    // 状态控制：是否显示历史记录页面
    var showHistoryScreen by remember { mutableStateOf(false) }

    // 如果显示历史页面，则渲染历史页面，否则渲染主骑行页面
    if (showHistoryScreen) {
        RideHistoryScreen(onBack = { showHistoryScreen = false })
    } else {
        RideMainContent(
            onShowHistory = { showHistoryScreen = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideMainContent(onShowHistory: () -> Unit) {
    var rideStatus = remember { mutableStateOf<RideStatus>(RideStatus.NotStarted) }
    var showReportDialog = remember { mutableStateOf(false) }

    var rideDuration = remember { mutableStateOf("00:05:24") }
    var rideDistance = remember { mutableStateOf("0.42") }
    var currentSpeed = remember { mutableStateOf("4.3") }
    var avgSpeed = remember { mutableStateOf("4.4") }
    var calories = remember { mutableStateOf("25") }

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
                        ),
                        // 添加右上角的历史记录按钮
                        actions = {
                            IconButton(onClick = onShowHistory) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange, // 使用时钟/日历图标
                                    contentDescription = "骑行记录",
                                    tint = Color.White
                                )
                            }
                        }
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
                RideStatus.NotStarted -> NotStartedContent(onStartClick = onStartClick)
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
    onStartClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // 稍微增加地图高度
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

            // 增加此处的留白高度，防止被底部导航栏遮挡
            Spacer(modifier = Modifier.height(120.dp))
        }
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
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text("骑行地图（含路线轨迹）", color = Color.Gray, fontSize = 16.sp) }
        }

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
    // 将布局改为LazyColumn以支持滚动
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
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
        }

        item {
            // 地图区域改为固定高度，不再使用weight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) { Text("骑行地图（暂停）", color = Color.Gray, fontSize = 16.sp) }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onResumeClick, modifier = Modifier.size(64.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))) { Text("继续", color = Color.White, fontSize = 14.sp) }
                Spacer(modifier = Modifier.width(40.dp))
                Button(onClick = onStopClick, modifier = Modifier.size(64.dp), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))) { Text("结束", color = Color.White, fontSize = 14.sp) }
            }
        }

        // 增加此处的留白高度，防止被底部导航栏遮挡，允许用户上滑
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

// ------------------------------
// 骑行记录页面
// ------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryScreen(onBack: () -> Unit) {
    // 编造的骑行历史数据
    val historyData = listOf(
        RideHistory("2025-10-18 15:41", "00:08:08", "3.25 km", "24.1 km/h", "156 kcal"),
        RideHistory("2025-10-17 22:33", "00:15:03", "5.67 km", "22.6 km/h", "210 kcal"),
        RideHistory("2025-10-16 07:12", "00:45:20", "15.2 km", "20.5 km/h", "520 kcal"),
        RideHistory("2025-10-14 18:30", "01:02:15", "22.8 km", "21.8 km/h", "890 kcal"),
        RideHistory("2025-10-12 16:20", "00:30:10", "10.5 km", "19.5 km/h", "350 kcal"),
        RideHistory("2025-10-10 09:00", "02:15:00", "50.2 km", "25.0 km/h", "1800 kcal"),
        RideHistory("2025-10-08 20:15", "00:22:45", "8.3 km", "18.5 km/h", "310 kcal"),
        RideHistory("2025-10-05 14:00", "00:12:30", "4.1 km", "15.2 km/h", "145 kcal")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("骑行记录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            items(historyData) { history ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = history.date,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                RideDataRow("用时", history.duration)
                                RideDataRow("距离", history.distance)
                            }
                            Column {
                                RideDataRow("均速", history.avgSpeed)
                                RideDataRow("消耗", history.calories)
                            }
                            // 模拟路线缩略图区域
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        Color.LightGray.copy(alpha = 0.3f),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("路线", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            // 底部留白
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun RideDataRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 4.dp)) {
        Text(text = "$label: ", fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}
package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// 骑行状态管理
sealed class RideStatus {
    object NotStarted : RideStatus() // 未开始（刚进入页面）
    object InProgress : RideStatus() // 进行中（开骑时）
    object Paused : RideStatus() // 暂停状态
}

// 历史记录数据类
data class RideHistory(
    val date: String,
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val calories: String
)

// 骑行报告数据类
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
fun RideScreen() {
    // 状态管理
    var rideStatus by remember { mutableStateOf<RideStatus>(RideStatus.NotStarted) }
    var showReportDialog by remember { mutableStateOf(false) }

    // 骑行数据
    var rideDuration by remember { mutableStateOf("00:05:24") }
    var rideDistance by remember { mutableStateOf("0.42") }
    var currentSpeed by remember { mutableStateOf("4.3") }
    var avgSpeed by remember { mutableStateOf("4.4") }
    var calories by remember { mutableStateOf("25") }

    // 示例历史记录数据（两条固定示例）
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

    // 骑行报告数据
    val rideReport = RideReport(
        duration = rideDuration,
        distance = "$rideDistance km",
        avgSpeed = "$avgSpeed km/h",
        maxSpeed = "28.5 km/h",
        calories = "$calories kcal",
        elevation = "45 m"
    )

    // 按钮点击事件
    val onStartClick = { rideStatus = RideStatus.InProgress }
    val onPauseClick = { rideStatus = RideStatus.Paused }
    val onResumeClick = { rideStatus = RideStatus.InProgress }
    val onStopClick = {
        rideStatus = RideStatus.NotStarted
        showReportDialog = true
    }
    val onCloseReport = { showReportDialog = false }
    val onShareToCommunity = {
        // 分享功能占位
        showReportDialog = false
    }

    // 骑行报告对话框
    if (showReportDialog) {
        RideReportDialog(
            report = rideReport,
            onClose = onCloseReport,
            onShare = onShareToCommunity
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            when (rideStatus) {
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
                            ) {
                                Text("结束", color = Color.White, fontSize = 14.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007AFF)
                        )
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
                            ) {
                                Text("结束", color = Color.White, fontSize = 14.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (rideStatus) {
                // 刚进入骑行时的页面（对应第二张图）
                RideStatus.NotStarted -> NotStartedContent(
                    historyList = historyList,
                    onStartClick = onStartClick
                )
                // 骑行中的页面
                RideStatus.InProgress -> InProgressContent(
                    duration = rideDuration,
                    distance = rideDistance,
                    currentSpeed = currentSpeed,
                    avgSpeed = avgSpeed,
                    calories = calories,
                    onPauseClick = onPauseClick,
                    onStopClick = onStopClick
                )
                // 暂停状态页面
                RideStatus.Paused -> PausedContent(
                    duration = rideDuration,
                    distance = rideDistance,
                    currentSpeed = currentSpeed,
                    avgSpeed = avgSpeed,
                    calories = calories,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick
                )
            }
        }
    }
}

/**
 * 未开始骑行页面（刚进入时）
 */
@Composable
private fun NotStartedContent(
    historyList: List<RideHistory>,
    onStartClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // 地图区域（文字替代图像）
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("地图显示区域", color = Color.Gray, fontSize = 16.sp)

                // 地图缩放按钮（右上角，文字替代图标）
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text("+", color = Color.Black, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text("-", color = Color.Black, fontSize = 18.sp)
                    }
                }
            }
        }

        // 位置信息卡片
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("经度: 120.394605", fontSize = 14.sp, color = Color.Gray)
                    Text("纬度: 30.311664", fontSize = 14.sp, color = Color.Gray)
                    Text("精度: 30.0m", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "浙江省杭州市钱塘区2号大街48-64号靠近工商大学云滨(地铁站)",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // 定位按钮（文字替代图标）
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.End),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text("定位", color = Color(0xFF007AFF), fontSize = 12.sp)
                    }
                }
            }
        }

        // 开始运动按钮（文字替代图标）
        item {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Text("开始运动", fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 最近运动标题
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("最近的运动", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("查看全部", fontSize = 14.sp, color = Color(0xFF007AFF))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 历史记录列表（两条示例）
        items(historyList) { history ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(history.date, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("用时: ${history.duration}", fontSize = 14.sp)
                            Text("距离: ${history.distance}", fontSize = 14.sp)
                            Text("均速: ${history.avgSpeed}", fontSize = 14.sp)
                            Text("卡路里: ${history.calories}", fontSize = 14.sp)
                        }
                        // 简易路线图占位（文字替代图像）
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("路线", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

/**
 * 骑行中页面
 */
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
    Column(modifier = Modifier.fillMaxSize()) {
        // 时间与数据卡片（顶部悬浮）
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 时间
                Text(
                    duration,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007AFF),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // 数据行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("距离(km)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("卡路里", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        // 地图区域（文字替代图像，带路线提示）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("骑行地图（含路线轨迹）", color = Color.Gray, fontSize = 16.sp)
        }

        // 底部操作按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 暂停按钮
            Button(
                onClick = onPauseClick,
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500))
            ) {
                Text("暂停", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(40.dp))

            // 结束按钮
            Button(
                onClick = onStopClick,
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
            ) {
                Text("结束", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

/**
 * 暂停状态页面
 */
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
        // 时间与数据卡片（顶部悬浮）
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 时间
                Text(
                    duration,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9500),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    "已暂停",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9500),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // 数据行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("距离(km)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("卡路里", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        // 地图区域（文字替代图像，带路线提示）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("骑行地图（已暂停）", color = Color.Gray, fontSize = 16.sp)
        }

        // 底部操作按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 继续按钮
            Button(
                onClick = onResumeClick,
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
            ) {
                Text("继续", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(40.dp))

            // 结束按钮
            Button(
                onClick = onStopClick,
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
            ) {
                Text("结束", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

/**
 * 骑行报告对话框
 */
@Composable
private fun RideReportDialog(
    report: RideReport,
    onClose: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "骑行报告",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007AFF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 数据可视化展示 - 简单版本
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("骑行数据统计图", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("距离: ${report.distance} | 时长: ${report.duration}", fontSize = 14.sp)
                        Text("均速: ${report.avgSpeed} | 卡路里: ${report.calories}", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 详细数据
                Column(modifier = Modifier.fillMaxWidth()) {
                    DataRow("骑行时长", report.duration)
                    DataRow("骑行距离", report.distance)
                    DataRow("平均速度", report.avgSpeed)
                    DataRow("最高速度", report.maxSpeed)
                    DataRow("消耗卡路里", report.calories)
                    DataRow("累计爬升", report.elevation)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("关闭", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                    ) {
                        Text("分享到社区", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun RideNotStartedPreview() {
    MaterialTheme {
        RideScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RideInProgressPreview() {
    MaterialTheme {
        InProgressContent(
            duration = "00:05:24",
            distance = "0.42",
            currentSpeed = "4.3",
            avgSpeed = "4.4",
            calories = "25",
            onPauseClick = {},
            onStopClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RidePausedPreview() {
    MaterialTheme {
        PausedContent(
            duration = "00:05:24",
            distance = "0.42",
            currentSpeed = "4.3",
            avgSpeed = "4.4",
            calories = "25",
            onResumeClick = {},
            onStopClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RideReportPreview() {
    MaterialTheme {
        RideReportDialog(
            report = RideReport(
                duration = "00:05:24",
                distance = "0.42 km",
                avgSpeed = "4.4 km/h",
                maxSpeed = "28.5 km/h",
                calories = "25 kcal",
                elevation = "45 m"
            ),
            onClose = {},
            onShare = {}
        )
    }
}
package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

// 活动数据类
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

// 活动类型枚举
enum class ActivityType {
    CYCLE,
    RUNNING,
    WALKING
}

// 活动状态枚举
enum class ActivityStatus {
    COMPLETED,
    IN_PROGRESS,
    UPCOMING
}

// 模拟活动数据
val mockActivities = listOf(
    ActivityItem(
        id = 1,
        title = "周末城市骑行",
        date = "2023-11-18 09:00",
        location = "市中心广场",
        distance = "25.6km",
        duration = "1小时30分钟",
        type = ActivityType.CYCLE,
        status = ActivityStatus.COMPLETED
    ),
    ActivityItem(
        id = 2,
        title = "夜跑训练",
        date = "2023-11-20 19:00",
        location = "滨江跑道",
        distance = "8.5km",
        duration = "45分钟",
        type = ActivityType.RUNNING,
        status = ActivityStatus.IN_PROGRESS
    ),
    ActivityItem(
        id = 3,
        title = "山地越野骑行",
        date = "2023-11-25 08:00",
        location = "郊外山地公园",
        distance = "42.3km",
        duration = "3小时",
        type = ActivityType.CYCLE,
        status = ActivityStatus.UPCOMING
    ),
    ActivityItem(
        id = 4,
        title = "晨练散步",
        date = "2023-11-15 07:30",
        location = "社区公园",
        distance = "4.2km",
        duration = "30分钟",
        type = ActivityType.WALKING,
        status = ActivityStatus.COMPLETED
    )
)

@Composable
fun MyActivitiesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            @Suppress("ExperimentalMaterial3Api")
            TopAppBar(
                title = { Text(text = "我的活动", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("返回")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 活动统计卡片
            item {
                ActivityStatsCard()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 活动列表
            items(mockActivities) { activity ->
                ActivityCard(activity)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// 活动统计卡片
@Composable
fun ActivityStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "活动统计",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityStatItem("已完成", "2")
                ActivityStatItem("进行中", "1")
                ActivityStatItem("即将到来", "1")
                ActivityStatItem("总距离", "70.4km")
            }
        }
    }
}

// 统计项组件
@Composable
fun ActivityStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// 活动卡片组件
@Composable
fun ActivityCard(activity: ActivityItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 活动标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                ActivityStatusBadge(activity.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 活动信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityInfoItem(activity.date)
                ActivityInfoItem(activity.location)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 活动数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityDataItem("距离", activity.distance)
                ActivityDataItem("时长", activity.duration)
                ActivityTypeItem(activity.type)
            }
        }
    }
}

// 活动状态徽章
@Composable
fun ActivityStatusBadge(status: ActivityStatus) {
    val (bgColor, textColor, text) = when (status) {
        ActivityStatus.COMPLETED -> Triple(Color(0xFF4CAF50), Color.White, "已完成")
        ActivityStatus.IN_PROGRESS -> Triple(Color(0xFF2196F3), Color.White, "进行中")
        ActivityStatus.UPCOMING -> Triple(Color(0xFFFFC107), Color.Black, "即将到来")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// 活动信息项
@Composable
fun ActivityInfoItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = text, fontSize = 14.sp, color = Color(0xFF666666), modifier = Modifier.padding(start = 8.dp))
    }
}

// 活动数据项
@Composable
fun ActivityDataItem(label: String, value: String) {
    Column() {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// 活动类型项
@Composable
fun ActivityTypeItem(type: ActivityType) {
    val (text, color) = when (type) {
        ActivityType.CYCLE -> Pair("骑行", Color(0xFF4CAF50))
        ActivityType.RUNNING -> Pair("跑步", Color(0xFF2196F3))
        ActivityType.WALKING -> Pair("步行", Color(0xFFFFC107))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 使用透明背景色
        Surface(
            color = Color.Transparent,
            shape = CircleShape,
            modifier = Modifier.size(40.dp),
            contentColor = color
        ) {
            Text(text = text, fontSize = 12.sp)
        }
        Text(text = text, fontSize = 12.sp, color = color, modifier = Modifier.padding(top = 4.dp))
    }
}

@Preview
@Composable
fun MyActivitiesScreenPreview() {
    // 在预览中使用占位符，实际应用中会通过导航传入
    val mockNavController = NavController(LocalContext.current)
    MyActivitiesScreen(navController = mockNavController)
}

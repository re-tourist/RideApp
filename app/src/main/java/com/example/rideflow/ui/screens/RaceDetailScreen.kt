package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.theme.RideFlowTheme
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailScreen(navController: NavController, raceId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    var showContactDialog by remember { mutableStateOf(false) }
    val handler = Handler(Looper.getMainLooper())
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    var eventTitle by remember { mutableStateOf("") }
    var organizerStr by remember { mutableStateOf("") }
    var eventDateStr by remember { mutableStateOf("") }
    var registrationTimeStr by remember { mutableStateOf("") }
    var locationStr by remember { mutableStateOf("") }
    var checkinLocationStr by remember { mutableStateOf("") }
    var eventTypeStr by remember { mutableStateOf("") }
    var isOpen by remember { mutableStateOf(true) }
    var coverImageUrl by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRegistered by remember { mutableStateOf(false) }
    LaunchedEffect(raceId) {
        if (raceId > 0) {
            Thread {
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                DatabaseHelper.processQuery(
                    "SELECT title, organizer, event_date, registration_time, location, checkin_location, event_type, is_open, cover_image_url, description FROM races WHERE race_id = ?",
                    listOf(raceId)
                ) { rs ->
                    if (rs.next()) {
                        val title = rs.getString(1) ?: ""
                        val organizer = rs.getString(2) ?: ""
                        val ts = rs.getTimestamp(3)
                        val dateStr = if (ts != null) sdf.format(Date(ts.time)) else ""
                        val registrationTime = rs.getString(4) ?: ""
                        val location = rs.getString(5) ?: ""
                        val checkinLocation = rs.getString(6) ?: ""
                        val eventType = rs.getString(7) ?: ""
                        val open = (rs.getInt(8) == 1)
                        val cover = rs.getString(9)
                        val desc = rs.getString(10) ?: ""
                        handler.post {
                            eventTitle = title
                            organizerStr = organizer
                            eventDateStr = dateStr
                            registrationTimeStr = registrationTime
                            locationStr = location
                            checkinLocationStr = checkinLocation
                            eventTypeStr = eventType
                            isOpen = open
                            coverImageUrl = cover
                            description = desc
                        }
                    }
                    Unit
                }
                DatabaseHelper.processQuery(
                    "SELECT tag_name FROM race_tags WHERE race_id = ?",
                    listOf(raceId)
                ) { trs ->
                    val list = mutableListOf<String>()
                    while (trs.next()) {
                        list.add(trs.getString(1) ?: "")
                    }
                    handler.post { tags = list }
                    Unit
                }
                val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull()
                if (userId != null && userId > 0) {
                    DatabaseHelper.processQuery(
                        "SELECT status FROM user_races WHERE user_id = ? AND race_id = ? AND relation = 'registered' LIMIT 1",
                        listOf(userId, raceId)
                    ) { urs ->
                        val has = urs.next()
                        handler.post {
                            isRegistered = has
                        }
                        Unit
                    }
                }
            }.start()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "赛事详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                // 赛事主图片
                Box(modifier = Modifier.height(200.dp)) {
                    if (!coverImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = coverImageUrl,
                            contentDescription = "赛事图片",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "赛事图片",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // 赛事名称
                    Text(
                            text = if (eventTitle.isNotBlank()) eventTitle else "赛事",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    // 报名状态
                    Text(
                        text = when {
                            !isOpen -> "已结束"
                            isRegistered -> "已报名"
                            else -> "报名中"
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color(0xFF00C853))
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // 赛事基本信息
                Column(modifier = Modifier.padding(16.dp)) {
                    // 赛事标签
                    Row(modifier = Modifier.padding(bottom = 16.dp)) {
                        tags.forEachIndexed { index, tag ->
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.padding(end = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(text = tag, fontSize = 12.sp)
                            }
                        }
                    }

                    // 赛事信息列表
                    RaceInfoItem(label = "主办方", value = if (organizerStr.isNotBlank()) organizerStr else "—")
                    RaceInfoItem(label = "赛事类型", value = if (eventTypeStr.isNotBlank()) eventTypeStr else "—")
                    RaceInfoItem(label = "比赛时间", value = eventDateStr)
                    RaceInfoItem(label = "报名时间", value = if (registrationTimeStr.isNotBlank()) registrationTimeStr else "—")
                    RaceInfoItem(
                        label = "签到地点",
                        value = when {
                            checkinLocationStr.isNotBlank() -> checkinLocationStr
                            locationStr.isNotBlank() -> locationStr
                            else -> "—"
                        }
                    )
                }

                // 标签页
                val tabItems = listOf("赛事详情", "赛事组别", "赛事规则")
                var selectedTab by remember { mutableStateOf(0) }

                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = item) }
                        )
                    }
                }

                // 标签页内容
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedTab) {
                        0 -> {
                            // 赛事详情
                            Text(
                                text = if (eventTitle.isNotBlank()) eventTitle else "赛事",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = if (description.isNotBlank()) description else "",
                                style = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // 详情图片
                            if (!coverImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = coverImageUrl,
                                    contentDescription = "赛事详情图片",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(bottom = 16.dp)
                                )
                            }
                        }
                        1 -> {
                            Text(
                                text = "赛事组别",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            RaceInfoItem(
                                label = "赛事类型",
                                value = if (eventTypeStr.isNotBlank()) eventTypeStr else "—"
                            )
                            if (tags.isNotEmpty()) {
                                Text(
                                    text = "标签：" + tags.joinToString("、"),
                                    style = TextStyle(fontSize = 14.sp),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "暂无标签信息",
                                    style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        2 -> {
                            Text(
                                text = "赛事规则",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            if (description.isNotBlank()) {
                                Text(
                                    text = description,
                                    style = TextStyle(fontSize = 14.sp),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            } else {
                                Text(
                                    text = "暂无赛事规则，具体以主办方说明为准",
                                    style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }
                    }
                }

                // 底部操作按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { showContactDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(text = "联系主办方")
                    }
                    Button(
                        onClick = { navController.navigate("${AppRoutes.RACE_REGISTRATION}/${raceId}") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(start = 8.dp)
                    ) {
                        Text(text = "立即报名")
                    }
                }

                // 联系主办方弹窗
                if (showContactDialog) {
                    AlertDialog(
                        onDismissRequest = { showContactDialog = false },
                        title = { Text(text = "联系主办方") },
                        text = {
                            Column {
                                RaceInfoItem(
                                    label = "主办方",
                                    value = if (organizerStr.isNotBlank()) organizerStr else "乐体体育"
                                )
                                RaceInfoItem(label = "联系电话", value = "400-987-6543")
                                RaceInfoItem(label = "邮箱", value = "race-support@rideflow.com")
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { showContactDialog = false },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(text = "关闭")
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun RaceInfoItem(label: String, value: String) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = "$label：",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
fun RaceGroupItem(title: String, distance: String, price: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = price,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                )
            }
            Text(
                text = distance,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = description,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// 暂时移除预览函数以避免NavController相关问题
// @Preview(showBackground = true)
// @Composable
// fun RaceDetailScreenPreview() {
//     RideFlowTheme {
//         RaceDetailScreen(navController = rememberNavController())
//     }
// }

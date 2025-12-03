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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailScreen(navController: NavController, raceId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    // 控制联系主办方弹窗的显示
    var showContactDialog by remember { mutableStateOf(false) }
    
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
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "赛事图片",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // 赛事名称
                    Text(
                            text = "BBR全国线上赛-复活吧，骑魂！",
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
                        text = "报名中",
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
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "线上", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "骑行", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "竞速", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "挑战", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(text = "长距离", fontSize = 12.sp)
                        }
                    }

                    // 赛事信息列表
                    RaceInfoItem(label = "主办方", value = "乐体体育")
                    RaceInfoItem(label = "比赛时间", value = "2025.11.22 00:00 ~ 2025.12.22 23:59")
                    RaceInfoItem(label = "报名时间", value = "2025.11.22 00:00 ~ 2025.12.22 16:00")
                    RaceInfoItem(label = "签到时间", value = "2025.11.22 00:00")
                    RaceInfoItem(label = "签到地点", value = "全国任意地点")
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
                                text = "BBR全国线上赛-复活吧，骑魂！",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "这是一场全国性的线上骑行比赛，欢迎所有骑行爱好者参与！无论你是新手还是资深骑手，都能在这里找到适合自己的挑战。让我们一起骑出精彩，展现骑魂！",
                                style = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // 详情图片
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "赛事详情图片",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(bottom = 16.dp)
                            )
                        }
                        1 -> {
                            // 赛事组别
                            RaceGroupItem(
                                title = "骑行组",
                                distance = "50km",
                                price = "¥59",
                                description = "适合有一定骑行基础的爱好者"
                            )
                            RaceGroupItem(
                                title = "竞速组",
                                distance = "100km",
                                price = "¥99",
                                description = "适合追求速度和挑战的资深骑手"
                            )
                            RaceGroupItem(
                                title = "长距离组",
                                distance = "200km",
                                price = "¥159",
                                description = "适合耐力型骑手"
                            )
                        }
                        2 -> {
                            // 赛事规则
                            Text(
                                text = "赛事规则",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "1. 参赛选手需在规定时间内完成相应距离的骑行任务\n" +
                                      "2. 需使用指定的骑行记录APP记录骑行数据\n" +
                                      "3. 必须是单次骑行完成，不允许分段累计\n" +
                                      "4. 严禁作弊行为，一经发现取消参赛资格\n" +
                                      "5. 最终解释权归主办方所有",
                                style = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
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
                                RaceInfoItem(label = "主办方", value = "乐体体育")
                                RaceInfoItem(label = "联系电话", value = "400-123-4567")
                                RaceInfoItem(label = "邮箱", value = "contact@letisport.com")
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
package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage

// 俱乐部徽章数据类
private data class Badge(val name: String, val color: Color)

// 成员数据类
private data class Member(val id: Int, val name: String, val avatarRes: Int, val role: String)

// 俱乐部详情数据类
private data class ClubDetail(
    val id: Int,
    val name: String,
    val city: String,
    val rank: Int,
    val totalMileage: Long,
    val annualHeat: Int,
    val monthlyHeat: Int,
    val captain: String,
    val members: List<Member>,
    val slogan: String,
    val logoRes: Int,
    val badges: List<Badge>,
    val logoUrl: String? = null,
    val themeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(
    clubId: Int,
    navController: NavController,
    showJoinButton: Boolean = true // [修改点]：新增参数控制底部按钮显示
) {
    val handler = Handler(Looper.getMainLooper())
    var clubDetail by remember { mutableStateOf<ClubDetail?>(null) }

    LaunchedEffect(clubId) {
        Thread {
            Thread.sleep(300)
            val mockData = getMockClubDetail(clubId)
            handler.post { clubDetail = mockData }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "俱乐部详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            // [修改点]：根据 showJoinButton 决定是否显示底部栏
            if (showJoinButton) {
                BottomAppBar(containerColor = Color.White) {
                    Button(
                        onClick = { /* 处理加入俱乐部逻辑 */ },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = clubDetail?.themeColor ?: Color.Gray)
                    ) {
                        Text(text = "已加入俱乐部", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (clubDetail == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val detail = clubDetail!!
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())) {
                // 1. 头部卡片
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Logo
                        if (detail.logoUrl != null) {
                            AsyncImage(model = detail.logoUrl, contentDescription = null, modifier = Modifier.size(80.dp).clip(CircleShape))
                        } else {
                            Image(painter = painterResource(id = detail.logoRes), contentDescription = null, modifier = Modifier.size(80.dp).clip(CircleShape))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = detail.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "ID: ${detail.id}  |  ${detail.city}", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            // 标签
                            Row {
                                Text("认证俱乐部", fontSize = 10.sp, color = Color.White, modifier = Modifier.background(detail.themeColor, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("活跃 Lv.${detail.rank}", fontSize = 10.sp, color = Color.White, modifier = Modifier.background(Color(0xFFFF9800), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }

                // 2. 荣誉墙 (Awards)
                if (detail.badges.isNotEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "获得荣誉", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(detail.badges) { badge ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier.size(56.dp).clip(CircleShape).background(badge.color.copy(alpha = 0.1f)).padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, tint = badge.color)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = badge.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))
                }

                // 3. 数据概览
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "俱乐部数据", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DataItem("总里程", "${detail.totalMileage}", "km")
                        DataItem("年热度", "${detail.annualHeat}", "℃")
                        DataItem("月热度", "${detail.monthlyHeat}", "℃")
                    }
                }
                Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))

                // 4. 成员展示
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "俱乐部成员", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "全部成员 >", fontSize = 14.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 队长
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = detail.captain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "队长", fontSize = 12.sp, color = detail.themeColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // 队员列表
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(detail.members) { member ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                                Image(painter = painterResource(id = member.avatarRes), contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = member.name, fontSize = 12.sp, maxLines = 1)
                                Text(text = member.role, fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
                Divider(thickness = 8.dp, color = Color(0xFFF5F5F5))

                // 5. 简介
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "俱乐部简介", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Text(text = detail.slogan, fontSize = 15.sp, lineHeight = 24.sp, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DataItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = unit, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
        }
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}

private fun getMockClubDetail(id: Int): ClubDetail {
    return when(id) {
        1 -> ClubDetail(
            id = 1, name = "飓风骑行俱乐部", city = "北京", rank = 1, totalMileage = 280540,
            annualHeat = 980000, monthlyHeat = 85000, captain = "飙速阿杰",
            members = listOf(
                Member(2, "风神", R.drawable.ic_launcher_foreground, "副队长"),
                Member(3, "闪电", R.drawable.ic_launcher_foreground, "冲刺手"),
                Member(4, "铁腿", R.drawable.ic_launcher_foreground, "爬坡手"),
                Member(5, "阿强", R.drawable.ic_launcher_foreground, "破风"),
                Member(6, "Lisa", R.drawable.ic_launcher_foreground, "后勤"),
                Member(7, "Tom", R.drawable.ic_launcher_foreground, "技师")
            ),
            slogan = "飓风过境，寸草不生！我们是追求极致速度的公路车队。每周二、四夜骑，周日长距离拉练。严格的纪律，科学的训练，只为在赛场上绽放光芒！",
            logoRes = R.drawable.ic_launcher_foreground,
            badges = listOf(
                Badge("年度总冠军", Color(0xFFFFD700)),
                Badge("速度之星", Color(0xFFE91E63)),
                Badge("百人团", Color(0xFF2196F3))
            ),
            themeColor = Color(0xFFD32F2F) // Red
        )
        2 -> ClubDetail(
            id = 2, name = "周末休闲骑", city = "上海", rank = 8, totalMileage = 54000,
            annualHeat = 120000, monthlyHeat = 9800, captain = "养生小王",
            members = listOf(
                Member(8, "咖啡", R.drawable.ic_launcher_foreground, "咖啡师"),
                Member(9, "甜甜", R.drawable.ic_launcher_foreground, "财务"),
                Member(10, "大光圈", R.drawable.ic_launcher_foreground, "摄影"),
                Member(11, "吃货1号", R.drawable.ic_launcher_foreground, "队员")
            ),
            slogan = "骑行是为了更好地吃喝！不拉爆，不竞速，主打一个快乐。欢迎喜欢拍照、探店、喝咖啡的朋友加入我们。",
            logoRes = R.drawable.ic_launcher_foreground,
            badges = listOf(
                Badge("美食猎人", Color(0xFFFF9800)),
                Badge("摄影达人", Color(0xFF9C27B0))
            ),
            themeColor = Color(0xFF1976D2) // Blue
        )
        3 -> ClubDetail(
            id = 3, name = "山地越野小队", city = "成都", rank = 3, totalMileage = 128000,
            annualHeat = 450000, monthlyHeat = 32000, captain = "泥巴佬",
            members = listOf(
                Member(12, "石头", R.drawable.ic_launcher_foreground, "开路先锋"),
                Member(13, "树根", R.drawable.ic_launcher_foreground, "技术指导"),
                Member(14, "飞包", R.drawable.ic_launcher_foreground, "大Pro")
            ),
            slogan = "柏油路是给汽车跑的，我们只属于山林！专注XC、Enduro和DH。无兄弟，不越野！",
            logoRes = R.drawable.ic_launcher_foreground,
            badges = listOf(
                Badge("山神", Color(0xFF4CAF50)),
                Badge("开拓者", Color(0xFF795548))
            ),
            themeColor = Color(0xFF388E3C) // Green
        )
        else -> ClubDetail(
            id = id, name = "未知俱乐部", city = "未知", rank = 99, totalMileage = 0,
            annualHeat = 0, monthlyHeat = 0, captain = "未知", members = emptyList(),
            slogan = "暂无信息", logoRes = R.drawable.ic_launcher_foreground, badges = emptyList(),
            themeColor = Color.Gray
        )
    }
}
package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
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
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage

// 俱乐部徽章数据类
private data class Badge(val name: String, val iconRes: Int, val iconUrl: String? = null)

// 成员数据类
private data class Member(val id: Int, val name: String, val avatarRes: Int, val avatarUrl: String? = null)

// 俱乐部详情数据类
private data class ClubDetail(
    val id: Int,
    val name: String,
    val city: String,
    val rank: Int,
    val totalMileage: Long,
    val annualHeat: Int,
    val monthlyHeat: Int,
    val averageMonthlyHeat: Int,
    val captain: String,
    val members: List<Member>,
    val slogan: String,
    val logoRes: Int,
    val badges: List<Badge>,
    val logoUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(clubId: Int, navController: NavController) {
    val handler = Handler(Looper.getMainLooper())
    var clubDetail by remember {
        mutableStateOf(
            ClubDetail(
                id = clubId,
                name = "",
                city = "",
                rank = 0,
                totalMileage = 0,
                annualHeat = 0,
                monthlyHeat = 0,
                averageMonthlyHeat = 0,
                captain = "",
                members = emptyList(),
                slogan = "",
                logoRes = R.drawable.ic_launcher_foreground,
                badges = emptyList(),
                logoUrl = null
            )
        )
    }
    LaunchedEffect(clubId) {
        Thread {
            var name = ""
            var city = ""
            var logo: String? = null
            DatabaseHelper.processQuery("SELECT name, city, logo_url FROM clubs WHERE club_id = ?", listOf(clubId)) { rs ->
                if (rs.next()) {
                    name = rs.getString(1) ?: ""
                    city = rs.getString(2) ?: ""
                    logo = rs.getString(3)
                }
                Unit
            }
            var rank = 0
            var totalMileage = 0L
            var annualHeat = 0
            var monthlyHeat = 0
            var averageMonthlyHeat = 0
            var slogan = ""
            var captainName = ""
            var captainId = 0
            DatabaseHelper.processQuery("SELECT rank, total_mileage, annual_heat, monthly_heat, average_monthly_heat, captain_user_id, slogan FROM club_details WHERE club_id = ?", listOf(clubId)) { rs ->
                if (rs.next()) {
                    rank = rs.getInt(1)
                    totalMileage = rs.getLong(2)
                    annualHeat = rs.getInt(3)
                    monthlyHeat = rs.getInt(4)
                    averageMonthlyHeat = rs.getInt(5)
                    captainId = rs.getInt(6)
                    slogan = rs.getString(7) ?: ""
                }
                Unit
            }
            if (captainId > 0) {
                DatabaseHelper.processQuery("SELECT nickname FROM users WHERE user_id = ?", listOf(captainId)) { rs ->
                    if (rs.next()) {
                        captainName = rs.getString(1) ?: ""
                    }
                    Unit
                }
            }
            val members = mutableListOf<Member>()
            DatabaseHelper.processQuery("SELECT m.user_id, u.nickname, u.avatar_url, m.role FROM club_members m JOIN users u ON m.user_id = u.user_id WHERE m.club_id = ?", listOf(clubId)) { rs ->
                while (rs.next()) {
                    val mid = rs.getInt(1)
                    val mname = rs.getString(2) ?: ""
                    val mavatar = rs.getString(3)
                    members.add(Member(mid, mname, R.drawable.ic_launcher_foreground, mavatar))
                }
                Unit
            }
            val badges = mutableListOf<Badge>()
            DatabaseHelper.processQuery("SELECT badge_name, icon_url FROM club_badges WHERE club_id = ?", listOf(clubId)) { rs ->
                while (rs.next()) {
                    val bname = rs.getString(1) ?: ""
                    val bicon = rs.getString(2)
                    badges.add(Badge(bname, R.drawable.ic_launcher_foreground, bicon))
                }
                Unit
            }
            handler.post {
                clubDetail = ClubDetail(
                    id = clubId,
                    name = name,
                    city = city,
                    rank = rank,
                    totalMileage = totalMileage,
                    annualHeat = annualHeat,
                    monthlyHeat = monthlyHeat,
                    averageMonthlyHeat = averageMonthlyHeat,
                    captain = captainName,
                    members = members,
                    slogan = slogan,
                    logoRes = R.drawable.ic_launcher_foreground,
                    badges = badges,
                    logoUrl = logo
                )
            }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "俱乐部详情") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "返回") } }
            )
        },
        content = {
            Column(modifier = Modifier.padding(it).fillMaxSize().verticalScroll(rememberScrollState())) {
                // 俱乐部头部信息
                Box(modifier = Modifier.background(Color(0xFFF5F5F5)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (clubDetail.logoUrl != null) {
                            AsyncImage(model = clubDetail.logoUrl, contentDescription = clubDetail.name, modifier = Modifier.size(80.dp).clip(CircleShape))
                        } else {
                            Image(painter = painterResource(id = clubDetail.logoRes), contentDescription = clubDetail.name, modifier = Modifier.size(80.dp).clip(CircleShape))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = clubDetail.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                // 空着奖杯图标位置
                            }
                            Row {
                                Text(text = "北京市", fontSize = 14.sp, color = Color.Gray)
                                Text(text = " ID:${clubDetail.id}", fontSize = 14.sp, color = Color.Gray)
                            }
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(text = "骑行", fontSize = 12.sp, color = Color.White, modifier = Modifier
                                    .background(Color(0xFF007AFF))
                                    .padding(horizontal = 8.dp, vertical = 2.dp))
                                Text(text = "休闲", fontSize = 12.sp, color = Color.White, modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(Color(0xFF007AFF))
                                    .padding(horizontal = 8.dp, vertical = 2.dp))
                                Text(text = "俱乐部", fontSize = 12.sp, color = Color.White, modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(Color(0xFF007AFF))
                                    .padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFFFF3E0))
                            .padding(8.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "奖杯", tint = Color(0xFFFFB300))
                                Text(text = "x${clubDetail.badges.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 数据统计
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Text(text = "北京市排名", fontSize = 14.sp)
                        Text(text = "${clubDetail.rank}", fontSize = 14.sp)
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(text = "总里程", fontSize = 14.sp)
                        Text(text = "${clubDetail.totalMileage}km", fontSize = 14.sp)
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(text = "年热度", fontSize = 14.sp)
                        Text(text = "${clubDetail.annualHeat}°C", fontSize = 14.sp, color = Color(0xFFFF5252))
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(text = "月热度", fontSize = 14.sp)
                        Text(text = "${clubDetail.monthlyHeat}°C", fontSize = 14.sp, color = Color(0xFFFF5252))
                    }
                    Divider()
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(text = "人均月贡献热度", fontSize = 14.sp)
                        Text(text = "${clubDetail.averageMonthlyHeat}°C", fontSize = 14.sp, color = Color(0xFFFF5252))
                    }
                }

                // 成员信息
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "队长", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (clubDetail.members.isNotEmpty()) {
                            val cap = clubDetail.members[0]
                            if (cap.avatarUrl != null) {
                                AsyncImage(model = cap.avatarUrl, contentDescription = clubDetail.captain, modifier = Modifier.size(40.dp).clip(CircleShape))
                            } else {
                                Image(painter = painterResource(id = cap.avatarRes), contentDescription = clubDetail.captain, modifier = Modifier.size(40.dp).clip(CircleShape))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = clubDetail.captain, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "队员", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    LazyRow {
                        items(clubDetail.members.size) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 16.dp)) {
                                val m = clubDetail.members[it]
                                if (m.avatarUrl != null) {
                                    AsyncImage(model = m.avatarUrl, contentDescription = m.name, modifier = Modifier.size(50.dp).clip(CircleShape))
                                } else {
                                    Image(painter = painterResource(id = m.avatarRes), contentDescription = m.name, modifier = Modifier.size(50.dp).clip(CircleShape))
                                }
                                Text(text = m.name, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }

                // 口号
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "口号", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Text(text = clubDetail.slogan, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { /* 处理加入俱乐部逻辑 */ },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text(text = "申请加入", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    )
}

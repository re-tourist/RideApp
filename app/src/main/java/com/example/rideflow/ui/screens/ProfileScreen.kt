package com.example.rideflow.ui.screens

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.theme.RideFlowTheme
import java.util.*
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage
import java.text.SimpleDateFormat

@Composable
fun ProfileScreen(navController: NavController, userId: String = "") {
    var showAchievementDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                DatabaseHelper.processQuery(
                    "SELECT nickname, avatar_url, user_id FROM users WHERE user_id = ?",
                    listOf(uid)
                ) { rs ->
                    if (rs.next()) {
                        val n = rs.getString(1) ?: ""
                        val a = rs.getString(2) ?: ""
                        handler.post {
                            nickname = n
                            avatarUrl = a ?: ""
                        }
                    }
                    Unit
                }
            }.start()
        }
    }
    
    // 跳转到个人资料详情页面
    fun navigateToEditProfile() {
        navController.navigate(AppRoutes.PROFILE_DETAIL)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(top = 24.dp)
    ) {
        item {
            // 个人信息区域
            Box(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFF3498DB))
                .padding(top = 20.dp, bottom = 30.dp)) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable(onClick = { navigateToEditProfile() }),
                                contentAlignment = Alignment.Center
                            ) {
                                if (avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "用户头像",
                                        modifier = Modifier.size(60.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "用户头像，点击编辑资料",
                                        tint = Color(0xFF3498DB),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = if (nickname.isNotBlank()) nickname else "",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.clickable(onClick = { navigateToEditProfile() })
                                )
                                Text(
                                    text = "用户ID: ${userId}",
                                    fontSize = 15.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.clickable(onClick = { navigateToEditProfile() })
                                )
                            }
                        }
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.End
                        ) {
                            IconButton(
                                onClick = { showNotificationsDialog = true },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "系统公告和信息",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Button(
                                onClick = { navController.navigate(AppRoutes.EDIT_PROFILE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.size(width = 100.dp, height = 36.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "编辑资料", fontSize = 18.sp, maxLines = 1)
                            }
                        }
                    }

                    // 成就勋章
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .clickable { navController.navigate(AppRoutes.ACHIEVEMENTS) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 18.dp, vertical = 6.dp)
                                .background(Color.White)
                                .clip(RoundedCornerShape(18.dp))
                        ) {
                            Text(
                                text = "铜",
                                color = Color(0xFFCD7F32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "成就勋章",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
        
        item {
            // 11月骑行统计
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "11月骑行",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "00:00",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "运动时间",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "0.0 KM",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3498DB)
                            )
                            Text(
                                text = "总距离",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "0",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "卡路里",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // 菜单项列表
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                // 骑行偏好
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(AppRoutes.RIDE_PREFERENCE) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "骑行偏好",
                            tint = Color(0xFF3498DB),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = "骑行偏好", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // 骑行记录
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(AppRoutes.RIDE_RECORD) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "骑行记录",
                            tint = Color(0xFF3498DB),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = "骑行记录", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(com.example.rideflow.navigation.AppRoutes.EXERCISE_CALENDAR) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "运动日历",
                                tint = Color(0xFF3498DB),
                                modifier = Modifier.size(24.dp)
                            )
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(text = "运动日历", fontSize = 16.sp)
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(com.example.rideflow.navigation.AppRoutes.MY_ACTIVITIES) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "我的活动",
                            tint = Color(0xFF3498DB),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = "我的活动", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // 系统设置
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showSettingsDialog = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "系统设置",
                            tint = Color(0xFF3498DB),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = "系统设置", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "更多",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    
    // 成就徽章对话框
    if (showAchievementDialog) {
        AlertDialog(
            onDismissRequest = { showAchievementDialog = false },
            title = { Text(text = "成就徽章") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(6) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index < 3) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (index < 3) Color(0xFF2196F3) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (index < 3) Icons.Default.Star else Icons.Default.Lock,
                                    contentDescription = "徽章图标",
                                    tint = if (index < 3) Color.Yellow else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = when (index) {
                                            0 -> "初次骑行"
                                            1 -> "连续骑行7天"
                                            2 -> "骑行达人"
                                            3 -> "马拉松骑手（未解锁）"
                                            4 -> "夜猫子骑手（未解锁）"
                                            5 -> "环保先锋（未解锁）"
                                            else -> "未知成就"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = when (index) {
                                            0 -> "完成第一次骑行"
                                            1 -> "连续7天完成骑行"
                                            2 -> "累计骑行100次"
                                            3 -> "单次骑行超过42公里"
                                            4 -> "晚上10点后骑行3次"
                                            5 -> "一个月内骑行20次"
                                            else -> "未知描述"
                                        },
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAchievementDialog = false }) {
                    Text(text = "关闭")
                }
            }
        )
    }
    
    // 日历对话框
    if (showCalendarDialog) {
        CalendarDialog(
            onDismiss = { showCalendarDialog = false }
        )
    }
    
    // 设置对话框
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(text = "设置") },
            text = {
                Column {
                    SettingItem(label = "修改密码", icon = Icons.Default.Person)
                    SettingItem(label = "通知设置", icon = Icons.Default.Notifications)
                    SettingItem(label = "隐私设置", icon = Icons.Default.Lock)
                    SettingItem(label = "关于我们", icon = Icons.Default.Info)
                }
            },
            confirmButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text(text = "关闭")
                }
            }
        )
    }
    
    // 通知对话框
    if (showNotificationsDialog) {
        NotificationsDialog(
            onDismiss = { showNotificationsDialog = false }
        )
    }
    

}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SettingItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Text(text = label, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
fun NotificationsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "系统公告和信息", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                // 通知列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .heightIn(max = 400.dp)
                ) {
                    // 系统公告
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            border = BorderStroke(1.dp, Color(0xFFBBDEFB))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "系统公告", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                                    Text(text = "2023-11-20", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = "2023冬季骑行挑战赛即将开始！参与活动赢取丰厚奖励！", modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                    
                    // 活动提醒
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "活动提醒", fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                                    Text(text = "2023-11-19", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = "您报名的'城市环线骑行'活动将于明日开始，请做好准备。", modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                    
                    // 成就解锁
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "成就解锁", fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                                    Text(text = "2023-11-15", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = "恭喜您解锁了'骑行达人'成就徽章！继续保持！", modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                    
                    // 系统更新
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "系统更新", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                                    Text(text = "2023-11-10", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = "RideFlow V2.1.0版本已发布，新增冬季主题和更多统计功能。", modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 编辑资料界面已移至ProfileScreen函数内部
}

@Composable
fun CalendarDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "2023年骑行日历", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                // 简化的日历显示
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF5F5F5))
                    .padding(8.dp)
                ) {
                    Text(
                        text = "日历视图：2023年已有128天完成骑行",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
    
    // 编辑资料界面已在文件前面定义，这里删除重复代码
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    RideFlowTheme {
        // 预览时使用默认的NavController
        val navController = androidx.navigation.compose.rememberNavController()
        ProfileScreen(navController = navController)
    }
}

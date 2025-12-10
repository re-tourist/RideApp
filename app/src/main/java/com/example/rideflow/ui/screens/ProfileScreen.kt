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
import java.util.Locale

data class ProfileAchievementItem(
    val id: Int,
    val name: String,
    val description: String,
    val iconUrl: String,
    val unlocked: Boolean,
    val progressPercent: Double,
    val unlockedAt: String?
)

@Composable
fun ProfileScreen(navController: NavController, userId: String = "") {
    var showAchievementDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    val authViewModel = org.koin.androidx.compose.koinViewModel<com.example.rideflow.auth.AuthViewModel>()
    var nickname by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var badgeItems by remember { mutableStateOf<List<ProfileAchievementItem>>(emptyList()) }
    var monthlyDurationSec by remember { mutableStateOf(0) }
    var monthlyDistanceKm by remember { mutableStateOf(0.0) }
    var monthlyCalories by remember { mutableStateOf(0) }
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
                val list = mutableListOf<ProfileAchievementItem>()
                DatabaseHelper.processQuery(
                    "SELECT b.badge_id, b.name, b.description, b.icon_url, COALESCE(p.is_unlocked, 0), COALESCE(p.progress_percent, 0), p.unlocked_at FROM achievement_badges b LEFT JOIN user_achievement_progress p ON p.badge_id = b.badge_id AND p.user_id = ? ORDER BY b.badge_id",
                    listOf(uid)
                ) { brs ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    while (brs.next()) {
                        val id = brs.getInt(1)
                        val name = brs.getString(2) ?: ""
                        val desc = brs.getString(3) ?: ""
                        val icon = brs.getString(4) ?: ""
                        val unlocked = brs.getInt(5) == 1
                        val progress = brs.getDouble(6)
                        val unlockedAtTs = brs.getTimestamp(7)
                        val unlockedAt = unlockedAtTs?.let { sdf.format(it) }
                        list.add(
                            ProfileAchievementItem(
                                id,
                                name,
                                desc,
                                icon,
                                unlocked,
                                progress,
                                unlockedAt
                            )
                        )
                    }
                    handler.post { badgeItems = list }
                    Unit
                }
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val startStr = startSdf.format(cal.time)
                cal.add(Calendar.MONTH, 1)
                val endStr = startSdf.format(cal.time)
                DatabaseHelper.processQuery(
                    "SELECT COALESCE(SUM(distance_km),0), COALESCE(SUM(duration_seconds),0), COALESCE(SUM(calories),0) FROM user_ride_records WHERE user_id = ? AND start_time >= ? AND start_time < ?",
                    listOf(uid, startStr, endStr)
                ) { srs ->
                    if (srs.next()) {
                        val dist = srs.getDouble(1)
                        val dur = srs.getInt(2)
                        val calo = srs.getInt(3)
                        handler.post {
                            monthlyDistanceKm = dist
                            monthlyDurationSec = dur
                            monthlyCalories = calo
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
                        text = "本月骑行",
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
                                text = String.format(Locale.getDefault(), "%02d:%02d", monthlyDurationSec / 3600, (monthlyDurationSec % 3600) / 60),
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
                                text = String.format(Locale.getDefault(), "%.1f KM", monthlyDistanceKm),
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
                                text = monthlyCalories.toString(),
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
        
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { navController.navigate(AppRoutes.RIDE_PREFERENCE) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "骑行偏好", tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                            Text(text = "骑行偏好", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { navController.navigate(AppRoutes.RIDE_RECORD) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "骑行记录", tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                            Text(text = "骑行记录", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { navController.navigate(AppRoutes.EXERCISE_CALENDAR) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "运动日历", tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                            Text(text = "运动日历", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { navController.navigate(AppRoutes.MY_ACTIVITIES) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "我的活动", tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                            Text(text = "我的活动", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showSettingsDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "系统设置", tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                            Text(text = "系统设置", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
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
                    items(badgeItems.size) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (badgeItems[index].unlocked) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (badgeItems[index].unlocked) Color(0xFF2196F3) else Color(0xFFE0E0E0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (badgeItems[index].iconUrl.isNotBlank()) {
                                    AsyncImage(model = badgeItems[index].iconUrl, contentDescription = "徽章图标", modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(imageVector = if (badgeItems[index].unlocked) Icons.Default.Star else Icons.Default.Lock, contentDescription = "徽章图标", tint = if (badgeItems[index].unlocked) Color.Yellow else Color.Gray, modifier = Modifier.size(32.dp))
                                }
                                Column(modifier = Modifier.padding(start = 12.dp)) {
                                    Text(
                                        text = badgeItems[index].name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = badgeItems[index].description,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    val progressText = String.format(Locale.getDefault(), "进度：%.0f%%", badgeItems[index].progressPercent)
                                    Text(text = progressText, fontSize = 12.sp, color = Color(0xFF2196F3), modifier = Modifier.padding(top = 4.dp))
                                    if (badgeItems[index].unlockedAt != null) {
                                        Text(text = "解锁于：${badgeItems[index].unlockedAt}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                                    }
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
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    SettingItem(
                        label = "退出登录",
                        icon = Icons.Default.ExitToApp,
                        onClick = {
                            authViewModel.logout()
                            navController.navigate(AppRoutes.LOGIN) {
                                popUpTo(AppRoutes.MAIN) { inclusive = true }
                            }
                            showSettingsDialog = false
                        },
                        iconTint = Color(0xFFD32F2F),
                        labelColor = Color(0xFFD32F2F)
                    )
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
    SettingItem(label = label, icon = icon, onClick = null, iconTint = Color.Unspecified, labelColor = Color.Unspecified)
}

@Composable
fun SettingItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    iconTint: Color = Color.Unspecified,
    labelColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp), tint = iconTint)
        Text(text = label, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp), color = labelColor)
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

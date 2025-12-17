package com.example.rideflow.ui.screens

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
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
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    val authViewModel = org.koin.androidx.compose.koinViewModel<com.example.rideflow.auth.AuthViewModel>()
    var nickname by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var badgeItems by remember { mutableStateOf<List<ProfileAchievementItem>>(emptyList()) }
    var monthlyDurationSec by remember { mutableStateOf(0) }
    var monthlyDistanceKm by remember { mutableStateOf(0.0) }
    var monthlyCalories by remember { mutableStateOf(0) }
    var followersCount by remember { mutableStateOf(0) }
    var followingCount by remember { mutableStateOf(0) }
    var clubsCount by remember { mutableStateOf(0) }
    var postsCount by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                DatabaseHelper.processQuery(
                    "SELECT nickname, avatar_url, bio, user_id FROM users WHERE user_id = ?",
                    listOf(uid)
                ) { rs ->
                    if (rs.next()) {
                        val n = rs.getString(1) ?: ""
                        val a = rs.getString(2) ?: ""
                        val b = rs.getString(3) ?: ""
                        handler.post {
                            nickname = n
                            avatarUrl = a ?: ""
                            bio = b
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
                DatabaseHelper.processQuery(
                    "SELECT COUNT(*) FROM user_follows WHERE followed_user_id = ?",
                    listOf(uid)
                ) { frs ->
                    if (frs.next()) {
                        val c = frs.getInt(1)
                        handler.post { followersCount = c }
                    }
                    Unit
                }
                DatabaseHelper.processQuery(
                    "SELECT COUNT(*) FROM user_follows WHERE follower_user_id = ?",
                    listOf(uid)
                ) { frs2 ->
                    if (frs2.next()) {
                        val c = frs2.getInt(1)
                        handler.post { followingCount = c }
                    }
                    Unit
                }
                DatabaseHelper.processQuery(
                    "SELECT COUNT(*) FROM club_members WHERE user_id = ?",
                    listOf(uid)
                ) { crs ->
                    if (crs.next()) {
                        val c = crs.getInt(1)
                        handler.post { clubsCount = c }
                    }
                    Unit
                }
                DatabaseHelper.processQuery(
                    "SELECT COUNT(*) FROM community_posts WHERE author_user_id = ?",
                    listOf(uid)
                ) { prs ->
                    if (prs.next()) {
                        val c = prs.getInt(1)
                        handler.post { postsCount = c }
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
            .padding(top = 8.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF5A5A5A), Color(0xFF3C3C3C))
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        try {
                            DiscoverNavigatorState.openRider = true
                        } catch (_: Exception) {
                        }
                        navController.navigate("${AppRoutes.MAIN}?tab=discover")
                    }) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "添加好友", tint = Color.White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = "消息列表", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "设置", tint = Color.White)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCCCCCC))
                                .clickable(onClick = { navigateToEditProfile() }),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "用户头像",
                                    modifier = Modifier.size(72.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "用户头像",
                                    tint = Color(0xFF9E9E9E),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                            Text(
                                text = if (nickname.isNotBlank()) nickname else "",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.clickable(onClick = { navigateToEditProfile() })
                            )
                            if (bio.isNotBlank()) {
                                Text(
                                    text = bio,
                                    fontSize = 13.sp,
                                    color = Color(0xFFDDDDDD),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (userId.isNotBlank()) {
                                Text(
                                    text = "ID: ${userId}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFBBBBBB)
                                )
                            }
                        }
                    }
                }
                HeaderSocialStatsRow(postsCount = postsCount, followingCount = followingCount, followersCount = followersCount)
                AchievementsPreviewRow(
                    badgeItems = badgeItems,
                    onClickDetails = { navController.navigate(AppRoutes.ACHIEVEMENTS) }
                )
            }
        }
        
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
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
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "运动时间",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", monthlyDistanceKm),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3498DB)
                                )
                                Text(
                                    text = " km",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Text(
                                text = "总距离",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = monthlyCalories.toString(),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = " kcal",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Text(
                                text = "卡路里",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { navController.navigate(AppRoutes.RIDE_PREFERENCE) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "骑行偏好", tint = Color(0xFF606060), modifier = Modifier.size(24.dp))
                        Text(text = "骑行偏好", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
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
                        Icon(imageVector = Icons.Default.Home, contentDescription = "骑行记录", tint = Color(0xFF606060), modifier = Modifier.size(24.dp))
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
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "运动日历", tint = Color(0xFF606060), modifier = Modifier.size(24.dp))
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
                        Icon(imageVector = Icons.Default.Home, contentDescription = "我的活动", tint = Color(0xFF606060), modifier = Modifier.size(24.dp))
                        Text(text = "我的活动", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray, modifier = Modifier.size(24.dp))
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
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
            onChangePassword = {
                showSettingsDialog = false
                navController.navigate(AppRoutes.CHANGE_PASSWORD)
            },
            onOpenInfo = { type ->
                showSettingsDialog = false
                navController.navigate("${AppRoutes.SETTINGS_INFO}/$type")
            },
            onRequestLogout = { showLogoutConfirmDialog = true }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text(text = "确认退出登录") },
            text = { Text(text = "退出后将需要重新登录才能继续使用账号相关功能。") },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.logout()
                        showLogoutConfirmDialog = false
                        showSettingsDialog = false
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.MAIN) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(text = "退出登录", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutConfirmDialog = false }) {
                    Text(text = "取消")
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
fun HeaderStatsItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 11.sp, color = Color(0xFFDDDDDD))
    }
}

@Composable
fun HeaderSocialStatsRow(postsCount: Int, followingCount: Int, followersCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderStatsItem(value = postsCount.toString(), label = "动态")
        HeaderStatsItem(value = followingCount.toString(), label = "关注")
        HeaderStatsItem(value = followersCount.toString(), label = "粉丝")
    }
}

@Composable
fun AchievementsPreviewRow(
    badgeItems: List<ProfileAchievementItem>,
    onClickDetails: () -> Unit
) {
    var selectedBadgeId by remember { mutableStateOf<Int?>(null) }
    val unlocked = badgeItems.filter { it.unlocked }
    val displayBadges = unlocked.take(3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val badge = displayBadges.getOrNull(index)
                val isSelected = badge != null && (selectedBadgeId == null && index == 0 || selectedBadgeId == badge.id)
                val size = if (isSelected) 52.dp else 44.dp
                val bgColor = when {
                    badge == null -> Color.White.copy(alpha = 0.18f)
                    isSelected -> Color(0xFFFFD54F)
                    else -> Color.White.copy(alpha = 0.35f)
                }

                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable(enabled = badge != null) { selectedBadgeId = badge?.id },
                    contentAlignment = Alignment.Center
                ) {
                    if (badge != null && badge.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = badge.iconUrl,
                            contentDescription = badge.name,
                            modifier = Modifier
                                .size(size * 0.7f)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "徽章",
                            tint = Color.White,
                            modifier = Modifier.size(size * 0.5f)
                        )
                    }
                }
            }
        }

        Text(
            text = "成就详情 >",
            fontSize = 13.sp,
            color = Color.White,
            modifier = Modifier
                .padding(start = 12.dp)
                .clickable(onClick = onClickDetails)
        )
    }
}

private enum class SettingsRowKind {
    Enabled,
    Info,
    Danger
}

@Composable
private fun SettingsDialog(
    onDismiss: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenInfo: (String) -> Unit,
    onRequestLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "系统设置", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                HorizontalDivider()
                SettingsRow(
                    title = "修改密码",
                    icon = Icons.Default.Lock,
                    kind = SettingsRowKind.Enabled,
                    onClick = onChangePassword
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsRow(
                    title = "通知设置",
                    icon = Icons.Default.Notifications,
                    kind = SettingsRowKind.Info,
                    onClick = { onOpenInfo("notifications") }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsRow(
                    title = "隐私设置",
                    icon = Icons.Default.Lock,
                    kind = SettingsRowKind.Info,
                    onClick = { onOpenInfo("privacy") }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsRow(
                    title = "关于我们",
                    icon = Icons.Default.Info,
                    kind = SettingsRowKind.Info,
                    onClick = { onOpenInfo("about") }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                SettingsRow(
                    title = "退出登录",
                    icon = Icons.Default.ExitToApp,
                    kind = SettingsRowKind.Danger,
                    onClick = onRequestLogout
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    kind: SettingsRowKind,
    onClick: () -> Unit
) {
    val dangerColor = Color(0xFFD32F2F)
    val iconTint = when (kind) {
        SettingsRowKind.Danger -> dangerColor
        else -> Color(0xFF606060)
    }
    val textColor = when (kind) {
        SettingsRowKind.Danger -> dangerColor
        else -> Color(0xFF111111)
    }
    val chevronTint = when (kind) {
        SettingsRowKind.Danger -> dangerColor
        else -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
            Text(text = title, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp), color = textColor)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = chevronTint,
            modifier = Modifier.size(22.dp)
        )
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
fun SocialStatChip(
    label: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7F7F7),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF606060), modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = count.toString(), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = label, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AchievementsRow(items: List<ProfileAchievementItem>) {
    val unlocked = items.filter { it.unlocked }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = "已解锁 ${unlocked.size} 枚勋章", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF606060))
        LazyRow(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(unlocked.take(12)) { badge ->
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEAEAEA))) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (badge.iconUrl.isNotBlank()) {
                            AsyncImage(model = badge.iconUrl, contentDescription = badge.name, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        } else {
                            Icon(imageVector = Icons.Default.Star, contentDescription = badge.name, tint = Color(0xFF3498DB), modifier = Modifier.size(24.dp))
                        }
                        Text(text = badge.name, fontSize = 13.sp, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
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

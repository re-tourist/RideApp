package com.example.rideflow.ui.screens.community

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star

// 用户详细信息数据类
data class UserDetailInfo(
    val userId: Int,
    val nickname: String,
    val bio: String,
    val ridingAge: String,
    val medals: List<String>
)

@Composable
fun CommunityScreen(navController: NavController, userId: String = "") {
    val handler = Handler(Looper.getMainLooper())
    val allPosts = remember { mutableStateListOf<Post>() }
    val followingUserIds = remember { mutableStateOf(setOf<Int>()) }

    // 用户弹窗状态
    var showUserDetailDialog by remember { mutableStateOf(false) }
    var selectedUserInfo by remember { mutableStateOf<UserDetailInfo?>(null) }
    var isUserLoading by remember { mutableStateOf(false) }

    // 数据加载逻辑
    LaunchedEffect(userId) {
        Thread {
            val posts = mutableListOf<Post>()
            val likesMap = mutableMapOf<Int, Int>()
            val commentsMap = mutableMapOf<Int, Int>()

            // 1. 注入"已加入俱乐部"的模拟动态数据
            val mockClubPosts = listOf(
                Post(901, 9001, Icons.Default.DateRange, "飓风骑行俱乐部", "10分钟前", "本周日将举行环湖拉练活动，请各位队员准时在北门集合！ #俱乐部活动", "[活动海报]", 32, 5),
                Post(902, 9002, Icons.Default.DateRange, "周末休闲骑", "2小时前", "上周的腐败骑行圆满结束，大家吃得开心吗？照片已上传相册。", "[聚餐合影]", 15, 8),
                Post(903, 9003, Icons.Default.DateRange, "山地越野小队", "1天前", "探索了一条新的林道，难度系数3星，欢迎老手来挑战。", "[林道照片]", 45, 12),
                Post(904, 9001, Icons.Default.DateRange, "飓风骑行俱乐部", "3天前", "恭喜车队在市级比赛中获得团体第二名！", "[奖杯照片]", 88, 20)
            )
            posts.addAll(mockClubPosts)

            // 2. 加载数据库中的帖子
            DatabaseHelper.processQuery(
                "SELECT post_id, author_user_id, content_text, image_url, created_at FROM community_posts ORDER BY created_at DESC LIMIT 200"
            ) { rs ->
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val uid = rs.getInt(2)
                    val content = rs.getString(3) ?: ""
                    val img = rs.getString(4) ?: "[图片]"
                    val created = rs.getTimestamp(5)
                    val timeStr = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    posts.add(Post(pid, uid, Icons.Default.Person, "", timeStr, content, img, 0, 0))
                }
                Unit
            }

            // 3. 补充用户信息/俱乐部名称
            DatabaseHelper.processQuery(
                "SELECT p.post_id, u.nickname, c.name FROM community_posts p " +
                        "LEFT JOIN users u ON p.author_user_id = u.user_id " +
                        "LEFT JOIN clubs c ON p.club_id = c.club_id"
            ) { rs ->
                val nameMap = mutableMapOf<Int, String>()
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val nick = rs.getString(2)
                    val clubName = rs.getString(3)
                    val displayName = if (!clubName.isNullOrEmpty()) clubName else (nick ?: "未知用户")
                    nameMap[pid] = displayName
                }
                posts.replaceAll { p -> if (p.userName.isEmpty()) p.copy(userName = nameMap[p.id] ?: "未知用户") else p }
                Unit
            }

            val merged = posts.map { p -> p.copy(likes = likesMap[p.id] ?: p.likes, comments = commentsMap[p.id] ?: p.comments) }
            handler.post { allPosts.clear(); allPosts.addAll(merged) }

            // 加载关注列表
            val uid = userId.toIntOrNull()
            if (uid != null) {
                val follows = mutableSetOf<Int>()
                DatabaseHelper.processQuery("SELECT followed_user_id FROM user_follows WHERE follower_user_id = ?", listOf(uid)) { frs ->
                    while (frs.next()) follows.add(frs.getInt(1))
                    Unit
                }
                handler.post { followingUserIds.value = follows }
            }
        }.start()
    }

    // 页面状态
    val categories = remember { listOf("关注动态", "热门动态", "社区交易", "俱乐部") }
    var selectedCategory by rememberSaveable { mutableStateOf(categories[1]) }
    var showPublishDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    // 关注回调
    val onFollowToggle: (Int, Boolean) -> Unit = { id, isFollowing ->
        if (isFollowing) {
            followingUserIds.value = followingUserIds.value + id
        } else {
            followingUserIds.value = followingUserIds.value - id
        }
    }

    // 处理头像点击，加载用户详细信息
    val onAvatarClick: (Int) -> Unit = { targetUserId ->
        isUserLoading = true
        showUserDetailDialog = true
        selectedUserInfo = null // Reset

        Thread {
            var nick = "未知用户"
            var bio = "这个用户很懒，什么都没写"
            var ridingAge = "1年"
            val medals = mutableListOf<String>()

            // 获取基本信息
            DatabaseHelper.processQuery(
                "SELECT nickname, bio, created_at FROM users WHERE user_id = ?",
                listOf(targetUserId)
            ) { rs ->
                if (rs.next()) {
                    nick = rs.getString(1) ?: nick
                    bio = rs.getString(2) ?: bio
                    val createdAt = rs.getTimestamp(3)
                    if (createdAt != null) {
                        val diff = Date().time - createdAt.time
                        val years = diff / (1000L * 60 * 60 * 24 * 365)
                        ridingAge = "${years + 1}年"
                    }
                }
                Unit
            }

            // 如果是模拟的俱乐部账号(ID > 9000)，给一些特殊文案
            if (targetUserId > 9000) {
                nick = allPosts.find { it.userId == targetUserId }?.userName ?: "俱乐部官方"
                bio = "官方账号，发布最新活动与资讯。"
                ridingAge = "5年"
            }

            // 获取勋章
            DatabaseHelper.processQuery(
                "SELECT b.name FROM achievement_badges b JOIN user_achievement_progress uap ON b.badge_id = uap.badge_id WHERE uap.user_id = ? AND uap.is_unlocked = 1",
                listOf(targetUserId)
            ) { rs ->
                while (rs.next()) {
                    medals.add(rs.getString(1))
                }
                Unit
            }
            // 模拟勋章数据
            if (medals.isEmpty()) {
                medals.add("骑行新星")
                if (targetUserId % 2 == 0) medals.add("百公里挑战")
            }

            handler.post {
                selectedUserInfo = UserDetailInfo(targetUserId, nick, bio, ridingAge, medals)
                isUserLoading = false
            }
        }.start()
    }

    Scaffold(
        topBar = { TopSearchBar(isSearching = isSearching, onSearchToggle = { isSearching = it }) },
        bottomBar = {
            CommunityBottomBar(
                onPublishClick = { showPublishDialog = true },
                onMessageClick = { showMessageDialog = true }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (!isSearching) {
                CategoryTabs(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            when (selectedCategory) {
                "关注动态" -> CommunityFollowingScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick)
                "热门动态" -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick)
                "社区交易" -> CommunityTradeScreen()
                "俱乐部" -> CommunityClubPortalScreen(navController = navController, allPosts = allPosts)
                else -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick)
            }
        }
    }

    if (showPublishDialog) {
        PublishPostDialog(onDismiss = { showPublishDialog = false })
    }
    if (showMessageDialog) {
        MessageInteractionDialog(onDismiss = { showMessageDialog = false })
    }

    // 用户详情弹窗
    if (showUserDetailDialog) {
        UserDetailInfoDialog(
            userInfo = selectedUserInfo,
            isLoading = isUserLoading,
            isFollowing = selectedUserInfo?.let { followingUserIds.value.contains(it.userId) } ?: false,
            onFollowClick = {
                selectedUserInfo?.let { user ->
                    onFollowToggle(user.userId, !followingUserIds.value.contains(user.userId))
                }
            },
            onDismiss = { showUserDetailDialog = false }
        )
    }
}

@Composable
fun UserDetailInfoDialog(
    userInfo: UserDetailInfo?,
    isLoading: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading || userInfo == null) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("加载用户信息...")
                } else {
                    // 头像
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 昵称
                    Text(
                        text = userInfo.nickname,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 骑行年龄
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "骑龄: ${userInfo.ridingAge}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFF1976D2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 个人语录
                    Text(
                        text = userInfo.bio,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 获得的奖章
                    Text(
                        text = "获得的奖章",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (userInfo.medals.isEmpty()) {
                            Text("暂无奖章", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            userInfo.medals.forEach { medal ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = medal,
                                        fontSize = 10.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 关注按钮
                    Button(
                        onClick = onFollowClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color.Gray else Color.Red
                        )
                    ) {
                        Text(if (isFollowing) "已关注" else "关注对方")
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityBottomBar(onPublishClick: () -> Unit, onMessageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Red)
                .clickable(onClick = onPublishClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "发布",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .clickable(onClick = onMessageClick)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "消息",
                tint = Color.Gray
            )
        }
    }
}
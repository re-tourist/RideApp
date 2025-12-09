package com.example.rideflow.ui.screens.community

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.*
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange

@Composable
fun CommunityScreen(navController: NavController, userId: String = "") {
    val handler = Handler(Looper.getMainLooper())
    val allPosts = remember { mutableStateListOf<Post>() }
    val followingUserIds = remember { mutableStateOf(setOf<Int>()) }

    // 数据加载逻辑
    LaunchedEffect(userId) {
        Thread {
            val posts = mutableListOf<Post>()
            val likesMap = mutableMapOf<Int, Int>()
            val commentsMap = mutableMapOf<Int, Int>()

            // 1. 注入"已加入俱乐部"的模拟动态数据
            // [修改点]：为不同发帖者分配不同的 userId (9001, 9002, 9003)，避免点击一个关注导致全部关注
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
                // 只更新那些名字为空的（即不是我们手动插入的模拟数据）
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
    // 使用 rememberSaveable 保存选中的标签页
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
                "关注动态" -> CommunityFollowingScreen(allPosts, followingUserIds.value, onFollowToggle)
                "热门动态" -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle)
                "社区交易" -> CommunityTradeScreen()
                "俱乐部" -> CommunityClubPortalScreen(navController = navController, allPosts = allPosts)
                else -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle)
            }
        }
    }

    if (showPublishDialog) {
        PublishPostDialog(onDismiss = { showPublishDialog = false })
    }
    if (showMessageDialog) {
        MessageInteractionDialog(onDismiss = { showMessageDialog = false })
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
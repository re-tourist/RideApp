package com.example.rideflow.ui.screens.community

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import android.util.Log
import com.example.rideflow.cache.AppCache
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import androidx.compose.ui.platform.LocalContext

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
    val allPosts = remember { mutableStateListOf<Post>() }
    val followingUserIds = remember { mutableStateOf(setOf<Int>()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val pageStart = remember { System.currentTimeMillis() }
    val context = LocalContext.current
    var currentPage by rememberSaveable { mutableStateOf(0) }
    val pageSize = 20
    var hasMore by rememberSaveable { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }

    // 统一跳转到个人主页；不再使用社区小卡片

    // 数据加载逻辑
    LaunchedEffect(userId) {
        val cached = loadCacheFirstPage(context)
        if (cached.isNotEmpty()) {
            allPosts.clear(); allPosts.addAll(cached)
        }
        val merged = withContext(Dispatchers.IO) {
            val posts = mutableListOf<Post>()
            val uid = userId.toIntOrNull()
            val likedSet = mutableSetOf<Int>()
            val dislikedSet = mutableSetOf<Int>()
            if (uid != null) {
                DatabaseHelper.processQuery("SELECT post_id FROM post_likes WHERE user_id = ?", listOf(uid)) { rs ->
                    while (rs.next()) likedSet.add(rs.getInt(1))
                    Unit
                }
                DatabaseHelper.processQuery("SELECT post_id FROM post_dislikes WHERE user_id = ?", listOf(uid)) { rs ->
                    while (rs.next()) dislikedSet.add(rs.getInt(1))
                    Unit
                }
            }
            // 统计数据容器在下方统一填充

            // 数据来源于数据库，不注入模拟动态

            // 2. 加载数据库中的帖子
            DatabaseHelper.processQuery(
                "SELECT post_id, author_user_id, club_id, COALESCE(author_type,'user'), content_text, image_url, created_at FROM community_posts ORDER BY created_at DESC LIMIT ? OFFSET ?",
                listOf(pageSize, currentPage * pageSize)
            ) { rs ->
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val auid = rs.getInt(2)
                    val clubId = rs.getInt(3).takeIf { !rs.wasNull() }
                    val aType = rs.getString(4) ?: "user"
                    val content = rs.getString(5) ?: ""
                    val img = rs.getString(6) ?: "[图片]"
                    val created = rs.getTimestamp(7)
                    val timeStr = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    val uid = if (aType == "club") (clubId ?: 0) else auid
                    posts.add(Post(pid, uid, Icons.Default.Person, "", timeStr, content, img, 0, 0, false, aType))
                }
                Unit
            }

            // 3. 补充用户信息/俱乐部名称
            DatabaseHelper.processQuery(
                "SELECT p.post_id, u.nickname, c.name, u.avatar_url, c.logo_url FROM community_posts p LEFT JOIN users u ON p.author_user_id = u.user_id LEFT JOIN clubs c ON p.club_id = c.club_id"
            ) { rs ->
                val nameMap = mutableMapOf<Int, String>()
                val avatarMap = mutableMapOf<Int, String?>()
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val nick = rs.getString(2)
                    val clubName = rs.getString(3)
                    val uAvatar = rs.getString(4)
                    val cLogo = rs.getString(5)
                    val displayName = if (!clubName.isNullOrEmpty()) clubName else (nick ?: "未知用户")
                    val avatar = if (!cLogo.isNullOrEmpty()) cLogo else uAvatar
                    nameMap[pid] = displayName
                    avatarMap[pid] = avatar
                }
                posts.replaceAll { p ->
                    val nm = if (p.userName.isEmpty()) nameMap[p.id] ?: "未知用户" else p.userName
                    val av = avatarMap[p.id]
                    p.copy(userName = nm, avatarUrl = av)
                }
                Unit
            }

            val likesMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) FROM post_likes GROUP BY post_id") { rs ->
                while (rs.next()) likesMap[rs.getInt(1)] = rs.getInt(2)
                Unit
            }
            val dislikesMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) FROM post_dislikes GROUP BY post_id") { rs ->
                while (rs.next()) dislikesMap[rs.getInt(1)] = rs.getInt(2)
                Unit
            }
            val commentsMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) FROM post_comments GROUP BY post_id") { rs ->
                while (rs.next()) commentsMap[rs.getInt(1)] = rs.getInt(2)
                Unit
            }
            posts.map { p ->
                val likeCount = likesMap[p.id] ?: p.likes
                val dislikeCount = dislikesMap[p.id] ?: p.dislikes
                val commentCount = commentsMap[p.id] ?: p.comments
                p.copy(
                    likes = likeCount,
                    dislikes = dislikeCount,
                    comments = commentCount,
                    initialIsLiked = likedSet.contains(p.id),
                    initialIsDisliked = dislikedSet.contains(p.id)
                )
            }
        }
        allPosts.clear(); allPosts.addAll(merged)
        isLoading = false
        hasMore = merged.size >= pageSize
        Log.d("Perf", "CommunityScreen RequestEnd: ${System.currentTimeMillis() - pageStart} ms")
        if (currentPage == 0 && merged.isNotEmpty()) {
            saveCacheFirstPage(context, merged)
        }
        val uid = userId.toIntOrNull()
        if (uid != null) {
            val follows = withContext(Dispatchers.IO) {
                val f = mutableSetOf<Int>()
                DatabaseHelper.processQuery("SELECT followed_user_id FROM user_follows WHERE follower_user_id = ?", listOf(uid)) { frs ->
                    while (frs.next()) f.add(frs.getInt(1))
                    Unit
                }
                f
            }
            followingUserIds.value = follows
        }
    }

    // 页面状态
    val categories = remember { listOf("关注动态", "热门动态", "社区交易", "俱乐部") }
    var selectedCategory by rememberSaveable { mutableStateOf(categories[1]) }
    var showPublishDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val onFollowToggle: (Int, Boolean) -> Unit = { id, isFollowing ->
        val uid = userId.toIntOrNull()
        if (isFollowing) {
            followingUserIds.value = followingUserIds.value + id
        } else {
            followingUserIds.value = followingUserIds.value - id
        }
        if (uid != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    if (isFollowing) {
                        DatabaseHelper.executeUpdate("INSERT IGNORE INTO user_follows (follower_user_id, followed_user_id) VALUES (?, ?)", listOf(uid, id))
                    } else {
                        DatabaseHelper.executeUpdate("DELETE FROM user_follows WHERE follower_user_id = ? AND followed_user_id = ?", listOf(uid, id))
                    }
                }
            }
        }
    }

    // 处理头像点击，加载用户详细信息
    val onAvatarClick: (Int, String) -> Unit = { targetId, aType ->
        if (aType == "club") {
            navController.navigate("${com.example.rideflow.navigation.AppRoutes.COMMUNITY_CLUB_DETAIL}/$targetId")
        } else {
            navController.navigate("${com.example.rideflow.navigation.AppRoutes.USER_PROFILE_DETAIL}/$targetId")
        }
    }

    val onPostClick: (Int) -> Unit = { postId ->
        navController.navigate("${com.example.rideflow.navigation.AppRoutes.POST_DETAIL}/$postId")
    }

    val onLikeToggle: (Int, Boolean) -> Unit = { postId, newIsLiked ->
        val uid = userId.toIntOrNull()
        uid?.let { uidVal ->
            scope.launch {
                withContext(Dispatchers.IO) {
                    if (newIsLiked) {
                        DatabaseHelper.executeUpdate("INSERT IGNORE INTO post_likes (post_id, user_id) VALUES (?, ?)", listOf(postId, uidVal))
                    } else {
                        DatabaseHelper.executeUpdate("DELETE FROM post_likes WHERE post_id = ? AND user_id = ?", listOf(postId, uidVal))
                    }
                    var newCount = 0
                    DatabaseHelper.processQuery("SELECT COUNT(*) FROM post_likes WHERE post_id = ?", listOf(postId)) { rs ->
                        if (rs.next()) newCount = rs.getInt(1)
                        Unit
                    }
                    val idx = allPosts.indexOfFirst { it.id == postId }
                    if (idx >= 0) {
                        val updated = allPosts[idx].copy(likes = newCount, initialIsLiked = newIsLiked)
                        allPosts[idx] = updated
                    }
                }
            }
        }
    }

    val onDislikeToggle: (Int, Boolean) -> Unit = { postId, newIsDisliked ->
        val uid = userId.toIntOrNull()
        uid?.let { uidVal ->
            scope.launch {
                withContext(Dispatchers.IO) {
                    if (newIsDisliked) {
                        DatabaseHelper.executeUpdate("INSERT IGNORE INTO post_dislikes (post_id, user_id) VALUES (?, ?)", listOf(postId, uidVal))
                    } else {
                        DatabaseHelper.executeUpdate("DELETE FROM post_dislikes WHERE post_id = ? AND user_id = ?", listOf(postId, uidVal))
                    }
                    var newCount = 0
                    DatabaseHelper.processQuery("SELECT COUNT(*) FROM post_dislikes WHERE post_id = ?", listOf(postId)) { rs ->
                        if (rs.next()) newCount = rs.getInt(1)
                        Unit
                    }
                    val idx = allPosts.indexOfFirst { it.id == postId }
                    if (idx >= 0) {
                        val updated = allPosts[idx].copy(dislikes = newCount, initialIsDisliked = newIsDisliked)
                        allPosts[idx] = updated
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { },
        bottomBar = {
            CommunityBottomBar(
                onPublishClick = { showPublishDialog = true },
                onMessageClick = { showMessageDialog = true }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CategoryTabs(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            if (isLoading && allPosts.isEmpty()) {
                PostListSkeleton()
            } else {
                Log.d("Perf", "CommunityScreen UIRender: ${System.currentTimeMillis() - pageStart} ms")
                when (selectedCategory) {
                "关注动态" -> CommunityFollowingScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick, onPostClick, onLikeToggle, onDislikeToggle, hasMore, isLoadingMore, onLoadMore = {
                    if (hasMore && !isLoadingMore) {
                        scope.launch {
                            isLoadingMore = true
                            currentPage += 1
                            val next = withContext(Dispatchers.IO) { loadPage(currentPage, pageSize, userId.toIntOrNull()) }
                            allPosts.addAll(next)
                            hasMore = next.size >= pageSize
                            isLoadingMore = false
                        }
                    }
                })
                "热门动态" -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick, onPostClick, onLikeToggle, onDislikeToggle, hasMore, isLoadingMore, onLoadMore = {
                    if (hasMore && !isLoadingMore) {
                        scope.launch {
                            isLoadingMore = true
                            currentPage += 1
                            val next = withContext(Dispatchers.IO) { loadPage(currentPage, pageSize, userId.toIntOrNull()) }
                            allPosts.addAll(next)
                            hasMore = next.size >= pageSize
                            isLoadingMore = false
                        }
                    }
                })
                "社区交易" -> CommunityTradeScreen(navController = navController)
                "俱乐部" -> CommunityClubPortalScreen(navController = navController, allPosts = allPosts, onLikeToggle = onLikeToggle, onDislikeToggle = onDislikeToggle)
                else -> CommunityHotScreen(allPosts, followingUserIds.value, onFollowToggle, onAvatarClick, onPostClick, onLikeToggle, onDislikeToggle, hasMore, isLoadingMore, onLoadMore = {
                    if (hasMore && !isLoadingMore) {
                        scope.launch {
                            isLoadingMore = true
                            currentPage += 1
                            val next = withContext(Dispatchers.IO) { loadPage(currentPage, pageSize, userId.toIntOrNull()) }
                            allPosts.addAll(next)
                            hasMore = next.size >= pageSize
                            isLoadingMore = false
                        }
                    }
                })
                }
            }
        }
    }

    if (showPublishDialog) {
        PublishPostDialog(onDismiss = { showPublishDialog = false })
    }
    if (showMessageDialog) {
        MessageInteractionDialog(onDismiss = { showMessageDialog = false })
    }

    // 统一使用个人主页，不再弹出用户小卡片
}

private suspend fun loadPage(page: Int, pageSize: Int, currentUserId: Int? = null): List<Post> {
    val posts = mutableListOf<Post>()
    DatabaseHelper.processQuery(
        "SELECT post_id, author_user_id, club_id, COALESCE(author_type,'user'), content_text, image_url, created_at FROM community_posts ORDER BY created_at DESC LIMIT ? OFFSET ?",
        listOf(pageSize, page * pageSize)
    ) { rs ->
        while (rs.next()) {
            val pid = rs.getInt(1)
            val auid = rs.getInt(2)
            val clubId = rs.getInt(3).takeIf { !rs.wasNull() }
            val aType = rs.getString(4) ?: "user"
            val content = rs.getString(5) ?: ""
            val img = rs.getString(6) ?: "[图片]"
            val created = rs.getTimestamp(7)
            val timeStr = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
            val uid = if (aType == "club") (clubId ?: 0) else auid
            posts.add(Post(pid, uid, Icons.Default.Person, "", timeStr, content, img, 0, 0, false, aType))
        }
        Unit
    }
    val nameMap = mutableMapOf<Int, String>()
    val avatarMap = mutableMapOf<Int, String?>()
    DatabaseHelper.processQuery(
        "SELECT p.post_id, u.nickname, c.name, u.avatar_url, c.logo_url FROM community_posts p LEFT JOIN users u ON p.author_user_id = u.user_id LEFT JOIN clubs c ON p.club_id = c.club_id LIMIT ? OFFSET ?",
        listOf(pageSize, page * pageSize)
    ) { rs ->
        while (rs.next()) {
            val pid = rs.getInt(1)
            val nick = rs.getString(2)
            val clubName = rs.getString(3)
            val uAvatar = rs.getString(4)
            val cLogo = rs.getString(5)
            val displayName = if (!clubName.isNullOrEmpty()) clubName else (nick ?: "未知用户")
            val avatar = if (!cLogo.isNullOrEmpty()) cLogo else uAvatar
            nameMap[pid] = displayName
            avatarMap[pid] = avatar
        }
        Unit
    }
    val likedSet = mutableSetOf<Int>()
    val dislikedSet = mutableSetOf<Int>()
    if (currentUserId != null) {
        DatabaseHelper.processQuery("SELECT post_id FROM post_likes WHERE user_id = ?", listOf(currentUserId)) { rs ->
            while (rs.next()) likedSet.add(rs.getInt(1))
            Unit
        }
        DatabaseHelper.processQuery("SELECT post_id FROM post_dislikes WHERE user_id = ?", listOf(currentUserId)) { rs ->
            while (rs.next()) dislikedSet.add(rs.getInt(1))
            Unit
        }
    }
    return posts.map { p ->
        val nm = if (p.userName.isEmpty()) nameMap[p.id] ?: "未知用户" else p.userName
        val av = avatarMap[p.id]
        p.copy(
            userName = nm,
            avatarUrl = av,
            initialIsLiked = likedSet.contains(p.id),
            initialIsDisliked = dislikedSet.contains(p.id)
        )
    }
}

private fun saveCacheFirstPage(context: android.content.Context, posts: List<Post>) {
    val arr = JSONArray()
    posts.take(40).forEach { p ->
        val o = JSONObject()
        o.put("id", p.id)
        o.put("uid", p.userId)
        o.put("name", p.userName)
        o.put("time", p.timeAgo)
        o.put("content", p.content)
        o.put("img", p.imagePlaceholder)
        o.put("likes", p.likes)
        o.put("comments", p.comments)
        o.put("aType", p.authorType)
        o.put("avatarUrl", p.avatarUrl ?: "")
        arr.put(o)
    }
    AppCache.put(context, "community_first_page", arr.toString(), TimeUnit.HOURS.toMillis(2))
}

private fun loadCacheFirstPage(context: android.content.Context): List<Post> {
    val s = AppCache.get(context, "community_first_page") ?: return emptyList()
    return try {
        val arr = JSONArray(s)
        (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            try {
                val id = o.optInt("id")
                val uid = o.optInt("uid")
                val name = o.optString("name")
                val time = o.optString("time")
                val content = o.optString("content")
                val img = o.optString("img")
                val likes = o.optInt("likes")
                val comments = o.optInt("comments")
                val aType = o.optString("aType", "user")
                val avatar = o.optString("avatarUrl").ifEmpty { null }
                Post(id, uid, Icons.Default.Person, name, time, content, img, likes, comments, false, aType, avatar)
            } catch (_: Exception) {
                null
            }
        }
    } catch (_: Exception) {
        emptyList()
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
                            containerColor = if (isFollowing) Color.Gray else MaterialTheme.colorScheme.primary
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
                .background(MaterialTheme.colorScheme.primary)
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

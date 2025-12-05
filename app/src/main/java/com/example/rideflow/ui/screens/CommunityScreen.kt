package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Locale

// ------------------------------------
// 1. 数据模型
// ------------------------------------

data class Post(
    val id: Int,
    val userId: Int,
    val userAvatar: ImageVector = Icons.Default.Person,
    val userName: String,
    val timeAgo: String,
    val content: String,
    val imagePlaceholder: String,
    val likes: Int,
    val comments: Int,
    val initialIsLiked: Boolean = false
)

data class TradeItem(
    val id: Int,
    val isOfficial: Boolean,
    val title: String,
    val description: String,
    val price: String,
    val imagePlaceholder: String,
    val externalUrl: String,
    val sellerName: String? = null,
    val isPublished: Boolean = true
)

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

data class Comment(
    val id: Int,
    val userName: String,
    val content: String,
    val time: String
)

data class Friend(
    val id: Int,
    val name: String
)

// 俱乐部相关模型
data class ClubMember(
    val id: Int,
    val name: String,
    var role: String,
    val avatarPlaceholder: String = "[头像]",
    val info: String = "这是用户详细信息示例。"
)

data class Applicant(
    val id: Int,
    val name: String,
    val reason: String = "想加入俱乐部，一起骑行交流"
)

data class RankingItem(
    val rank: Int,
    val name: String,
    val distanceKm: Double
)

// ------------------------------------
// 2. 社区主屏幕 Composable
// ------------------------------------

@Composable
fun CommunityScreen(userId: String = "") {
    val handler = Handler(Looper.getMainLooper())
    val allPosts = remember { mutableStateListOf<Post>() }
    val followingUserIds = remember { mutableStateOf(setOf<Int>()) }
    LaunchedEffect(userId) {
        Thread {
            val posts = mutableListOf<Post>()
            val likesMap = mutableMapOf<Int, Int>()
            val commentsMap = mutableMapOf<Int, Int>()
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
            DatabaseHelper.processQuery("SELECT p.post_id, u.nickname FROM community_posts p JOIN users u ON p.author_user_id = u.user_id") { urs ->
                val nameMap = mutableMapOf<Int, String>()
                while (urs.next()) nameMap[urs.getInt(1)] = urs.getString(2) ?: ""
                posts.replaceAll { p -> p.copy(userName = nameMap[p.id] ?: "") }
                Unit
            }
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) c FROM post_likes GROUP BY post_id") { lrs ->
                while (lrs.next()) likesMap[lrs.getInt(1)] = lrs.getInt(2)
                Unit
            }
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) c FROM post_comments GROUP BY post_id") { crs ->
                while (crs.next()) commentsMap[crs.getInt(1)] = crs.getInt(2)
                Unit
            }
            val merged = posts.map { p -> p.copy(likes = likesMap[p.id] ?: 0, comments = commentsMap[p.id] ?: 0) }
            handler.post { allPosts.clear(); allPosts.addAll(merged) }
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

    // 新的分类标签
    val categories = remember { listOf("关注动态", "热门动态", "社区交易", "俱乐部") }
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    var showPublishDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    // 关注/取消关注的逻辑
    val onFollowToggle: (Int, Boolean) -> Unit = { userId, isFollowing ->
        if (isFollowing) {
            followingUserIds.value = followingUserIds.value + userId
        } else {
            followingUserIds.value = followingUserIds.value - userId
        }
    }

    Scaffold(
        topBar = { TopSearchBar(isSearching = isSearching, onSearchToggle = { isSearching = it }) },
        bottomBar = {
            BottomNavigationBar(
                onPublishClick = { showPublishDialog = true },
                onMessageClick = { showMessageDialog = true }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 当不在搜索状态时，显示分类标签
            if (!isSearching) {
                CategoryTabs(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // 根据选中的标签显示不同的子页面
            when (selectedCategory) {
                "关注动态" -> FollowingDynamicScreen(allPosts, followingUserIds.value, onFollowToggle)
                "热门动态" -> HotDynamicScreen(allPosts, followingUserIds.value, onFollowToggle)
                "社区交易" -> TradeScreen()
                "俱乐部" -> ClubScreen()
                else -> FollowingDynamicScreen(allPosts, followingUserIds.value, onFollowToggle) // 默认
            }
        }
    }

    // 弹窗逻辑
    if (showPublishDialog) {
        PublishPostDialog(onDismiss = { showPublishDialog = false })
    }
    if (showMessageDialog) {
        MessageInteractionDialog(onDismiss = { showMessageDialog = false })
    }
}

// ------------------------------------
// 3. 顶部搜索栏 Composable
// ------------------------------------

@Composable
fun TopSearchBar(isSearching: Boolean, onSearchToggle: (Boolean) -> Unit) {
    if (isSearching) {
        // 搜索输入框状态
        var searchText by remember { mutableStateOf("") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("输入关键词搜索...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onSearchToggle(false) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                modifier = Modifier.height(56.dp)
            ) {
                Text("取消", color = Color.White)
            }
        }
    } else {
        // 热门搜索区域 (默认状态)
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("热搜", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text("第十七届山地越野赛即将开始报名", fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { onSearchToggle(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("搜索", color = Color.Black)
                }
            }
        }
    }
    HorizontalDivider(color = Color(0xFFF0F0F0))
}

// ------------------------------------
// 4. 分类标签 Composable
// ------------------------------------

@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val displayedCategories = categories.take(4)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        displayedCategories.forEach { category ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category,
                    color = if (category == selectedCategory) Color.Red else Color.Black,
                    fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onCategorySelected(category) }
                )
                if (category == selectedCategory) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Red)
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
    HorizontalDivider(color = Color(0xFFF0F0F0))
}

// ------------------------------------
// 5. 动态卡片 Composable (PostCard)
// ------------------------------------

@Composable
fun PostCard(
    post: Post,
    isFollowing: Boolean,
    onFollowToggle: (Int, Boolean) -> Unit,
    showFollowButton: Boolean = true // 新增参数：控制是否显示关注按钮，默认为显示
) {
    // 状态：点赞
    var isLiked by remember { mutableStateOf(post.initialIsLiked) }
    // 状态：转发/评论弹窗
    var showShareDialog by remember { mutableStateOf(false) }
    var showCommentSection by remember { mutableStateOf(false) }

    val onLikeClicked: () -> Unit = {
        isLiked = !isLiked
    }

    if (showShareDialog) {
        ShareDialog(onDismiss = { showShareDialog = false })
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // 头部信息
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = post.userAvatar,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(post.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(post.timeAgo, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))

            // 根据参数决定是否显示关注按钮
            if (showFollowButton) {
                Button(
                    onClick = { onFollowToggle(post.userId, !isFollowing) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) Color.Gray.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(if (isFollowing) "已关注" else "+ 关注", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 内容
        Text(post.content, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        // 图片占位符
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(post.imagePlaceholder, color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        // 底部互动按钮 (转发, 评论, 点赞)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InteractionButton(
                icon = Icons.Default.Refresh,
                text = "转发",
                onClick = { showShareDialog = true }
            )
            InteractionButton(
                icon = Icons.Default.MailOutline,
                text = "评论",
                onClick = { showCommentSection = !showCommentSection }
            )
            InteractionButton(
                icon = Icons.Default.ThumbUp,
                text = if (isLiked) (post.likes + 1).toString() else post.likes.toString(),
                tint = if (isLiked) Color.Red else Color.Gray,
                onClick = onLikeClicked
            )
        }

        // 评论区域
        if (showCommentSection) {
            CommentSection(post)
        }
    }
}

@Composable
fun InteractionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    tint: Color = Color.Gray
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = tint)
        Spacer(Modifier.width(4.dp))
        Text(text, color = tint, fontSize = 14.sp)
    }
}

// ------------------------------------
// 6. 底部导航栏 Composable
// ------------------------------------

@Composable
fun BottomNavigationBar(onPublishClick: () -> Unit, onMessageClick: () -> Unit) {
    val navItems = listOf(
        BottomNavItem("首页", Icons.Default.Home, "home"),
        BottomNavItem("视频", Icons.Default.PlayArrow, "video"),
        BottomNavItem("我的", Icons.Default.Person, "my")
    )

    val currentRoute = "home"

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(60.dp)
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = item.route == currentRoute,
                onClick = { /* 导航操作 */ }
            )
        }

        // 插入发布按钮
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .size(60.dp)
                .offset(y = (-10).dp)
                .clip(CircleShape)
                .background(Color.Red)
                .clickable(onClick = onPublishClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "发布", tint = Color.White)
        }

        // 消息互动按钮
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onMessageClick)
                .wrapContentSize(Alignment.Center)
        ) {
            Icon(Icons.Default.Email, contentDescription = "消息", tint = Color.Gray)
            // 模拟红点
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

// ------------------------------------
// 7. 弹窗 Composable
// ------------------------------------

@Composable
fun PublishPostDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("动态发布", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("发布图文动态 (图文内容)")
                Spacer(Modifier.height(8.dp))
                Text("关联骑行记录 (关联按钮)")
                Spacer(Modifier.height(8.dp))
                Text("话题参与 (选择话题)")
                Spacer(Modifier.height(16.dp))
                Text("这是“动态发布”相关的弹窗内容。")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
fun MessageInteractionDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("消息互动", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("1. 点赞评论功能 (跳转到评论/点赞列表)")
                Spacer(Modifier.height(8.dp))
                Text("2. 私信聊天 (跳转到聊天列表)")
                Spacer(Modifier.height(8.dp))
                Text("3. 系统通知 (跳转到通知中心)")
                Spacer(Modifier.height(16.dp))
                Text("这是“消息互动”相关的弹窗内容。")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
fun ShareDialog(onDismiss: () -> Unit) {
    val friends = remember { listOf(Friend(1, "山地车王"), Friend(2, "城市骑手小李"), Friend(3, "官方资讯")) }
    var showSentSnackbar by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("转发动态给好友", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                friends.forEach { friend ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(friend.name, modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                showSentSnackbar = true
                                onDismiss()
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("转发")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                Text("取消", color = Color.Black)
            }
        }
    )

    if (showSentSnackbar) {
        LaunchedEffect(Unit) {
            delay(1500)
            showSentSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(8.dp))
        ) {
            Text("已发送给好友！")
        }
    }
}


@Composable
fun CommentSection(post: Post) {
    val comments = remember(post.id) { getCommentsForPost(post.id) }
    var myComment by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        HorizontalDivider()
        Text("评论 (${comments.size})", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))

        comments.forEach { comment ->
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text("${comment.userName}: ${comment.content}", fontSize = 14.sp)
                Text(comment.time, color = Color.Gray, fontSize = 12.sp)
            }
        }

        OutlinedTextField(
            value = myComment,
            onValueChange = { myComment = it },
            label = { Text("发表你的评论...") },
            trailingIcon = {
                Icon(Icons.Default.Send, contentDescription = "发送",
                    modifier = Modifier.clickable {
                        if (myComment.isNotBlank()) {
                            myComment = ""
                        }
                    })
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true
        )
    }
}

// ------------------------------------
// 8. 子页面内容 Composable
// ------------------------------------

@Composable
fun FollowingDynamicScreen(
    allPosts: List<Post>,
    followingUserIds: Set<Int>,
    onFollowToggle: (Int, Boolean) -> Unit
) {
    val posts = allPosts.filter { followingUserIds.contains(it.userId) }

    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("你还没有关注任何动态，去热门动态看看吧！", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(posts, key = { it.id }) { post ->
            PostCard(post = post, isFollowing = true, onFollowToggle = onFollowToggle)
            Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
        }
    }
}

@Composable
fun HotDynamicScreen(
    allPosts: List<Post>,
    followingUserIds: Set<Int>,
    onFollowToggle: (Int, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(allPosts, key = { it.id }) { post ->
            val isFollowing = followingUserIds.contains(post.userId)
            PostCard(post = post, isFollowing = isFollowing, onFollowToggle = onFollowToggle)
            Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
        }
    }
}

// ------------------------------------
// 9. 社区交易子模块 (TradeScreen)
// ------------------------------------

@Composable
fun TradeScreen() {
    val tabs = listOf("二手交易", "官方售卖")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tradeItems = remember { mutableStateListOf<TradeItem>() }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(Unit) {
        Thread {
            val list = mutableListOf<TradeItem>()
            DatabaseHelper.processQuery("SELECT item_id, is_official, title, description, price, image_url, external_url, seller_user_id, category, is_published, created_at FROM trade_items ORDER BY created_at DESC LIMIT 200") { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val off = rs.getInt(2) == 1
                    val title = rs.getString(3) ?: ""
                    val desc = rs.getString(4) ?: ""
                    val price = rs.getBigDecimal(5)?.toPlainString() ?: "0"
                    val img = rs.getString(6) ?: "[图片]"
                    val url = rs.getString(7) ?: ""
                    val sellerId = rs.getInt(8)
                    val cat = rs.getString(9) ?: ""
                    val pub = rs.getInt(10) == 1
                    var sellerName: String? = null
                    if (sellerId > 0) {
                        DatabaseHelper.processQuery("SELECT nickname FROM users WHERE user_id = ?", listOf(sellerId)) { urs ->
                            if (urs.next()) sellerName = urs.getString(1)
                            Unit
                        }
                    }
                    list.add(TradeItem(id, off, title, desc, "¥ ${price}", img, url, sellerName, pub))
                }
                handler.post { tradeItems.clear(); tradeItems.addAll(list) }
                Unit
            }
        }.start()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) Color.Red else Color.Black) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        when (tabs[selectedTabIndex]) {
            "二手交易" -> SecondHandMarketScreen(tradeItems)
            "官方售卖" -> OfficialStoreScreen(tradeItems)
        }
    }
}

@Composable
fun SecondHandMarketScreen(allTradeItems: List<TradeItem>) {
    val secondhandItems = allTradeItems.filter { !it.isOfficial && it.isPublished }
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("搜索二手商品关键词...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(secondhandItems, key = { it.id }) { item ->
                TradePostCard(item = item) {
                    println("Navigate to external URL: ${item.externalUrl}")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Button(
            onClick = { /* 模拟发布弹窗 */ },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
        ) {
            Text("发布二手交易链接")
        }
    }
}

@Composable
fun OfficialStoreScreen(allTradeItems: List<TradeItem>) {
    val officialItems = allTradeItems.filter { it.isOfficial }
    val categories = listOf("骑行服", "配件", "整车", "其他")
    var selectedCategory by remember { mutableStateOf("骑行服") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            categories.forEach { category ->
                Text(
                    text = category,
                    color = if (category == selectedCategory) Color.Red else Color.Gray,
                    fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedCategory = category }
                )
            }
        }
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(officialItems.filter { it.description.contains(selectedCategory) || selectedCategory == "配件" }, key = { it.id }) { item ->
                TradePostCard(item = item) {
                    println("Navigate to official store: ${item.externalUrl}")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun TradePostCard(item: TradeItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(item.imagePlaceholder, fontSize = 12.sp, color = Color.DarkGray)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.description, color = Color.Gray, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.price, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                if (!item.isOfficial) {
                    Text("发布者: ${item.sellerName}", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "详情",
            tint = Color.Gray
        )
    }
}

// ------------------------------------
// 10. 俱乐部主屏幕 (ClubScreen) - 修改版
// ------------------------------------

@Composable
fun ClubScreen() {
    // 俱乐部局部状态（控制子页面/弹窗）
    var showApplicantsDialog by remember { mutableStateOf(false) }
    var showClubManagement by remember { mutableStateOf(false) }
    var showClubIntro by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }
    var showMemberManageDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<ClubMember?>(null) }

    // 模拟俱乐部成员列表（可变）
    val members = remember {
        mutableStateListOf(
            ClubMember(1, "陈部长", "部长", avatarPlaceholder = "[头像-陈部长]", info = "俱乐部创始人，热爱山地越野"),
            ClubMember(2, "王副", "副部长", avatarPlaceholder = "[头像-王副]", info = "负责活动与线路设计"),
            ClubMember(3, "李小四", "成员", avatarPlaceholder = "[头像-李小四]", info = "擅长维修与拍照"),
            ClubMember(4, "阿美", "成员", avatarPlaceholder = "[头像-阿美]", info = "每周打卡达人"),
            ClubMember(5, "山地车神", "成员", avatarPlaceholder = "[头像-山地车神]", info = "赛事取得多次奖项")
        )
    }

    // 模拟申请者列表
    val applicants = remember {
        mutableStateListOf(
            Applicant(101, "申请者_小张"),
            Applicant(102, "申请者_小王"),
            Applicant(103, "申请者_小刘")
        )
    }

    // 模拟排行榜数据（本周按骑行距离）
    val ranking = remember {
        listOf(
            RankingItem(1, "山地车神", 242.5),
            RankingItem(2, "陈部长", 198.3),
            RankingItem(3, "阿美", 175.0),
            RankingItem(4, "李小四", 143.7),
            RankingItem(5, "王副", 120.2)
        )
    }

    // 俱乐部动态数据 (复用Post模型)
    val clubPosts = remember { mutableStateListOf<Post>() }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(Unit) {
        Thread {
            val posts = mutableListOf<Post>()
            val likesMap = mutableMapOf<Int, Int>()
            val commentsMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery("SELECT post_id, author_user_id, content_text, image_url, created_at FROM community_posts WHERE club_id IS NOT NULL ORDER BY created_at DESC LIMIT 200") { rs ->
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val uid = rs.getInt(2)
                    val content = rs.getString(3) ?: ""
                    val img = rs.getString(4) ?: "[图片]"
                    val created = rs.getTimestamp(5)
                    val timeStr = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    posts.add(Post(pid, uid, Icons.Default.DateRange, "", timeStr, content, img, 0, 0))
                }
                Unit
            }
            DatabaseHelper.processQuery("SELECT p.post_id, c.name FROM community_posts p JOIN clubs c ON p.club_id = c.club_id WHERE p.club_id IS NOT NULL") { crs ->
                val nameMap = mutableMapOf<Int, String>()
                while (crs.next()) nameMap[crs.getInt(1)] = crs.getString(2) ?: ""
                posts.replaceAll { p -> p.copy(userName = nameMap[p.id] ?: "") }
                Unit
            }
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) c FROM post_likes GROUP BY post_id") { lrs ->
                while (lrs.next()) likesMap[lrs.getInt(1)] = lrs.getInt(2)
                Unit
            }
            DatabaseHelper.processQuery("SELECT post_id, COUNT(*) c FROM post_comments GROUP BY post_id") { crs2 ->
                while (crs2.next()) commentsMap[crs2.getInt(1)] = crs2.getInt(2)
                Unit
            }
            val merged = posts.map { p -> p.copy(likes = likesMap[p.id] ?: 0, comments = commentsMap[p.id] ?: 0) }
            handler.post { clubPosts.clear(); clubPosts.addAll(merged) }
        }.start()
    }

    // 主视图：如果进入子页面则显示子页面内容，否则显示主俱乐部页面
    if (showClubManagement) {
        ClubManagementScreen(
            members = members,
            onBack = { showClubManagement = false },
            onMemberClick = { member ->
                selectedMember = member
                showMemberManageDialog = true
            }
        )
    } else if (showClubIntro) {
        ClubIntroScreen(
            onBack = { showClubIntro = false },
            onShare = { /* 弹出分享给好友弹窗 */ }
        )
    } else if (showLeaderboard) {
        ClubLeaderboardScreen(
            ranking = ranking,
            onBack = { showLeaderboard = false }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 顶部 Header 区域 (背景图 + 俱乐部信息)
            item {
                ClubHeaderSection(
                    onBackClick = { /* 模拟返回 */ },
                    onMenuClick = { showClubIntro = true }, // 改为跳转俱乐部详情页
                    onShareClick = { /* 可触发分享弹窗或其他 */ }
                )
            }

            // 2. 热度统计条
            item {
                ClubHeatSection()
            }

            // 3. 功能按钮网格 (去掉“活动”按钮)
            item {
                ClubActionGridModified(
                    onItemClick = { title ->
                        when (title) {
                            "队员排名" -> showLeaderboard = true
                            "队友位置" -> {
                                // 保留原有行为：展示位置相关弹窗或页面（此处简化为弹窗）
                                // 使用 AlertDialog 简单提示
                            }
                        }
                    }
                )
            }

            // 4. 列表菜单 (管理、申请)
            item {
                ClubMenuSection(
                    onItemClick = { title ->
                        when (title) {
                            "俱乐部管理" -> showClubManagement = true
                            "入队申请" -> showApplicantsDialog = true
                        }
                    }
                )
            }

            // 5. 动态内容标题 (保持原样)
            item {
                Spacer(modifier = Modifier.height(12.dp).fillMaxWidth().background(Color(0xFFF5F5F5)))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "飓风骑行俱乐部",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.background(Color(0xFFFFA500), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                        Text("Lv.8", color = Color.White, fontSize = 10.sp)
                    }
                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Blue),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("动态", fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Gray),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("置顶", fontSize = 14.sp)
                    }
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            // 6. 动态列表 (复用 PostCard，但隐藏关注按钮)
            items(clubPosts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isFollowing = true,
                    onFollowToggle = { _, _ -> },
                    showFollowButton = false
                )
                Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
            }
        }
    }

    // 入队申请弹窗
    if (showApplicantsDialog) {
        AlertDialog(
            onDismissRequest = { showApplicantsDialog = false },
            title = { Text("入队申请", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("以下用户申请加入俱乐部：", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    applicants.forEach { applicant ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                Text(applicant.name.take(1))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(applicant.name, fontWeight = FontWeight.Bold)
                                Text(applicant.reason, color = Color.Gray, fontSize = 12.sp)
                            }
                            Button(onClick = {
                                // 同意：将申请者转换为成员（虚构角色为成员）
                                members.add(ClubMember(applicant.id, applicant.name, "成员", avatarPlaceholder = "[头像-${applicant.name}]"))
                                applicants.remove(applicant)
                            }, contentPadding = PaddingValues(horizontal = 12.dp)) {
                                Text("同意")
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { applicants.remove(applicant) }, contentPadding = PaddingValues(horizontal = 12.dp)) {
                                Text("拒绝")
                            }
                        }
                    }
                    if (applicants.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("当前没有待处理的申请。", color = Color.Gray)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showApplicantsDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }

    // 成员管理弹窗（更改职位/踢出/查看信息）
    if (showMemberManageDialog && selectedMember != null) {
        val member = selectedMember!!
        var roleMenuExpanded by remember { mutableStateOf(false) }
        val roles = listOf("部长", "副部长", "成员")
        AlertDialog(
            onDismissRequest = { showMemberManageDialog = false; selectedMember = null },
            title = { Text("管理: ${member.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Text(member.avatarPlaceholder)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(member.name, fontWeight = FontWeight.Bold)
                            Text("当前职位：${member.role}", color = Color.Gray)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("操作：", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    // 更改职位：下拉选择
                    Box {
                        OutlinedButton(onClick = { roleMenuExpanded = true }) {
                            Text("更改职位")
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = roleMenuExpanded, onDismissRequest = { roleMenuExpanded = false }) {
                            roles.forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = {
                                    // 修改职位
                                    val idx = members.indexOfFirst { it.id == member.id }
                                    if (idx >= 0) members[idx] = members[idx].copy(role = r)
                                    roleMenuExpanded = false
                                    showMemberManageDialog = false
                                    selectedMember = null
                                })
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // 踢出按钮
                    OutlinedButton(onClick = {
                        members.removeIf { it.id == member.id }
                        showMemberManageDialog = false
                        selectedMember = null
                    }) {
                        Text("踢出成员")
                    }
                    Spacer(Modifier.height(8.dp))
                    // 查看信息
                    Button(onClick = {
                        // 简单弹窗显示信息
                    }) {
                        Text("查看信息")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMemberManageDialog = false; selectedMember = null }) {
                    Text("取消")
                }
            }
        )
    }
}

// 俱乐部动作网格（已移除“活动”按钮）
@Composable
fun ClubActionGridModified(onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ClubActionItem(icon = Icons.Default.Menu, title = "队员排名", onClick = { onItemClick("队员排名") })
        ClubActionItem(icon = Icons.Default.LocationOn, title = "队友位置", onClick = { onItemClick("队友位置") })
    }
    HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F5F5))
}
@Composable
fun ClubActionItem(icon: ImageVector, title: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Color.Red else Color(0xFF0091EA), // 蓝色系图标
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isSelected) Color.Red else Color.Gray
        )
        if (isSelected) {
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier.width(20.dp).height(2.dp).background(Color.Red))
        }
    }
}

@Composable
fun ClubHeaderSection(onBackClick: () -> Unit, onMenuClick: () -> Unit, onShareClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        Image(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            alpha = 0.3f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.clickable(onClick = onBackClick)
                )
                Row {
                    Icon(
                        imageVector = Icons.Default.Info, // 改为详情图标
                        contentDescription = "详情",
                        tint = Color.White,
                        modifier = Modifier.clickable(onClick = onMenuClick)
                    )
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.clickable(onClick = onShareClick)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier.size(70.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Red, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = "飓风骑行俱乐部",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("总热度: 57万℃", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Text(" x285", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD4AF37), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("年度第1骑", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ClubHeatSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("6月 热度", color = Color.Gray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("5000℃", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(14.dp))
                Text("x2", color = Color(0xFFD4AF37), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = 0.78f,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color.Red,
            trackColor = Color(0xFFEEEEEE)
        )
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("3909℃", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
    HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F5F5))
}

@Composable
fun ClubMenuSection(onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ClubMenuItem(icon = Icons.Default.Settings, title = "俱乐部管理", subText = "今日活跃 25人", onClick = { onItemClick("俱乐部管理") })
        HorizontalDivider(color = Color(0xFFEEEEEE))
        ClubMenuItem(icon = Icons.Default.Add, title = "入队申请", onClick = { onItemClick("入队申请") })
    }
}

@Composable
fun ClubMenuItem(icon: ImageVector, title: String, subText: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 16.sp, color = Color.Black)
        Spacer(Modifier.weight(1f))
        if (subText != null) {
            Text(subText, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.width(4.dp))
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

// ------------------------------------
// 11. 俱乐部管理子页面（成员列表）
// ------------------------------------

@Composable
fun ClubManagementScreen(members: List<ClubMember>, onBack: () -> Unit, onMemberClick: (ClubMember) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("俱乐部管理", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("成员 ${members.size}", color = Color.Gray)
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(members, key = { it.id }) { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMemberClick(member) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Text(member.name.take(1))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, fontWeight = FontWeight.Bold)
                        Text(member.role, color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.MoreVert, contentDescription = null)
                }
                HorizontalDivider()
            }
        }
    }
}

// ------------------------------------
// 12. 队员排名页面（按本周骑行距离）
// ------------------------------------

@Composable
fun ClubLeaderboardScreen(ranking: List<RankingItem>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("本周队员排名（按骑行距离）", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(ranking, key = { it.rank }) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${item.rank}", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Medium)
                        Text("本周骑行 ${item.distanceKm} km", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF0091EA))
                }
                HorizontalDivider()
            }
        }
    }
}

// ------------------------------------
// 13. 俱乐部介绍页面（包含名言/创建目的/奖项/分享）
// ------------------------------------

@Composable
fun ClubIntroScreen(onBack: () -> Unit, onShare: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("俱乐部介绍", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.clickable(onClick = onShare))
        }
        HorizontalDivider()
        Column(modifier = Modifier.padding(16.dp)) {
            Text("俱乐部名言：", fontWeight = FontWeight.Bold)
            Text("“风在耳边，路在脚下。”", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Text("创建目的：", fontWeight = FontWeight.Bold)
            Text("为本地骑友提供线路共享、训练交流与赛事备战支持。", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Text("俱乐部荣誉：", fontWeight = FontWeight.Bold)
            Text("- 2023省级越野团体赛 冠军\n- 2024市级环湖挑战赛 团体第三\n- 多次社区优秀组织奖", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Spacer(Modifier.height(12.dp))
            Button(onClick = { /* 打开分享好友列表: 此处可展示分享弹窗 */ }) {
                Text("分享给好友")
            }
        }
    }
}

// ------------------------------------
// 14. 模拟数据生成器（保留原样）
// ------------------------------------

private fun generateTradeItems(): List<TradeItem> = listOf(
    TradeItem(
        id = 1, isOfficial = false, title = "9成新碳纤维公路车架",
        description = "尺寸M，超轻，只用了半年，因为换车出售。可小刀。",
        price = "¥ 4500", imagePlaceholder = "[车架封面图]", sellerName = "山地车王",
        externalUrl = "taobao://item/12345"
    ),
    TradeItem(
        id = 2, isOfficial = false, title = "Shimano 105套件（二手）",
        description = "飞轮、链条、牙盘全套，正常使用痕迹，功能完好。",
        price = "¥ 1200", imagePlaceholder = "[套件封面图]", sellerName = "城市骑手小李",
        externalUrl = "xianyu://item/67890"
    ),
    TradeItem(
        id = 3, isOfficial = false, title = "冬季骑行抓绒手套",
        description = "全新未拆封，L号，防水防风，多买了一副，便宜出。",
        price = "¥ 99", imagePlaceholder = "[手套封面图]", sellerName = "装备测评师",
        externalUrl = "taobao://item/11223"
    ),
    TradeItem(
        id = 4, isOfficial = true, title = "RideFlow 2024新款速干骑行服套装",
        description = "骑行服，透气排汗，夏季必备。分类：骑行服",
        price = "¥ 399", imagePlaceholder = "[官方骑行服]", externalUrl = "app://official/product/399"
    ),
    TradeItem(
        id = 5, isOfficial = true, title = "高性能GPS码表（R700型号）",
        description = "精准定位，超长续航，支持心率监测。分类：配件",
        price = "¥ 1899", imagePlaceholder = "[官方码表]", externalUrl = "app://official/product/r700"
    ),
    TradeItem(
        id = 6, isOfficial = true, title = "山地越野头盔（Pro系列）",
        description = "MIPS保护系统，轻量化设计，多色可选。分类：配件",
        price = "¥ 599", imagePlaceholder = "[官方头盔]", externalUrl = "app://official/product/prohelmet"
    ),
)

private fun generateSamplePosts(): List<Post> = listOf(
    Post(
        id = 101, userId = 2, userName = "骑行侠",
        timeAgo = "1小时前", content = "刚刚完成了200KM的挑战！天气超棒，一路风景绝美，唯一的遗憾是爬坡太累了！#越野挑战 #骑行记录",
        imagePlaceholder = "[风景图占位符 - 200KM骑行记录]", likes = 145, comments = 32
    ),
    Post(
        id = 102, userId = 2, userName = "骑行侠",
        timeAgo = "3小时前", content = "今天的日落太美了，停下来拍一张，分享给所有爱骑行的朋友们！路程虽短，心满意足。",
        imagePlaceholder = "[日落图占位符]", likes = 78, comments = 11
    ),
    Post(
        id = 103, userId = 2, userName = "骑行侠",
        timeAgo = "1天前", content = "新买了山地胎，周末去山里试试性能如何。希望这次不会爆胎！",
        imagePlaceholder = "[装备图占位符]", likes = 55, comments = 7
    ),
    Post(
        id = 201, userId = 3, userName = "城市铁人",
        timeAgo = "1天前", content = "【二手转让】出9成新Shimano Ultegra套件，只用过一次山地骑行。详情私信！",
        imagePlaceholder = "[商品图片占位符]", likes = 88, comments = 5
    ),
    Post(
        id = 202, userId = 3, userName = "城市铁人",
        timeAgo = "2天前", content = "今天上下班通勤总共30KM，虽然有点累，但比开车快多了！#绿色出行 #骑行记录",
        imagePlaceholder = "[通勤记录占位符 - 30KM骑行记录]", likes = 42, comments = 10
    ),
    Post(
        id = 301, userId = 4, userName = "RideFlow官方",
        timeAgo = "1天前", content = "官方商城新品上架！新款透气骑行服限时八折，点击标签跳转购买！",
        imagePlaceholder = "[官方售卖链接占位符]", likes = 502, comments = 99
    ),
    Post(
        id = 302, userId = 4, userName = "RideFlow官方",
        timeAgo = "3天前", content = "本周骑行活动公告：周末城市环湖骑行，欢迎所有等级的骑友参加！请提前报名。",
        imagePlaceholder = "[活动海报占位符]", likes = 310, comments = 75
    ),
    Post(
        id = 401, userId = 5, userName = "装备测评师",
        timeAgo = "2天前", content = "最新款Garmin码表评测：精度提升，续航逆天！详细数据在下方图片链接。",
        imagePlaceholder = "[码表评测占位符]", likes = 210, comments = 45, initialIsLiked = true
    ),
    Post(
        id = 402, userId = 5, userName = "装备测评师",
        timeAgo = "3天前", content = "测评：轻量化头盔真的安全吗？我们进行了碰撞测试，结果让人大跌眼镜。",
        imagePlaceholder = "[头盔测试占位符]", likes = 99, comments = 28
    ),
    Post(
        id = 501, userId = 6, userName = "山地车神",
        timeAgo = "5小时前", content = "征服了那条难度最大的黑线！车技又提升了一截，但摔得不轻。#山地速降 #骑行记录",
        imagePlaceholder = "[越野记录占位符 - 难度黑线记录]", likes = 350, comments = 60
    ),
    Post(
        id = 601, userId = 7, userName = "公路妹子",
        timeAgo = "4小时前", content = "今天在滨海公路刷了80KM，海风吹着太舒服了！唯一的缺点是防晒没做好。#公路骑行 #骑行记录",
        imagePlaceholder = "[公路骑行图占位符 - 80KM骑行记录]", likes = 180, comments = 40
    )
)

private val COMMENTS_DATA: Map<Int, List<Comment>> = mapOf(
    101 to listOf(
        Comment(1, "山地老王", "200KM太猛了，请问全程爬升多少？", "10分钟前"),
        Comment(2, "用户A", "路书分享一下！想去挑战这条线。", "5分钟前")
    ),
    102 to listOf(
        Comment(3, "日落追光者", "这颜色太治愈了，骑行结束看到这个值得！", "1小时前"),
        Comment(4, "小李", "同款风景，我昨天也去那儿了！", "30分钟前")
    ),
    103 to listOf(
        Comment(5, "硬核玩家", "山地胎选对了很重要，期待你的测评！", "1小时前"),
        Comment(6, "配件狂魔", "哪个牌子的？我也想换！", "30分钟前")
    )
    // 省略其它映射以节省篇幅（示例中已有）
)

private fun getCommentsForPost(postId: Int): List<Comment> {
    return COMMENTS_DATA[postId] ?: emptyList()
}

private fun generateClubPosts(): List<Post> = listOf(
    Post(
        id = 801, userId = 88, userName = "飓风骑行俱乐部",
        userAvatar = Icons.Default.DateRange,
        timeAgo = "56分钟前",
        content = "发布了活动【2024-6-8 骑行碗子城】\n集合时间：上午 8:00\n集合地点：市体育馆北门\n难度：中等，请佩戴头盔。",
        imagePlaceholder = "[活动路线图：2024-6-8 骑行碗子城]",
        likes = 24, comments = 5
    ),
    Post(
        id = 802, userId = 88, userName = "飓风骑行俱乐部",
        userAvatar = Icons.Default.DateRange,
        timeAgo = "5天前",
        content = "发布了活动【2024-6-2 骑行珏山】\n风景绝美的一条路线，爬升800m，欢迎挑战！",
        imagePlaceholder = "[活动回顾图：珏山合影]",
        likes = 45, comments = 12
    ),
    Post(
        id = 803, userId = 88, userName = "飓风骑行俱乐部",
        userAvatar = Icons.Default.DateRange,
        timeAgo = "1周前",
        content = "恭喜本俱乐部成员 @山地车神 在省山地赛中获得第三名！大家一起为他点赞！",
        imagePlaceholder = "[获奖照片]",
        likes = 128, comments = 56
    )
)

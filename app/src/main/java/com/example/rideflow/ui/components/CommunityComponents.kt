package com.example.rideflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.rideflow.model.*
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.rideflow.backend.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import org.koin.androidx.compose.koinViewModel
import com.example.rideflow.auth.AuthViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

// ------------------------------------
// 1. 顶部搜索栏
// ------------------------------------
@Composable
fun TopSearchBar(isSearching: Boolean, onSearchToggle: (Boolean) -> Unit) {
    if (isSearching) {
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
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
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("热搜", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text("第十七届山地越野赛即将开始报名", fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { onSearchToggle(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
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

@Composable
fun PostListSkeleton(count: Int = 6) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        repeat(count) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray.copy(alpha = 0.5f)))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Box(modifier = Modifier.width(120.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                            Spacer(Modifier.height(6.dp))
                            Box(modifier = Modifier.width(80.dp).height(10.dp).background(Color.LightGray.copy(alpha = 0.4f)))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}

@Composable
fun LoadMoreFooter(hasMore: Boolean, isLoadingMore: Boolean, onLoadMore: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (hasMore) {
            Button(onClick = onLoadMore, enabled = !isLoadingMore) {
                Text(if (isLoadingMore) "正在加载..." else "加载更多")
            }
        } else {
            Text("没有更多了", color = Color.Gray)
        }
    }
}

// ------------------------------------
// 2. 分类标签栏
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
                    color = if (category == selectedCategory) MaterialTheme.colorScheme.primary else Color.Black,
                    fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onCategorySelected(category) }
                )
                if (category == selectedCategory) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary)
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
// 3. 动态卡片 (PostCard) 及其子组件
// ------------------------------------
@Composable
fun PostCard(
    post: Post,
    isFollowing: Boolean,
    onFollowToggle: (Int, Boolean) -> Unit,
    showFollowButton: Boolean = true,
    onAvatarClick: (Int, String) -> Unit = { _, _ -> },
    onPostClick: (Int) -> Unit = {},
    onLikeToggle: (Int, Boolean) -> Unit = { _, _ -> }
) {
    var isLiked by remember { mutableStateOf(post.initialIsLiked) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showCommentSection by remember { mutableStateOf(false) }
    var latestComments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    val authViewModel = koinViewModel<AuthViewModel>()
    val scope = rememberCoroutineScope()

    val onLikeClicked: () -> Unit = {
        val newState = !isLiked
        isLiked = newState
        onLikeToggle(post.id, newState)
    }

    if (showShareDialog) {
        ShareDialog(onDismiss = { showShareDialog = false })
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // 头部信息
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { onAvatarClick(post.userId, post.authorType) }
            ) {
                if (!post.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = post.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = post.userAvatar,
                        contentDescription = "Avatar",
                        tint = Color.Gray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = post.userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onAvatarClick(post.userId, post.authorType) }
                )
                Text(post.timeAgo, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))

            if (showFollowButton) {
                Button(
                    onClick = { onFollowToggle(post.userId, !isFollowing) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) Color.Gray.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(if (isFollowing) "已关注" else "+ 关注", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 内容
        Text(post.content, modifier = Modifier.fillMaxWidth().clickable { onPostClick(post.id) })
        Spacer(Modifier.height(8.dp))

        // 图片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            AsyncImage(
                model = post.imagePlaceholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        // 最新三条评论
        LaunchedEffect(post.id) {
            withContext(Dispatchers.IO) {
                val list = mutableListOf<Comment>()
                DatabaseHelper.processQuery(
                    "SELECT pc.comment_id, u.nickname, pc.content, pc.created_at FROM post_comments pc JOIN users u ON pc.user_id = u.user_id WHERE pc.post_id = ? ORDER BY pc.created_at DESC LIMIT 3",
                    listOf(post.id)
                ) { rs ->
                    while (rs.next()) {
                        val cid = rs.getInt(1)
                        val nick = rs.getString(2) ?: "匿名"
                        val ctt = rs.getString(3) ?: ""
                        val created = rs.getTimestamp(4)
                        val ts = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                        list.add(Comment(cid, nick, ctt, ts))
                    }
                    Unit
                }
                latestComments = list
            }
        }

        if (latestComments.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("最新评论", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                latestComments.forEach { c ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(c.userName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(8.dp))
                            Text(c.time, fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(c.content, fontSize = 13.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // 底部互动按钮
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
                text = post.likes.toString(),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray,
                onClick = onLikeClicked
            )
        }

        if (showCommentSection) {
            // 展开评论输入与发送
            var myComment by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                HorizontalDivider()
                Text("发表评论", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = myComment,
                    onValueChange = { myComment = it },
                    label = { Text("发表你的评论...") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "发送",
                            modifier = Modifier.clickable {
                                val text = myComment.trim()
                                val uid = authViewModel.getCurrentUser()?.userId?.toIntOrNull()
                                if (text.isNotEmpty() && uid != null) {
                                    scope.launch(Dispatchers.IO) {
                                        val newId = DatabaseHelper.insertAndReturnId(
                                            "INSERT INTO post_comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)",
                                            listOf(post.id, uid, text)
                                        )
                                        if (newId != null) {
                                            val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                            withContext(Dispatchers.Main) {
                                                latestComments = listOf(Comment(newId, authViewModel.getCurrentUser()?.nickname ?: "我", text, now)) + latestComments.take(2)
                                                myComment = ""
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    singleLine = true
                )
            }
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
        // 在实际UI中，Snackbar 通常由 ScaffoldState 控制，这里仅作逻辑占位
    }
}

// 旧的模拟评论已移除，评论改为数据库加载

// ------------------------------------
// 4. 交易相关组件
// ------------------------------------
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
            AsyncImage(
                model = item.imagePlaceholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.description, color = Color.Gray, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
// 5. 弹窗组件
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

// ------------------------------------
// 6. 俱乐部展示组件
// ------------------------------------
@Composable
fun ClubHeaderSection(onBackClick: () -> Unit, onMenuClick: () -> Unit, onShareClick: () -> Unit) {
    // ... (保持不变)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // ... (保持不变)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
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
                        imageVector = Icons.Default.Info,
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
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
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
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color(0xFFEEEEEE)
        )
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("3909℃", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
        if (isSelected) {
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier.width(20.dp).height(2.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}

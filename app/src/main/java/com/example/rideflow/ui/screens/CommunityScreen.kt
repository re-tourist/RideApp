package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Add

// 模拟数据类
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatar: Int,
    val authorBadge: String?,
    val content: String,
    val images: List<Int>,
    val date: String,
    val comments: Int,
    val likes: Int,
    val isPinned: Boolean = false,
    val category: String = "社区动态",
    val price: String? = null,
    val eventParticipants: Int = 0,
    val isOfficial: Boolean = false
)

private data class TabItem(val title: String, val category: String)

private val tabs = listOf(
    TabItem("社区动态", "社区动态"),
    TabItem("俱乐部", "俱乐部"),
    TabItem("线下活动", "线下活动"),
    TabItem("社区交易", "社区交易"),
    TabItem("俱乐部管理", "管理")
)

private val mockPosts = listOf(
    Post(
        id = 1,
        authorName = "热门推荐",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "热门",
        content = "环青海湖骑行攻略大公开",
        images = List(3) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 18:30",
        comments = 156,
        likes = 892,
        category = "社区动态"
    ),
    Post(
        id = 4,
        authorName = "北京骑行俱乐部",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方俱乐部",
        content = "本周六组织西山骑行活动",
        images = List(1) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 17:00",
        comments = 28,
        likes = 89,
        category = "俱乐部"
    ),
    Post(
        id = 7,
        authorName = "城市骑行节",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方活动",
        content = "第三届城市骑行节开始报名啦",
        images = List(3) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 15:45",
        comments = 89,
        likes = 234,
        category = "线下活动",
        eventParticipants = 156
    ),
    Post(
        id = 10,
        authorName = "装备小店",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方认证",
        content = "Giro头盔特价促销",
        images = List(2) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 09:30",
        comments = 12,
        likes = 28,
        category = "社区交易",
        price = "¥450",
        isOfficial = true
    )
)

@Composable
fun CommunityScreen(navController: androidx.navigation.NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    var showPostDialog by remember { mutableStateOf(false) }
    var showClubDialog by remember { mutableStateOf(false) }
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var showCreateTradeDialog by remember { mutableStateOf(false) }
    var posts by remember { mutableStateOf(mockPosts) }
    val filteredPosts = remember(selectedTab, posts) {
        if (selectedTab == 4) emptyList() else posts.filter { it.category == tabs[selectedTab].category }
    }

    var eventTitle by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var tradeTitle by remember { mutableStateOf("") }
    var tradePrice by remember { mutableStateOf("") }
    var tradeDesc by remember { mutableStateOf("") }

    if (showPostDialog) {
        AlertDialog(onDismissRequest = { showPostDialog = false }, confirmButton = { Button(onClick = { showPostDialog = false }) { Text("关闭") } }, title = { Text("发布动态") }, text = { Text("在此编写并发布你的动态") })
    }
    if (showClubDialog) {
        AlertDialog(onDismissRequest = { showClubDialog = false }, confirmButton = { Button(onClick = { showClubDialog = false }) { Text("关闭") } }, title = { Text("加入俱乐部") }, text = { Text("选择或搜索俱乐部进行加入") })
    }
    if (showCreateEventDialog) {
        AlertDialog(
            onDismissRequest = { showCreateEventDialog = false },
            confirmButton = {
                Button(onClick = {
                    val newId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                    posts = posts + Post(
                        id = newId,
                        authorName = "我",
                        authorAvatar = R.drawable.ic_launcher_foreground,
                        authorBadge = null,
                        content = eventTitle.ifEmpty { "新活动" } + " · " + eventDate.ifEmpty { "时间待定" } + " · " + eventLocation.ifEmpty { "地点待定" },
                        images = emptyList(),
                        date = "2025-11-25 12:00",
                        comments = 0,
                        likes = 0,
                        category = "线下活动",
                        eventParticipants = 1
                    )
                    eventTitle = ""
                    eventDate = ""
                    eventLocation = ""
                    showCreateEventDialog = false
                }) { Text("发布") }
            },
            dismissButton = { Button(onClick = { showCreateEventDialog = false }) { Text("取消") } },
            title = { Text("发起活动") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = eventTitle, onValueChange = { eventTitle = it }, label = { Text("活动标题") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = eventDate, onValueChange = { eventDate = it }, label = { Text("活动时间") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = eventLocation, onValueChange = { eventLocation = it }, label = { Text("活动地点") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }

    if (showCreateTradeDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTradeDialog = false },
            confirmButton = {
                Button(onClick = {
                    val newId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                    posts = posts + Post(
                        id = newId,
                        authorName = "我",
                        authorAvatar = R.drawable.ic_launcher_foreground,
                        authorBadge = null,
                        content = tradeTitle.ifEmpty { "新交易" } + " · " + tradeDesc,
                        images = emptyList(),
                        date = "2025-11-25 12:00",
                        comments = 0,
                        likes = 0,
                        category = "社区交易",
                        price = if (tradePrice.isNotBlank()) "¥${tradePrice}" else null
                    )
                    tradeTitle = ""
                    tradePrice = ""
                    tradeDesc = ""
                    showCreateTradeDialog = false
                }) { Text("发布") }
            },
            dismissButton = { Button(onClick = { showCreateTradeDialog = false }) { Text("取消") } },
            title = { Text("发布交易") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = tradeTitle, onValueChange = { tradeTitle = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = tradePrice, onValueChange = { tradePrice = it }, label = { Text("价格") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = tradeDesc, onValueChange = { tradeDesc = it }, label = { Text("描述") }, modifier = Modifier.fillMaxWidth())
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "社区",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        when (selectedTab) {
                            0 -> { IconButton(onClick = { showPostDialog = true }) { Icon(imageVector = Icons.Filled.Create, contentDescription = "发布动态") } }
                            1 -> { IconButton(onClick = { showClubDialog = true }) { Icon(imageVector = Icons.Filled.Add, contentDescription = "加入俱乐部") } }
                            2 -> { Button(onClick = { showCreateEventDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)), modifier = Modifier.height(36.dp)) { Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.size(4.dp)); Text("发起活动", fontSize = 14.sp) } }
                            3 -> { Button(onClick = { showCreateTradeDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759)), modifier = Modifier.height(36.dp)) { Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.size(4.dp)); Text("发布交易", fontSize = 14.sp) } }
                            else -> { IconButton(onClick = { }) { Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "更多选项") } }
                        }
                    }
                }
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF007AFF),
                    divider = { Divider() }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            text = { Text(text = tab.title, style = MaterialTheme.typography.bodyMedium) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        }
    ) {
        if (selectedTab == 4) {
            ClubManagementScreen(members = listOf(
                ClubMember(1, "张部长", R.drawable.ic_launcher_foreground, "管理者", true),
                ClubMember(2, "李副部", R.drawable.ic_launcher_foreground, "管理者", false),
                ClubMember(3, "骑行达人小明", R.drawable.ic_launcher_foreground, "成员", true)
            ))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = it
            ) {
                items(filteredPosts) {
                    PostCard(post = it)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class ClubMember(
    val id: Int,
    val name: String,
    val avatar: Int,
    val role: String,
    val isOnline: Boolean
)
@Composable
fun ClubManagementScreen(members: List<ClubMember>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(text = members.count { it.isOnline }.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(text = "在线", fontSize = 12.sp, color = Color.Gray) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(text = members.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold); Text(text = "成员", fontSize = 12.sp, color = Color.Gray) }
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)) {
            items(members) { member ->
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = member.avatar), contentDescription = member.name, modifier = Modifier.size(40.dp).clip(CircleShape))
                        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(text = member.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = member.role + if (member.isOnline) " · 在线" else " · 离线", fontSize = 12.sp, color = Color.Gray)
                        }
                        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))) { Text("管理") }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatar),
                    contentDescription = post.authorName,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (post.authorBadge != null) {
                            Badge(
                                modifier = Modifier.padding(start = 4.dp),
                                containerColor = Color.Yellow,
                                contentColor = Color.Black
                            ) {
                                Text(text = post.authorBadge, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(text = post.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            // 帖子内容
            Text(
                text = post.content,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
            
            // 帖子图片
            if (post.images.isNotEmpty()) {
                when (post.images.size) {
                    1 -> {
                        Image(
                            painter = painterResource(id = post.images.first()),
                            contentDescription = "Post image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 12.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Row(modifier = Modifier.padding(top = 12.dp)) {
                            post.images.take(3).forEachIndexed { index, image ->
                                Image(
                                    painter = painterResource(id = image),
                                    contentDescription = "Post image $index",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .padding(end = if (index < 2) 8.dp else 0.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (post.images.size > 3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .background(Color.Gray)
                                        .padding(end = 8.dp)
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        text = "共${post.images.size}张",
                                        color = Color.White,
                                        modifier = Modifier
                                            .align(androidx.compose.ui.Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 操作栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    IconButton(onClick = { /* 评论 */ }) {
                        Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "评论"
                    )
                    }
                    Text(text = post.comments.toString())
                }
                Row {
                    IconButton(onClick = { /* 点赞 */ }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            contentDescription = "Like"
                        )
                    }
                    Text(text = post.likes.toString())
                }
                Row {
                    IconButton(onClick = { /* 分享 */ }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
                if (post.isPinned) {
                    Button(
                        onClick = { /* 置顶操作 */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "置顶", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    CommunityScreen(navController = androidx.navigation.compose.rememberNavController())
}

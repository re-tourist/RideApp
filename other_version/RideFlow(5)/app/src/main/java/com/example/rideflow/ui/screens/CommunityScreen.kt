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
    val isPinned: Boolean = false
)

private data class TabItem(val title: String)

private val tabs = listOf(
    TabItem("全部"),
    TabItem("装备讨论"),
    TabItem("二手交易"),
    TabItem("徒步户外")
)

private val mockPosts = listOf(
    Post(
        id = 1,
        authorName = "shockman911",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "金牌骑客",
        content = "【置顶】十月初一，寒衣节，早归。天刚擦黑，已有心急的人出来\"放火\"啦。又见到，在绿化带树坑里，放火的人。心里有些好奇，...",
        images = List(4) { R.drawable.ic_launcher_foreground },
        date = "2025-11-20 20:46",
        comments = 0,
        likes = 198,
        isPinned = true
    ),
    Post(
        id = 2,
        authorName = "KCT江鹰",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "菜鸟骑迹",
        content = "【置顶】24小时挑战400公里成功，为七十岁划了一个圆满句号。全程21小时另4发...",
        images = List(1) { R.drawable.ic_launcher_foreground },
        date = "2025-11-20 18:10",
        comments = 40,
        likes = 5435,
        isPinned = true
    ),
    Post(
        id = 3,
        authorName = "至若",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = null,
        content = "今天骑行感觉很棒，分享一下我的新路线...",
        images = emptyList(),
        date = "2025-11-19 19:31",
        comments = 5,
        likes = 128
    )
)

@Composable
fun CommunityScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                // 顶部操作栏
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
                        IconButton(onClick = { /* 创建帖子 */ }) {
                            Icon(
                                imageVector = Icons.Filled.Create,
                                contentDescription = "创建帖子"
                            )
                        }
                        IconButton(onClick = { /* 更多选项 */ }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "更多选项"
                            )
                        }
                    }
                }
                
                // 标签栏
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = it
        ) {
            items(mockPosts) {
                PostCard(post = it)
                Spacer(modifier = Modifier.height(16.dp))
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
    CommunityScreen()
}
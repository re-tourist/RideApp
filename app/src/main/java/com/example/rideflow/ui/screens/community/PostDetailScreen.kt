package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.model.Comment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetailScreen(navController: NavController, postId: Int) {
    val postData = remember(postId) {
        when (postId) {
            901 -> DemoPost("飓风骑行俱乐部", "10分钟前", "本周日将举行环湖拉练活动，请各位队员准时在北门集合！ #俱乐部活动", "[活动海报]", 32, 5)
            902 -> DemoPost("周末休闲骑", "2小时前", "上周的腐败骑行圆满结束，大家吃得开心吗？照片已上传相册。", "[聚餐合影]", 15, 8)
            903 -> DemoPost("山地越野小队", "1天前", "探索了一条新的林道，难度系数3星，欢迎老手来挑战。", "[林道照片]", 45, 12)
            else -> DemoPost("用户$postId", "刚刚", "这是示例动态内容。", "[图片]", 0, 0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "动态详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        val comments = remember(postId) { demoComments(postId) }
        var visibleCount by remember { mutableStateOf(10) }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(postData.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(postData.timeAgo, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(text = postData.content, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(color = Color(0xFFF5F5F5)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = postData.imagePlaceholder, color = Color.Gray)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "赞: ${postData.likes}", color = Color.Gray)
                    Text(text = "评论: ${comments.size}", color = Color.Gray)
                }
                Spacer(Modifier.height(12.dp))
                Text(text = "评论", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            items(comments.take(visibleCount)) { c ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(text = c.userName, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(8.dp))
                        Text(text = c.time, color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(text = c.content)
                }
                Divider()
            }
            item {
                if (visibleCount < comments.size) {
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { visibleCount = minOf(visibleCount + 5, comments.size) }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "加载更多评论")
                    }
                }
            }
        }
    }
}

private data class DemoPost(
    val userName: String,
    val timeAgo: String,
    val content: String,
    val imagePlaceholder: String,
    val likes: Int,
    val comments: Int
)

private fun demoComments(postId: Int): List<Comment> {
    val base = listOf(
        Comment(1, "山地老王", "太猛了，路线分享一下！", "10分钟前"),
        Comment(2, "城市骑手小李", "周末一起骑？", "20分钟前"),
        Comment(3, "硬核玩家", "爬坡感觉如何？", "1小时前"),
        Comment(4, "配件狂魔", "换了什么胎？", "1小时前"),
        Comment(5, "日落追光者", "风景太美了", "2小时前"),
        Comment(6, "用户A", "报名活动了没？", "昨天"),
        Comment(7, "骑行新手", "求带！", "昨天"),
        Comment(8, "百公里挑战者", "支持！", "昨天"),
        Comment(9, "夜骑爱好者", "夜骑走起", "2天前"),
        Comment(10, "圈圈", "赞！", "2天前"),
        Comment(11, "网友甲", "加油！", "3天前"),
        Comment(12, "网友乙", "nice", "3天前"),
        Comment(13, "网友丙", "厉害", "4天前"),
        Comment(14, "网友丁", "学习了", "4天前"),
        Comment(15, "网友戊", "关注了", "5天前")
    )
    return base.map { it.copy(id = it.id + postId) }
}

package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person

// 模拟数据类
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val imageRes: Int,
    val date: String,
    val views: Int
)

private val mockArticles = listOf(
    Article(
        id = 1,
        title = "骑车时别听歌，除非你...",
        author = "骑行达人",
        imageRes = R.drawable.ic_launcher_foreground,
        date = "2025-11-20",
        views = 2444
    ),
    Article(
        id = 2,
        title = "告别'耳内闷罐'！这款耳机，成了我的通勤与运动'全能搭子'",
        author = "骑行爱好者",
        imageRes = R.drawable.ic_launcher_foreground,
        date = "2025-11-20",
        views = 1876
    )
)

@Composable
fun DiscoverScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // 顶部广告横幅
        item {
            BannerSection()
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 功能分类导航
        item {
            CategorySection()
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // 文章列表
        items(mockArticles) {
            ArticleCard(article = it)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BannerSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.height(180.dp)) {
            // 这里应该是一个实际的广告图片
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(color = Color.Black.copy(alpha = 0.2f))
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "和美海岛-上海后花园",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = """奔跑吧，庙镇""" + "定向骑行线上赛",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { /* 报名按钮点击事件 */ },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(alignment = androidx.compose.ui.Alignment.Start),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text(text = "立即报名")
                }
            }
        }
    }
}

@Composable
fun CategorySection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CategoryItem(
            icon = Icons.Filled.Star,
            label = "赛事"
        )
        CategoryItem(
            icon = Icons.Filled.List,
            label = "路书"
        )
        CategoryItem(
            icon = Icons.Filled.Home,
            label = "活动"
        )
        CategoryItem(
            icon = Icons.Filled.Home,
            label = "俱乐部"
        )
        CategoryItem(
            icon = Icons.Filled.Person,
            label = "骑友"
        )
    }
}

@Composable
fun CategoryItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(56.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF007AFF)
                )
            }
        }
        Text(
            text = label,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = article.author,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Image(
                painter = painterResource(id = article.imageRes),
                contentDescription = article.title,
                modifier = Modifier.size(80.dp, 80.dp),
                contentScale = ContentScale.Crop
            )
        }
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = article.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${article.views} 阅读",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    DiscoverScreen()
}
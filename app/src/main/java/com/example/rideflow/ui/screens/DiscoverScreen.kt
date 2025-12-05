package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper

// 模拟数据类
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val imageUrl: String? = null,
    val date: String,
    val views: Int
)

private fun loadArticles(handler: Handler, onLoaded: (List<Article>) -> Unit) {
    Thread {
        val list = mutableListOf<Article>()
        DatabaseHelper.processQuery(
            "SELECT a.article_id, a.title, u.nickname, a.publish_date, a.views, a.image_url FROM articles a JOIN users u ON a.author_id = u.user_id ORDER BY a.publish_date DESC LIMIT 20"
        ) { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val author = rs.getString(3)
                val date = rs.getDate(4)?.toString() ?: ""
                val views = rs.getInt(5)
                val img = rs.getString(6) ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg"
                list.add(Article(id, title, author, img, date, views))
            }
            handler.post { onLoaded(list) }
            Unit
        }
    }.start()
}

@Composable
fun DiscoverScreen(navController: androidx.navigation.NavController, userId: String = "") {
    var subPage by remember { mutableStateOf(DiscoverSubPage.Main) }
    val handler = Handler(Looper.getMainLooper())
    var articles by remember { mutableStateOf<List<Article>>(emptyList()) }
    LaunchedEffect(Unit) { loadArticles(handler) { list -> articles = list } }
    when (subPage) {
        DiscoverSubPage.Main -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    BannerSection()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    CategorySection(
            onRouteBookClick = { subPage = DiscoverSubPage.RouteBook },
            onClubClick = { navController.navigate(com.example.rideflow.navigation.AppRoutes.CLUB_SCREEN) },
            onRiderClick = { subPage = DiscoverSubPage.Rider },
            navController = navController
        )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(articles) {
                    ArticleCard(article = it)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        // 活动页面现在是一个独立的导航路由，不在这里渲染
        DiscoverSubPage.RouteBook -> {
            RouteBookScreen(
                onBack = { subPage = DiscoverSubPage.Main },
                onOpenMyRouteBook = { subPage = DiscoverSubPage.MyRouteBook },
                userId = userId
            )
        }
        DiscoverSubPage.MyRouteBook -> {
            MyRouteBookScreen(onBack = { subPage = DiscoverSubPage.RouteBook }, userId = userId)
        }
        DiscoverSubPage.Race -> {
            // RaceScreen现在是一个独立的导航路由，不在这里渲染
        }
        DiscoverSubPage.CreateRace -> {
            CreateRaceScreen(onBack = { subPage = DiscoverSubPage.Race })
        }
        // 俱乐部子页面已改为独立路由
        DiscoverSubPage.Club -> {
            // 这个情况不会再发生，因为点击俱乐部类别会直接导航
            ClubScreen(onBack = { subPage = DiscoverSubPage.Main }, navController = navController)
        }
        // 骑友子页面
        DiscoverSubPage.Rider -> {
            RiderScreen(onBack = { subPage = DiscoverSubPage.Main }, userId = userId)
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
fun CategorySection(
    onRouteBookClick: () -> Unit,
    onClubClick: () -> Unit,
    onRiderClick: () -> Unit,
    navController: androidx.navigation.NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CategoryItem(
            icon = Icons.Filled.Star,
            label = "赛事",
            onClick = { navController.navigate(AppRoutes.RACE) }
        )
        CategoryItem(
            icon = Icons.Filled.List,
            label = "路书",
            onClick = onRouteBookClick
        )
        CategoryItem(
            icon = Icons.Filled.Home,
            label = "活动",
            onClick = { navController.navigate(AppRoutes.ACTIVITIES) }
        )
        CategoryItem(
            icon = Icons.Filled.Home,
            label = "俱乐部",
            onClick = onClubClick
        )
        CategoryItem(
            icon = Icons.Filled.Person,
            label = "骑友",
            onClick = onRiderClick
        )
    }
}

@Composable
fun CategoryItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(56.dp).clickable { onClick() },
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

enum class DiscoverSubPage { Main, RouteBook, MyRouteBook, Race, Club, Rider, CreateRace }

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
            coil.compose.AsyncImage(
                model = article.imageUrl ?: "https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg",
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
    DiscoverScreen(navController = androidx.navigation.compose.rememberNavController())
}

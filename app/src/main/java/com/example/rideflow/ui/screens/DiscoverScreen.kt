package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
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
object DiscoverNavigatorState { var openRouteBook: Boolean = false }
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
    LaunchedEffect(Unit) {
        if (DiscoverNavigatorState.openRouteBook) {
            subPage = DiscoverSubPage.RouteBook
            DiscoverNavigatorState.openRouteBook = false
        }
    }
    val handler = Handler(Looper.getMainLooper())
    var banners by remember { mutableStateOf<List<String>>(emptyList()) }
    var recommendedRaces by remember { mutableStateOf<List<Race>>(emptyList()) }
    var recommendedActivities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var recommendedClubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    LaunchedEffect(Unit) {
        banners = listOf(
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/22de9d074c1216b8b16d2bb448665a5f.jpg",
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/2f84ddd2eb35ad0e9cd155533388df8e.jpg",
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/4a3941c35bb891a6bc108405f498837b.jpg"
        )
        Thread { loadRecommendedRaces(handler) { list -> recommendedRaces = list } }.start()
        Thread { loadRecommendedActivities(handler) { list -> recommendedActivities = list } }.start()
        Thread { loadRecommendedClubs(handler) { list -> recommendedClubs = list } }.start()
    }
    when (subPage) {
        DiscoverSubPage.Main -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)
            ) {
                item {
                    PromoCarousel(images = banners)
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
                item {
                    Text(text = "推荐的赛事：", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
                        items(recommendedRaces) { race ->
                            RaceHorizontalCard(race = race) {
                                navController.navigate("${AppRoutes.RACE_DETAIL}/${race.id}")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(text = "推荐的活动：", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
                        items(recommendedActivities) { activity ->
                            ActivityHorizontalCard(activity = activity) {
                                navController.navigate("${AppRoutes.ACTIVITY_DETAIL}/${activity.id}")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(text = "推荐的俱乐部：", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
                        items(recommendedClubs) { club ->
                            ClubHorizontalCard(club = club) {
                                navController.navigate("club_detail/${club.id}")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }
            }
        }
        // 活动页面现在是一个独立的导航路由，不在这里渲染
        DiscoverSubPage.RouteBook -> {
            RouteBookScreen(
                onBack = { subPage = DiscoverSubPage.Main },
                onOpenMyRouteBook = { subPage = DiscoverSubPage.MyRouteBook },
                userId = userId,
                navController = navController
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
            RiderScreen(onBack = { subPage = DiscoverSubPage.Main }, userId = userId, navController = navController)
        }
    }
}

@Composable
fun PromoCarousel(images: List<String>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(images) { url ->
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .height(180.dp)
                    .padding(end = 12.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                coil.compose.AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
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

private fun loadRecommendedRaces(handler: Handler, onLoaded: (List<Race>) -> Unit) {
    Thread {
        val list = mutableListOf<Race>()
        DatabaseHelper.processQuery("SELECT event_id, title, event_date, location, event_type, is_open, cover_image_url FROM events ORDER BY event_date DESC LIMIT 30") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val date = rs.getTimestamp(3)?.toString() ?: ""
                val loc = rs.getString(4) ?: ""
                val type = rs.getString(5) ?: "娱乐赛"
                val open = rs.getBoolean(6)
                val coverUrl = rs.getString(7)
                val tags = mutableListOf<String>()
                DatabaseHelper.processQuery("SELECT tag_name FROM event_tags WHERE event_id = ?", listOf(id)) { trs ->
                    while (trs.next()) tags.add(trs.getString(1) ?: "")
                    Unit
                }
                val isRace = type in listOf("娱乐赛", "竞速赛") || tags.any { it in listOf("娱乐赛", "竞速赛") }
                if (isRace) {
                    list.add(Race(id, title, "时间：" + (if (date.isNotEmpty()) date.substring(0, 10) else "待定"), "地点：" + loc, if (tags.isEmpty()) listOf(type) else tags, R.drawable.ic_launcher_foreground, coverUrl, open, false))
                }
            }
            handler.post { onLoaded(list.take(10)) }
            Unit
        }
    }.start()
}

private fun loadRecommendedActivities(handler: Handler, onLoaded: (List<Activity>) -> Unit) {
    Thread {
        val list = mutableListOf<Activity>()
        DatabaseHelper.processQuery("SELECT event_id, title, event_date, location, event_type, is_open, cover_image_url FROM events ORDER BY event_date DESC LIMIT 30") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val date = rs.getTimestamp(3)?.toString() ?: ""
                val loc = rs.getString(4) ?: ""
                val type = rs.getString(5) ?: "周末活动"
                val open = rs.getBoolean(6)
                val coverUrl = rs.getString(7)
                val tags = mutableListOf<String>()
                DatabaseHelper.processQuery("SELECT tag_name FROM event_tags WHERE event_id = ?", listOf(id)) { trs ->
                    while (trs.next()) tags.add(trs.getString(1) ?: "")
                    Unit
                }
                val isActivity = tags.any { it in listOf("亲子活动", "公益活动", "周末活动") }
                if (isActivity) {
                    list.add(Activity(id, title, "时间：" + (if (date.isNotEmpty()) date.substring(0, 16) else "待定"), "地点：" + loc, if (tags.isEmpty()) listOf(type) else tags, R.drawable.ic_launcher_foreground, coverUrl, open, false))
                }
            }
            handler.post { onLoaded(list.take(10)) }
            Unit
        }
    }.start()
}

private fun loadRecommendedClubs(handler: Handler, onLoaded: (List<Club>) -> Unit) {
    Thread {
        val list = mutableListOf<Club>()
        DatabaseHelper.processQuery("SELECT club_id, name, city, logo_url, members_count, heat FROM clubs ORDER BY heat DESC LIMIT 20") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val name = rs.getString(2)
                val city = rs.getString(3) ?: ""
                val logo = rs.getString(4)
                val members = rs.getInt(5)
                val heat = rs.getInt(6)
                list.add(Club(id, name, city, members, heat, R.drawable.ic_launcher_foreground, logo))
            }
            handler.post { onLoaded(list.take(10)) }
            Unit
        }
    }.start()
}

@Composable
fun RaceHorizontalCard(race: Race, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(280.dp).height(220.dp).clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp)) {
                val url = race.imageUrl
                if (url != null) {
                    coil.compose.AsyncImage(model = url, contentDescription = race.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Image(painter = painterResource(id = race.imageRes), contentDescription = race.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                    if (race.isOpen) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))) { Text(text = "报名中") }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = race.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = race.date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ActivityHorizontalCard(activity: Activity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(280.dp).clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp)) {
                val url = activity.imageUrl
                if (url != null) {
                    coil.compose.AsyncImage(model = url, contentDescription = activity.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Image(painter = painterResource(id = activity.imageRes), contentDescription = activity.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
                    if (activity.isOpen) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))) { Text(text = "报名中") }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = activity.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = activity.date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ClubHorizontalCard(club: Club, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).height(120.dp).clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (club.logoUrl != null) {
                    coil.compose.AsyncImage(model = club.logoUrl, contentDescription = club.name, modifier = Modifier.size(56.dp), contentScale = ContentScale.Crop)
                } else {
                    Image(painter = painterResource(id = club.logoRes), contentDescription = club.name, modifier = Modifier.size(56.dp), contentScale = ContentScale.Crop)
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = club.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = club.city, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "成员 ${club.members}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(24.dp))
                Text(text = "热度 ${club.heat}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    DiscoverScreen(navController = androidx.navigation.compose.rememberNavController())
}

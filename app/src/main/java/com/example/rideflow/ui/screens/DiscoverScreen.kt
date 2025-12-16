package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import com.example.rideflow.cache.AppCache
import java.util.concurrent.TimeUnit


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

private suspend fun loadArticlesIO(): List<Article> {
    return withContext(Dispatchers.IO) {
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
            Unit
        }
        list
    }
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
    val context = LocalContext.current
    var banners by remember { mutableStateOf<List<String>>(emptyList()) }
    var recommendedRaces by remember { mutableStateOf<List<Race>>(emptyList()) }
    var recommendedActivities by remember { mutableStateOf<List<Activity>>(emptyList()) }
    var recommendedClubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    var isLoadingRaces by remember { mutableStateOf(true) }
    var isLoadingActivities by remember { mutableStateOf(true) }
    var isLoadingClubs by remember { mutableStateOf(true) }
    val pageStart = remember { System.currentTimeMillis() }
    LaunchedEffect(Unit) {
        banners = listOf(
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/22de9d074c1216b8b16d2bb448665a5f.jpg",
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/2f84ddd2eb35ad0e9cd155533388df8e.jpg",
            "https://rideapp.oss-cn-hangzhou.aliyuncs.com/recommend/4a3941c35bb891a6bc108405f498837b.jpg"
        )
        val cache = loadDiscoverCache(context)
        if (cache.first.isNotEmpty()) recommendedRaces = cache.first
        if (cache.second.isNotEmpty()) recommendedActivities = cache.second
        if (cache.third.isNotEmpty()) recommendedClubs = cache.third
        val races = loadRecommendedRacesIO()
        recommendedRaces = races
        isLoadingRaces = false
        val activities = loadRecommendedActivitiesIO()
        recommendedActivities = activities
        isLoadingActivities = false
        val clubs = loadRecommendedClubsIO()
        recommendedClubs = clubs
        isLoadingClubs = false
        saveDiscoverCache(context, races, activities, clubs)
        Log.d("Perf", "DiscoverScreen RequestEnd: ${System.currentTimeMillis() - pageStart} ms")
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
                        if (recommendedRaces.isEmpty() && isLoadingRaces) {
                            items(3) {
                                RaceCardSkeleton()
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        } else {
                            items(recommendedRaces) { race ->
                                RaceHorizontalCard(race = race) {
                                    navController.navigate("${AppRoutes.RACE_DETAIL}/${race.id}")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(text = "推荐的活动：", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
                        if (recommendedActivities.isEmpty() && isLoadingActivities) {
                            items(3) {
                                ActivityCardSkeleton()
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        } else {
                            items(recommendedActivities) { activity ->
                                ActivityHorizontalCard(activity = activity) {
                                    navController.navigate("${AppRoutes.ACTIVITY_DETAIL}/${activity.id}")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Text(text = "推荐的俱乐部：", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
                        if (recommendedClubs.isEmpty() && isLoadingClubs) {
                            items(3) {
                                ClubCardSkeleton()
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        } else {
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
        CategoryItemDrawable(
            imageRes = R.drawable.ic_discover_race,
            label = "赛事",
            onClick = { navController.navigate(AppRoutes.RACE) }
        )
        CategoryItemDrawable(
            imageRes = R.drawable.ic_discover_routebook,
            label = "路书",
            onClick = onRouteBookClick
        )
        CategoryItemDrawable(
            imageRes = R.drawable.ic_discover_activity,
            label = "活动",
            onClick = { navController.navigate(AppRoutes.ACTIVITIES) }
        )
        CategoryItemDrawable(
            imageRes = R.drawable.ic_discover_club,
            label = "俱乐部",
            onClick = onClubClick
        )
        CategoryItemDrawable(
            imageRes = R.drawable.ic_discover_rider,
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

@Composable
fun CategoryItemDrawable(imageRes: Int, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(72.dp).clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = label,
                    modifier = Modifier.size(44.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Text(
            text = label,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
fun CategoryItemUrl(iconUrl: String, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(56.dp).clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                coil.compose.AsyncImage(
                    model = iconUrl,
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
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

private suspend fun loadRecommendedRacesIO(): List<Race> {
    return withContext(Dispatchers.IO) {
        val list = mutableListOf<Race>()
        DatabaseHelper.processQuery("SELECT race_id, title, event_date, location, event_type, is_open, cover_image_url FROM races ORDER BY event_date DESC LIMIT 30") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val date = rs.getTimestamp(3)?.toString() ?: ""
                val loc = rs.getString(4) ?: ""
                val type = rs.getString(5) ?: "娱乐赛"
                val open = rs.getBoolean(6)
                val coverUrl = rs.getString(7)
                val tags = mutableListOf<String>()
                DatabaseHelper.processQuery("SELECT tag_name FROM race_tags WHERE race_id = ?", listOf(id)) { trs ->
                    while (trs.next()) tags.add(trs.getString(1) ?: "")
                    Unit
                }
                list.add(Race(id, title, "时间：" + (if (date.isNotEmpty()) date.substring(0, 10) else "待定"), "地点：" + loc, if (tags.isEmpty()) listOf(type) else tags, R.drawable.ic_launcher_foreground, coverUrl, open, false))
            }
            Unit
        }
        list.take(10)
    }
}

private suspend fun loadRecommendedActivitiesIO(): List<Activity> {
    return withContext(Dispatchers.IO) {
        val list = mutableListOf<Activity>()
        DatabaseHelper.processQuery("SELECT activity_id, title, event_date, location, event_type, is_open, cover_image_url FROM activities ORDER BY event_date DESC LIMIT 30") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val title = rs.getString(2)
                val date = rs.getTimestamp(3)?.toString() ?: ""
                val loc = rs.getString(4) ?: ""
                val type = rs.getString(5) ?: "周末活动"
                val open = rs.getBoolean(6)
                val coverUrl = rs.getString(7)
                val tags = mutableListOf<String>()
                DatabaseHelper.processQuery("SELECT tag_name FROM activity_tags WHERE activity_id = ?", listOf(id)) { trs ->
                    while (trs.next()) tags.add(trs.getString(1) ?: "")
                    Unit
                }
                list.add(Activity(id, title, "时间：" + (if (date.isNotEmpty()) date.substring(0, 16) else "待定"), "地点：" + loc, if (tags.isEmpty()) listOf(type) else tags, R.drawable.ic_launcher_foreground, coverUrl, open, false))
            }
            Unit
        }
        list.take(10)
    }
}

private suspend fun loadRecommendedClubsIO(): List<Club> {
    return withContext(Dispatchers.IO) {
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
            Unit
        }
        list.take(10)
    }
}

@Composable
fun RaceCardSkeleton() {
    Card(modifier = Modifier.width(280.dp).height(220.dp)) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f)))
            Column(modifier = Modifier.padding(12.dp)) {
                Box(modifier = Modifier.width(160.dp).height(16.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(120.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.4f)))
            }
        }
    }
}

@Composable
fun ActivityCardSkeleton() {
    Card(modifier = Modifier.width(280.dp).height(220.dp)) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f)))
            Column(modifier = Modifier.padding(12.dp)) {
                Box(modifier = Modifier.width(160.dp).height(16.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(120.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.4f)))
            }
        }
    }
}

@Composable
fun ClubCardSkeleton() {
    Card(modifier = Modifier.width(260.dp).height(120.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(56.dp).background(Color.LightGray.copy(alpha = 0.3f)))
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Box(modifier = Modifier.width(120.dp).height(16.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.4f)))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(60.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(24.dp))
                Box(modifier = Modifier.width(60.dp).height(12.dp).background(Color.LightGray.copy(alpha = 0.3f)))
            }
        }
    }
}

private fun saveDiscoverCache(context: android.content.Context, races: List<Race>, activities: List<Activity>, clubs: List<Club>) {
    val r = JSONArray()
    races.take(10).forEach { x ->
        val o = JSONObject()
        o.put("id", x.id)
        o.put("title", x.title)
        o.put("date", x.date)
        o.put("imageUrl", x.imageUrl ?: "")
        o.put("location", x.location)
        r.put(o)
    }
    val a = JSONArray()
    activities.take(10).forEach { x ->
        val o = JSONObject()
        o.put("id", x.id)
        o.put("title", x.title)
        o.put("date", x.date)
        o.put("imageUrl", x.imageUrl ?: "")
        o.put("location", x.location)
        a.put(o)
    }
    val c = JSONArray()
    clubs.take(10).forEach { x ->
        val o = JSONObject()
        o.put("id", x.id)
        o.put("name", x.name)
        o.put("city", x.city)
        o.put("logoUrl", x.logoUrl ?: "")
        o.put("members", x.members)
        o.put("heat", x.heat)
        c.put(o)
    }
    AppCache.put(context, "discover_races", r.toString(), java.util.concurrent.TimeUnit.HOURS.toMillis(2))
    AppCache.put(context, "discover_activities", a.toString(), java.util.concurrent.TimeUnit.HOURS.toMillis(2))
    AppCache.put(context, "discover_clubs", c.toString(), java.util.concurrent.TimeUnit.HOURS.toMillis(2))
}

private fun loadDiscoverCache(context: android.content.Context): Triple<List<Race>, List<Activity>, List<Club>> {
    val rStr = AppCache.get(context, "discover_races")
    val aStr = AppCache.get(context, "discover_activities")
    val cStr = AppCache.get(context, "discover_clubs")
    val r = if (rStr != null) {
        val arr = JSONArray(rStr)
        (0 until arr.length()).mapNotNull {
            val o = arr.optJSONObject(it)
            if (o == null) null else try {
                Race(o.getInt("id"), o.getString("title"), o.getString("date"), o.getString("location"), emptyList(), R.drawable.ic_launcher_foreground, o.optString("imageUrl").ifEmpty { null }, false, false)
            } catch (e: Exception) { null }
        }
    } else emptyList()
    val a = if (aStr != null) {
        val arr = JSONArray(aStr)
        (0 until arr.length()).mapNotNull {
            val o = arr.optJSONObject(it)
            if (o == null) null else try {
                Activity(o.getInt("id"), o.getString("title"), o.getString("date"), o.getString("location"), emptyList(), R.drawable.ic_launcher_foreground, o.optString("imageUrl").ifEmpty { null }, false, false)
            } catch (e: Exception) { null }
        }
    } else emptyList()
    val c = if (cStr != null) {
        val arr = JSONArray(cStr)
        (0 until arr.length()).mapNotNull {
            val o = arr.optJSONObject(it)
            if (o == null) null else try {
                Club(o.getInt("id"), o.getString("name"), o.getString("city"), o.getInt("members"), o.getInt("heat"), R.drawable.ic_launcher_foreground, o.optString("logoUrl").ifEmpty { null })
            } catch (e: Exception) { null }
        }
    } else emptyList()
    return Triple(r, a, c)
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

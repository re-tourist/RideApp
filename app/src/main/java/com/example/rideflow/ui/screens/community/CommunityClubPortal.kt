package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable // 引入 rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.PostCard
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper

data class SimpleClubInfo(
    val id: Int,
    val name: String,
    val intro: String,
    val avatarPlaceholder: String,
    val logoUrl: String? = null
)

@Composable
fun CommunityClubPortalScreen(
    navController: NavController,
    allPosts: List<Post>,
    onLikeToggle: (Int, Boolean) -> Unit,
    onDislikeToggle: (Int, Boolean) -> Unit
) {
    // 关键修改：使用 rememberSaveable 保存标签页状态
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("俱乐部动态", "俱乐部列表")

    // 俱乐部列表（来自数据库）
    var clubs by remember { mutableStateOf<List<SimpleClubInfo>>(emptyList()) }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(Unit) {
        Thread {
            val list = mutableListOf<SimpleClubInfo>()
            DatabaseHelper.processQuery(
                "SELECT club_id, name, city, logo_url, members_count, heat FROM clubs ORDER BY heat DESC LIMIT 100"
            ) { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val name = rs.getString(2) ?: "俱乐部"
                    val city = rs.getString(3) ?: ""
                    val logo = rs.getString(4)
                    val members = rs.getInt(5)
                    val heat = rs.getInt(6)
                    val intro = listOf(city.takeIf { it.isNotEmpty() }, "成员$members", "热度$heat").filterNotNull().joinToString(" · ")
                    list.add(SimpleClubInfo(id, name, intro, (name.firstOrNull() ?: '俱').toString(), logo))
                }
                handler.post { clubs = list }
                Unit
            }
        }.start()
    }

    // 过滤逻辑：显示所有由俱乐部发布的动态
    val clubPosts = allPosts.filter { post -> post.authorType == "club" }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Black,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> ClubDynamicsTab(posts = clubPosts, navController = navController, onLikeToggle = onLikeToggle, onDislikeToggle = onDislikeToggle)
            1 -> ClubListTab(clubs = clubs, onClubClick = { clubId ->
                navController.navigate("${com.example.rideflow.navigation.AppRoutes.COMMUNITY_CLUB_DETAIL}/$clubId")
            })
        }
    }
}

@Composable
fun ClubDynamicsTab(
    posts: List<Post>,
    navController: NavController,
    onLikeToggle: (Int, Boolean) -> Unit,
    onDislikeToggle: (Int, Boolean) -> Unit
) {
    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无俱乐部动态", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isFollowing = true,
                    onFollowToggle = { _, _ -> },
                    showFollowButton = false, // 隐藏关注按钮
                    onAvatarClick = { targetId, _ -> navController.navigate("${com.example.rideflow.navigation.AppRoutes.COMMUNITY_CLUB_DETAIL}/$targetId") },
                    onPostClick = { pid -> navController.navigate("${com.example.rideflow.navigation.AppRoutes.POST_DETAIL}/$pid") },
                    onLikeToggle = onLikeToggle,
                    onDislikeToggle = onDislikeToggle
                )
                Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
            }
        }
    }
}

@Composable
fun ClubListTab(clubs: List<SimpleClubInfo>, onClubClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(clubs) { club ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClubClick(club.id) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (!club.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = club.logoUrl,
                            contentDescription = club.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = club.avatarPlaceholder,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = club.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = club.intro,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF0F0F0))
        }
    }
}

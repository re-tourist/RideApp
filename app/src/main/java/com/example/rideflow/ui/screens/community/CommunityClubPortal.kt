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

data class SimpleClubInfo(
    val id: Int,
    val name: String,
    val intro: String,
    val avatarPlaceholder: String
)

@Composable
fun CommunityClubPortalScreen(
    navController: NavController,
    allPosts: List<Post>
) {
    // 关键修改：使用 rememberSaveable 保存标签页状态
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("俱乐部动态", "俱乐部列表")

    // 我加入的俱乐部列表
    val myClubs = remember {
        listOf(
            SimpleClubInfo(1, "飓风骑行俱乐部", "风在耳边，路在脚下", "飓"),
            SimpleClubInfo(2, "周末休闲骑", "主打吃喝玩乐", "周"),
            SimpleClubInfo(3, "山地越野小队", "不走寻常路", "山")
        )
    }

    // 过滤逻辑：只显示我加入的俱乐部的动态
    val clubNames = myClubs.map { it.name }.toSet()
    val clubPosts = allPosts.filter { post ->
        clubNames.any { clubName -> post.userName.contains(clubName) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = Color.Red,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.Red
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
                            color = if (selectedTab == index) Color.Red else Color.Black,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> ClubDynamicsTab(posts = clubPosts)
            1 -> ClubListTab(clubs = myClubs, onClubClick = { clubId ->
                navController.navigate("community_club_detail/$clubId")
            })
        }
    }
}

@Composable
fun ClubDynamicsTab(posts: List<Post>) {
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
                    showFollowButton = false // 隐藏关注按钮
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
                    Text(
                        text = club.avatarPlaceholder,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
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
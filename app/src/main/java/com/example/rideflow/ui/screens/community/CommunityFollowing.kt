package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.PostCard

@Composable
fun CommunityFollowingScreen(
    allPosts: List<Post>,
    followingUserIds: Set<Int>,
    onFollowToggle: (Int, Boolean) -> Unit,
    onAvatarClick: (Int) -> Unit // 新增参数
) {
    // 过滤出关注用户的帖子
    val posts = allPosts.filter { followingUserIds.contains(it.userId) }

    if (posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("你还没有关注任何动态，去热门动态看看吧！", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(posts, key = { it.id }) { post ->
            // 在关注列表中，显示为已关注 (isFollowing = true)
            PostCard(
                post = post,
                isFollowing = true,
                onFollowToggle = onFollowToggle,
                onAvatarClick = onAvatarClick // 传递回调
            )
            Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
        }
    }
}
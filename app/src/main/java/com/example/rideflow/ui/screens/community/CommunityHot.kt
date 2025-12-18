package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.PostCard

@Composable
fun CommunityHotScreen(
    allPosts: List<Post>,
    followingUserIds: Set<Int>,
    onFollowToggle: (Int, Boolean) -> Unit,
    onAvatarClick: (Int, String) -> Unit,
    onPostClick: (Int) -> Unit,
    onLikeToggle: (Int, Boolean) -> Unit,
    onDislikeToggle: (Int, Boolean) -> Unit,
    hasMore: Boolean = false,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(allPosts, key = { it.id }) { post ->
            // 判断当前帖子作者是否已被关注
            val isFollowing = followingUserIds.contains(post.userId)

            PostCard(
                post = post,
                isFollowing = isFollowing,
                onFollowToggle = onFollowToggle,
                showFollowButton = post.authorType == "user",
                onAvatarClick = onAvatarClick,
                onPostClick = onPostClick,
                onLikeToggle = onLikeToggle,
                onDislikeToggle = onDislikeToggle
            )
            Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
        }
        item {
            com.example.rideflow.ui.components.LoadMoreFooter(hasMore = hasMore, isLoadingMore = isLoadingMore, onLoadMore = onLoadMore)
        }
    }
}

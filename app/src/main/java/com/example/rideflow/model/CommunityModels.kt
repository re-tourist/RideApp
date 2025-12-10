package com.example.rideflow.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// 动态帖子模型
data class Post(
    val id: Int,
    val userId: Int,
    val userAvatar: ImageVector = Icons.Default.Person,
    val userName: String,
    val timeAgo: String,
    val content: String,
    val imagePlaceholder: String,
    val likes: Int,
    val comments: Int,
    val initialIsLiked: Boolean = false
)

// 交易物品模型
data class TradeItem(
    val id: Int,
    val isOfficial: Boolean,
    val title: String,
    val description: String,
    val price: String,
    val imagePlaceholder: String,
    val externalUrl: String,
    val sellerName: String? = null,
    val isPublished: Boolean = true
)

// 底部导航项模型
data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

// 评论模型
data class Comment(
    val id: Int,
    val userName: String,
    val content: String,
    val time: String
)

// 好友模型
data class Friend(
    val id: Int,
    val name: String
)

// 俱乐部成员模型
data class ClubMember(
    val id: Int,
    val name: String,
    var role: String,
    val avatarPlaceholder: String = "[头像]",
    val info: String = "这是用户详细信息示例。"
)

// 俱乐部申请者模型
data class Applicant(
    val id: Int,
    val name: String,
    val reason: String = "想加入俱乐部，一起骑行交流"
)

// 排行榜项模型
data class RankingItem(
    val rank: Int,
    val name: String,
    val distanceKm: Double
)
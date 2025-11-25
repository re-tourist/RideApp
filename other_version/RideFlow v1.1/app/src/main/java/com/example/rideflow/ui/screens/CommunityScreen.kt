package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rideflow.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp

// 模拟数据类 - 添加更多字段
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatar: Int,
    val authorBadge: String?,
    val content: String,
    val images: List<Int>,
    val date: String,
    val comments: Int,
    val likes: Int,
    val isPinned: Boolean = false,
    val category: String = "社区动态",
    val isHot: Boolean = false,
    val isFollowed: Boolean = false,
    val isClubLeader: Boolean = false,
    val eventParticipants: Int = 0,
    val isOfficial: Boolean = false,
    val isSecondHand: Boolean = false,
    val price: String? = null
)

// 俱乐部成员数据类
data class ClubMember(
    val id: Int,
    val name: String,
    val avatar: Int,
    val role: String, // "管理者", "成员"
    val isOnline: Boolean,
    val joinDate: String,
    val rideDistance: Double,
    val activitiesCount: Int
)

private data class TabItem(val title: String, val category: String)

private val tabs = listOf(
    TabItem("社区动态", "社区动态"),
    TabItem("俱乐部", "俱乐部"),
    TabItem("线下活动", "线下活动"),
    TabItem("社区交易", "社区交易"),
    TabItem("俱乐部管理", "俱乐部管理") // 新增俱乐部管理标签
)

// 模拟俱乐部成员数据
private val mockClubMembers = listOf(
    ClubMember(
        id = 1,
        name = "张部长",
        avatar = R.drawable.ic_launcher_foreground,
        role = "管理者",
        isOnline = true,
        joinDate = "2024-01-15",
        rideDistance = 2850.5,
        activitiesCount = 45
    ),
    ClubMember(
        id = 2,
        name = "李副部",
        avatar = R.drawable.ic_launcher_foreground,
        role = "管理者",
        isOnline = false,
        joinDate = "2024-02-20",
        rideDistance = 1980.2,
        activitiesCount = 32
    ),
    ClubMember(
        id = 3,
        name = "骑行达人小明",
        avatar = R.drawable.ic_launcher_foreground,
        role = "成员",
        isOnline = true,
        joinDate = "2024-03-10",
        rideDistance = 1250.8,
        activitiesCount = 28
    ),
    ClubMember(
        id = 4,
        name = "山地车爱好者",
        avatar = R.drawable.ic_launcher_foreground,
        role = "成员",
        isOnline = true,
        joinDate = "2024-04-05",
        rideDistance = 980.3,
        activitiesCount = 18
    ),
    ClubMember(
        id = 5,
        name = "周末骑手",
        avatar = R.drawable.ic_launcher_foreground,
        role = "成员",
        isOnline = false,
        joinDate = "2024-05-12",
        rideDistance = 650.7,
        activitiesCount = 12
    ),
    ClubMember(
        id = 6,
        name = "夜骑小王子",
        avatar = R.drawable.ic_launcher_foreground,
        role = "成员",
        isOnline = true,
        joinDate = "2024-06-08",
        rideDistance = 890.4,
        activitiesCount = 15
    ),
    ClubMember(
        id = 7,
        name = "公路车新手",
        avatar = R.drawable.ic_launcher_foreground,
        role = "成员",
        isOnline = false,
        joinDate = "2024-07-01",
        rideDistance = 320.1,
        activitiesCount = 8
    )
)

// 扩展模拟数据，为每个分类创建不同的帖子
private val mockPosts = listOf(
    // 社区动态分类的帖子
    Post(
        id = 1,
        authorName = "热门推荐",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "热门",
        content = "【热门内容】环青海湖骑行攻略大公开！最美路线、住宿推荐、装备清单全在这里！",
        images = List(3) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 18:30",
        comments = 156,
        likes = 892,
        category = "社区动态",
        isHot = true
    ),
    Post(
        id = 2,
        authorName = "骑行达人小明",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "已关注",
        content = "今天完成了100公里挑战！沿着海岸线骑行，风景真的太美了！分享一些沿途的照片～",
        images = List(4) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 16:45",
        comments = 34,
        likes = 267,
        category = "社区动态",
        isFollowed = true
    ),
    Post(
        id = 3,
        authorName = "山地车爱好者",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = null,
        content = "周末去山里越野，发现了一条超级刺激的下坡路线！有喜欢越野的朋友吗？",
        images = List(2) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 14:20",
        comments = 23,
        likes = 145,
        category = "社区动态"
    ),

    // 俱乐部分类的帖子
    Post(
        id = 4,
        authorName = "北京骑行俱乐部",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方俱乐部",
        content = "本周六组织西山骑行活动，全程约50公里，难度适中，欢迎新老会员参加！",
        images = List(1) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 17:00",
        comments = 28,
        likes = 89,
        category = "俱乐部"
    ),
    Post(
        id = 5,
        authorName = "张部长",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "俱乐部部长",
        content = "【意见征求】关于明年俱乐部会费调整方案，请大家投票表决！",
        images = emptyList(),
        date = "2025-11-21 10:30",
        comments = 45,
        likes = 23,
        category = "俱乐部",
        isClubLeader = true
    ),
    Post(
        id = 6,
        authorName = "夜骑小分队",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "兴趣小组",
        content = "每周三晚8点，长安街夜骑活动继续！装备好车灯，注意安全～",
        images = List(1) { R.drawable.ic_launcher_foreground },
        date = "2025-11-20 22-15",
        comments = 19,
        likes = 67,
        category = "俱乐部"
    ),

    // 线下活动分类的帖子
    Post(
        id = 7,
        authorName = "城市骑行节",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方活动",
        content = "第三届城市骑行节开始报名啦！12月5日在奥林匹克公园举行，设有多个组别，奖品丰厚！",
        images = List(3) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 15:45",
        comments = 89,
        likes = 234,
        category = "线下活动",
        eventParticipants = 156
    ),
    Post(
        id = 8,
        authorName = "周末骑行营",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "培训活动",
        content = "新手骑行训练营开课！专业教练指导，学习正确的骑行姿势和安全知识",
        images = List(2) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 11:20",
        comments = 34,
        likes = 78,
        category = "线下活动",
        eventParticipants = 42
    ),
    Post(
        id = 9,
        authorName = "慈善骑行",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "公益活动",
        content = "为山区儿童募捐的慈善骑行活动，每完成1公里捐赠10元，期待您的参与！",
        images = List(1) { R.drawable.ic_launcher_foreground },
        date = "2025-11-20 19:30",
        comments = 56,
        likes = 189,
        category = "线下活动",
        eventParticipants = 89
    ),

    // 社区交易分类的帖子
    Post(
        id = 10,
        authorName = "捷安特官方店",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方认证",
        content = "新款Defy Advanced Pro 0上市！碳纤维车架，电子变速，现货发售！",
        images = List(4) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 16:00",
        comments = 23,
        likes = 45,
        category = "社区交易",
        isOfficial = true,
        price = "¥12,800"
    ),
    Post(
        id = 11,
        authorName = "个人卖家",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "二手交易",
        content = "转让一辆9成新美利达挑战者300，去年购买，骑行不到500公里，因升级换车转让",
        images = List(3) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 13:45",
        comments = 18,
        likes = 32,
        category = "社区交易",
        isSecondHand = true,
        price = "¥1,800"
    ),
    Post(
        id = 12,
        authorName = "装备小店",
        authorAvatar = R.drawable.ic_launcher_foreground,
        authorBadge = "官方认证",
        content = "Giro头盔特价促销！原价680，现价450，多色可选，正品保证！",
        images = List(2) { R.drawable.ic_launcher_foreground },
        date = "2025-11-21 09:30",
        comments = 12,
        likes = 28,
        category = "社区交易",
        isOfficial = true,
        price = "¥450"
    )
)

@Composable
fun CommunityScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var showPostDialog by remember { mutableStateOf(false) }
    var showClubDialog by remember { mutableStateOf(false) }
    var showCreateEventDialog by remember { mutableStateOf(false) } // 新增：发起活动对话框状态
    var showMemberActionDialog by remember { mutableStateOf(false) } // 新增：成员操作对话框状态
    var selectedMember by remember { mutableStateOf<ClubMember?>(null) } // 新增：选中的成员

    // 根据选中的标签筛选帖子
    val filteredPosts = remember(selectedTab) {
        if (selectedTab == 4) { // 俱乐部管理标签
            emptyList()
        } else {
            val selectedCategory = tabs[selectedTab].category
            mockPosts.filter { it.category == selectedCategory }
        }
    }

    // 处理成员点击
    fun onMemberClick(member: ClubMember) {
        selectedMember = member
        showMemberActionDialog = true
    }

    // 发布动态对话框
    if (showPostDialog) {
        PostDialog(onDismiss = { showPostDialog = false })
    }

    // 加入俱乐部对话框
    if (showClubDialog) {
        ClubDialog(onDismiss = { showClubDialog = false })
    }

    // 发起活动对话框
    if (showCreateEventDialog) {
        CreateEventDialog(onDismiss = { showCreateEventDialog = false })
    }

    // 成员操作对话框
    if (showMemberActionDialog && selectedMember != null) {
        MemberActionDialog(
            member = selectedMember!!,
            onDismiss = {
                showMemberActionDialog = false
                selectedMember = null
            },
            onViewDetails = { /* 查看详细信息 */ },
            onAppointManager = { /* 任命管理者 */ },
            onRemoveManager = { /* 移除管理者 */ },
            onKickOut = { /* 踢出俱乐部 */ }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                // 顶部操作栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "社区",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        // 根据当前选中的标签显示不同的图标
                        when (selectedTab) {
                            0 -> { // 社区动态 - 编辑图标
                                IconButton(onClick = { showPostDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Create,
                                        contentDescription = "发布动态"
                                    )
                                }
                            }
                            1 -> { // 俱乐部 - 加号图标
                                IconButton(onClick = { showClubDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "加入俱乐部"
                                    )
                                }
                            }
                            2 -> { // 线下活动 - 发起活动按钮
                                Button(
                                    onClick = { showCreateEventDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF007AFF)
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add, // 使用 Add 图标替代 Event
                                        contentDescription = "发起活动",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("发起活动", fontSize = 14.sp)
                                }
                            }
                            4 -> { // 俱乐部管理 - 管理图标
                                IconButton(onClick = { /* 管理设置 */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Star, // 使用 Star 图标替代 AdminPanelSettings
                                        contentDescription = "俱乐部设置"
                                    )
                                }
                            }
                            else -> { // 其他标签 - 更多选项
                                IconButton(onClick = { /* 更多选项 */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "更多选项"
                                    )
                                }
                            }
                        }
                    }
                }

                // 标签栏
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF007AFF),
                    divider = { Divider() }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            text = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            4 -> { // 俱乐部管理页面
                ClubManagementScreen(
                    members = mockClubMembers,
                    onMemberClick = ::onMemberClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> { // 其他标签页
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    if (filteredPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无帖子",
                                    fontSize = 18.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        items(filteredPosts) { post ->
                            when (post.category) {
                                "社区动态" -> CommunityPostCard(post = post)
                                "俱乐部" -> ClubPostCard(post = post)
                                "线下活动" -> EventPostCard(post = post)
                                "社区交易" -> TradePostCard(post = post)
                                else -> PostCard(post = post)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// 俱乐部管理页面
@Composable
fun ClubManagementScreen(
    members: List<ClubMember>,
    onMemberClick: (ClubMember) -> Unit,
    modifier: Modifier = Modifier
) {
    // 排序成员：先按角色（管理者在前），再按在线状态（在线在前）
    val sortedMembers = remember(members) {
        members.sortedWith(
            compareByDescending<ClubMember> { it.role == "管理者" }
                .thenByDescending { it.isOnline }
                .thenBy { it.name }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 统计信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = members.size.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007AFF)
                    )
                    Text(
                        text = "总成员",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = members.count { it.role == "管理者" }.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "管理者",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = members.count { it.isOnline }.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        text = "在线",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // 成员列表
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(sortedMembers) { member ->
                ClubMemberItem(
                    member = member,
                    onClick = { onMemberClick(member) }
                )
                Divider()
            }
        }
    }
}

// 俱乐部成员项
@Composable
fun ClubMemberItem(
    member: ClubMember,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier.size(50.dp)
        ) {
            Image(
                painter = painterResource(id = member.avatar),
                contentDescription = member.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            // 在线状态指示器
            if (member.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 成员信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = member.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                // 角色标签
                Badge(
                    containerColor = when (member.role) {
                        "管理者" -> Color(0xFFFF6B35)
                        else -> Color(0xFF007AFF)
                    },
                    contentColor = Color.White
                ) {
                    Text(
                        text = member.role,
                        fontSize = 10.sp
                    )
                }
            }
            Text(
                text = "加入时间: ${member.joinDate}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "骑行里程: ${member.rideDistance}km | 活动次数: ${member.activitiesCount}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 在线状态文字
        Text(
            text = if (member.isOnline) "在线" else "离线",
            fontSize = 12.sp,
            color = if (member.isOnline) Color(0xFF4CAF50) else Color.Gray
        )
    }
}

// 成员操作对话框
@Composable
fun MemberActionDialog(
    member: ClubMember,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit,
    onAppointManager: () -> Unit,
    onRemoveManager: () -> Unit,
    onKickOut: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // 成员信息摘要
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = member.avatar),
                        contentDescription = member.name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = member.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${member.role} • ${if (member.isOnline) "在线" else "离线"}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 操作按钮
                ActionButton(
                    icon = Icons.Filled.Info, // 使用 Info 图标替代 Visibility
                    text = "查看详细信息",
                    onClick = {
                        onViewDetails()
                        onDismiss()
                    }
                )

                ActionButton(
                    icon = Icons.Filled.Star, // 使用 Star 图标替代 AdminPanelSettings
                    text = "任命管理者",
                    onClick = {
                        onAppointManager()
                        onDismiss()
                    },
                    enabled = member.role != "管理者"
                )

                ActionButton(
                    icon = Icons.Filled.Person, // 使用 Person 图标替代 PersonRemove
                    text = "移除管理者",
                    onClick = {
                        onRemoveManager()
                        onDismiss()
                    },
                    enabled = member.role == "管理者"
                )

                ActionButton(
                    icon = Icons.Filled.ExitToApp,
                    text = "踢出俱乐部",
                    onClick = {
                        onKickOut()
                        onDismiss()
                    },
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 取消按钮
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("取消", fontSize = 16.sp)
                }
            }
        }
    }
}

// 操作按钮组件
@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF007AFF),
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp)
    }
}

// 发起活动对话框
@Composable
fun CreateEventDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "发起活动",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 活动内容输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("请输入活动内容描述...") },
                    label = { Text("活动内容") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 图文上传区域
                OutlinedButton(
                    onClick = { /* 上传图片 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "上传图片",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("上传活动图片")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 定位输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请输入活动地点...") },
                    label = { Text("活动定位") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 可见人群输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("例如：所有人、仅会员、仅好友...") },
                    label = { Text("可见人群") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 可参加人数输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请输入可参加的最大人数...") },
                    label = { Text("可参加人数") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 截止报名时间输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请选择截止报名时间...") },
                    label = { Text("截止报名时间") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("取消", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onDismiss, // 点击后关闭对话框
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                    ) {
                        Text("发布活动", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// 发布动态对话框
@Composable
fun PostDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "发布动态",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 文案输入框
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("分享你的骑行故事...") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 功能选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* 添加图片 */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "添加图片",
                            modifier = Modifier.size(32.dp)
                        )
                        Text("添加图片", fontSize = 12.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* 添加骑行记录 */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "骑行记录",
                            modifier = Modifier.size(32.dp)
                        )
                        Text("骑行记录", fontSize = 12.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* 添加位置 */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "添加位置",
                            modifier = Modifier.size(32.dp)
                        )
                        Text("添加位置", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 发布按钮
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("发布", fontSize = 16.sp)
                }
            }
        }
    }
}

// 加入俱乐部对话框
@Composable
fun ClubDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "加入俱乐部",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 俱乐部列表
                val clubs = listOf(
                    "北京骑行俱乐部" to "2000+ 成员",
                    "夜骑小分队" to "500+ 成员",
                    "山地车爱好者联盟" to "1200+ 成员",
                    "公路车竞技队" to "300+ 成员",
                    "周末休闲骑" to "800+ 成员"
                )

                LazyColumn {
                    items(clubs) { (name, members) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clickable { /* 加入俱乐部 */ },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = name, fontWeight = FontWeight.Medium)
                                Text(text = members, fontSize = 12.sp, color = Color.Gray)
                            }
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "加入",
                                tint = Color(0xFF007AFF)
                            )
                        }
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 取消按钮
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("取消", fontSize = 16.sp)
                }
            }
        }
    }
}

// 社区动态帖子卡片
@Composable
fun CommunityPostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatar),
                    contentDescription = post.authorName,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (post.authorBadge != null) {
                            Badge(
                                modifier = Modifier.padding(start = 4.dp),
                                containerColor = when {
                                    post.isHot -> Color.Red
                                    post.isFollowed -> Color(0xFF007AFF)
                                    else -> Color.Yellow
                                },
                                contentColor = Color.White
                            ) {
                                Text(text = post.authorBadge, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(text = post.date, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 帖子内容
            Text(
                text = post.content,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // 帖子图片
            if (post.images.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    post.images.take(3).forEachIndexed { index, image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Post image $index",
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(end = if (index < 2) 8.dp else 0.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // 操作栏
            PostActions(post = post)
        }
    }
}

// 俱乐部帖子卡片
@Composable
fun ClubPostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatar),
                    contentDescription = post.authorName,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (post.authorBadge != null) {
                            Badge(
                                modifier = Modifier.padding(start = 4.dp),
                                containerColor = when {
                                    post.isClubLeader -> Color(0xFFFF6B35)
                                    else -> Color(0xFF4CAF50)
                                },
                                contentColor = Color.White
                            ) {
                                Text(text = post.authorBadge, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(text = post.date, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 帖子内容
            Text(
                text = post.content,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // 帖子图片
            if (post.images.isNotEmpty()) {
                Image(
                    painter = painterResource(id = post.images.first()),
                    contentDescription = "Club image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 12.dp),
                    contentScale = ContentScale.Crop
                )
            }

            PostActions(post = post)
        }
    }
}

// 线下活动帖子卡片
@Composable
fun EventPostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatar),
                    contentDescription = post.authorName,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (post.authorBadge != null) {
                            Badge(
                                modifier = Modifier.padding(start = 4.dp),
                                containerColor = Color(0xFF9C27B0),
                                contentColor = Color.White
                            ) {
                                Text(text = post.authorBadge, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(text = post.date, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 帖子内容
            Text(
                text = post.content,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // 帖子图片
            if (post.images.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    post.images.forEachIndexed { index, image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Event image $index",
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .padding(end = if (index < post.images.size - 1) 8.dp else 0.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // 活动参与人数
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已报名人数",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${post.eventParticipants}人",
                    color = Color(0xFF007AFF),
                    fontWeight = FontWeight.Bold
                )
            }

            PostActions(post = post)
        }
    }
}

// 社区交易帖子卡片
@Composable
fun TradePostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 作者信息
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatar),
                    contentDescription = post.authorName,
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (post.authorBadge != null) {
                            Badge(
                                modifier = Modifier.padding(start = 4.dp),
                                containerColor = when {
                                    post.isOfficial -> Color(0xFF4CAF50)
                                    post.isSecondHand -> Color(0xFFFF9800)
                                    else -> Color.Gray
                                },
                                contentColor = Color.White
                            ) {
                                Text(text = post.authorBadge, fontSize = 10.sp)
                            }
                        }
                    }
                    Text(text = post.date, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 帖子内容
            Text(
                text = post.content,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // 帖子图片
            if (post.images.isNotEmpty()) {
                Row(modifier = Modifier.padding(top = 12.dp)) {
                    post.images.forEachIndexed { index, image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = "Product image $index",
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .padding(end = if (index < post.images.size - 1) 8.dp else 0.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // 价格信息
            post.price?.let { price ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "价格",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = price,
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            PostActions(post = post)
        }
    }
}

// 通用帖子操作栏
@Composable
fun PostActions(post: Post) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            IconButton(onClick = { /* 评论 */ }) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "评论"
                )
            }
            Text(text = post.comments.toString())
        }
        Row {
            IconButton(onClick = { /* 点赞 */ }) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Like"
                )
            }
            Text(text = post.likes.toString())
        }
        Row {
            IconButton(onClick = { /* 分享 */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
        }

        // 特殊按钮
        when (post.category) {
            "线下活动" -> {
                Button(
                    onClick = { /* 报名活动 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "立即报名", fontSize = 12.sp)
                }
            }
            "社区交易" -> {
                Button(
                    onClick = { /* 购买 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "立即购买", fontSize = 12.sp)
                }
            }
            else -> {
                if (post.isPinned) {
                    Button(
                        onClick = { /* 置顶操作 */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "置顶", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// 通用帖子卡片（备用）
@Composable
fun PostCard(post: Post) {
    CommunityPostCard(post = post)
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    CommunityScreen()
}

@Preview(showBackground = true)
@Composable
fun CreateEventDialogPreview() {
    CreateEventDialog(onDismiss = {})
}

@Preview(showBackground = true)
@Composable
fun ClubManagementScreenPreview() {
    ClubManagementScreen(
        members = mockClubMembers,
        onMemberClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun MemberActionDialogPreview() {
    MemberActionDialog(
        member = mockClubMembers[0],
        onDismiss = { },
        onViewDetails = { },
        onAppointManager = { },
        onRemoveManager = { },
        onKickOut = { }
    )
}
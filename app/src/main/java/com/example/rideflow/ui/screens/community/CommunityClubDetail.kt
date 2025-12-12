package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.os.Handler
import android.os.Looper
import com.example.rideflow.backend.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.rideflow.R
import com.example.rideflow.model.Post
import com.example.rideflow.ui.components.PostCard

// UI显示用的轻量级俱乐部信息
data class UIClubInfo(
    val name: String,
    val level: Int,
    val heat: String,
    val rankTag: String,
    val avatarInitial: String,
    val themeColor: Color,
    val description: String, // 简短描述用于弹窗
    val logoUrl: String? = null
)

@Composable
fun CommunityClubDetailScreen(
    navController: NavController,
    clubId: Int
) {
    // 1. 俱乐部信息与动态列表
    var clubInfo by remember(clubId) { mutableStateOf<UIClubInfo?>(null) }
    var clubPosts by remember(clubId) { mutableStateOf<List<com.example.rideflow.model.Post>>(emptyList()) }
    val handler = Handler(Looper.getMainLooper())

    LaunchedEffect(clubId) {
        // 加载俱乐部基本信息
        Thread {
            DatabaseHelper.processQuery(
                "SELECT name, city, logo_url, members_count, heat FROM clubs WHERE club_id = ?",
                listOf(clubId)
            ) { rs ->
                if (rs.next()) {
                    val name = rs.getString(1) ?: "俱乐部"
                    val city = rs.getString(2) ?: ""
                    val logo = rs.getString(3)
                    val members = rs.getInt(4)
                    val heat = rs.getInt(5)
                    val info = UIClubInfo(
                        name = name,
                        level = 1,
                        heat = "$heat",
                        rankTag = "认证俱乐部",
                        avatarInitial = (name.firstOrNull() ?: '俱').toString(),
                        themeColor = Color(0xFF1976D2),
                        description = listOf(city.takeIf { it.isNotEmpty() }, "成员$members").filterNotNull().joinToString(" · "),
                        logoUrl = logo
                    )
                    handler.post { clubInfo = info }
                }
                Unit
            }
        }.start()

        // 加载俱乐部动态（来自数据库 community_posts）
        Thread {
            val list = mutableListOf<com.example.rideflow.model.Post>()
            DatabaseHelper.processQuery(
                "SELECT p.post_id, p.content_text, p.image_url, p.created_at, c.name, c.logo_url FROM community_posts p JOIN clubs c ON p.club_id = c.club_id WHERE p.club_id = ? ORDER BY p.created_at DESC",
                listOf(clubId)
            ) { rs ->
                while (rs.next()) {
                    val pid = rs.getInt(1)
                    val content = rs.getString(2) ?: ""
                    val img = rs.getString(3) ?: "[图片]"
                    val created = rs.getTimestamp(4)
                    val name = rs.getString(5) ?: "未知俱乐部"
                    val logo = rs.getString(6)
                    val timeStr = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    list.add(
                        com.example.rideflow.model.Post(
                            id = pid,
                            userId = clubId,
                            userName = name,
                            timeAgo = timeStr,
                            content = content,
                            imagePlaceholder = img,
                            likes = 0,
                            comments = 0,
                            initialIsLiked = false,
                            authorType = "club",
                            avatarUrl = logo
                        )
                    )
                }
                Unit
            }

            // 统计点赞与评论数量
            val likesMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery(
                "SELECT pl.post_id, COUNT(*) FROM post_likes pl WHERE pl.post_id IN (SELECT post_id FROM community_posts WHERE club_id = ?) GROUP BY pl.post_id",
                listOf(clubId)
            ) { lrs ->
                while (lrs.next()) likesMap[lrs.getInt(1)] = lrs.getInt(2)
                Unit
            }
            val commentsMap = mutableMapOf<Int, Int>()
            DatabaseHelper.processQuery(
                "SELECT pc.post_id, COUNT(*) FROM post_comments pc WHERE pc.post_id IN (SELECT post_id FROM community_posts WHERE club_id = ?) GROUP BY pc.post_id",
                listOf(clubId)
            ) { crs ->
                while (crs.next()) commentsMap[crs.getInt(1)] = crs.getInt(2)
                Unit
            }

            val merged = list.map { p -> p.copy(likes = likesMap[p.id] ?: 0, comments = commentsMap[p.id] ?: 0) }
            handler.post { clubPosts = merged }
        }.start()
    }

    // 2. 弹窗状态管理
    var showRankingDialog by remember { mutableStateOf(false) }
    var showManageDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var joinReason by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // 顶部 Header
            item {
                val info = clubInfo ?: UIClubInfo("俱乐部", 1, "0", "认证俱乐部", "俱", Color(0xFF1976D2), "", null)
                ClubDetailHeader(
                    info = info,
                    onBackClick = { navController.popBackStack() },
                    // [修改点]：点击详情图标，传递参数告知目标页面隐藏加入按钮
                    onDetailClick = { navController.navigate("club_detail/$clubId?showJoin=false") },
                    onShareClick = { /* 分享逻辑 */ }
                )
            }

            // 热度统计
            item {
                ClubDetailHeatSection(
                    heatValue = clubInfo?.heat ?: "0",
                    progress = when (clubId) { 1 -> 0.8f; 2 -> 0.4f; else -> 0.6f }
                )
            }

            // 功能按钮网格 (队员排名、队友位置)
            item {
                ClubDetailActionGrid(
                    onRankingClick = { showRankingDialog = true },
                    onLocationClick = { /* 队友位置逻辑 */ }
                )
            }

            // 列表菜单 (管理、申请)
            item {
                ClubDetailMenuSection(
                    onManageClick = { showManageDialog = true },
                    onJoinClick = { showJoinDialog = true }
                )
            }

            // 动态内容 Header
            item {
                Spacer(modifier = Modifier.height(12.dp).fillMaxWidth().background(Color(0xFFF5F5F5)))
                val info2 = clubInfo ?: UIClubInfo("俱乐部", 1, "0", "认证俱乐部", "俱", Color(0xFF1976D2), "", null)
                PostListHeader(info2)
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }

            // 动态列表
            if (clubPosts.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("暂无动态，快来发布第一条吧！", color = Color.Gray)
                    }
                }
            } else {
                items(clubPosts) { post ->
                    PostCard(
                        post = post,
                        isFollowing = true,
                        onFollowToggle = { _, _ -> },
                        showFollowButton = false,
                        onAvatarClick = { targetId, _ -> navController.navigate("club_detail/$targetId") },
                        onPostClick = { pid -> navController.navigate("${com.example.rideflow.navigation.AppRoutes.POST_DETAIL}/$pid") }
                    )
                    Spacer(modifier = Modifier.height(8.dp).background(Color(0xFFF0F0F0)))
                }
            }
        }

        // --- 弹窗实现 ---

        // 1. 队员排名弹窗
        if (showRankingDialog) {
            AlertDialog(
                onDismissRequest = { showRankingDialog = false },
                title = { Text("本周贡献榜 - ${clubInfo?.name ?: "俱乐部"}", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                        val members = getMockMembers(clubId)
                        members.forEachIndexed { index, name ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // 前三名显示奖牌颜色
                                    val rankColor = when(index) {
                                        0 -> Color(0xFFFFD700) // 金
                                        1 -> Color(0xFFC0C0C0) // 银
                                        2 -> Color(0xFFCD7F32) // 铜
                                        else -> Color.Gray
                                    }
                                    Box(modifier = Modifier.size(24.dp).background(rankColor, CircleShape), contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(name, fontSize = 16.sp)
                                }
                                Text("${(1000 - index * 50)} 贡献", color = Color.Red, fontSize = 14.sp)
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRankingDialog = false }) { Text("关闭") }
                }
            )
        }

        // 2. 俱乐部管理弹窗
        if (showManageDialog) {
            AlertDialog(
                onDismissRequest = { showManageDialog = false },
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                title = { Text("管理面板") },
                text = {
                    Column {
                        Text("当前权限：普通成员")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("查看公告") }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("退出俱乐部", color = Color.Red) }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showManageDialog = false }) { Text("关闭") }
                }
            )
        }

        // 3. 入队申请弹窗
        if (showJoinDialog) {
            AlertDialog(
                onDismissRequest = { showJoinDialog = false },
                title = { Text("申请加入") },
                text = {
                    Column {
                        Text("申请加入 ${clubInfo?.name ?: "俱乐部"}，请填写申请理由：")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = joinReason,
                            onValueChange = { joinReason = it },
                            label = { Text("我是骑行爱好者...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showJoinDialog = false
                        joinReason = ""
                    }) { Text("发送申请") }
                },
                dismissButton = {
                    TextButton(onClick = { showJoinDialog = false }) { Text("取消") }
                }
            )
        }
    }
}

// --- 组件部分 ---

@Composable
private fun ClubDetailHeader(info: UIClubInfo, onBackClick: () -> Unit, onDetailClick: () -> Unit, onShareClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        Box(modifier = Modifier.fillMaxSize().background(info.themeColor.copy(alpha = 0.3f)))
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White, modifier = Modifier.clickable(onClick = onBackClick))
                Row {
                    // 详情按钮 (Info Icon)
                    Icon(Icons.Default.Info, "Details", tint = Color.White, modifier = Modifier.clickable(onClick = onDetailClick))
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Default.Share, "Share", tint = Color.White, modifier = Modifier.clickable(onClick = onShareClick))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier.size(70.dp).clickable(onClick = onDetailClick) // 点击头像也能跳转
                ) {
                    if (!info.logoUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = info.logoUrl,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(info.avatarInitial, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = info.themeColor)
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.clickable(onClick = onDetailClick)) { // 点击名字也能跳转
                    Text(info.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("总热度: ${info.heat}", color = Color.LightGray, fontSize = 12.sp)
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Text(" Lv.${info.level}", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(modifier = Modifier.background(Color(0xFFD4AF37), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(info.rankTag, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostListHeader(info: UIClubInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(info.themeColor),
            contentAlignment = Alignment.Center
        ) {
            Text(info.avatarInitial, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Text(info.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.background(Color(0xFFFFA500), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
            Text("Lv.${info.level}", color = Color.White, fontSize = 10.sp)
        }
        Spacer(Modifier.weight(1f))

        // 筛选按钮
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Blue),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(24.dp).border(1.dp, Color.Blue, RoundedCornerShape(12.dp))
        ) {
            Text("动态", fontSize = 12.sp)
        }
    }
}

@Composable
private fun ClubDetailHeatSection(heatValue: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("本月活跃度", color = Color.Gray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(heatValue, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Star, null, tint = Color(0xFFD4AF37), modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color.Red,
            trackColor = Color(0xFFEEEEEE)
        )
    }
    HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F5F5))
}

@Composable
private fun ClubDetailActionGrid(onRankingClick: () -> Unit, onLocationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ClubDetailActionItem(Icons.Default.Menu, "队员排名", onRankingClick)
        ClubDetailActionItem(Icons.Default.LocationOn, "队友位置", onLocationClick)
    }
    HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F5F5))
}

@Composable
private fun ClubDetailMenuSection(onManageClick: () -> Unit, onJoinClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ClubDetailMenuItem(Icons.Default.Settings, "俱乐部管理", "管理员功能", onManageClick)
        HorizontalDivider(color = Color(0xFFEEEEEE))
        ClubDetailMenuItem(Icons.Default.Add, "入队申请", "招募中", onJoinClick)
    }
}

@Composable
private fun ClubDetailActionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, title, tint = Color(0xFF0091EA), modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun ClubDetailMenuItem(icon: ImageVector, title: String, subText: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(title, fontSize = 16.sp, color = Color.Black)
        Spacer(Modifier.weight(1f))
        if (subText != null) {
            Text(subText, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.width(4.dp))
        }
        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

// --- 模拟数据 ---

private fun getMockClubInfo(id: Int): UIClubInfo {
    return when(id) {
        1 -> UIClubInfo("飓风骑行俱乐部", 8, "57万℃", "年度第1骑", "飓", Color.Red, "追求速度与激情的专业车队")
        2 -> UIClubInfo("周末休闲骑", 3, "12万℃", "美食猎人", "周", Color.Blue, "骑车只是借口，干饭才是正事")
        3 -> UIClubInfo("山地越野小队", 5, "33万℃", "山神降临", "山", Color(0xFF006400), "无兄弟，不越野，探索未知林道")
        else -> UIClubInfo("未知俱乐部", 1, "0℃", "新手上路", "?", Color.Gray, "")
    }
}

private fun getMockMembers(id: Int): List<String> {
    return when(id) {
        1 -> listOf("飙速阿杰", "风神", "闪电侠", "大腿哥", "破风手", "冲刺王", "爬坡架", "新星")
        2 -> listOf("养生小王", "咖啡豆", "美食家", "摄影师", "慢骑手", "看风景", "快乐水")
        3 -> listOf("泥巴佬", "石头人", "树根", "飞包", "避震器", "软尾", "硬尾")
        else -> listOf("路人甲", "路人乙")
    }
}

private fun getMockClubPosts(id: Int): List<Post> {
    return when(id) {
        1 -> listOf(
            Post(801, 88, Icons.Default.DateRange, "飓风骑行俱乐部", "10分钟前", "【赛事战报】恭喜我队在今日的城市绕圈赛中包揽前三！大家的汗水没有白流！\n#飓风冲冲冲 #骑行比赛", "[颁奖台合影]", 234, 45),
            Post(802, 88, Icons.Default.DateRange, "飓风骑行俱乐部", "3小时前", "本周日拉练计划：全程120km，爬升800m，均速要求30+，请做好体能储备。", "[路线海拔图]", 156, 88)
        )
        2 -> listOf(
            Post(803, 99, Icons.Default.DateRange, "周末休闲骑", "1小时前", "这周六的目的地是城西的网红咖啡馆，骑行20km，喝咖啡2小时，来回轻松愉快~", "[咖啡馆照片]", 89, 34),
            Post(804, 99, Icons.Default.DateRange, "周末休闲骑", "昨天", "上周的农家乐烤全羊太香了，发几张图深夜放毒。", "[烤全羊特写]", 112, 56)
        )
        3 -> listOf(
            Post(805, 77, Icons.Default.DateRange, "山地越野小队", "30分钟前", "刚探索了一条隐藏林道，难度系数4星，有几个大落差，建议全盔护具带齐。", "[林道第一视角]", 67, 12),
            Post(806, 77, Icons.Default.DateRange, "山地越野小队", "2天前", "下雨后的泥地太滑了，不过这就是越野的乐趣！洗车要半天哈哈。", "[满身泥巴的车]", 98, 23)
        )
        else -> emptyList()
    }
}

package com.example.rideflow.ui.screens.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.model.Comment
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel
import com.example.rideflow.auth.AuthViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostDetailScreen(navController: NavController, postId: Int) {
    var userName by remember { mutableStateOf("") }
    var timeAgo by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("[图片]") }
    var imagePlaceholder by remember { mutableStateOf("[图片]") }
    var likeCount by remember { mutableStateOf(0) }
    var comments by remember { mutableStateOf(listOf<Comment>()) }
    var visibleCount by remember { mutableStateOf(10) }
    var newCommentText by remember { mutableStateOf("") }
    val authViewModel = koinViewModel<AuthViewModel>()

    LaunchedEffect(postId) {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            DatabaseHelper.processQuery(
                "SELECT p.content_text, p.image_url, p.created_at, COALESCE(c.name, u.nickname) AS author_name FROM community_posts p LEFT JOIN clubs c ON p.club_id = c.club_id LEFT JOIN users u ON p.author_user_id = u.user_id WHERE p.post_id = ?",
                listOf(postId)
            ) { rs ->
                if (rs.next()) {
                    val ct = rs.getString(1) ?: ""
                    val img = rs.getString(2) ?: "[图片]"
                    val created = rs.getTimestamp(3)
                    val name = rs.getString(4) ?: "未知用户"
                    val ts = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    handler.post {
                        content = ct
                        imagePlaceholder = img
                        userName = name
                        timeAgo = ts
                    }
                }
                Unit
            }
            DatabaseHelper.processQuery(
                "SELECT COUNT(*) FROM post_likes WHERE post_id = ?",
                listOf(postId)
            ) { lrs ->
                if (lrs.next()) {
                    val cnt = lrs.getInt(1)
                    handler.post { likeCount = cnt }
                }
                Unit
            }
            DatabaseHelper.processQuery(
                "SELECT pc.comment_id, u.nickname, pc.content, pc.created_at FROM post_comments pc JOIN users u ON pc.user_id = u.user_id WHERE pc.post_id = ? ORDER BY pc.created_at DESC",
                listOf(postId)
            ) { crs ->
                val list = mutableListOf<Comment>()
                while (crs.next()) {
                    val cid = crs.getInt(1)
                    val nick = crs.getString(2) ?: "匿名"
                    val ctt = crs.getString(3) ?: ""
                    val created = crs.getTimestamp(4)
                    val ts = if (created != null) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(created) else ""
                    list.add(Comment(cid, nick, ctt, ts))
                }
                handler.post { comments = list }
                Unit
            }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "动态详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(userName.ifEmpty { "未知用户" }, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(timeAgo, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(text = content, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    coil.compose.AsyncImage(
                        model = imagePlaceholder,
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "赞: ${likeCount}", color = Color.Gray)
                    Text(text = "评论: ${comments.size}", color = Color.Gray)
                }
                Spacer(Modifier.height(12.dp))
                Text(text = "评论", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            items(comments.take(visibleCount)) { c ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(text = c.userName, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(8.dp))
                        Text(text = c.time, color = Color.Gray, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(text = c.content)
                }
                Divider()
            }
            item {
                if (visibleCount < comments.size) {
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { visibleCount = minOf(visibleCount + 5, comments.size) }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "加载更多评论")
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("发表你的评论...") }
                    )
                    Spacer(Modifier.width(8.dp))
                    val currentUser = authViewModel.getCurrentUser()
                    Button(
                        onClick = {
                            val uid = currentUser?.userId?.toIntOrNull()
                            val text = newCommentText.trim()
                            if (uid == null || text.isEmpty()) return@Button
                            val handler = Handler(Looper.getMainLooper())
                            Thread {
                                val newId = DatabaseHelper.insertAndReturnId(
                                    "INSERT INTO post_comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)",
                                    listOf(postId, uid, text)
                                )
                                if (newId != null) {
                                    val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                                    handler.post {
                                        comments = listOf(Comment(newId, currentUser.nickname, text, now)) + comments
                                        newCommentText = ""
                                    }
                                }
                            }.start()
                        },
                        enabled = newCommentText.isNotBlank()
                    ) {
                        Text("发送")
                    }
                }
            }
        }
    }
}

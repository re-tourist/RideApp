package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.layout.ContentScale
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.profile.ProfileViewModel
import com.example.rideflow.ui.theme.RideFlowTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Send

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProfileDetailScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = koinViewModel()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()

    // 页面加载时获取用户资料
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfileData()
    }
    Scaffold(
        topBar = {
            // 使用Material3的TopAppBar
            TopAppBar(
                title = { Text(text = "个人资料") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 显示加载状态
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "加载中...", fontSize = 16.sp)
                } else if (errorMessage != null) {
                    // 显示错误信息
                    Text(
                        text = errorMessage ?: "加载失败",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { profileViewModel.loadUserProfileData() }) {
                        Text(text = "重试")
                    }
                } else if (userProfile == null) {
                    // 显示未登录或用户资料为空
                    Text(
                        text = "用户未登录或资料为空",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                } else {
                    // 显示用户资料
                    // 头像
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        val avatar = userProfile?.avatarUrl
                        if (!avatar.isNullOrBlank()) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "用户头像",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "用户头像",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.DarkGray
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 个人信息列表
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileInfoRow(label = "昵称:", value = userProfile?.nickname ?: "未设置")
                        ProfileInfoRow(label = "性别:", value = when (userProfile?.gender) {
                            1 -> "男"
                            2 -> "女"
                            else -> "其他"
                        })
                        ProfileInfoRow(label = "出生年月:", value = userProfile?.birthday ?: "未设置")
                        ProfileInfoRow(label = "邮箱:", value = userProfile?.email ?: "未设置")
                        ProfileInfoRow(label = "个人简介:", value = userProfile?.bio ?: "未设置", isMultiLine = true)
                        ProfileInfoRow(label = "紧急联系人:", value = userProfile?.emergencyContact ?: "未设置")
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // 底部按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate(AppRoutes.EDIT_PROFILE) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = "编辑资料")
                        }
                        
                        OutlinedButton(
                            onClick = { navController.navigate("${com.example.rideflow.navigation.AppRoutes.MAIN}?tab=profile") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "关闭")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileInfoRow(label: String, value: String, isMultiLine: Boolean = false) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = if (isMultiLine) {
                Modifier.widthIn(max = 300.dp)
            } else {
                Modifier
            },
            maxLines = if (isMultiLine) Int.MAX_VALUE else 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileDetailScreenPreview() {
    RideFlowTheme {
        // 预览时使用一个简单的模拟实现
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "预览模式 - 实际运行时将显示真实用户数据",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(navController: NavController, userId: Int) {
    var nickname by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<String?>(null) }
    var bio by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    val handler = android.os.Handler(android.os.Looper.getMainLooper())

    LaunchedEffect(userId) {
        Thread {
            com.example.rideflow.backend.DatabaseHelper.processQuery(
                "SELECT nickname, avatar_url, bio FROM users WHERE user_id = ?",
                listOf(userId)
            ) { rs ->
                if (rs.next()) {
                    val n = rs.getString(1) ?: ""
                    val a = rs.getString(2)
                    val b = rs.getString(3) ?: ""
                    handler.post {
                        nickname = n
                        avatar = a
                        bio = b
                        city = ""
                        level = ""
                    }
                } else {
                    handler.post {
                        nickname = "骑友$userId"
                        avatar = null
                        bio = ""
                        city = ""
                        level = ""
                    }
                }
                Unit
            }
        }.start()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "个人主页") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(120.dp), shape = CircleShape, color = Color.LightGray) {
                val av = avatar
                if (!av.isNullOrBlank()) {
                    AsyncImage(model = av, contentDescription = "头像", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "头像", modifier = Modifier.size(60.dp), tint = Color.DarkGray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = nickname, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = city, fontSize = 14.sp, color = Color.Gray)
                Text(text = level, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileInfoRow(label = "简介:", value = bio)
                ProfileInfoRow(label = "里程:", value = "1324 km")
                ProfileInfoRow(label = "均速:", value = "22.8 km/h")
                ProfileInfoRow(label = "累计骑行:", value = "86 次")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navController.navigate("${AppRoutes.CHAT}/$userId") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Icon(imageVector = Icons.Filled.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "发消息", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, targetUserId: Int, currentUserId: Int) {
    data class ChatMsg(val senderId: Int, val text: String)
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMsg(targetUserId, "你好，我是骑友$targetUserId"),
                ChatMsg(currentUserId, "你好！最近有空吗？"),
                ChatMsg(targetUserId, "一起夜骑吗？"),
                ChatMsg(currentUserId, "周六晚上可以。")
            )
        )
    }
    var input by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "私聊") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) { Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回") }
            })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
                items(messages) { msg ->
                    val isMine = msg.senderId == currentUserId
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isMine) {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(6.dp))
                        }
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (isMine) Color(0xFFCCE5FF) else Color(0xFFEFEFEF)
                        ) {
                            Text(text = msg.text, modifier = Modifier.padding(10.dp))
                        }
                        if (isMine) {
                            Spacer(Modifier.width(6.dp))
                            Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
            Divider()
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text(text = "输入消息") })
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (input.isNotBlank()) {
                        messages = messages + ChatMsg(currentUserId, input)
                        input = ""
                    }
                }) { Text(text = "发送") }
            }
        }
    }
}

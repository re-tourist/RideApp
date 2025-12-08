package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.profile.ProfileViewModel
import com.example.rideflow.ui.theme.RideFlowTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProfileDetailScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = koinViewModel()
    val userProfileState = profileViewModel.userProfile.collectAsState()
    val userProfile = userProfileState.value
    val isLoadingState = profileViewModel.isLoading.collectAsState()
    val isLoading = isLoadingState.value
    val errorMessageState = profileViewModel.errorMessage.collectAsState()
    val errorMessage = errorMessageState.value

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
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "用户头像",
                                modifier = Modifier.size(60.dp),
                                tint = Color.DarkGray
                            )
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
                            onClick = { navController.popBackStack() },
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

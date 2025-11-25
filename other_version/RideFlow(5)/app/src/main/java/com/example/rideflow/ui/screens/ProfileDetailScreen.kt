package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.rideflow.ui.theme.RideFlowTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProfileDetailScreen(navController: NavController) {
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
                // 头像
                // 用户头像
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
                    ProfileInfoRow(label = "昵称:", value = "maxzill")
                    ProfileInfoRow(label = "性别:", value = "男")
                    ProfileInfoRow(label = "出生年月:", value = "1990-01-01")
                    ProfileInfoRow(label = "邮箱:", value = "user@example.com")
                    ProfileInfoRow(label = "个人简介:", value = "热爱骑行，享受户外运动的乐趣", isMultiLine = true)
                    ProfileInfoRow(label = "紧急联系人:", value = "张三 138****1234")
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
        ProfileDetailScreen(navController = androidx.navigation.compose.rememberNavController())
    }
}

package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.theme.RideFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(navController: NavController, activityId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    // 控制联系主办方弹窗的显示
    var showContactDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "活动详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 分享功能 */ }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "收藏"
                        )
                    }
                }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 主图片展示区
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "活动主图",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                
                // 活动状态标签
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color(0x80000000), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "报名中",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 活动基本信息
            Column(modifier = Modifier.padding(16.dp)) {
                // 活动标题
                Text(
                    text = "周末骑行休闲游",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // 活动时间和地点
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "时间",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "2025-11-15 09:00",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "地点",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "上海市浦东新区",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                // 分隔线
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // 活动详情描述
                Text(
                    text = "活动详情",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "这是一场轻松的周末骑行休闲活动，适合所有水平的骑行爱好者参加。我们将从市中心出发，沿着滨江大道骑行，欣赏美丽的城市风光。活动全程约30公里，中途会有休息点提供饮用水和小食。欢迎大家踊跃报名！",
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                
                // 分隔线
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // 活动规则
                Text(
                    text = "活动规则",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    RuleItem("1. 参与者需年满18周岁，身体健康")
                    RuleItem("2. 请自备骑行装备，包括头盔、手套等安全装备")
                    RuleItem("3. 活动过程中请遵守交通规则，听从组织者指挥")
                    RuleItem("4. 如有任何不适，请及时告知组织者")
                    RuleItem("5. 请保持环境清洁，不要随意丢弃垃圾")
                }
                
                // 底部按钮区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 联系主办方按钮
                    Button(
                        onClick = { showContactDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text(text = "联系主办方", color = Color.White)
                    }
                    
                    // 立即报名按钮
                    Button(
                        onClick = { navController.navigate("${AppRoutes.ACTIVITY_REGISTRATION}/${activityId}") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text(text = "立即报名", color = Color.White)
                    }
                }
            }
        }
    }
    
    // 联系主办方弹窗
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text(text = "联系主办方") },
            text = {
                Column {
                    Text(text = "主办方：乐体体育")
                    Text(text = "电话：400-123-4567")
                    Text(text = "邮箱：contact@letisport.com")
                }
            },
            confirmButton = {
                Button(onClick = { showContactDialog = false }) {
                    Text(text = "关闭")
                }
            }
        )
    }
}

@Composable
fun RuleItem(text: String) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF2196F3), RoundedCornerShape(4.dp))
                .align(Alignment.Top)
                .padding(2.dp)
        ) {
            Text(
                text = text.substring(0, 1),
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = text.substring(2),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityDetailScreenPreview() {
    ActivityDetailScreen(navController = androidx.navigation.compose.rememberNavController())
}

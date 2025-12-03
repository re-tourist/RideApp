package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.rideflow.R
import com.example.rideflow.ui.components.ImageUploadComponent
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClubScreen(navController: NavController) {
    var clubName by remember { mutableStateOf("") }
    var clubDescription by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current

    val categories = listOf("骑行", "休闲", "竞技", "长途", "山地", "公路")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "创建俱乐部") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // 模拟创建俱乐部
                            isLoading = true
                            // 这里可以添加实际的创建俱乐部逻辑
                            navController.popBackStack()
                        },
                        enabled = clubName.isNotEmpty() && city.isNotEmpty() && category.isNotEmpty()
                    ) {
                        Text(text = "创建", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 俱乐部logo上传区域
                ImageUploadComponent(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                    selectedImage = selectedImage,
                    onImageSelected = { file -> selectedImage = file },
                    isCircular = true,
                    placeholderIcon = R.drawable.ic_launcher_foreground
                )
                Text(
                    text = "点击上传俱乐部logo",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 俱乐部名称
                OutlinedTextField(
                    value = clubName,
                    onValueChange = { clubName = it },
                    label = { Text(text = "俱乐部名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 所在城市
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(text = "所在城市") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 俱乐部类别
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(text = "俱乐部类别") },
                    placeholder = { Text(text = "如：骑行、休闲、竞技等") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "推荐类别：${categories.joinToString(", ")}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // 俱乐部简介
                OutlinedTextField(
                    value = clubDescription,
                    onValueChange = { clubDescription = it },
                    label = { Text(text = "俱乐部简介") },
                    placeholder = { Text(text = "介绍一下您的俱乐部吧...") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    maxLines = 5,
                    minLines = 5
                )
                Spacer(modifier = Modifier.height(24.dp))

                // 提示信息
                Surface(color = Color(0xFFFFF9C4), shape = MaterialTheme.shapes.small) {
                    Text(
                        text = "创建俱乐部后，您将成为俱乐部管理员，可以管理成员、发布活动等。",
                        fontSize = 12.sp,
                        color = Color(0xFFF57C00),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

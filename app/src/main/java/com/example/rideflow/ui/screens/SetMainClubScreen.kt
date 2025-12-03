package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetMainClubScreen(
    navController: NavController
) {
    // 模拟数据：已加入的俱乐部列表
    val joinedClubs = remember {
        listOf(
            Club(
                id = 1,
                name = "骑货单车俱乐部",
                city = "北京市",
                members = 4,
                heat = 482053,
                logoRes = R.drawable.ic_launcher_foreground
            ),
            Club(
                id = 2,
                name = "山地车爱好者协会",
                city = "上海市",
                members = 5,
                heat = 321456,
                logoRes = R.drawable.ic_launcher_foreground
            ),
            Club(
                id = 3,
                name = "公路车竞速队",
                city = "广州市",
                members = 3,
                heat = 654321,
                logoRes = R.drawable.ic_launcher_foreground
            )
        )
    }

    // 模拟当前选中的主俱乐部（null表示未设置）
    val currentMainClub by remember { mutableStateOf<Club?>(null) }
    var selectedClubId by remember { mutableStateOf<Int?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "设置主俱乐部") },
                navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "设置主俱乐部后，您上传轨迹所产生的热度值将导入主俱乐部中。",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 主俱乐部部分
                Text(
                    text = "主俱乐部",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 主俱乐部选择区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { 
                            // 如果已选择俱乐部，则设置为主俱乐部
                            if (selectedClubId != null) {
                                showSuccessDialog = true
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 主俱乐部Logo或加号
                    if (currentMainClub != null) {
                        Image(
                            painter = painterResource(id = currentMainClub!!.logoRes),
                            contentDescription = currentMainClub!!.name,
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentMainClub!!.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // 未设置主俱乐部时显示加号
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "设置主俱乐部",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 提示文字
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentMainClub != null) "已设置为主俱乐部" else "点击设置主俱乐部",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // 右箭头
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "进入设置",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 我的俱乐部部分
                Text(
                    text = "我的俱乐部",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(joinedClubs) { club ->
                        ClubItem(
                            club = club,
                            isSelected = selectedClubId == club.id,
                            onSelect = { selectedClubId = club.id }
                        )
                        Divider()
                    }
                }

                Button(
                    onClick = {
                        if (selectedClubId != null) {
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = selectedClubId != null
                ) {
                    Text(text = "设置为主俱乐部")
                }
            }
        }
    }

    // 成功提示对话框
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(text = "设置成功") },
            text = { Text(text = "主俱乐部已设置成功！") },
            confirmButton = {
                Button(onClick = { 
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text(text = "确定")
                }
            }
        )
    }
}

@Composable
fun ClubItem(
    club: Club,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 俱乐部Logo
        Image(
            painter = painterResource(id = club.logoRes),
            contentDescription = club.name,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 俱乐部信息
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = club.city,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // 单选按钮
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetMainClubScreenPreview() {
    SetMainClubScreen(navController = androidx.navigation.compose.rememberNavController())
}

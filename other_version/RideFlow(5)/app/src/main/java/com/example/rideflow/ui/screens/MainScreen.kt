package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.navigation.NavController
import com.example.rideflow.R

@Composable
fun MainScreen(navController: NavController) {
    val navItems = listOf(
        NavItem(
            title = "骑行",
            icon = Icons.Filled.Star,
            screen = { SimpleRideScreen() }
        ),
        NavItem(
            title = "发现",
            icon = Icons.Filled.Home,
            screen = { DiscoverScreen() }
        ),
        NavItem(
            title = "社区",
            icon = @Suppress("DEPRECATION") Icons.Filled.List,
            screen = { CommunityScreen() }
        ),
        NavItem(
            title = "我的",
            icon = Icons.Filled.Person,
            screen = { ProfileScreen(navController = navController) }
        )
    )
    
    var currentIndex by remember { mutableIntStateOf(1) } // 默认选中发现页面
    
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(item.title)
                        },
                        selected = currentIndex == index,
                        onClick = {
                            currentIndex = index
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF007AFF),
                            selectedTextColor = Color(0xFF007AFF),
                            indicatorColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        }
    ) {
        // 使用padding值避免内容被底部导航栏遮挡
        Box(modifier = Modifier.padding(it)) {
            navItems[currentIndex].screen()
        }
    }
}

data class NavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRideScreen() {
    // 空白界面占位，用于底部导航栏的骑行项
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑行") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF7B68EE)
                )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "骑行功能开发中...",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 预览时使用默认参数
    MainScreen(navController = androidx.navigation.compose.rememberNavController())
}
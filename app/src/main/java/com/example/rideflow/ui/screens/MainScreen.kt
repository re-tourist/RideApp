package com.example.rideflow.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.navigation.NavController
import com.example.rideflow.R

@Composable
fun MainScreen(navController: NavController) {
    val navItems = listOf(
        NavItem(
            title = "运动",
            icon = Icons.Filled.Star,
            screen = { RideScreen(navController) }
        ),
        NavItem(
            title = "发现",
            icon = Icons.Filled.Home,
            screen = { DiscoverScreen(navController) }
        ),
        NavItem(
            title = "社区",
            icon = Icons.Filled.List,
            screen = { CommunityScreen(navController) }
        ),
        NavItem(
            title = "我的",
            icon = Icons.Filled.Person,
            screen = { ProfileScreen(navController = navController) }
        )
    )
    
    var currentIndex by remember { mutableStateOf(1) } // 默认选中发现页面
    
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
        navItems[currentIndex].screen(navController)
    }
}

data class NavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val screen: @Composable (NavController) -> Unit
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 预览时使用默认参数
    MainScreen(navController = androidx.navigation.compose.rememberNavController())
}

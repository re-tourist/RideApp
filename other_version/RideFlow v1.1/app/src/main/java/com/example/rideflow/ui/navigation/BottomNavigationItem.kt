package com.example.rideflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val label: String, val icon: ImageVector) {
    object Discover : NavigationItem("discover", "发现", Icons.Filled.Home)
    object Community : NavigationItem("community", "社区", Icons.Filled.List)
    object RideRecord : NavigationItem("riderecord", "运动", Icons.Filled.Star)
    object Profile : NavigationItem("profile", "我的", Icons.Filled.Person)
}
package com.example.rideflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rideflow.auth.AuthState
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.ui.screens.EditProfileScreen
import com.example.rideflow.ui.screens.LoginScreen
import com.example.rideflow.ui.screens.MainScreen
import com.example.rideflow.ui.screens.ProfileDetailScreen
import com.example.rideflow.ui.screens.RegisterScreen
import com.example.rideflow.ui.screens.AchievementsScreen
import com.example.rideflow.ui.screens.ExerciseCalendarScreen
import com.example.rideflow.ui.screens.MyActivitiesScreen

/**
 * 应用导航图
 * 管理所有页面的导航路由
 */
@Composable
fun AppNavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    // 获取认证状态
    val authState = authViewModel.collectAuthState()

    NavHost(
        navController = navController,
        // 根据认证状态决定起始路由
        startDestination = when (authState) {
            is AuthState.Authenticated -> AppRoutes.MAIN
            else -> AppRoutes.LOGIN
        }
    ) {
        // 登录页面
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // 注册页面
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 主应用页面
        composable(AppRoutes.MAIN) {
            MainScreen(navController = navController)
        }
        
        // 编辑资料页面
        composable(AppRoutes.EDIT_PROFILE) {
            EditProfileScreen(onBackPress = { navController.popBackStack() })
        }
        
        // 个人资料详情页面
        composable(AppRoutes.PROFILE_DETAIL) {
            ProfileDetailScreen(navController = navController)
        }

        composable(AppRoutes.RIDE_PREFERENCE) {
            com.example.rideflow.ui.screens.RidePreferenceScreen(navController = navController)
        }

        composable(AppRoutes.ACHIEVEMENTS) {
            AchievementsScreen(navController = navController)
        }

        composable(AppRoutes.EXERCISE_CALENDAR) {
            ExerciseCalendarScreen(navController = navController)
        }

        composable(AppRoutes.MY_ACTIVITIES) {
            MyActivitiesScreen(navController = navController)
        }

        composable(AppRoutes.RIDE_RECORD) {
            com.example.rideflow.ui.screens.RideRecordScreen(navController = navController)
        }

        // 注意：其他子页面（如发现等）的路由在MainScreen内部通过BottomNavigationBar管理
    }
}

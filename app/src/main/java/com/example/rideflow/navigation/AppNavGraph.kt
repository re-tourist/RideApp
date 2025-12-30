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
import com.example.rideflow.ui.screens.RegisterScreen
import com.example.rideflow.ui.screens.MainScreen
import com.example.rideflow.ui.screens.ProfileDetailScreen
import com.example.rideflow.ui.screens.AchievementsScreen
import com.example.rideflow.ui.screens.ExerciseCalendarScreen
import com.example.rideflow.ui.screens.MyActivitiesScreen
import com.example.rideflow.ui.screens.RaceDetailScreen
import com.example.rideflow.ui.screens.ActivityDetailScreen
import com.example.rideflow.ui.screens.ActivitiesScreen
import com.example.rideflow.ui.screens.CreateActivityScreen
import com.example.rideflow.ui.screens.RaceRegistrationScreen
import com.example.rideflow.ui.screens.AddRegistrationCardScreen
import com.example.rideflow.ui.screens.ActivityRegistrationScreen
import com.example.rideflow.ui.screens.ClubDetailScreen
import com.example.rideflow.ui.screens.ClubScreen
import com.example.rideflow.ui.screens.CreateClubScreen
import com.example.rideflow.ui.screens.SetMainClubScreen
import com.example.rideflow.ui.screens.PreparationScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.rideflow.ui.screens.community.TradeDetailScreen
import com.example.rideflow.ui.screens.community.CommunityPostDetailScreen
import com.example.rideflow.ui.screens.community.CommunityClubDetailScreen
import com.example.rideflow.ui.screens.UserProfileDetailScreen
import com.example.rideflow.ui.screens.ChangePasswordScreen
import com.example.rideflow.ui.screens.SettingsInfoScreen
import com.example.rideflow.ui.screens.message.MessageDetailScreen
import com.example.rideflow.ui.screens.message.MessageListScreen

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
        startDestination = AppRoutes.PREPARATION
        ) {
        composable(AppRoutes.PREPARATION) {
            PreparationScreen(onFinished = {
                val target = when (authState) {
                    is AuthState.Authenticated -> AppRoutes.MAIN
                    else -> AppRoutes.LOGIN
                }
                navController.navigate(target) {
                    popUpTo(AppRoutes.PREPARATION) { inclusive = true }
                }
            })
        }
        // 登录页面
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // 注册页面
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 主应用页面（支持可选tab参数）
        composable(route = "${AppRoutes.MAIN}?tab={tab}", arguments = listOf(navArgument("tab") { type = NavType.StringType; defaultValue = "sport" })) { backStackEntry ->
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            val startTab = backStackEntry.arguments?.getString("tab") ?: "sport"
            MainScreen(navController = navController, userId = uid, startTab = startTab)
        }
        // 主应用页面（兼容旧路由）
        composable(AppRoutes.MAIN) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            MainScreen(navController = navController, userId = uid, startTab = "sport")
        }
        
        // 编辑资料页面
        composable(AppRoutes.EDIT_PROFILE) {
            EditProfileScreen(onBackPress = { navController.navigate("${AppRoutes.MAIN}?tab=profile") })
        }
        
        // 个人资料详情页面
        composable(AppRoutes.PROFILE_DETAIL) {
            ProfileDetailScreen(navController = navController)
        }

        composable(AppRoutes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }

        composable(
            route = "${AppRoutes.SETTINGS_INFO}/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "about"
            SettingsInfoScreen(navController = navController, type = type)
        }

        composable(AppRoutes.RIDE_PREFERENCE) {
            val uid = when (authState) {
                is com.example.rideflow.auth.AuthState.Authenticated -> (authState as com.example.rideflow.auth.AuthState.Authenticated).userData.userId
                else -> ""
            }
            com.example.rideflow.ui.screens.RidePreferenceScreen(navController = navController, userId = uid)
        }

        composable(AppRoutes.ACHIEVEMENTS) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            AchievementsScreen(navController = navController, userId = uid)
        }

        composable(AppRoutes.EXERCISE_CALENDAR) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            ExerciseCalendarScreen(navController = navController, userId = uid)
        }

        composable(AppRoutes.MY_ACTIVITIES) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            MyActivitiesScreen(navController = navController, userId = uid)
        }

        composable(AppRoutes.RIDE_RECORD) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            com.example.rideflow.ui.screens.RideRecordScreen(navController = navController, userId = uid)
        }

        composable(AppRoutes.RACE) {
            com.example.rideflow.ui.screens.RaceScreen(
                onBack = { navController.popBackStack() },
                onCreateRace = { navController.navigate(AppRoutes.CREATE_RACE) },
                navController = navController
            )
        }

        composable(AppRoutes.CREATE_RACE) {
            com.example.rideflow.ui.screens.CreateRaceScreen(onBack = { navController.popBackStack() })
        }

        composable("${AppRoutes.RACE_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            RaceDetailScreen(navController = navController, raceId = id)
        }

        // 赛事报名页面
        composable("${AppRoutes.RACE_REGISTRATION}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            RaceRegistrationScreen(navController = navController, raceId = id)
        }

        // 添加报名卡页面
        composable("${AppRoutes.ADD_REGISTRATION_CARD}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            AddRegistrationCardScreen(navController = navController, raceId = id)
        }

        // 活动页面
        composable(
            route = "${AppRoutes.ACTIVITIES}?category={category}",
            arguments = listOf(navArgument("category") { type = NavType.IntType; defaultValue = 0 })
        ) { backStackEntry ->
            val initialCategory = backStackEntry.arguments?.getInt("category") ?: 0
            ActivitiesScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                onCreateActivity = { navController.navigate(AppRoutes.CREATE_ACTIVITY) },
                initialCategoryIndex = initialCategory
            )
        }
        
        // 创建活动页面
        composable(AppRoutes.CREATE_ACTIVITY) {
            CreateActivityScreen(onBack = { navController.popBackStack() })
        }
        
        // 活动详情页面
        composable("${AppRoutes.ACTIVITY_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            ActivityDetailScreen(navController = navController, activityId = id)
        }

        // 活动报名页面
        composable("${AppRoutes.ACTIVITY_REGISTRATION}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            ActivityRegistrationScreen(navController = navController, activityId = id)
        }

        composable(AppRoutes.MESSAGE_LIST) {
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            MessageListScreen(navController = navController, ownerId = uid)
        }

        composable(
            route = "${AppRoutes.MESSAGE_DETAIL}/{peerId}",
            arguments = listOf(navArgument("peerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).userData.userId
                else -> ""
            }
            val peerId = backStackEntry.arguments?.getString("peerId").orEmpty()
            MessageDetailScreen(navController = navController, ownerId = uid, peerId = peerId)
        }

        // 路书详情页面
        composable("${AppRoutes.ROUTE_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            com.example.rideflow.ui.screens.RouteBookDetailScreen(navController = navController, routeId = id)
        }
        
        // 骑行导航页面（与骑行页内容相同，可返回）
        composable(AppRoutes.RIDE_NAVIGATION) {
            com.example.rideflow.ui.screens.RideNavigationScreen(navController = navController)
        }
        composable("${AppRoutes.TRADE_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            TradeDetailScreen(navController = navController, itemId = id)
        }
        composable("${AppRoutes.POST_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            CommunityPostDetailScreen(navController = navController, postId = id)
        }
        composable("${AppRoutes.USER_PROFILE_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            UserProfileDetailScreen(navController = navController, userId = id)
        }

        composable("${AppRoutes.COMMUNITY_CLUB_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            CommunityClubDetailScreen(navController = navController, clubId = id)
        }

        // 俱乐部详情页面
        composable(
            route = "${AppRoutes.CLUB_DETAIL}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            ClubDetailScreen(
                clubId = it.arguments?.getInt("id") ?: 0,
                navController = navController
            )
        }
        // 俱乐部页面
        composable(route = AppRoutes.CLUB_SCREEN) {
            ClubScreen(onBack = { navController.popBackStack() }, navController = navController)
        }
        // 创建俱乐部页面
        composable(route = AppRoutes.CREATE_CLUB) {
            CreateClubScreen(navController = navController)
        }
        // 设置主俱乐部页面
        composable(route = AppRoutes.SET_MAIN_CLUB) {
            SetMainClubScreen(navController = navController)
        }

        // 注意：其他子页面（如发现等）的路由在MainScreen内部通过BottomNavigationBar管理
    }
}

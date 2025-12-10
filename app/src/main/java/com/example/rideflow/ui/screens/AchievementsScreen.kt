package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.navigation.AppRoutes
import android.os.Handler
import android.os.Looper
import com.example.rideflow.backend.DatabaseHelper

data class AchievementItem(
    val id: Int,
    val title: String,
    val description: String,
    val iconRes: Int? = null,
    val isUnlocked: Boolean = true,
    val progress: Int = 100
)

enum class AchievementTab { SPORT_LIFE, TIME_LIMITED_CHALLENGE, PERSONAL_BEST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(navController: NavController, userId: String = "") {
    val selectedTab = remember { mutableStateOf(AchievementTab.SPORT_LIFE) }
    val sportLifeAchievements = remember { mutableStateOf<List<AchievementItem>>(emptyList()) }
    val timeLimitedAchievements = remember { mutableStateOf<List<AchievementItem>>(emptyList()) }
    val personalBestAchievements = remember { mutableStateOf<List<AchievementItem>>(emptyList()) }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                val sport = mutableListOf<AchievementItem>()
                val timeLimited = mutableListOf<AchievementItem>()
                val personal = mutableListOf<AchievementItem>()
                DatabaseHelper.processQuery(
                    "SELECT b.badge_id, b.name, b.description, b.rule_type, COALESCE(p.is_unlocked,0), COALESCE(p.progress_percent,0) FROM achievement_badges b LEFT JOIN user_achievement_progress p ON p.badge_id = b.badge_id AND p.user_id = ? ORDER BY b.badge_id",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val title = rs.getString(2) ?: ""
                        val desc = rs.getString(3) ?: ""
                        val rule = rs.getString(4) ?: ""
                        val unlocked = rs.getInt(5) == 1
                        val progressPercent = rs.getDouble(6)
                        val progressInt = kotlin.math.round(progressPercent).toInt().coerceIn(0, 100)
                        val item = AchievementItem(id = id, title = title, description = desc, iconRes = null, isUnlocked = unlocked, progress = progressInt)
                        when (rule) {
                            "first_ride", "total_rides", "night_rides" -> sport.add(item)
                            "streak_days", "monthly_rides" -> timeLimited.add(item)
                            "single_distance" -> personal.add(item)
                            else -> sport.add(item)
                        }
                    }
                    handler.post {
                        sportLifeAchievements.value = sport
                        timeLimitedAchievements.value = timeLimited
                        personalBestAchievements.value = personal
                    }
                    Unit
                }
            }.start()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "成就勋章") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("${AppRoutes.MAIN}?tab=profile") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab.value.ordinal, containerColor = Color(0xFF3498DB)) {
                AchievementTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab.value == tab,
                        onClick = { selectedTab.value = tab },
                        text = {
                            Text(
                                text = when (tab) {
                                    AchievementTab.SPORT_LIFE -> "运动人生"
                                    AchievementTab.TIME_LIMITED_CHALLENGE -> "限时挑战"
                                    AchievementTab.PERSONAL_BEST -> "个人最佳"
                                },
                                color = Color.White
                            )
                        }
                    )
                }
            }
            when (selectedTab.value) {
                AchievementTab.SPORT_LIFE -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(sportLifeAchievements.value) { item ->
                            AchievementItemCard(achievement = item)
                        }
                    }
                }
                AchievementTab.TIME_LIMITED_CHALLENGE -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = timeLimitedAchievements.value) { item ->
                            AchievementItemCard(achievement = item)
                        }
                    }
                }
                AchievementTab.PERSONAL_BEST -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = personalBestAchievements.value) { item ->
                            AchievementItemCard(achievement = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItemCard(achievement: AchievementItem) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(if (achievement.isUnlocked) Color(0xFFF3E5F5) else Color.Gray).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF9C27B0)), contentAlignment = Alignment.Center) {
                Text(text = achievement.id.toString(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = achievement.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp), textAlign = TextAlign.Center)
        Text(text = achievement.description, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)
        if (!achievement.isUnlocked) {
            LinearProgressIndicator(progress = achievement.progress / 100f, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), color = Color(0xFF3498DB))
            Text(text = "${achievement.progress}%", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// 移除本地示例数据，改为数据库加载

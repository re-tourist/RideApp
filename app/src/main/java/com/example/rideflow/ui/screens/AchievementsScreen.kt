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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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
fun AchievementsScreen(navController: NavController) {
    val selectedTab = remember { mutableStateOf(AchievementTab.SPORT_LIFE) }
    val sportLifeAchievements = remember { getSportLifeAchievements() }
    val timeLimitedAchievements = remember { getTimeLimitedAchievements() }
    val personalBestAchievements = remember { getPersonalBestAchievements() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "成就勋章") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                        items(sportLifeAchievements) { item ->
                            AchievementItemCard(achievement = item)
                        }
                    }
                }
                AchievementTab.TIME_LIMITED_CHALLENGE -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = timeLimitedAchievements) { item ->
                            AchievementItemCard(achievement = item)
                        }
                    }
                }
                AchievementTab.PERSONAL_BEST -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = personalBestAchievements) { item ->
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

fun getSportLifeAchievements(): List<AchievementItem> {
    return listOf(
        AchievementItem(id = 1, title = "KG等级3", description = "累计运动天数达到一定标准", isUnlocked = true),
        AchievementItem(id = 2, title = "跑步等级R2", description = "累计跑步14.07公里", isUnlocked = true),
        AchievementItem(id = 3, title = "累计运动5天", description = "连续运动5天", isUnlocked = true),
        AchievementItem(id = 4, title = "行走等级H1", description = "累计行走0公里", isUnlocked = false, progress = 0),
        AchievementItem(id = 5, title = "骑行等级C1", description = "累计骑行0公里", isUnlocked = false, progress = 0),
        AchievementItem(id = 6, title = "瑜伽等级Y1", description = "累计瑜伽0分钟", isUnlocked = false, progress = 0),
        AchievementItem(id = 7, title = "训练等级W1", description = "累计训练0分钟", isUnlocked = false, progress = 0)
    )
}

fun getTimeLimitedAchievements(): List<AchievementItem> {
    return listOf(
        AchievementItem(id = 8, title = "冬季挑战", description = "在冬季完成30次运动", isUnlocked = false, progress = 10),
        AchievementItem(id = 9, title = "新年挑战", description = "在新年完成50次运动", isUnlocked = false, progress = 0)
    )
}

fun getPersonalBestAchievements(): List<AchievementItem> {
    return listOf(
        AchievementItem(id = 10, title = "最长距离", description = "单次骑行最长距离", isUnlocked = true),
        AchievementItem(id = 11, title = "最快速度", description = "骑行最快速度", isUnlocked = true)
    )
}


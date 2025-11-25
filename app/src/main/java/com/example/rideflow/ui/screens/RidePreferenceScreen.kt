package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RidePreferenceScreen(navController: NavController) {
    val bg = Color(0xFFE8F5E9)
    val sections = listOf(
        "热点风标" to listOf("宝藏新咖", "2025NBA", "大家都在看"),
        "情感星座" to listOf("情绪生活", "星座运势", "国学测算", "树洞语录"),
        "生活学习" to listOf("旅游出行", "摄影拍照", "美食种草", "厨艺教程", "美食日常分享", "教育", "校园生活", "人文艺术"),
        "搞笑&萌宠" to listOf("喜剧演员", "狗狗可爱秀", "异宠野生乐园"),
        "时尚美妆" to listOf("潮流主理人", "宝藏产品分享", "时尚医美", "时尚穿搭")
    )
    val selected = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑行偏好") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bg,
                            contentColor = Color.Black
                        )
                    ) { Text(text = "跳过") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bg)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val count = selected.values.sumOf { it.size }
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(text = "我选好了（已选${count}个）", fontSize = 16.sp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(bg)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sections) { section ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xCCFFFFFF)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = section.first, fontSize = 18.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRowSection(
                            labels = section.second,
                            selected = selected.getOrPut(section.first) { mutableStateListOf() }
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun FlowRowSection(labels: List<String>, selected: SnapshotStateList<String>) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            val isSelected = label in selected
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) selected.remove(label) else selected.add(label)
                },
                leadingIcon = if (isSelected) {
                    { Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White) }
                } else null,
                label = { Text(text = label) },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF1F8E9),
                    labelColor = Color(0xFF2E7D32),
                    iconColor = Color(0xFF2E7D32),
                    selectedContainerColor = Color(0xFF66BB6A),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFBDBDBD),
                    selectedBorderColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}
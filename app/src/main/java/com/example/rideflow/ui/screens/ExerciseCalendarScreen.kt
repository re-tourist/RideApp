package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCalendarScreen(navController: NavController) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val exerciseDates = remember {
        mutableStateOf(setOf(
            LocalDate.of(2025, 11, 5),
            LocalDate.of(2025, 11, 12),
            LocalDate.of(2025, 11, 18),
            LocalDate.of(2025, 11, 24)
        ))
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "运动日历") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) { Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上一月") }
                Text(text = "${currentMonth.value.year}年 ${currentMonth.value.month.value}月", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) { Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下一月") }
            }
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        CalendarStatItem("运动里程(km)", "0")
                        CalendarStatItem("运动时间(h)", "0")
                        CalendarStatItem("累计爬升(m)", "0")
                        CalendarStatItem("运动次数", "0")
                    }
                }
            }
            Text(text = "本月运动日历", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                weekdays.forEach { weekday ->
                    Text(text = weekday, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
            CalendarGrid(yearMonth = currentMonth.value, exerciseDates = exerciseDates.value)
        }
    }
}

@Composable
fun CalendarStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun CalendarGrid(yearMonth: YearMonth, exerciseDates: Set<LocalDate>) {
    val calendar = java.util.Calendar.getInstance()
    calendar.set(java.util.Calendar.YEAR, yearMonth.year)
    calendar.set(java.util.Calendar.MONTH, yearMonth.monthValue - 1)
    calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    val daysInMonth = yearMonth.lengthOfMonth()
    val rows = (firstDayOfWeek - 2 + daysInMonth + 6) / 7
    Column(modifier = Modifier.fillMaxWidth()) {
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col
                    val dayOfMonth = dayIndex - (firstDayOfWeek - 2) + 1
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = LocalDate.of(yearMonth.year, yearMonth.month, dayOfMonth)
                        val hasExercise = exerciseDates.contains(date)
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp).clip(CircleShape).background(if (hasExercise) Color(0xFFE3F2FD) else Color.Transparent).border(width = if (hasExercise) 2.dp else 0.dp, color = if (hasExercise) Color(0xFF2196F3) else Color.Transparent, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = dayOfMonth.toString(), fontSize = 14.sp, color = if (hasExercise) Color(0xFF2196F3) else Color.Black)
                            if (hasExercise) {
                                Box(modifier = Modifier.padding(bottom = 4.dp).align(Alignment.BottomCenter).clip(CircleShape).background(Color(0xFFFFC107)).aspectRatio(0.08f))
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}


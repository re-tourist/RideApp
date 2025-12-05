package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCalendarScreen(navController: NavController, userId: String) {
    // 当前显示的月份
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val exerciseDates = remember { mutableStateOf(setOf<LocalDate>()) }
    var totalDistanceKm by remember { mutableStateOf(0.0) }
    var totalDurationSec by remember { mutableStateOf(0) }
    var totalCount by remember { mutableStateOf(0) }
    val handler = Handler(Looper.getMainLooper())
    LaunchedEffect(currentMonth.value, userId) {
        val uid = userId.toIntOrNull()
        if (uid != null) {
            Thread {
                val ym = currentMonth.value
                val start = ym.atDay(1).toString() + " 00:00:00"
                val end = ym.plusMonths(1).atDay(1).toString() + " 00:00:00"
                val days = mutableSetOf<LocalDate>()
                var sumDist = 0.0
                var sumDur = 0
                var cnt = 0
                DatabaseHelper.processQuery(
                    "SELECT DATE(start_time) AS d, SUM(distance_km), SUM(duration_sec), COUNT(*) FROM user_ride_records WHERE user_id = ? AND start_time >= ? AND start_time < ? GROUP BY d",
                    listOf(uid, start, end)
                ) { rs ->
                    while (rs.next()) {
                        val d = LocalDate.parse(rs.getString(1))
                        val dist = rs.getDouble(2)
                        val dur = rs.getInt(3)
                        val c = rs.getInt(4)
                        days.add(d)
                        sumDist += dist
                        sumDur += dur
                        cnt += c
                    }
                    handler.post {
                        exerciseDates.value = days
                        totalDistanceKm = sumDist
                        totalDurationSec = sumDur
                        totalCount = cnt
                    }
                    Unit
                }
            }.start()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "运动日历") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFB74D)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 月份选择器
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "上一月")
                }
                Text(
                    text = "${currentMonth.value.year}年 ${currentMonth.value.month.getValue()}月",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "下一月")
                }
            }

            // 运动统计信息
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CalendarStatItem("运动里程(km)", String.format(Locale.getDefault(), "%.2f", totalDistanceKm))
                        CalendarStatItem("运动时间(h)", String.format(Locale.getDefault(), "%.1f", totalDurationSec / 3600.0))
                        CalendarStatItem("累计爬升(m)", "0")
                        CalendarStatItem("运动次数", totalCount.toString())
                    }
                }
            }

            // 日历标题
            Text(
                text = "本月运动日历",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            // 星期标题
            Row(modifier = Modifier.fillMaxWidth()) {
                val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                for (weekday in weekdays) {
                    Text(
                        text = weekday,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }

            // 日历网格
            CalendarGrid(
                yearMonth = currentMonth.value,
                exerciseDates = exerciseDates.value
            )
        }
    }
}

@Composable
fun CalendarStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CalendarGrid(yearMonth: YearMonth, exerciseDates: Set<LocalDate>) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, yearMonth.year)
    calendar.set(Calendar.MONTH, yearMonth.monthValue - 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // 获取当月第一天是星期几（1=周日, 2=周一, ..., 7=周六）
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    // 获取当月天数
    val daysInMonth = yearMonth.lengthOfMonth()

    // 计算需要显示的行数
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
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(if (hasExercise) Color(0xFFE3F2FD) else Color.Transparent)
                                .border(
                                    width = if (hasExercise) 2.dp else 0.dp,
                                    color = if (hasExercise) Color(0xFF2196F3) else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayOfMonth.toString(),
                                fontSize = 14.sp,
                                color = if (hasExercise) Color(0xFF2196F3) else Color.Black
                            )
                            // 如果有运动，在底部显示一个小圆点
                            if (hasExercise) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFC107))
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 4.dp)
                                )
                            }
                        }
                    } else {
                        // 空白单元格
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCalendarScreenPreview() {
    ExerciseCalendarScreen(navController = androidx.navigation.compose.rememberNavController(), userId = "1")
}

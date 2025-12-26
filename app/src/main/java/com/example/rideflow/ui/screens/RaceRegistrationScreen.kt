package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceRegistrationScreen(navController: NavController, raceId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    var isAgreed by remember { mutableStateOf(false) }
    var hasCard by remember { mutableStateOf(false) }
    val handler = Handler(Looper.getMainLooper())
    val context = LocalContext.current
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "赛事报名") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 报名卡部分
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "报名卡 (必填)",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "报名卡",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("${AppRoutes.ADD_REGISTRATION_CARD}/${raceId}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(text = "填写报名卡", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 同意声明
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextButton(
                        onClick = { isAgreed = !isAgreed },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Checkbox(
                            checked = isAgreed,
                            onCheckedChange = { isAgreed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Blue
                            )
                        )
                        Text(
                            text = "我已阅读并同意免责声明",
                            style = TextStyle(fontSize = 14.sp, color = Color.Blue),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull() ?: 0
                            if (userId <= 0) {
                                Toast.makeText(context, "用户未登录，请先登录后再报名", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            Thread {
                                val sql = """
                                    INSERT INTO user_races (user_id, race_id, relation, status, notes)
                                    VALUES (?, ?, 'registered', 'upcoming', '报名成功')
                                    ON DUPLICATE KEY UPDATE 
                                        status = VALUES(status),
                                        notes = VALUES(notes)
                                """.trimIndent()
                                val result = DatabaseHelper.executeUpdate(sql, listOf(userId, raceId))
                                handler.post {
                                    if (result >= 0) {
                                        Toast.makeText(context, "报名成功", Toast.LENGTH_SHORT).show()
                                        navController.navigate(com.example.rideflow.navigation.AppRoutes.RACE)
                                    } else {
                                        Toast.makeText(context, "报名失败，请稍后重试", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.start()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = isAgreed
                    ) {
                        Text(text = "下一步", fontSize = 16.sp)
                    }
                }
            }
        }
    )
}

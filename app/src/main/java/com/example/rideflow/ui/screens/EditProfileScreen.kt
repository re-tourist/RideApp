package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditProfileScreen(onBackPress: () -> Unit) {
    var nickname by remember { mutableStateOf("这个人比较厉害耶") }
    var bio by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(0) } // 0未知，1男，2女
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var emergencyContact by remember { mutableStateOf("") } // 紧急联系人
    
    // 格式化日期显示
    fun formatDate(date: LocalDate?): String {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: ""
    }
    
    // 保存资料函数
    fun saveProfile() {
        // 这里可以添加保存逻辑，例如调用API保存到服务器
        // 目前只是简单地打印日志表示保存成功
        println("保存成功：昵称=$nickname, 个人简介=$bio, 邮箱=$email, 性别=$gender, 出生日期=${formatDate(birthDate)}, 紧急联系人=$emergencyContact")
        // 保存成功后返回上一页
        onBackPress()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color(0xFF3498DB)
                )
            }
            Text(
                text = "编辑资料",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            TextButton(onClick = { saveProfile() }) {
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    color = Color(0xFF3498DB)
                )
            }
        }
        
        // 个人信息编辑区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像显示部分
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户头像",
                    tint = Color(0xFFBDC3C7),
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center)
                )
            }
            
            // 个人信息输入部分
            Column(modifier = Modifier.fillMaxWidth()) {
                // 昵称输入
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "昵称",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "请输入昵称") },
                        maxLines = 1
                    )
                }
                
                // 个人简介输入
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "个人简介",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text(text = "介绍一下自己吧") },
                        maxLines = 4
                    )
                }
                
                // 性别选择
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "性别",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = gender == 0,
                            onClick = { gender = 0 },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(text = "未知", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(
                            selected = gender == 1,
                            onClick = { gender = 1 },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(text = "男", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(
                            selected = gender == 2,
                            onClick = { gender = 2 },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(text = "女", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
                
                // 出生日期选择
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "出生日期",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = formatDate(birthDate),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "YYYY-MM-DD") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack, // 简化处理，实际应该使用日历图标
                                    contentDescription = "选择日期"
                                )
                            }
                        }
                    )
                }
                
                // 邮箱绑定
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "绑定邮箱",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "请输入邮箱地址") },
                        maxLines = 1,
                        // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
                
                // 紧急联系人
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(
                        text = "紧急联系人",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "请输入紧急联系人姓名和电话") },
                        maxLines = 2,
                        supportingText = { Text(text = "最多100个字符") }
                    )
                }
                
                // 简化的日期选择器处理
                if (showDatePicker) {
                    // 这里应该显示一个真实的DatePicker，为了简化实现，我们只显示一个消息
                    // 实际项目中应使用Material 3的DatePicker组件或其他第三方日期选择器
                    println("打开日期选择器")
                    // 模拟选择今天的日期
                    birthDate = LocalDate.now()
                    showDatePicker = false
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(onBackPress = {})
}

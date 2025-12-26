package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRegistrationCardScreen(navController: NavController, raceId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    val context = LocalContext.current
    // 表单字段状态
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("二代身份证") }
    var idNumber by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var detailedAddress by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }

    // 下拉菜单状态
    var genderExpanded by remember { mutableStateOf(false) }
    var idTypeExpanded by remember { mutableStateOf(false) }
    var addressExpanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("请选择", "男", "女")
    val idTypeOptions = listOf("二代身份证", "护照", "军官证")
    val addressOptions = listOf("请选择省/市/区") // 实际应用中应使用地址选择器

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "添加报名卡") },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Text(
                        text = "报名人信息将用于核验赛事报名资格，请如实填写并核对信息！",
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "*为必填项",
                        style = TextStyle(fontSize = 12.sp, color = Color.Red)
                    )

                    // 表单字段
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                    // 姓名
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "姓名 *") },
                        placeholder = { Text(text = "请保持与证件姓名一致") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 性别
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        TextField(
                            value = gender.ifEmpty { "请选择" },
                            onValueChange = {},
                            label = { Text(text = "性别 *") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 手机
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(text = "手机 *") },
                        placeholder = { Text(text = "请填写真实手机号码") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 证件类型
                    ExposedDropdownMenuBox(
                        expanded = idTypeExpanded,
                        onExpandedChange = { idTypeExpanded = !idTypeExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        TextField(
                            value = idType,
                            onValueChange = {},
                            label = { Text(text = "证件类型 *") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = idTypeExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = idTypeExpanded,
                            onDismissRequest = { idTypeExpanded = false }
                        ) {
                            idTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        idType = option
                                        idTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 证件号码
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        label = { Text(text = "证件号码 *") },
                        placeholder = { Text(text = "请保持与证件一致") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    val birthdayCalendar = remember { Calendar.getInstance() }
                    val birthdayFormatter = remember {
                        SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = birthday.ifEmpty { "" },
                            onValueChange = {},
                            label = { Text(text = "出生日期 *") },
                            placeholder = { Text(text = "请选择") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            singleLine = true
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    DatePickerDialog(
                                        navController.context,
                                        { _, year, month, dayOfMonth ->
                                            birthdayCalendar.set(Calendar.YEAR, year)
                                            birthdayCalendar.set(Calendar.MONTH, month)
                                            birthdayCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            birthday = birthdayFormatter.format(birthdayCalendar.time)
                                        },
                                        birthdayCalendar.get(Calendar.YEAR),
                                        birthdayCalendar.get(Calendar.MONTH),
                                        birthdayCalendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                        )
                    }

                    // 邮箱
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "邮箱") },
                        placeholder = { Text(text = "选填，邮箱地址") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 地址
                    ExposedDropdownMenuBox(
                        expanded = addressExpanded,
                        onExpandedChange = { addressExpanded = !addressExpanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        TextField(
                            value = address.ifEmpty { "请选择" },
                            onValueChange = {},
                            label = { Text(text = "地址") },
                            placeholder = { Text(text = "选填，省/市/区") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = addressExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = addressExpanded,
                            onDismissRequest = { addressExpanded = false }
                        ) {
                            addressOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        address = option
                                        addressExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 详细地址
                    OutlinedTextField(
                        value = detailedAddress,
                        onValueChange = { detailedAddress = it },
                        label = { Text(text = "详细地址") },
                        placeholder = { Text(text = "选填，小区、写字楼、门牌号等") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 紧急联系人
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text(text = "紧急联系人") },
                        placeholder = { Text(text = "选填，紧急联系人姓名") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // 紧急联系人电话
                    OutlinedTextField(
                        value = emergencyPhone,
                        onValueChange = { emergencyPhone = it },
                        label = { Text(text = "紧急联系人电话") },
                        placeholder = { Text(text = "选填，紧急联系人手机") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                    )

                    // 提交按钮
                    Button(
                        onClick = {
                            val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull() ?: 0
                            if (userId <= 0) {
                                android.widget.Toast.makeText(context, "用户未登录，请先登录后再提交报名卡", android.widget.Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (name.isBlank() || gender.isBlank() || phone.isBlank() || idNumber.isBlank() || birthday.isBlank()) {
                                android.widget.Toast.makeText(context, "请填写所有必填项", android.widget.Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val genderValue = if (gender == "请选择") "" else gender
                            val addressValue = if (address == "请选择省/市/区") "" else address
                            Thread {
                                val sql = """
                                    INSERT INTO user_registration_cards (
                                        user_id, name, gender, phone, id_type, id_number, birthday,
                                        email, address, detailed_address, emergency_contact, emergency_phone
                                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                    ON DUPLICATE KEY UPDATE
                                        name = VALUES(name),
                                        gender = VALUES(gender),
                                        phone = VALUES(phone),
                                        id_type = VALUES(id_type),
                                        id_number = VALUES(id_number),
                                        birthday = VALUES(birthday),
                                        email = VALUES(email),
                                        address = VALUES(address),
                                        detailed_address = VALUES(detailed_address),
                                        emergency_contact = VALUES(emergency_contact),
                                        emergency_phone = VALUES(emergency_phone)
                                """.trimIndent()
                                val params = listOf(
                                    userId,
                                    name,
                                    genderValue,
                                    phone,
                                    idType,
                                    idNumber,
                                    birthday,
                                    email,
                                    addressValue,
                                    detailedAddress,
                                    emergencyContact,
                                    emergencyPhone
                                )
                                val result = com.example.rideflow.backend.DatabaseHelper.executeUpdate(sql, params)
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    if (result >= 0) {
                                        android.widget.Toast.makeText(context, "报名卡保存成功", android.widget.Toast.LENGTH_SHORT).show()
                                        onBack()
                                    } else {
                                        android.widget.Toast.makeText(context, "报名卡保存失败，请稍后重试", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.start()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(text = "提交", fontSize = 16.sp)
                    }
                }
            }
        }
    }
)
}

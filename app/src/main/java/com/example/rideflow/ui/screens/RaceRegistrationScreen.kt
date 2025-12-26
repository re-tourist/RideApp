package com.example.rideflow.ui.screens

import android.app.DatePickerDialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.R
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.navigation.AppRoutes
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceRegistrationScreen(navController: NavController, raceId: Int = 0, onBack: () -> Unit = { navController.popBackStack() }) {
    var isAgreed by remember { mutableStateOf(false) }
    val handler = Handler(Looper.getMainLooper())
    val context = LocalContext.current
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()

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

    var genderExpanded by remember { mutableStateOf(false) }
    var idTypeExpanded by remember { mutableStateOf(false) }
    var addressExpanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("请选择", "男", "女")
    val idTypeOptions = listOf("二代身份证", "护照", "军官证")
    val addressOptions = listOf("请选择省/市/区")

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
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "报名人信息将用于核验赛事报名资格，请如实填写并核对信息！",
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "*为必填项",
                        style = TextStyle(fontSize = 12.sp, color = Color.Red)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "姓名 *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
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
                        DropdownMenu(
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
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(text = "手机 *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
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
                        DropdownMenu(
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
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        label = { Text(text = "证件号码 *") },
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
                                        context,
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
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "邮箱") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
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
                        DropdownMenu(
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
                    OutlinedTextField(
                        value = detailedAddress,
                        onValueChange = { detailedAddress = it },
                        label = { Text(text = "详细地址") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text(text = "紧急联系人") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = emergencyPhone,
                        onValueChange = { emergencyPhone = it },
                        label = { Text(text = "紧急联系人电话") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                            if (name.isBlank() || gender.isBlank() || phone.isBlank() || idNumber.isBlank() || birthday.isBlank()) {
                                Toast.makeText(context, "请填写所有必填项", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val genderValue = if (gender == "请选择") "" else gender
                            val addressValue = if (address == "请选择省/市/区") "" else address
                            Thread {
                                val cardSql = """
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
                                val cardParams = listOf(
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
                                val cardResult = DatabaseHelper.executeUpdate(cardSql, cardParams)
                                if (cardResult < 0) {
                                    handler.post {
                                        Toast.makeText(context, "报名信息保存失败，请稍后重试", Toast.LENGTH_SHORT).show()
                                    }
                                    return@Thread
                                }
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
                                        navController.navigate(AppRoutes.RACE)
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
                        Text(text = "报名", fontSize = 16.sp)
                    }
                }
            }
        }
    )
}

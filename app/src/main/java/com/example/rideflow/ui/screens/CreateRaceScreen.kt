package com.example.rideflow.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.ui.components.ImageUploadComponent
import com.example.rideflow.ui.theme.RideFlowTheme
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRaceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val handler = Handler(Looper.getMainLooper())
    
    // 基本信息
    var raceTitle by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var raceDescription by remember { mutableStateOf("") }
    
    // 图片上传相关
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    
    // 赛事设置
    var raceCategory by remember { mutableIntStateOf(0) } // 0: 娱乐赛, 1: 竞速赛
    var registrationTime by remember { mutableStateOf("") }
    var raceTime by remember { mutableStateOf("") }
    var raceLocation by remember { mutableStateOf("") }
    var participationForm by remember { mutableIntStateOf(0) } // 0: 个人赛, 1: 团体赛
    
    // 赛事标签
    val tagList = listOf(
        "新手友好", "进阶挑战", "周末赛事", "节假日", "亲子赛事", 
        "团队赛", "个人赛", "短途", "长途", "山地赛", "公路赛", "夜赛"
    )
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    
    // 签到字段
    var checkMobile by remember { mutableStateOf(true) }
    var checkName by remember { mutableStateOf(true) }
    var checkId by remember { mutableStateOf(false) }
    var checkRemark by remember { mutableStateOf(false) }
    
    // 奖品设置
    var prizes by remember { mutableStateOf(listOf("")) }
    
    // 参赛口令
    var participationPassword by remember { mutableStateOf("") }

    // 添加新奖品
    fun addPrize() {
        prizes = prizes + ""
    }
    
    // 更新奖品
    fun updatePrize(index: Int, value: String) {
        val newPrizes = prizes.toMutableList()
        newPrizes[index] = value
        prizes = newPrizes
    }
    
    // 移除奖品
    fun removePrize(index: Int) {
        if (prizes.size > 1) {
            prizes = prizes.filterIndexed { i, _ -> i != index }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "创建赛事活动") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                // 赛事图片上传区域
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(text = "赛事图片", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    ImageUploadComponent(
                        selectedImage = selectedImageFile,
                        onImageSelected = { file ->
                            selectedImageFile = file
                            // 这里可以添加图片上传到服务器的逻辑
                            println("图片已选择: ${file.absolutePath}")
                        },
                        isCircular = false
                    )
                    Text(text = "图片建议大小828×436或等比缩放", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                // 基本信息
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(text = "基本信息", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    // 标题
                    OutlinedTextField(
                        value = raceTitle,
                        onValueChange = { raceTitle = it },
                        label = { Text("标题 *") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = raceTitle.isEmpty()
                    )
                    
                    // 主办方
                    OutlinedTextField(
                        value = organizer,
                        onValueChange = { organizer = it },
                        label = { Text("主办方 *") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = organizer.isEmpty()
                    )
                    
                    // 赛事介绍
                    OutlinedTextField(
                        value = raceDescription,
                        onValueChange = { raceDescription = it },
                        label = { Text("赛事介绍") },
                        placeholder = { Text("请输入赛事的简单介绍") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }

                // 赛事设置
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(text = "赛事设置", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    // 赛事类别
                    Row(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "赛事类别", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Row {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = raceCategory == 0,
                                    onClick = { raceCategory = 0 }
                                )
                                Text(text = "娱乐赛", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                RadioButton(
                                    selected = raceCategory == 1,
                                    onClick = { raceCategory = 1 }
                                )
                                Text(text = "竞速赛", modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                    
                    // 报名时间段
                    OutlinedTextField(
                        value = registrationTime,
                        onValueChange = { registrationTime = it },
                        label = { Text("报名时间段 *") },
                        placeholder = { Text("格式：yyyy-MM-dd HH:mm ~ yyyy-MM-dd HH:mm") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = registrationTime.isEmpty()
                    )

                    // 比赛时间段
                    OutlinedTextField(
                        value = raceTime,
                        onValueChange = { raceTime = it },
                        label = { Text("比赛时间段 *") },
                        placeholder = { Text("格式：yyyy-MM-dd HH:mm ~ yyyy-MM-dd HH:mm") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = raceTime.isEmpty()
                    )

                    // 比赛地点
                    OutlinedTextField(
                        value = raceLocation,
                        onValueChange = { raceLocation = it },
                        label = { Text("比赛地点 *") },
                        placeholder = { Text("请输入比赛地点") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = raceLocation.isEmpty()
                    )
                    
                    // 参赛形式
                    Row(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "参赛形式", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Row {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = participationForm == 0,
                                    onClick = { participationForm = 0 }
                                )
                                Text(text = "个人赛", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                RadioButton(
                                    selected = participationForm == 1,
                                    onClick = { participationForm = 1 }
                                )
                                Text(text = "团体赛", modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                    
                    // 赛事标签
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "赛事标签", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Column {
                            var rowIndex = 0
                            while (rowIndex < tagList.size) {
                                val endIndex = minOf(rowIndex + 4, tagList.size)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (i in rowIndex until endIndex) {
                                        val tag = tagList[i]
                                        FilterChip(
                                            selected = selectedTags.contains(tag),
                                            onClick = {
                                                selectedTags = if (selectedTags.contains(tag)) {
                                                    selectedTags - tag
                                                } else {
                                                    selectedTags + tag
                                                }
                                            },
                                            label = { Text(tag) },
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF2196F3), // 使用亮蓝色作为选中背景色
                                                selectedLabelColor = Color.White, // 选中时文字为白色
                                                containerColor = Color(0xFFF5F5F5), // 未选中时背景为浅灰色
                                                labelColor = Color.Black // 未选中时文字为黑色
                                            )
                                        )
                                    }
                                }
                                rowIndex = endIndex
                            }
                        }
                    }
                    
                    // 签到字段
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "签到字段", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = checkMobile,
                                    onCheckedChange = { checkMobile = it }
                                )
                                Text(text = "手机号", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                Checkbox(
                                    checked = checkName,
                                    onCheckedChange = { checkName = it }
                                )
                                Text(text = "姓名", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                Checkbox(
                                    checked = checkId,
                                    onCheckedChange = { checkId = it }
                                )
                                Text(text = "证件号", modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checkRemark,
                                onCheckedChange = { checkRemark = it }
                            )
                            Text(text = "其他", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    
                    // 奖品设置
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "奖品设置", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        prizes.forEachIndexed { index, prize ->
                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                OutlinedTextField(
                                    value = prize,
                                    onValueChange = { updatePrize(index, it) },
                                    label = { Text(if (index == 0) "一等奖" else "奖品") },
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                    singleLine = true
                                )
                                if (prizes.size > 1) {
                                    IconButton(onClick = { removePrize(index) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "删除奖品",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                        TextButton(onClick = { addPrize() }) {
                            Text(text = "+ 添加奖品", color = Color(0xFF007AFF))
                        }
                    }
                    
                    // 参赛口令
                    OutlinedTextField(
                        value = participationPassword,
                        onValueChange = { participationPassword = it },
                        label = { Text("参赛口令") },
                        placeholder = { Text("请输入参赛口令，邀请请凭口令参赛") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 创建按钮
                Button(
                    onClick = {
                        val regex = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}""")
                        val match = regex.find(raceTime)?.value
                        if (match == null) {
                            Toast.makeText(context, "请输入有效时间段：yyyy-MM-dd HH:mm ~ yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show()
                        } else {
                            Thread {
                                val isOpen = 1
                                val coverUrl: String? = null
                                val insertId = DatabaseHelper.insertAndReturnId(
                                    "INSERT INTO events (title, event_date, location, is_open, cover_image_url, description) VALUES (?,?,?,?,?,?)",
                                    listOf<Any>(raceTitle, match, raceLocation, isOpen, coverUrl ?: "", raceDescription)
                                )
                                if (insertId != null) {
                                    selectedTags.forEach { tag ->
                                        DatabaseHelper.executeUpdate(
                                            "INSERT INTO event_tags (event_id, tag_name) VALUES (?,?)",
                                            listOf(insertId, tag)
                                        )
                                    }
                                    handler.post {
                                        Toast.makeText(context, "赛事已创建", Toast.LENGTH_SHORT).show()
                                        onBack()
                                    }
                                } else {
                                    handler.post { Toast.makeText(context, "创建失败，请稍后重试", Toast.LENGTH_SHORT).show() }
                                }
                            }.start()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = raceTitle.isNotEmpty() && organizer.isNotEmpty() && 
                             registrationTime.isNotEmpty() && raceTime.isNotEmpty() && raceLocation.isNotEmpty()
                ) {
                    Text(text = "创建赛事", fontSize = 16.sp)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateRaceScreenPreview() {
    RideFlowTheme {
        CreateRaceScreen(onBack = {})
    }
}

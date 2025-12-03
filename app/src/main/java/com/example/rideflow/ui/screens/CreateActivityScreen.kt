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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    
    // 基本信息
    var activityTitle by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }
    
    // 图片上传相关
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }
    
    // 活动设置
    var activityCategory by remember { mutableIntStateOf(0) } // 0: 骑行活动, 1: 跑步活动, 2: 徒步活动, 3: 其他活动
    var registrationTime by remember { mutableStateOf("") }
    var activityTime by remember { mutableStateOf("") }
    var activityLocation by remember { mutableStateOf("") }
    var participationForm by remember { mutableIntStateOf(0) } // 0: 个人参与, 1: 团体参与
    
    // 活动标签
    val tagList = listOf(
        "新手友好", "进阶挑战", "周末活动", "节假日", 
        "亲子活动", "摄影活动", "美食路线", "夜骑", 
        "公益活动", "竞赛活动", "长距离", "短距离"
    )
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    
    // 签到字段
    var checkMobile by remember { mutableStateOf(true) }
    var checkName by remember { mutableStateOf(true) }
    var checkId by remember { mutableStateOf(false) }
    var checkRemark by remember { mutableStateOf(false) }
    
    // 活动福利
    var benefits by remember { mutableStateOf(listOf("")) }
    
    // 参与口令
    var participationPassword by remember { mutableStateOf("") }

    // 添加新福利
    fun addBenefit() {
        benefits = benefits + ""
    }
    
    // 更新福利
    fun updateBenefit(index: Int, value: String) {
        val newBenefits = benefits.toMutableList()
        newBenefits[index] = value
        benefits = newBenefits
    }
    
    // 移除福利
    fun removeBenefit(index: Int) {
        if (benefits.size > 1) {
            benefits = benefits.filterIndexed { i, _ -> i != index }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "创建活动") },
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
                // 活动图片上传区域
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(text = "活动图片", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
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
                        value = activityTitle,
                        onValueChange = { activityTitle = it },
                        label = { Text("标题 *") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = activityTitle.isEmpty()
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
                    
                    // 活动介绍
                    OutlinedTextField(
                        value = activityDescription,
                        onValueChange = { activityDescription = it },
                        label = { Text("活动介绍") },
                        placeholder = { Text("请输入活动的简单介绍") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }

                // 活动设置
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(text = "活动设置", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    // 活动类别
                    Row(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "活动类别", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Column {
                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = activityCategory == 0,
                                        onClick = { activityCategory = 0 }
                                    )
                                    Text(text = "骑行活动", modifier = Modifier.padding(start = 4.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                    RadioButton(
                                        selected = activityCategory == 1,
                                        onClick = { activityCategory = 1 }
                                    )
                                    Text(text = "跑步活动", modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                            Row {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = activityCategory == 2,
                                        onClick = { activityCategory = 2 }
                                    )
                                    Text(text = "徒步活动", modifier = Modifier.padding(start = 4.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                    RadioButton(
                                        selected = activityCategory == 3,
                                        onClick = { activityCategory = 3 }
                                    )
                                    Text(text = "其他活动", modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                    }
                    
                    // 报名时间段
                    OutlinedTextField(
                        value = registrationTime,
                        onValueChange = { registrationTime = it },
                        label = { Text("报名时间段 *") },
                        placeholder = { Text("请选择报名开始和结束时间") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = registrationTime.isEmpty()
                    )

                    // 活动时间段
                    OutlinedTextField(
                        value = activityTime,
                        onValueChange = { activityTime = it },
                        label = { Text("活动时间段 *") },
                        placeholder = { Text("请选择活动开始和结束时间") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = activityTime.isEmpty()
                    )

                    // 活动地点
                    OutlinedTextField(
                        value = activityLocation,
                        onValueChange = { activityLocation = it },
                        label = { Text("活动地点 *") },
                        placeholder = { Text("请输入活动地点") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        isError = activityLocation.isEmpty()
                    )
                    
                    // 活动形式
                    Row(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "活动形式", fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Row {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = participationForm == 0,
                                    onClick = { participationForm = 0 }
                                )
                                Text(text = "个人活动", modifier = Modifier.padding(start = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                RadioButton(
                                    selected = participationForm == 1,
                                    onClick = { participationForm = 1 }
                                )
                                Text(text = "集体活动", modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                    
                    // 活动标签
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "活动标签", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
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
                    
                    // 签到方式
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "签到方式", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
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
                                Text(text = "证件号", modifier = Modifier.padding(start = 16.dp))
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
                    
                    // 活动福利
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(text = "活动福利", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        benefits.forEachIndexed { index, benefit ->
                            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                                OutlinedTextField(
                                    value = benefit,
                                    onValueChange = { updateBenefit(index, it) },
                                    label = { Text(if (index == 0) "福利一" else "福利") },
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                    singleLine = true
                                )
                                if (benefits.size > 1) {
                                    IconButton(onClick = { removeBenefit(index) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "删除福利",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                        TextButton(onClick = { addBenefit() }) {
                            Text(text = "+ 添加福利", color = Color(0xFF007AFF))
                        }
                    }
                    
                    // 参与口令
                    OutlinedTextField(
                        value = participationPassword,
                        onValueChange = { participationPassword = it },
                        label = { Text("参与口令") },
                        placeholder = { Text("请输入参与口令，邀请请凭口令参与") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 创建按钮
                Button(
                    onClick = {
                        // 这里可以添加创建活动的逻辑
                        // 目前只是简单返回
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = activityTitle.isNotEmpty() && organizer.isNotEmpty() && 
                             registrationTime.isNotEmpty() && activityTime.isNotEmpty() && activityLocation.isNotEmpty()
                ) {
                    Text(text = "创建活动", fontSize = 16.sp)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreateActivityScreenPreview() {
    RideFlowTheme {
        CreateActivityScreen(onBack = {})
    }
}

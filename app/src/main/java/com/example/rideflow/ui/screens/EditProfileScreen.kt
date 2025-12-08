package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.profile.EditProfileViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBackPress: () -> Unit) {
    val editProfileViewModel: EditProfileViewModel = koinViewModel()
    val userProfileState = editProfileViewModel.userProfile.collectAsState()
    val userProfile = userProfileState.value
    val formDataState = editProfileViewModel.formData.collectAsState()
    val formData = formDataState.value
    val isLoadingState = editProfileViewModel.isLoading.collectAsState()
    val isLoading = isLoadingState.value
    val errorMessageState = editProfileViewModel.errorMessage.collectAsState()
    val errorMessage = errorMessageState.value
    val updateSuccessState = editProfileViewModel.updateSuccess.collectAsState()
    val updateSuccess = updateSuccessState.value
    
    // 页面加载时获取用户资料
    LaunchedEffect(Unit) {
        editProfileViewModel.loadUserProfileData()
    }
    
    // 处理保存成功后的导航
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            onBackPress()
        }
    }
    
    // 格式化日期显示
    fun formatDate(dateString: String?): String {
        return dateString ?: ""
    }
    
    // 保存资料函数
    fun saveProfile() {
        editProfileViewModel.saveUserProfile()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "编辑资料",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { saveProfile() }) {
                Text("保存")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 加载状态和错误处理
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            errorMessage?.let { message ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载失败: $message",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // 头像区域
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "头像",
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { }) {
                    Text("更换头像")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 表单区域
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 昵称
                OutlinedTextField(
                    value = formData.nickname ?: "",
                    onValueChange = { editProfileViewModel.updateFormData(nickname = it) },
                    label = { Text("昵称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // 个人简介
                OutlinedTextField(
                    value = formData.bio ?: "",
                    onValueChange = { editProfileViewModel.updateFormData(bio = it) },
                    label = { Text("个人简介") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                // 性别选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("性别：", modifier = Modifier.align(Alignment.CenterVertically))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = formData.gender == "male",
                            onClick = { editProfileViewModel.updateFormData(gender = "male") },
                            label = { Text("男") }
                        )
                        FilterChip(
                            selected = formData.gender == "female",
                            onClick = { editProfileViewModel.updateFormData(gender = "female") },
                            label = { Text("女") }
                        )
                        FilterChip(
                            selected = formData.gender == "other",
                            onClick = { editProfileViewModel.updateFormData(gender = "other") },
                            label = { Text("未知") }
                        )
                    }
                }
                
                // 出生日期
                OutlinedTextField(
                    value = formData.birthday ?: "",
                    onValueChange = { editProfileViewModel.updateFormData(birthday = it) },
                    label = { Text("出生日期") },
                    placeholder = { Text("格式：xxxx-xx-xx") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // 邮箱
                OutlinedTextField(
                    value = formData.email ?: "",
                    onValueChange = { editProfileViewModel.updateFormData(email = it) },
                    label = { Text("邮箱") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // 紧急联系人
                OutlinedTextField(
                    value = formData.emergencyContact ?: "",
                    onValueChange = { editProfileViewModel.updateFormData(emergencyContact = it) },
                    label = { Text("紧急联系人") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(onBackPress = {})
}

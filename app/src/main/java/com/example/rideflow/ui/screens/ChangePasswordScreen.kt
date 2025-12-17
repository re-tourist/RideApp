package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rideflow.backend.AuthDatabaseHelper
import com.example.rideflow.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val userId = authViewModel.getCurrentUser()?.userId?.toString().orEmpty()
    val scope = rememberCoroutineScope()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val canSubmit = !isSubmitting && userId.isNotBlank() && oldPassword.isNotBlank() && newPassword.length >= 6 && newPassword == confirmPassword

    fun submit() {
        errorText = ""
        if (userId.isBlank()) {
            errorText = "当前账号状态异常，请重新登录后再试"
            return
        }
        if (oldPassword.isBlank()) {
            errorText = "请输入当前密码"
            return
        }
        if (newPassword.length < 6) {
            errorText = "新密码至少需要6个字符"
            return
        }
        if (newPassword != confirmPassword) {
            errorText = "两次输入的新密码不一致"
            return
        }

        isSubmitting = true
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                AuthDatabaseHelper.changePassword(userId = userId, oldPassword = oldPassword, newPassword = newPassword)
            }
            isSubmitting = false
            if (ok) {
                showSuccessDialog = true
            } else {
                errorText = "当前密码验证失败或更新未生效"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "修改密码", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("当前密码") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("新密码（至少6位）") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("确认新密码") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (errorText.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorText, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }
            Button(
                onClick = { submit() },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(text = if (isSubmitting) "正在保存" else "保存", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "为保障账号安全，建议定期更新密码。", fontSize = 12.sp, color = Color.Gray)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(text = "密码已更新") },
            text = { Text(text = "你的账号密码已成功更新。") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text(text = "返回")
                }
            }
        )
    }
}


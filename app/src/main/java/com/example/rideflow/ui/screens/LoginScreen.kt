package com.example.rideflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
// 使用完全限定名而不是导入
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview

import com.example.rideflow.auth.AuthState
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.theme.RideFlowTheme
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController? = null) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState = authViewModel.collectAuthState()
    val coroutineScope = rememberCoroutineScope()
    
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // 判断输入是否为邮箱
    fun isEmail(input: String): Boolean {
        return input.contains('@')
    }

    // 处理登录逻辑
    fun handleLogin() {
        errorMessage = ""

        // 输入验证
        if (usernameOrEmail.isBlank()) {
            errorMessage = "请输入昵称或邮箱"
            return
        }

        if (password.isBlank()) {
            errorMessage = "请输入密码"
            return
        }

        // 使用AuthViewModel处理登录
        coroutineScope.launch {
            authViewModel.login(usernameOrEmail, password)
        }
    }

    // 监听认证状态变化
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // 登录成功，跳转到主界面
                navController?.navigate(AppRoutes.MAIN) {
                    // 清空导航栈，防止用户返回登录页
                    popUpTo(AppRoutes.LOGIN) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // 显示认证错误
                errorMessage = (authState as AuthState.Error).errorMessage
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "欢迎回来",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "请登录以使用RideFlow的全部功能",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 登录表单
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 用户名/邮箱输入框
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                label = { Text("邮箱或昵称") },
                leadingIcon = {
                    Icon(
                        imageVector = if (isEmail(usernameOrEmail)) Icons.Filled.Email else Icons.Filled.Person,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isEmail(usernameOrEmail)) KeyboardType.Email else KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // 密码输入框
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = "密码")
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                singleLine = true
            )

            // 错误信息
            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 登录按钮
            Button(
                onClick = { handleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = authState !is AuthState.Authenticating,
                shape = MaterialTheme.shapes.medium
            ) {
                if (authState is AuthState.Authenticating) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(text = "登录", fontSize = 16.sp)
        }
            }

            // 注册链接
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "没有账号？")
                TextButton(onClick = { navController?.navigate(AppRoutes.REGISTER) }) {
                    Text(text = "立即注册")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    RideFlowTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        LoginScreen(navController = navController)
    }
}
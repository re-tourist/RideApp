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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.example.rideflow.auth.AuthState
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.navigation.AppRoutes
import com.example.rideflow.ui.theme.RideFlowTheme

@Composable
fun RegisterScreen(navController: NavController? = null, authViewModel: AuthViewModel = koinViewModel()) {
    // authViewModel already provided as parameter
    val authState = authViewModel.collectAuthState()
    val coroutineScope = rememberCoroutineScope()
    
    var email by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }
    
    // 验证邮箱格式
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
        return emailRegex.matches(email)
    }

    // 处理注册逻辑
    fun handleRegister() {
        registerError = ""
        
        // 输入验证
        if (email.isBlank()) {
            registerError = "请输入邮箱"
            return
        }
        
        if (!isValidEmail(email)) {
            registerError = "请输入有效的邮箱地址"
            return
        }
        
        if (nickname.isBlank()) {
            registerError = "请输入昵称"
            return
        }
        
        if (password.isBlank()) {
            registerError = "请输入密码"
            return
        }
        
        if (password.length < 6) {
            registerError = "密码至少需要6个字符"
            return
        }
        
        if (password != confirmPassword) {
            registerError = "两次输入的密码不一致"
            return
        }
        
        // 使用AuthViewModel处理注册
        coroutineScope.launch {
            authViewModel.register(email, nickname, password)
        }
    }

    // 监听认证状态变化
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // 注册成功后自动登录并跳转到主界面
                navController?.navigate(AppRoutes.MAIN) {
                    // 清空导航栈，防止用户返回注册页
                    popUpTo(AppRoutes.REGISTER) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // 显示认证错误
                registerError = (authState as AuthState.Error).errorMessage
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
            text = "创建账户",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "加入RideFlow骑行社区",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 注册表单
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 邮箱输入框
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("邮箱") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // 昵称输入框
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("昵称") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // 密码输入框
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码 (至少6位)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            // 确认密码输入框
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("确认密码") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                singleLine = true
            )

            // 错误信息
            if (registerError.isNotBlank()) {
                Text(
                    text = registerError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // 注册按钮
            Button(
                onClick = { handleRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = authState !is AuthState.Authenticating,
                shape = MaterialTheme.shapes.medium
            ) {
                if (authState is AuthState.Authenticating) {
                    CircularProgressIndicator()
                } else {
                    Text(text = "注册", fontSize = 16.sp)
                }
            }

            // 登录链接
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "已有账号？", fontSize = 14.sp)
                TextButton(onClick = { navController?.navigate(AppRoutes.LOGIN) }, contentPadding = PaddingValues(0.dp)) {
                    Text(text = "立即登录", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    // 预览用的空导航控制器
    val navController = androidx.navigation.compose.rememberNavController()
    // 直接使用koinViewModel()获取真实的AuthViewModel进行预览
    // 在实际开发中，也可以使用mockk库创建模拟对象
    RideFlowTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RegisterScreen(navController = navController)
        }
    }
}

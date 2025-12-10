package com.example.rideflow.auth

import com.example.rideflow.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 认证仓库
 * 处理用户登录、注册、登出等核心认证逻辑
 */
class AuthRepository {
    // 认证状态的可变状态流
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    
    // 暴露给外部的只读状态流
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * 用户登录方法
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     */
    suspend fun login(usernameOrEmail: String, password: String) {
        _authState.value = AuthState.Authenticating
        
        try {
            // 模拟网络请求延迟
            Thread.sleep(1000)
            
            // 在实际项目中，这里应该调用真实的登录API
            // 此处仅为演示，直接创建模拟用户数据
            
            // 简单判断输入是否有效
            if (usernameOrEmail.isBlank() || password.isBlank()) {
                throw Exception("用户名和密码不能为空")
            }
            
            // 模拟成功登录，创建用户数据
            val isEmail = usernameOrEmail.contains("@")
            val userData = UserData(
                userId = "user_${System.currentTimeMillis()}",
                nickname = if (isEmail) usernameOrEmail.substringBefore("@") else usernameOrEmail,
                email = if (isEmail) usernameOrEmail else "${usernameOrEmail}@example.com",
                avatarUrl = null,
                bio = "热爱骑行的用户"
            )
            
            _authState.value = AuthState.Authenticated(userData)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "登录失败，请重试")
        }
    }

    /**
     * 用户注册方法
     * @param email 邮箱
     * @param nickname 昵称
     * @param password 密码
     */
    suspend fun register(email: String, nickname: String, password: String) {
        _authState.value = AuthState.Authenticating
        
        try {
            // 模拟网络请求延迟
            Thread.sleep(1000)
            
            // 在实际项目中，这里应该调用真实的注册API
            // 此处仅为演示，直接创建模拟用户数据
            
            // 简单验证输入
            if (email.isBlank() || nickname.isBlank() || password.isBlank()) {
                throw Exception("请填写所有必填信息")
            }
            
            if (password.length < 6) {
                throw Exception("密码至少需要6位字符")
            }
            
            // 模拟成功注册，创建用户数据
            val userData = UserData(
                userId = "user_${System.currentTimeMillis()}",
                nickname = nickname,
                email = email,
                avatarUrl = null,
                bio = "新加入的骑行爱好者"
            )
            
            _authState.value = AuthState.Authenticated(userData)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "注册失败，请重试")
        }
    }

    /**
     * 用户登出方法
     */
    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * 清除认证错误
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        return _authState.value is AuthState.Authenticated
    }

    /**
     * 获取当前登录用户数据
     */
    fun getCurrentUser(): UserData? {
        return if (_authState.value is AuthState.Authenticated) {
            (_authState.value as AuthState.Authenticated).userData
        } else {
            null
        }
    }
}
package com.example.rideflow.auth

import com.example.rideflow.backend.AuthApi
import com.example.rideflow.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * 认证仓库
 * 处理用户登录、注册、登出等核心认证逻辑
 */
class AuthRepository {
    private val authApi = AuthApi()
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
            if (usernameOrEmail.isBlank() || password.isBlank()) {
                throw Exception("用户名和密码不能为空")
            }

            val response = withContext(Dispatchers.IO) {
                authApi.login(usernameOrEmail, password)
            }

            val result = response.data
            if (result != null) {
                val mappedGender = when (result.gender?.lowercase()) {
                    "male" -> 1
                    "female" -> 2
                    else -> 0
                }
                val mapped = UserData(
                    userId = result.userId.toString(),
                    nickname = (result.nickname ?: result.username),
                    email = result.email ?: "",
                    avatarUrl = result.avatarUrl,
                    bio = result.bio,
                    gender = mappedGender,
                    birthday = null,
                    emergencyContact = null
                )
                _authState.value = AuthState.Authenticated(mapped)
            } else {
                throw Exception(response.message.ifBlank { "登录失败，请重试" })
            }
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
            if (email.isBlank() || nickname.isBlank() || password.isBlank()) {
                throw Exception("请填写所有必填信息")
            }

            if (password.length < 6) {
                throw Exception("密码至少需要6位字符")
            }

            val username = if (email.isNotBlank()) email else nickname
            val response = withContext(Dispatchers.IO) {
                authApi.register(username, password)
            }

            val result = response.data
            if (result != null) {
                val mappedGender = when (result.gender?.lowercase()) {
                    "male" -> 1
                    "female" -> 2
                    else -> 0
                }
                val mapped = UserData(
                    userId = result.userId.toString(),
                    nickname = (result.nickname ?: result.username),
                    email = result.email ?: "",
                    avatarUrl = result.avatarUrl,
                    bio = result.bio,
                    gender = mappedGender,
                    birthday = null,
                    emergencyContact = null
                )
                _authState.value = AuthState.Authenticated(mapped)
            } else {
                throw Exception(response.message.ifBlank { "注册失败，请重试" })
            }
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

    /**
     * 获取当前登录用户ID
     */
    fun getCurrentUserId(): String? {
        return getCurrentUser()?.userId
    }
}

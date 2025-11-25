package com.example.rideflow.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 认证ViewModel
 * 连接UI层和认证仓库，处理UI相关的认证逻辑
 */
class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {

    // 暴露认证状态流供UI层观察
    val authState: StateFlow<AuthState> = authRepository.authState
    
    // 用于Composable中收集认证状态的扩展函数
    @Composable
    fun collectAuthState(): AuthState {
        return authState.collectAsState(initial = AuthState.Unauthenticated).value
    }

    /**
     * 用户登录
     */
    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            authRepository.login(usernameOrEmail, password)
        }
    }

    /**
     * 用户注册
     */
    fun register(email: String, nickname: String, password: String) {
        viewModelScope.launch {
            authRepository.register(email, nickname, password)
        }
    }

    /**
     * 用户登出
     */
    fun logout() {
        authRepository.logout()
    }

    /**
     * 清除认证错误
     */
    fun clearError() {
        authRepository.clearError()
    }

    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    /**
     * 获取当前用户数据
     */
    fun getCurrentUser() = authRepository.getCurrentUser()
}

/**
 * 收集认证状态的Composable函数
 * 方便UI组件获取认证状态
 */


/**
 * 获取认证状态的扩展函数
 */
@Composable
fun AuthState?.asLoginState(): AuthState {
    return this ?: AuthState.Unauthenticated
}
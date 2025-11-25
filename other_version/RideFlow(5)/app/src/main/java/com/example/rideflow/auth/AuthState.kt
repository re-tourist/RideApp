package com.example.rideflow.auth

import com.example.rideflow.model.UserData

/**
 * 认证状态密封类
 * 定义了用户可能的认证状态
 */
sealed class AuthState {
    // 未认证状态
    object Unauthenticated : AuthState()
    
    // 认证中状态
    object Authenticating : AuthState()
    
    // 已认证状态，包含用户数据
    data class Authenticated(val userData: UserData) : AuthState()
    
    // 认证错误状态
    data class Error(val errorMessage: String) : AuthState()
}
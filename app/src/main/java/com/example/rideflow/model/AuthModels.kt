package com.example.rideflow.model

import com.google.gson.annotations.SerializedName
// 登录 / 注册的请求体
data class LoginRequest(
    val username: String,
    val password: String
)

// 注册请求体（如果需要额外字段可以再加）
data class RegisterRequest(
    val username: String,
    val password: String
)

// 登录 / 注册成功后返回的数据
data class AuthResult(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("token") val token: String
)

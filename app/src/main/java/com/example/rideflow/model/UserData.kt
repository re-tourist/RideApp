package com.example.rideflow.model

/**
 * 用户数据模型
 * 包含用户的基本信息
 */
data class UserData(
    val userId: String,
    val nickname: String,
    val email: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val gender: Int = 0, // 0未知，1男，2女
    val birthday: String? = null, // 日期字符串 YYYY-MM-DD 格式
    val emergencyContact: String? = null
)
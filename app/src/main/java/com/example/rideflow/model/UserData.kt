package com.example.rideflow.model

data class UserData(
    val userId: String,
    val nickname: String?,
    val email: String?,
    val avatarUrl: String?,
    val bio: String?,
    val gender: Int,
    val birthday: String?,
    val emergencyContact: String?
)


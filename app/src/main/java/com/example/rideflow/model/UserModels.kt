package com.example.rideflow.model

data class UserProfileBTO(
    val userId: Long,
    val nickname: String,
    val email: String?,
    val avatarUrl: String?,
    val bio: String?,
    val gender: String?,
    val stats: UserStatsBTO? = null
)

data class UserProfileUpdateRequest(
    val nickname: String?,
    val email: String?,
    val avatarUrl: String?,
    val bio: String?,
    val gender: String?,
    val birthday: String?,
    val emergencyContact: String?
)

data class UserStatsBTO(
    val totalRides: Int,
    val totalDistanceKm: Double,
    val totalDurationMin: Int,
    val avgSpeedKmh: Double
)

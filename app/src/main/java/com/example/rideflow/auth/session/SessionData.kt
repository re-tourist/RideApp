package com.example.rideflow.auth.session

data class SessionData(
    val userId: String,
    val loginAt: Long,
    val expireAt: Long,
    val schemaVersion: Int = 1
) {
    fun isValid(now: Long): Boolean = userId.isNotEmpty() && expireAt > now
}


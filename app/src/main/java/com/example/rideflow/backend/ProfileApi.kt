package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.AuthResult
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileApi {
    private val gson = Gson()

    fun getProfile(userId: String): ApiResponse<AuthResult> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_PROFILE + "/get"
        val body = mapOf("userId" to userId)
        val json = HttpClient.postJson(url, body)
        val type = object : TypeToken<ApiResponse<AuthResult>>() {}.type
        return gson.fromJson(json, type)
    }

    fun updateProfile(
        userId: String,
        nickname: String?,
        email: String?,
        avatarUrl: String?,
        bio: String?,
        gender: String?,
        birthday: String?,
        emergencyContact: String?
    ): ApiResponse<AuthResult> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_PROFILE + "/update"
        val body = mapOf(
            "userId" to userId,
            "nickname" to nickname,
            "email" to email,
            "avatarUrl" to avatarUrl,
            "bio" to bio,
            "gender" to gender,
            "birthday" to birthday,
            "emergencyContact" to emergencyContact
        )
        val json = HttpClient.postJson(url, body)
        val type = object : TypeToken<ApiResponse<AuthResult>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isNicknameAvailable(nickname: String, excludeUserId: String?): ApiResponse<Boolean> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_PROFILE + "/check_nickname"
        val body = mapOf("nickname" to nickname, "excludeUserId" to excludeUserId)
        val json = HttpClient.postJson(url, body)
        val type = object : TypeToken<ApiResponse<Boolean>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isEmailAvailable(email: String, excludeUserId: String?): ApiResponse<Boolean> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_PROFILE + "/check_email"
        val body = mapOf("email" to email, "excludeUserId" to excludeUserId)
        val json = HttpClient.postJson(url, body)
        val type = object : TypeToken<ApiResponse<Boolean>>() {}.type
        return gson.fromJson(json, type)
    }
}


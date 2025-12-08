package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.UserProfileBTO
import com.example.rideflow.model.UserProfileUpdateRequest
import com.example.rideflow.model.UserStatsBTO
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileApiKit {
    private val gson = Gson()

    suspend fun getMyProfile(token: String): ApiResponse<UserProfileBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_USER_ME
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<UserProfileBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun updateMyProfile(token: String, body: UserProfileUpdateRequest): ApiResponse<UserProfileBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_USER_ME_UPDATE
        val json = HttpClient.put(url, body, token)
        val type = object : TypeToken<ApiResponse<UserProfileBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getMyStats(token: String): ApiResponse<UserStatsBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_USER_ME_STATS
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<UserStatsBTO>>() {}.type
        return gson.fromJson(json, type)
    }
}

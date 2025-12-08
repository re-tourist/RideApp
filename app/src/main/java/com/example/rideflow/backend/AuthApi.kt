package com.example.rideflow.backend

import com.example.rideflow.model.*
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthApi {

    private val gson = Gson()

    private fun join(base: String, path: String): String {
        val b = if (base.endsWith("/")) base.dropLast(1) else base
        val p = if (path.startsWith("/")) path.drop(1) else path
        return "$b/$p"
    }

    fun register(username: String, password: String): ApiResponse<AuthResult> {
        val url = join(ApiConfig.BASE_URL, ApiConfig.PATH_REGISTER)
        val req = RegisterRequest(username, password)

        val json = HttpClient.postJson(url, req)

        val type = object : TypeToken<ApiResponse<AuthResult>>() {}.type
        return gson.fromJson(json, type)
    }

    fun login(username: String, password: String): ApiResponse<AuthResult> {
        val url = join(ApiConfig.BASE_URL, ApiConfig.PATH_LOGIN)
        val req = LoginRequest(username, password)

        val json = HttpClient.postJson(url, req)

        val type = object : TypeToken<ApiResponse<AuthResult>>() {}.type
        return gson.fromJson(json, type)
    }
}

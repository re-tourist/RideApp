package com.example.rideflow.utils

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object HttpClient {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    /**
     * 发送 POST JSON 请求，返回响应字符串（JSON）
     */
    fun get(url: String, token: String? = null): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .header("Accept", "application/json")
            .apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }
            .build()
        Log.d("HttpClient", "GET $url")
        client.newCall(request).execute().use { response ->
            Log.d("HttpClient", "HTTP ${response.code}")
            if (!response.isSuccessful) throw RuntimeException("HTTP error: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    fun post(url: String, bodyObj: Any? = null, token: String? = null): String {
        val jsonBody = gson.toJson(bodyObj ?: emptyMap<String, Any>())
        val requestBody = jsonBody.toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Accept", "application/json")
            .apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }
            .build()
        Log.d("HttpClient", "POST $url")
        client.newCall(request).execute().use { response ->
            Log.d("HttpClient", "HTTP ${response.code}")
            if (!response.isSuccessful) throw RuntimeException("HTTP error: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    fun put(url: String, bodyObj: Any? = null, token: String? = null): String {
        val jsonBody = gson.toJson(bodyObj ?: emptyMap<String, Any>())
        val requestBody = jsonBody.toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .header("Accept", "application/json")
            .apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }
            .build()
        Log.d("HttpClient", "PUT $url")
        client.newCall(request).execute().use { response ->
            Log.d("HttpClient", "HTTP ${response.code}")
            if (!response.isSuccessful) throw RuntimeException("HTTP error: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    fun delete(url: String, bodyObj: Any? = null, token: String? = null): String {
        val jsonBody = gson.toJson(bodyObj ?: emptyMap<String, Any>())
        val requestBody = jsonBody.toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .delete(requestBody)
            .header("Accept", "application/json")
            .apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }
            .build()
        Log.d("HttpClient", "DELETE $url")
        client.newCall(request).execute().use { response ->
            Log.d("HttpClient", "HTTP ${response.code}")
            if (!response.isSuccessful) throw RuntimeException("HTTP error: ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    // 兼容旧接口
    fun postJson(url: String, bodyObj: Any): String = post(url, bodyObj, null)
}

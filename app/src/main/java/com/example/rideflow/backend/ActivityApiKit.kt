package com.example.rideflow.backend

import com.example.rideflow.model.ActivityCreateRequest
import com.example.rideflow.model.ActivityDetailBTO
import com.example.rideflow.model.ActivitySummaryBTO
import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ActivityApiKit {
    private val gson = Gson()

    suspend fun listActivities(token: String?, page: Int, pageSize: Int, city: String?, status: String?): ApiResponse<PageResult<ActivitySummaryBTO>> {
        val params = mutableListOf("page=$page", "pageSize=$pageSize")
        if (!city.isNullOrBlank()) params.add("city=$city")
        if (!status.isNullOrBlank()) params.add("status=$status")
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ACTIVITY_LIST + "?" + params.joinToString("&")
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<PageResult<ActivitySummaryBTO>>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getActivityDetail(token: String?, id: Long): ApiResponse<ActivityDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ACTIVITY_DETAIL + id
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<ActivityDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun createActivity(token: String, body: ActivityCreateRequest): ApiResponse<ActivityDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ACTIVITY_CREATE
        val json = HttpClient.post(url, body, token)
        val type = object : TypeToken<ApiResponse<ActivityDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun joinActivity(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ACTIVITY_JOIN + "$id/join"
        val json = HttpClient.post(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun quitActivity(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ACTIVITY_JOIN + "$id/join"
        val json = HttpClient.delete(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getMyActivities(token: String, type: String): ApiResponse<List<ActivitySummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_MY_ACTIVITIES + "?type=$type"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<List<ActivitySummaryBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.RideRecordDetailBTO
import com.example.rideflow.model.RideRecordSummaryBTO
import com.example.rideflow.model.RideUploadRequest
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RideApiKit {
    private val gson = Gson()

    suspend fun getMyRides(token: String, page: Int, pageSize: Int): ApiResponse<PageResult<RideRecordSummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_RIDE_LIST + "?page=$page&pageSize=$pageSize"
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<PageResult<RideRecordSummaryBTO>>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getRideDetail(token: String, rideId: Long): ApiResponse<RideRecordDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_RIDE_DETAIL + rideId
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<RideRecordDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun uploadRide(token: String, body: RideUploadRequest): ApiResponse<RideRecordDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_RIDE_CREATE
        val json = HttpClient.post(url, body, token)
        val type = object : TypeToken<ApiResponse<RideRecordDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun deleteRide(token: String, rideId: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_RIDE_DELETE + rideId
        val json = HttpClient.delete(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }
}

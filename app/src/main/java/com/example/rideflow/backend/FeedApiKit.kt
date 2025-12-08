package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.FeedCreateRequest
import com.example.rideflow.model.FeedDetailBTO
import com.example.rideflow.model.FeedSummaryBTO
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FeedApiKit {
    private val gson = Gson()

    suspend fun listFeeds(token: String, type: String, page: Int, pageSize: Int): ApiResponse<PageResult<FeedSummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_LIST + "?type=$type&page=$page&pageSize=$pageSize"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<PageResult<FeedSummaryBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun getFeedDetail(token: String?, id: Long): ApiResponse<FeedDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_DETAIL + id
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<FeedDetailBTO>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun createFeed(token: String, body: FeedCreateRequest): ApiResponse<FeedDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_CREATE
        val json = HttpClient.post(url, body, token)
        val typeToken = object : TypeToken<ApiResponse<FeedDetailBTO>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun deleteFeed(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_DELETE + id
        val json = HttpClient.delete(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun likeFeed(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_LIKE + "$id/like"
        val json = HttpClient.post(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun unlikeFeed(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_FEED_LIKE + "$id/like"
        val json = HttpClient.delete(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

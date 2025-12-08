package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.CommentBTO
import com.example.rideflow.model.CommentCreateRequest
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CommentApiKit {
    private val gson = Gson()

    suspend fun listComments(token: String, targetType: String, targetId: Long, page: Int, pageSize: Int): ApiResponse<PageResult<CommentBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMMENT_LIST + "?targetType=$targetType&targetId=$targetId&page=$page&pageSize=$pageSize"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<PageResult<CommentBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun createComment(token: String, body: CommentCreateRequest): ApiResponse<CommentBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMMENT_CREATE
        val json = HttpClient.post(url, body, token)
        val typeToken = object : TypeToken<ApiResponse<CommentBTO>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun deleteComment(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMMENT_DELETE + id
        val json = HttpClient.delete(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun likeComment(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMMENT_LIKE + "$id/like"
        val json = HttpClient.post(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun unlikeComment(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMMENT_LIKE + "$id/like"
        val json = HttpClient.delete(url, null, token)
        val typeToken = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

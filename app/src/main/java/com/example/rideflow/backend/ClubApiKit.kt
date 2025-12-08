package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.ClubDetailBTO
import com.example.rideflow.model.ClubMemberBTO
import com.example.rideflow.model.ClubSummaryBTO
import com.example.rideflow.model.ClubCreateRequest
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ClubApiKit {
    private val gson = Gson()

    suspend fun listClubs(token: String?, page: Int, pageSize: Int, city: String?): ApiResponse<PageResult<ClubSummaryBTO>> {
        val params = mutableListOf("page=$page", "pageSize=$pageSize")
        if (!city.isNullOrBlank()) params.add("city=$city")
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_LIST + "?" + params.joinToString("&")
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<PageResult<ClubSummaryBTO>>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getClubDetail(token: String?, id: Long): ApiResponse<ClubDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_DETAIL + id
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<ClubDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun createClub(token: String, body: ClubCreateRequest): ApiResponse<ClubDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_CREATE
        val json = HttpClient.post(url, body, token)
        val type = object : TypeToken<ApiResponse<ClubDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun joinClub(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_JOIN + "$id/join"
        val json = HttpClient.post(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun quitClub(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_JOIN + "$id/join"
        val json = HttpClient.delete(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getClubMembers(token: String?, id: Long): ApiResponse<List<ClubMemberBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_CLUB_MEMBERS + "$id/members"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<List<ClubMemberBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }

    suspend fun getMyClubs(token: String, type: String): ApiResponse<List<ClubSummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_MY_CLUBS + "?type=$type"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<List<ClubSummaryBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.CompetitionDetailBTO
import com.example.rideflow.model.CompetitionSummaryBTO
import com.example.rideflow.model.CompetitionRegisterRequest
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CompetitionApiKit {
    private val gson = Gson()

    suspend fun listCompetitions(token: String?, page: Int, pageSize: Int, category: String?, city: String?): ApiResponse<PageResult<CompetitionSummaryBTO>> {
        val params = mutableListOf("page=$page", "pageSize=$pageSize")
        if (!category.isNullOrBlank()) params.add("category=$category")
        if (!city.isNullOrBlank()) params.add("city=$city")
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMPETITION_LIST + "?" + params.joinToString("&")
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<PageResult<CompetitionSummaryBTO>>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getCompetitionDetail(token: String?, id: Long): ApiResponse<CompetitionDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMPETITION_DETAIL + id
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<CompetitionDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun registerCompetition(token: String, id: Long, body: CompetitionRegisterRequest): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMPETITION_REGISTER + "$id/register"
        val json = HttpClient.post(url, body, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun cancelCompetitionRegistration(token: String, id: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_COMPETITION_REGISTER + "$id/register"
        val json = HttpClient.delete(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getMyCompetitions(token: String, type: String): ApiResponse<List<CompetitionSummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_MY_COMPETITIONS + "?type=$type"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<List<CompetitionSummaryBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

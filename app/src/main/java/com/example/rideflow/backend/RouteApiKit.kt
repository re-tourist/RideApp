package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.PageResult
import com.example.rideflow.model.RouteCreateRequest
import com.example.rideflow.model.RouteDetailBTO
import com.example.rideflow.model.RouteSummaryBTO
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RouteApiKit {
    private val gson = Gson()

    suspend fun listRoutes(token: String?, page: Int, pageSize: Int, sort: String?, tag: String?): ApiResponse<PageResult<RouteSummaryBTO>> {
        val params = mutableListOf("page=$page", "pageSize=$pageSize")
        if (!sort.isNullOrBlank()) params.add("sort=$sort")
        if (!tag.isNullOrBlank()) params.add("tag=$tag")
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ROUTE_LIST + "?" + params.joinToString("&")
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<PageResult<RouteSummaryBTO>>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getRouteDetail(token: String?, routeId: Long): ApiResponse<RouteDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ROUTE_DETAIL + routeId
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<RouteDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun createRoute(token: String, body: RouteCreateRequest): ApiResponse<RouteDetailBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ROUTE_CREATE
        val json = HttpClient.post(url, body, token)
        val type = object : TypeToken<ApiResponse<RouteDetailBTO>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun favoriteRoute(token: String, routeId: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ROUTE_FAVORITE + "$routeId/favorite"
        val json = HttpClient.post(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun unfavoriteRoute(token: String, routeId: Long): ApiResponse<Unit> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_ROUTE_FAVORITE + "$routeId/favorite"
        val json = HttpClient.delete(url, null, token)
        val type = object : TypeToken<ApiResponse<Unit>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getMyRoutes(token: String, type: String): ApiResponse<List<RouteSummaryBTO>> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_MY_ROUTES + "?type=$type"
        val json = HttpClient.get(url, token)
        val typeToken = object : TypeToken<ApiResponse<List<RouteSummaryBTO>>>() {}.type
        return gson.fromJson(json, typeToken)
    }
}

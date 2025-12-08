package com.example.rideflow.backend

import com.example.rideflow.model.ApiResponse
import com.example.rideflow.model.DiscoveryHomeBTO
import com.example.rideflow.utils.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DiscoveryApiKit {
    private val gson = Gson()

    suspend fun getDiscoveryHome(token: String?): ApiResponse<DiscoveryHomeBTO> {
        val url = ApiConfig.BASE_URL + ApiConfig.PATH_DISCOVERY_HOME
        val json = HttpClient.get(url, token)
        val type = object : TypeToken<ApiResponse<DiscoveryHomeBTO>>() {}.type
        return gson.fromJson(json, type)
    }
}

package com.example.rideflow.model

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long
)

val ApiResponse<*>.isSuccess: Boolean
    get() = code == 0

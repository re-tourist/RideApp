package com.example.rideflow.core.result

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()
}

sealed class AppError(open val message: String, open val cause: Throwable? = null) {
    data class Network(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Database(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Validation(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
    data class Unknown(override val message: String, override val cause: Throwable? = null) : AppError(message, cause)
}

inline fun <T> appResultOf(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: Exception) {
    AppResult.Error(AppError.Unknown(e.message ?: "未知错误", e))
}


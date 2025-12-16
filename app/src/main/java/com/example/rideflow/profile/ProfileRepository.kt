package com.example.rideflow.profile

import android.util.Log
import com.example.rideflow.backend.AuthDatabaseHelper
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.core.result.AppError
import com.example.rideflow.core.result.AppResult
import com.example.rideflow.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用户资料仓库
 * 处理用户信息的获取、更新等操作
 */
class ProfileRepository(private val authRepository: com.example.rideflow.auth.AuthRepository) {
    private val TAG = "ProfileRepository"

    suspend fun getCurrentUserProfile(userId: String): AppResult<UserData> {
        return withContext(Dispatchers.IO) {
            try {
                val user = AuthDatabaseHelper.getUserById(userId)
                if (user != null) {
                    AppResult.Success(user)
                } else {
                    AppResult.Error(AppError.Database("未找到用户"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "查询用户资料失败: ${e.message}", e)
                AppResult.Error(AppError.Database("加载用户资料失败", e))
            }
        }
    }

    suspend fun updateUserProfile(
        nickname: String? = null,
        email: String? = null,
        avatarUrl: String? = null,
        bio: String? = null,
        gender: String? = null,
        birthday: String? = null,
        emergencyContact: String? = null
    ): AppResult<Unit> {
        return try {
            Log.d(TAG, "开始更新用户资料")
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                Log.d(TAG, "用户未登录，无法更新资料")
                AppResult.Error(AppError.Validation("用户未登录"))
            } else {
                withContext(Dispatchers.IO) {
                    val result = AuthDatabaseHelper.updateUser(
                        userId = currentUser.userId.toString(),
                        nickname = nickname,
                        email = email,
                        avatarUrl = avatarUrl,
                        bio = bio,
                        gender = gender,
                        birthday = birthday,
                        emergencyContact = emergencyContact
                    )
                    if (result) {
                        Log.d(TAG, "用户资料更新成功")
                        AppResult.Success(Unit)
                    } else {
                        Log.d(TAG, "用户资料更新失败")
                        AppResult.Error(AppError.Database("用户资料更新失败"))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "更新用户资料异常: ${e.message}", e)
            AppResult.Error(AppError.Database("更新用户资料异常", e))
        }
    }

    suspend fun isNicknameAvailable(nickname: String): AppResult<Boolean> {
        return try {
            Log.d(TAG, "检查昵称是否可用: $nickname")
            val currentUser = authRepository.getCurrentUser()
            val available = withContext(Dispatchers.IO) {
                val sql = "SELECT COUNT(*) FROM users WHERE nickname = ? AND user_id != ?"
                val count = DatabaseHelper.querySingleValue(
                    sql,
                    listOf<Any>(nickname, currentUser?.userId ?: 0)
                ) as? Long
                count != null && count == 0L
            }
            Log.d(TAG, "昵称可用性检查结果: $nickname -> $available")
            AppResult.Success(available)
        } catch (e: Exception) {
            Log.e(TAG, "检查昵称可用性异常: ${e.message}", e)
            AppResult.Error(AppError.Database("检查昵称可用性失败", e))
        }
    }

    suspend fun isEmailAvailable(email: String): AppResult<Boolean> {
        return try {
            Log.d(TAG, "检查邮箱是否可用: $email")
            val currentUser = authRepository.getCurrentUser()
            val available = withContext(Dispatchers.IO) {
                val sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?"
                val count = DatabaseHelper.querySingleValue(
                    sql,
                    listOf<Any>(email, currentUser?.userId ?: 0)
                ) as? Long
                count != null && count == 0L
            }
            Log.d(TAG, "邮箱可用性检查结果: $email -> $available")
            AppResult.Success(available)
        } catch (e: Exception) {
            Log.e(TAG, "检查邮箱可用性异常: ${e.message}", e)
            AppResult.Error(AppError.Database("检查邮箱可用性失败", e))
        }
    }
}

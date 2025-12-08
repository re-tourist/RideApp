package com.example.rideflow.profile

import android.util.Log
import com.example.rideflow.backend.ProfileApi
import com.example.rideflow.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用户资料仓库
 * 处理用户信息的获取、更新等操作
 */
class ProfileRepository(private val authRepository: com.example.rideflow.auth.AuthRepository) {
    private val TAG = "ProfileRepository"
    private val api = ProfileApi()

    /**
     * 根据用户ID获取用户资料
     */
    suspend fun getCurrentUserProfile(userId: String): UserData? {
        return withContext(Dispatchers.IO) {
            val resp = api.getProfile(userId)
            val r = resp.data
            if (r != null) {
                val genderMapped = when (r.gender?.lowercase()) {
                    "male" -> 1
                    "female" -> 2
                    else -> 0
                }
                UserData(
                    userId = r.userId.toString(),
                    nickname = r.nickname ?: r.username,
                    email = r.email ?: "",
                    avatarUrl = r.avatarUrl,
                    bio = r.bio,
                    gender = genderMapped,
                    birthday = null,
                    emergencyContact = null
                )
            } else null
        }
    }

    /**
     * 更新用户资料
     */
    suspend fun updateUserProfile(
        nickname: String? = null,
        email: String? = null,
        avatarUrl: String? = null,
        bio: String? = null,
        gender: String? = null,
        birthday: String? = null,
        emergencyContact: String? = null
    ): Boolean {
        return try {
            Log.d(TAG, "📝 开始更新用户资料")
            
            // 获取当前登录用户
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                Log.d(TAG, "❌ 用户未登录，无法更新资料")
                return false
            }
            
            Log.d(TAG, "🔄 更新用户资料，用户ID: ${currentUser.userId}")
            
            withContext(Dispatchers.IO) {
                val resp = api.updateProfile(
                    userId = currentUser.userId.toString(),
                    nickname = nickname,
                    email = email,
                    avatarUrl = avatarUrl,
                    bio = bio,
                    gender = gender,
                    birthday = birthday,
                    emergencyContact = emergencyContact
                )
                val ok = resp.data != null
                ok
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 更新用户资料异常: ${e.message}", e)
            false
        }
    }

    /**
     * 检查昵称是否可用
     */
    suspend fun isNicknameAvailable(nickname: String): Boolean {
        return try {
            Log.d(TAG, "🔍 检查昵称是否可用: $nickname")
            
            // 获取当前登录用户
            val currentUser = authRepository.getCurrentUser()
            
            withContext(Dispatchers.IO) {
                val resp = api.isNicknameAvailable(nickname, currentUser?.userId)
                resp.data == true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 检查昵称可用性异常: ${e.message}", e)
            false
        }
    }

    /**
     * 检查邮箱是否可用
     */
    suspend fun isEmailAvailable(email: String): Boolean {
        return try {
            Log.d(TAG, "🔍 检查邮箱是否可用: $email")
            
            // 获取当前登录用户
            val currentUser = authRepository.getCurrentUser()
            
            withContext(Dispatchers.IO) {
                val resp = api.isEmailAvailable(email, currentUser?.userId)
                resp.data == true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 检查邮箱可用性异常: ${e.message}", e)
            false
        }
    }
}

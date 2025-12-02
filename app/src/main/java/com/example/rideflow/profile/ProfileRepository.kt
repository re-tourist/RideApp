package com.example.rideflow.profile

import android.util.Log
import com.example.rideflow.backend.AuthDatabaseHelper
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 用户资料仓库
 * 处理用户信息的获取、更新等操作
 */
class ProfileRepository(private val authRepository: com.example.rideflow.auth.AuthRepository) {
    private val TAG = "ProfileRepository"

    /**
     * 根据用户ID获取用户资料
     */
    suspend fun getCurrentUserProfile(userId: String): UserData? {
        return withContext(Dispatchers.IO) {
            AuthDatabaseHelper.getUserById(userId)
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
        gender: Int? = null,
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
            
            // 在IO线程执行数据库操作
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
                    Log.d(TAG, "✅ 用户资料更新成功")
                } else {
                    Log.d(TAG, "❌ 用户资料更新失败")
                }
                result
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
            
            // 在IO线程执行数据库操作
            withContext(Dispatchers.IO) {
                val sql = "SELECT COUNT(*) FROM users WHERE nickname = ? AND user_id != ?"
                val count = DatabaseHelper.querySingleValue(
                    sql, 
                    listOf<Any>(nickname, currentUser?.userId ?: 0)
                ) as? Long
                
                val available = count != null && count == 0L
                Log.d(TAG, "📊 昵称可用性检查结果: $nickname -> $available")
                available
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
            
            // 在IO线程执行数据库操作
            withContext(Dispatchers.IO) {
                val sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id != ?"
                val count = DatabaseHelper.querySingleValue(
                    sql, 
                    listOf<Any>(email, currentUser?.userId ?: 0)
                ) as? Long
                
                val available = count != null && count == 0L
                Log.d(TAG, "📊 邮箱可用性检查结果: $email -> $available")
                available
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 检查邮箱可用性异常: ${e.message}", e)
            false
        }
    }
}
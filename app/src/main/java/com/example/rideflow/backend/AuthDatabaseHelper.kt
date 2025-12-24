package com.example.rideflow.backend

import android.util.Log
import com.example.rideflow.model.UserData
import java.security.MessageDigest
import java.util.*

/**
 * 用户认证数据库服务类
 * 专门处理用户登录、注册、密码验证等认证相关操作
 */
object AuthDatabaseHelper {
    private const val TAG = "AuthDatabaseHelper"

    /**
     * 用户登录验证
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 登录成功的用户数据，登录失败返回null
     */
    fun login(usernameOrEmail: String, password: String): UserData? {
        Log.d(TAG, "🔐 开始登录验证: usernameOrEmail=$usernameOrEmail")
        return try {
            // 先查询用户信息（支持邮箱或昵称登录）
            val sql = """
                SELECT user_id, nickname, email, password_hash, avatar_url, bio, 
                       gender, birthday, emergency_contact, status, email_verified,
                       last_login_at, created_at, updated_at
                FROM users 
                WHERE (email = ? OR nickname = ?) AND status = 0
            """.trimIndent()

            Log.d(TAG, "📝 执行登录查询SQL: $sql, 参数: [$usernameOrEmail, $usernameOrEmail]")
            val userRow = DatabaseHelper.querySingleRow(sql, listOf<Any>(usernameOrEmail, usernameOrEmail))
            
            if (userRow == null) {
                Log.d(TAG, "❌ 用户不存在或账号已被禁用: $usernameOrEmail")
                return null
            }

            Log.d(TAG, "✅ 用户查询成功，找到用户: ${userRow["nickname"]} (${userRow["email"]})")

            // 验证密码
            val storedPasswordHash = userRow["password_hash"]?.toString() ?: ""
            if (!verifyPassword(password, storedPasswordHash)) {
                Log.d(TAG, "❌ 密码验证失败: $usernameOrEmail")
                return null
            }

            Log.d(TAG, "✅ 密码验证成功")

            // 更新最后登录时间
            val userId = userRow["user_id"]?.toString() ?: ""
            Log.d(TAG, "🔄 更新最后登录时间，用户ID: $userId")
            updateLastLoginTime(userId)

            // 转换为UserData对象
            val userData = mapToUserData(userRow)
            Log.d(TAG, "✅ 登录成功，用户数据: ${userData.nickname} (${userData.email})")
            userData
        } catch (e: Exception) {
            Log.e(TAG, "❌ 登录验证失败: ${e.message}", e)
            null
        }
    }

    /**
     * 用户注册
     * @param email 邮箱
     * @param nickname 昵称
     * @param password 密码
     * @return 注册成功的用户数据，注册失败返回null
     */
    fun register(email: String, nickname: String, password: String): UserData? {
        Log.d(TAG, "📝 开始用户注册: email=$email, nickname=$nickname")
        return try {
            // 检查邮箱是否已存在
            Log.d(TAG, "🔍 检查邮箱是否已存在: $email")
            if (isEmailExists(email)) {
                Log.d(TAG, "❌ 邮箱已存在: $email")
                return null
            }
            Log.d(TAG, "✅ 邮箱可用: $email")

            // 检查昵称是否已存在
            Log.d(TAG, "🔍 检查昵称是否已存在: $nickname")
            if (isNicknameExists(nickname)) {
                Log.d(TAG, "❌ 昵称已存在: $nickname")
                return null
            }
            Log.d(TAG, "✅ 昵称可用: $nickname")

            // 密码加密
            Log.d(TAG, "🔐 密码加密处理")
            val passwordHash = hashPassword(password)

            // 插入新用户
            val sql = """
                INSERT INTO users (nickname, email, password_hash, status, email_verified)
                VALUES (?, ?, ?, 0, false)
            """.trimIndent()

            Log.d(TAG, "📝 执行注册SQL: $sql, 参数: [$nickname, $email, ***]")
            val affectedRows = DatabaseHelper.executeUpdate(sql, listOf<Any>(nickname, email, passwordHash))
            
            if (affectedRows > 0) {
                Log.d(TAG, "✅ 用户注册成功，影响行数: $affectedRows")
                // 注册成功后自动登录
                Log.d(TAG, "🔄 注册成功后自动登录")
                login(email, password)
            } else {
                Log.d(TAG, "❌ 用户注册失败，影响行数: $affectedRows")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 用户注册失败: ${e.message}", e)
            null
        }
    }

    /**
     * 检查邮箱是否存在
     */
    private fun isEmailExists(email: String): Boolean {
        Log.d(TAG, "🔍 检查邮箱是否存在: $email")
        val sql = "SELECT COUNT(*) FROM users WHERE email = ?"
        val count = DatabaseHelper.querySingleValue(sql, listOf<Any>(email)) as? Long
        val exists = count != null && count > 0
        Log.d(TAG, "📊 邮箱存在检查结果: $email -> $exists (count=$count)")
        return exists
    }

    /**
     * 检查昵称是否存在
     */
    private fun isNicknameExists(nickname: String): Boolean {
        Log.d(TAG, "🔍 检查昵称是否存在: $nickname")
        val sql = "SELECT COUNT(*) FROM users WHERE nickname = ?"
        val count = DatabaseHelper.querySingleValue(sql, listOf<Any>(nickname)) as? Long
        val exists = count != null && count > 0
        Log.d(TAG, "📊 昵称存在检查结果: $nickname -> $exists (count=$count)")
        return exists
    }

    /**
     * 更新最后登录时间
     */
    private fun updateLastLoginTime(userId: String) {
        try {
            val sql = "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE user_id = ?"
            DatabaseHelper.executeUpdate(sql, listOf<Any>(userId))
        } catch (e: Exception) {
            Log.e(TAG, "更新最后登录时间失败: ${e.message}")
        }
    }

    /**
     * 密码处理（直接存储明文，不加密）
     */
    private fun hashPassword(password: String): String {
        // 直接返回明文密码，不进行加密
        Log.d(TAG, "🔓 密码处理: 直接存储明文")
        return password
    }

    /**
     * 密码验证
     */
    private fun verifyPassword(password: String, storedPassword: String): Boolean {
        // 直接比较明文密码
        val isValid = password == storedPassword
        Log.d(TAG, "🔑 密码验证结果: $isValid")
        return isValid
    }

    /**
     * 根据用户ID获取用户信息
     */
    fun getUserById(userId: String): UserData? {
        return try {
            val sql = """
                SELECT user_id, nickname, email, avatar_url, bio, gender, 
                       birthday, emergency_contact, status, email_verified,
                       last_login_at, created_at, updated_at
                FROM users 
                WHERE user_id = ?
            """.trimIndent()

            val userRow = DatabaseHelper.querySingleRow(sql, listOf<Any>(userId))
            userRow?.let { mapToUserData(it) }
        } catch (e: Exception) {
            Log.e(TAG, "获取用户信息失败: ${e.message}")
            null
        }
    }

    /**
     * 更新用户信息
     */
    fun updateUser(userId: String, nickname: String?, email: String?, avatarUrl: String?, 
                   bio: String?, gender: String?, birthday: String?, emergencyContact: String?): Boolean {
        return try {
            val sql = """
                UPDATE users 
                SET nickname = COALESCE(NULLIF(?, ''), nickname),
                    email = COALESCE(NULLIF(?, ''), email),
                    avatar_url = COALESCE(NULLIF(?, ''), avatar_url),
                    bio = COALESCE(NULLIF(?, ''), bio),
                    gender = COALESCE(NULLIF(?, ''), gender),
                    birthday = COALESCE(NULLIF(?, ''), birthday),
                    emergency_contact = COALESCE(NULLIF(?, ''), emergency_contact),
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
            """.trimIndent()

            // 处理可空参数，将null转换为空字符串，让NULLIF函数处理
            val params = listOf<Any>(
                nickname ?: "",
                email ?: "",
                avatarUrl ?: "",
                bio ?: "",
                gender ?: "",
                birthday ?: "",
                emergencyContact ?: "",
                userId
            )
            
            val affectedRows = DatabaseHelper.executeUpdate(sql, params)
            
            affectedRows > 0
        } catch (e: Exception) {
            Log.e(TAG, "更新用户信息失败: ${e.message}")
            false
        }
    }

    /**
     * 修改密码
     */
    fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        return try {
            // 先验证旧密码
            val user = getUserById(userId)
            if (user == null) {
                Log.d(TAG, "用户不存在: $userId")
                return false
            }

            // 这里需要获取用户的密码哈希进行验证
            val sql = "SELECT password_hash FROM users WHERE user_id = ?"
            val passwordHash = DatabaseHelper.querySingleValue(sql, listOf<Any>(userId)) as? String
            
            if (passwordHash == null || !verifyPassword(oldPassword, passwordHash)) {
                Log.d(TAG, "旧密码验证失败: $userId")
                return false
            }

            // 更新密码
            val newPasswordHash = hashPassword(newPassword)
            val updateSql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?"
            val affectedRows = DatabaseHelper.executeUpdate(updateSql, listOf<Any>(newPasswordHash, userId))
            
            affectedRows > 0
        } catch (e: Exception) {
            Log.e(TAG, "修改密码失败: ${e.message}")
            false
        }
    }

    /**
     * 将数据库行数据映射为UserData对象
     */
    private fun mapToUserData(row: Map<String, Any>): UserData {
        return UserData(
            userId = row["user_id"]?.toString() ?: "",
            nickname = row["nickname"]?.toString() ?: "",
            email = row["email"]?.toString() ?: "",
            avatarUrl = row["avatar_url"]?.toString(),
            bio = row["bio"]?.toString(),
            gender = when (val genderValue = row["gender"]) {
                is Int -> genderValue as Int
                is Long -> (genderValue as Long).toInt()
                is String -> when (genderValue) {
                    "male" -> 1
                    "female" -> 2
                    else -> 0
                }
                else -> 0
            },
            birthday = row["birthday"]?.toString(),
            emergencyContact = row["emergency_contact"]?.toString()
        )
    }

    /**
     * 测试数据库连接
     */
    fun testConnection(): Boolean {
        return DatabaseHelper.testConnection()
    }

    /**
     * 检查users表是否存在
     */
    fun usersTableExists(): Boolean {
        return DatabaseHelper.tableExists("users")
    }
}

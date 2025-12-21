package com.example.rideflow.auth

import com.example.rideflow.backend.AuthDatabaseHelper
import com.example.rideflow.model.UserData
import com.example.rideflow.auth.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AuthRepository(private val sessionManager: SessionManager) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun login(usernameOrEmail: String, password: String) {
        _authState.value = AuthState.Authenticating

        try {
            if (usernameOrEmail.isBlank() || password.isBlank()) {
                throw Exception("用户名和密码不能为空")
            }

            val userData = withContext(Dispatchers.IO) {
                loginRemote(usernameOrEmail, password)
            }

            sessionManager.loginSuccess(userData.userId)
            _authState.value = AuthState.Authenticated(userData)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "登录失败，请重试")
        }
    }

    suspend fun register(email: String, nickname: String, password: String) {
        _authState.value = AuthState.Authenticating

        try {
            if (email.isBlank() || password.isBlank()) {
                throw Exception("请填写邮箱和密码")
            }

            if (password.length < 6) {
                throw Exception("密码至少需要6位字符")
            }

            val username = if (email.isNotBlank()) email else nickname

            val userData = withContext(Dispatchers.IO) {
                registerRemote(username, password)
            }

            sessionManager.loginSuccess(userData.userId)
            _authState.value = AuthState.Authenticated(userData)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "注册失败，请重试")
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun isLoggedIn(): Boolean {
        return _authState.value is AuthState.Authenticated
    }

    fun getCurrentUser(): UserData? {
        return if (_authState.value is AuthState.Authenticated) {
            (_authState.value as AuthState.Authenticated).userData
        } else {
            null
        }
    }

    fun getCurrentUserId(): String? {
        return getCurrentUser()?.userId
    }

    suspend fun resumeSession(userId: String) {
        val user = withContext(Dispatchers.IO) { AuthDatabaseHelper.getUserById(userId) }
        if (user != null) {
            _authState.value = AuthState.Authenticated(user)
        }
    }

    private fun loginRemote(usernameOrEmail: String, password: String): UserData {
        val url = URL("$BASE_URL/api/v1/auth/login")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 15000
            doInput = true
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        return conn.useJsonCall(
            bodyBuilder = {
                put("username", usernameOrEmail)
                put("password", password)
            }
        )
    }

    private fun registerRemote(username: String, password: String): UserData {
        val url = URL("$BASE_URL/api/v1/auth/register")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10000
            readTimeout = 15000
            doInput = true
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        return conn.useJsonCall(
            bodyBuilder = {
                put("username", username)
                put("password", password)
            }
        )
    }

    private fun HttpURLConnection.useJsonCall(
        bodyBuilder: JSONObject.() -> Unit
    ): UserData {
        try {
            val body = JSONObject().apply(bodyBuilder)
            outputStream.use { os ->
                os.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val code = responseCode
            val stream = if (code in 200..299) inputStream else errorStream
            val responseText = stream.bufferedReader().use { it.readText() }

            if (code !in 200..299) {
                throw Exception("网络请求失败($code)")
            }

            val root = JSONObject(responseText)
            val bizCode = root.optInt("code", -1)
            if (bizCode != 0) {
                val msg = root.optString("message", "请求失败")
                throw Exception(msg)
            }

            val data = root.optJSONObject("data") ?: throw Exception("响应数据为空")

            val userId = data.optLong("user_id", 0L)
            if (userId <= 0L) {
                throw Exception("用户数据不合法")
            }

            val nickname = data.optString("nickname", "")
            val username = data.optString("username", "")
            val email = data.optString("email", "")
            val avatarUrl = data.optString("avatar_url", "")
            val bio = data.optString("bio", "")
            val genderStr = data.optString("gender", "")

            val gender = when (genderStr.lowercase()) {
                "male", "m", "1" -> 1
                "female", "f", "2" -> 2
                else -> 0
            }

            return UserData(
                userId = userId.toString(),
                nickname = if (nickname.isNotEmpty()) nickname else username,
                email = email,
                avatarUrl = avatarUrl.takeIf { it.isNotEmpty() },
                bio = bio.takeIf { it.isNotEmpty() },
                gender = gender,
                birthday = null,
                emergencyContact = null
            )
        } finally {
            disconnect()
        }
    }

    companion object {
        private const val BASE_URL = "http://101.37.79.220:8080"
    }
}

package com.example.rideflow.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideflow.auth.AuthRepository
import com.example.rideflow.core.result.AppResult
import com.example.rideflow.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ProfileViewModel - 管理用户信息界面的状态和逻辑
 */
class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 用户资料状态
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 更新状态
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    /**
     * 加载用户资料
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    when (val result = profileRepository.getCurrentUserProfile(userId)) {
                        is AppResult.Success -> _userProfile.value = result.data
                        is AppResult.Error -> _errorMessage.value = result.error.message
                        AppResult.Loading -> {}
                    }
                } else {
                    _errorMessage.value = "用户未登录"
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载用户资料失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 触发加载用户资料（公共方法）
     */
    fun loadUserProfileData() {
        loadUserProfile()
    }

    /**
     * 更新用户资料
     */
    fun updateUserProfile(
        nickname: String? = null,
        email: String? = null,
        avatarUrl: String? = null,
        bio: String? = null,
        gender: String? = null,
        birthday: String? = null,
        emergencyContact: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false
            
            try {
                // 获取当前登录用户ID
                val currentUserId = authRepository.getCurrentUserId()
                if (currentUserId.isNullOrEmpty()) {
                    _errorMessage.value = "用户未登录"
                    return@launch
                }

                // 检查昵称是否可用（如果提供了新昵称）
                if (!nickname.isNullOrEmpty() && nickname != _userProfile.value?.nickname) {
                    when (val result = profileRepository.isNicknameAvailable(nickname)) {
                        is AppResult.Success -> {
                            if (!result.data) {
                                _errorMessage.value = "昵称已被使用"
                                return@launch
                            }
                        }
                        is AppResult.Error -> {
                            _errorMessage.value = result.error.message
                            return@launch
                        }
                        AppResult.Loading -> {}
                    }
                }

                // 检查邮箱是否可用（如果提供了新邮箱）
                if (!email.isNullOrEmpty() && email != _userProfile.value?.email) {
                    when (val result = profileRepository.isEmailAvailable(email)) {
                        is AppResult.Success -> {
                            if (!result.data) {
                                _errorMessage.value = "邮箱已被使用"
                                return@launch
                            }
                        }
                        is AppResult.Error -> {
                            _errorMessage.value = result.error.message
                            return@launch
                        }
                        AppResult.Loading -> {}
                    }
                }

                // 更新用户资料
                val result = profileRepository.updateUserProfile(
                    nickname = nickname,
                    email = email,
                    avatarUrl = avatarUrl,
                    bio = bio,
                    gender = gender,
                    birthday = birthday,
                    emergencyContact = emergencyContact
                )

                when (result) {
                    is AppResult.Success -> {
                        _updateSuccess.value = true
                        loadUserProfile()
                    }
                    is AppResult.Error -> {
                        _errorMessage.value = result.error.message
                    }
                    AppResult.Loading -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新用户资料失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * 清除更新成功状态
     */
    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }

    /**
     * 检查用户是否登录
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentUserId()?.isNotEmpty() == true
    }

    /**
     * 获取当前用户ID
     */
    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
}

package com.example.rideflow.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideflow.auth.AuthRepository
import com.example.rideflow.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * EditProfileViewModel - 管理个人修改信息界面的状态和逻辑
 */
class EditProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 当前用户资料状态
    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> = _userProfile.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 更新成功状态
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    // 表单数据
    private val _formData = MutableStateFlow(EditProfileFormData())
    val formData: StateFlow<EditProfileFormData> = _formData.asStateFlow()

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
                    val userData = profileRepository.getCurrentUserProfile(userId)
                    _userProfile.value = userData
                    
                    // 将用户数据填充到表单
                    userData?.let { user ->
                        _formData.value = EditProfileFormData(
                            nickname = user.nickname ?: "",
                            email = user.email ?: "",
                            bio = user.bio ?: "",
                            gender = when (user.gender) {
                                1 -> "male"
                                2 -> "female"
                                else -> "other"
                            },
                            birthday = user.birthday ?: "",
                            emergencyContact = user.emergencyContact ?: ""
                        )
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
     * 更新表单数据
     */
    fun updateFormData(
        nickname: String? = null,
        email: String? = null,
        bio: String? = null,
        gender: String? = null,
        birthday: String? = null,
        emergencyContact: String? = null
    ) {
        val current = _formData.value
        _formData.value = current.copy(
            nickname = nickname ?: current.nickname,
            email = email ?: current.email,
            bio = bio ?: current.bio,
            gender = gender ?: current.gender,
            birthday = birthday ?: current.birthday,
            emergencyContact = emergencyContact ?: current.emergencyContact
        )
    }

    /**
     * 保存用户资料
     */
    fun saveUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false
            
            try {
                val currentFormData = _formData.value
                
                // 检查必填字段
                if (currentFormData.nickname.isBlank()) {
                    _errorMessage.value = "昵称不能为空"
                    return@launch
                }
                
                // 更新用户资料
                val success = profileRepository.updateUserProfile(
                    nickname = currentFormData.nickname.ifBlank { null },
                    email = currentFormData.email.ifBlank { null },
                    bio = currentFormData.bio.ifBlank { null },
                    gender = if (currentFormData.gender != "other") currentFormData.gender else null,
                    birthday = currentFormData.birthday.ifBlank { null },
                    emergencyContact = currentFormData.emergencyContact.ifBlank { null }
                )

                if (success) {
                    _updateSuccess.value = true
                    // 重新加载用户资料以获取最新数据
                    loadUserProfile()
                } else {
                    _errorMessage.value = "保存用户资料失败"
                }
            } catch (e: Exception) {
                _errorMessage.value = "保存用户资料失败: ${e.message}"
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
}

/**
 * 编辑资料表单数据类
 */
data class EditProfileFormData(
    val nickname: String = "",
    val email: String = "",
    val bio: String = "",
    val gender: String = "other", // "male"男，"female"女，"other"其他
    val birthday: String = "",
    val emergencyContact: String = ""
)
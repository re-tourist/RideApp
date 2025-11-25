package com.example.rideflow.di

import com.example.rideflow.auth.AuthRepository
import com.example.rideflow.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 应用模块
 * 提供应用中所需的依赖项
 */
val appModule = module {
    // 单例提供AuthRepository
    single { AuthRepository() }
    
    // 提供AuthViewModel
    viewModel { AuthViewModel(get()) }
}

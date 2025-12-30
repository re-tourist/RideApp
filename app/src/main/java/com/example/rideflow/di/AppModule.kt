package com.example.rideflow.di

import androidx.room.Room
import com.example.rideflow.auth.AuthRepository
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.auth.session.SessionManager
import com.example.rideflow.auth.session.SessionStore
import com.example.rideflow.message.MessageDetailViewModel
import com.example.rideflow.message.MessageListViewModel
import com.example.rideflow.message.MessageRepository
import com.example.rideflow.message.data.MessageDatabase
import com.example.rideflow.profile.EditProfileViewModel
import com.example.rideflow.profile.ProfileRepository
import com.example.rideflow.profile.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 应用模块
 * 提供应用中所需的依赖项
 */
val appModule = module {
    single{ SessionStore(get()) }
    single{ SessionManager(get()) }
    single{ AuthRepository(get()) }

    // 单例提供ProfileRepository
    single{ ProfileRepository(get()) }

    // 提供AuthViewModel
    viewModel { AuthViewModel(get(), get()) }

    // 提供ProfileViewModel
    viewModel { ProfileViewModel(get(), get()) }

    // 提供EditProfileViewModel
    viewModel { EditProfileViewModel(get(), get()) }

    single {
        Room.databaseBuilder(get(), MessageDatabase::class.java, "message.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<MessageDatabase>().messageDao() }
    single { get<MessageDatabase>().conversationDao() }
    single { MessageRepository(get(), get(), get()) }

    viewModel { (ownerId: String) -> MessageListViewModel(ownerId = ownerId, repository = get()) }
    viewModel { (ownerId: String, peerId: String) ->
        MessageDetailViewModel(ownerId = ownerId, peerId = peerId, repository = get())
    }
}

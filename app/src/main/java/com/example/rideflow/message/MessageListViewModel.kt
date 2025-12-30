package com.example.rideflow.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideflow.message.data.ConversationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConversationUiModel(
    val peerId: String,
    val peerName: String,
    val lastMessage: String,
    val lastTime: Long,
    val unreadCount: Int
)

class MessageListViewModel(
    private val ownerId: String,
    private val repository: MessageRepository
) : ViewModel() {
    val conversations: StateFlow<List<ConversationUiModel>> = repository
        .observeConversations(ownerId)
        .map { list -> list.map { it.toUiModel() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeeded(ownerId)
        }
    }

    fun simulateIncoming() {
        viewModelScope.launch {
            repository.ensureSeeded(ownerId)
            repository.simulateIncoming(ownerId)
        }
    }

    fun peerName(peerId: String): String {
        return repository.demoPeers().firstOrNull { it.userId == peerId }?.displayName ?: "用户 $peerId"
    }

    private fun ConversationEntity.toUiModel(): ConversationUiModel {
        return ConversationUiModel(
            peerId = peerId,
            peerName = peerName(peerId),
            lastMessage = lastMessage,
            lastTime = lastTime,
            unreadCount = unreadCount
        )
    }
}


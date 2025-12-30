package com.example.rideflow.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideflow.message.data.MessageEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessageDetailViewModel(
    private val ownerId: String,
    private val peerId: String,
    private val repository: MessageRepository
) : ViewModel() {
    val messages: StateFlow<List<MessageEntity>> = repository
        .observeMessages(ownerId, peerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeeded(ownerId)
            repository.markConversationRead(ownerId, peerId)
        }
    }

    fun sendText(content: String) {
        viewModelScope.launch {
            repository.sendText(ownerId, peerId, content)
        }
    }

    fun markRead() {
        viewModelScope.launch {
            repository.markConversationRead(ownerId, peerId)
        }
    }

    fun peerName(): String {
        return repository.demoPeers().firstOrNull { it.userId == peerId }?.displayName ?: "用户 $peerId"
    }
}


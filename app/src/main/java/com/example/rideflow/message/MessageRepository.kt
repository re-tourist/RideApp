package com.example.rideflow.message

import androidx.room.withTransaction
import com.example.rideflow.message.data.ConversationDao
import com.example.rideflow.message.data.ConversationEntity
import com.example.rideflow.message.data.MessageDao
import com.example.rideflow.message.data.MessageDatabase
import com.example.rideflow.message.data.MessageEntity
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val db: MessageDatabase,
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) {
    fun observeConversations(ownerId: String): Flow<List<ConversationEntity>> {
        return conversationDao.observeByOwner(ownerId)
    }

    fun observeMessages(ownerId: String, peerId: String): Flow<List<MessageEntity>> {
        return messageDao.observeConversation(ownerId, peerId)
    }

    suspend fun ensureSeeded(ownerId: String) {
        val count = conversationDao.countByOwner(ownerId)
        if (count > 0) return

        val now = System.currentTimeMillis()
        val peers = demoPeers()
        db.withTransaction {
            val messages = buildList {
                peers.forEachIndexed { peerIndex, peer ->
                    val base = now - (peers.size - peerIndex) * 60 * 60 * 1000L
                    add(
                        MessageEntity(
                            senderId = peer.userId,
                            receiverId = ownerId,
                            content = "${peer.displayName}：欢迎加入骑迹！",
                            messageType = 1,
                            isRead = if (peerIndex == 0) 0 else 1,
                            createTime = base + 1_000L
                        )
                    )
                    add(
                        MessageEntity(
                            senderId = ownerId,
                            receiverId = peer.userId,
                            content = "你好，我刚开始用这个 App。",
                            messageType = 1,
                            isRead = 0,
                            createTime = base + 20_000L
                        )
                    )
                    add(
                        MessageEntity(
                            senderId = peer.userId,
                            receiverId = ownerId,
                            content = when (peer.userId) {
                                "2001" -> "小提示：记得开启定位，轨迹更准。"
                                "2002" -> "周末一起骑吗？"
                                else -> "系统通知：你可以在这里收到离线留言。"
                            },
                            messageType = 1,
                            isRead = if (peerIndex == 0) 0 else 1,
                            createTime = base + 40_000L
                        )
                    )
                    add(
                        MessageEntity(
                            senderId = ownerId,
                            receiverId = peer.userId,
                            content = "收到，感谢！",
                            messageType = 1,
                            isRead = 0,
                            createTime = base + 60_000L
                        )
                    )
                }
            }

            messages.forEach { messageDao.insert(it) }

            peers.forEach { peer ->
                upsertConversation(ownerId = ownerId, peerId = peer.userId)
                upsertConversation(ownerId = peer.userId, peerId = ownerId)
            }
        }
    }

    suspend fun sendText(ownerId: String, peerId: String, content: String) {
        val text = content.trim()
        if (text.isEmpty()) return

        val now = System.currentTimeMillis()
        db.withTransaction {
            messageDao.insert(
                MessageEntity(
                    senderId = ownerId,
                    receiverId = peerId,
                    content = text,
                    messageType = 1,
                    isRead = 0,
                    createTime = now
                )
            )
            upsertConversation(ownerId = ownerId, peerId = peerId)
            upsertConversation(ownerId = peerId, peerId = ownerId)
        }
    }

    suspend fun markConversationRead(ownerId: String, peerId: String) {
        db.withTransaction {
            messageDao.markRead(ownerId, peerId)
            upsertConversation(ownerId = ownerId, peerId = peerId)
        }
    }

    suspend fun simulateIncoming(ownerId: String) {
        val peers = demoPeers()
        if (peers.isEmpty()) return
        val peer = peers[(System.currentTimeMillis() % peers.size).toInt()]
        val now = System.currentTimeMillis()
        db.withTransaction {
            messageDao.insert(
                MessageEntity(
                    senderId = peer.userId,
                    receiverId = ownerId,
                    content = when (peer.userId) {
                        "2001" -> "我刚看了你的路线，很棒！"
                        "2002" -> "等你下次一起骑。"
                        else -> "系统通知：今日骑行已同步。"
                    },
                    messageType = 1,
                    isRead = 0,
                    createTime = now
                )
            )
            upsertConversation(ownerId = ownerId, peerId = peer.userId)
            upsertConversation(ownerId = peer.userId, peerId = ownerId)
        }
    }

    private suspend fun upsertConversation(ownerId: String, peerId: String) {
        val latest = messageDao.latestInConversation(ownerId, peerId) ?: return
        val unreadCount = messageDao.countUnread(ownerId, peerId)
        conversationDao.upsert(
            ConversationEntity(
                ownerId = ownerId,
                peerId = peerId,
                lastMessage = latest.content,
                lastTime = latest.createTime,
                unreadCount = unreadCount
            )
        )
    }

    data class DemoPeer(val userId: String, val displayName: String)

    fun demoPeers(): List<DemoPeer> {
        return listOf(
            DemoPeer(userId = "2001", displayName = "小李"),
            DemoPeer(userId = "2002", displayName = "小王"),
            DemoPeer(userId = "0", displayName = "骑迹助手")
        )
    }
}


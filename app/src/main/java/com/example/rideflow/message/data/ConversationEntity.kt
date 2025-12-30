package com.example.rideflow.message.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "conversations",
    primaryKeys = ["owner_id", "peer_id"]
)
data class ConversationEntity(
    @ColumnInfo(name = "owner_id") val ownerId: String,
    @ColumnInfo(name = "peer_id") val peerId: String,
    @ColumnInfo(name = "last_message") val lastMessage: String,
    @ColumnInfo(name = "last_time") val lastTime: Long,
    @ColumnInfo(name = "unread_count") val unreadCount: Int
)


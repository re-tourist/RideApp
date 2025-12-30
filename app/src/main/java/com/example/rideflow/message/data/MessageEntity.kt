package com.example.rideflow.message.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["receiver_id", "is_read"]),
        Index(value = ["sender_id", "receiver_id", "create_time"])
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "receiver_id") val receiverId: String,
    val content: String,
    @ColumnInfo(name = "message_type") val messageType: Int = 1,
    @ColumnInfo(name = "is_read") val isRead: Int = 0,
    @ColumnInfo(name = "create_time") val createTime: Long
)


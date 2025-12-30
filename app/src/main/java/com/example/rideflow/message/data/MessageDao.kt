package com.example.rideflow.message.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query(
        """
        SELECT * FROM messages
        WHERE (sender_id = :ownerId AND receiver_id = :peerId)
           OR (sender_id = :peerId AND receiver_id = :ownerId)
        ORDER BY create_time ASC
        """
    )
    fun observeConversation(ownerId: String, peerId: String): Flow<List<MessageEntity>>

    @Query(
        """
        SELECT * FROM messages
        WHERE (sender_id = :ownerId AND receiver_id = :peerId)
           OR (sender_id = :peerId AND receiver_id = :ownerId)
        ORDER BY create_time DESC
        LIMIT 1
        """
    )
    suspend fun latestInConversation(ownerId: String, peerId: String): MessageEntity?

    @Query("SELECT COUNT(*) FROM messages WHERE receiver_id = :ownerId AND sender_id = :peerId AND is_read = 0")
    suspend fun countUnread(ownerId: String, peerId: String): Int

    @Query("UPDATE messages SET is_read = 1 WHERE receiver_id = :ownerId AND sender_id = :peerId AND is_read = 0")
    suspend fun markRead(ownerId: String, peerId: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(message: MessageEntity): Long
}


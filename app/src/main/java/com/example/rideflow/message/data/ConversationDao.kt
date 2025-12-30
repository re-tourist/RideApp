package com.example.rideflow.message.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE owner_id = :ownerId ORDER BY last_time DESC")
    fun observeByOwner(ownerId: String): Flow<List<ConversationEntity>>

    @Query("SELECT COUNT(*) FROM conversations WHERE owner_id = :ownerId")
    suspend fun countByOwner(ownerId: String): Int

    @Upsert
    suspend fun upsert(conversation: ConversationEntity)
}


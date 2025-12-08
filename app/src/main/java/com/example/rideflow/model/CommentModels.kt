package com.example.rideflow.model

data class CommentBTO(
    val commentId: Long,
    val feedId: Long,
    val authorId: Long,
    val content: String,
    val createdAt: String
)

data class CommentCreateRequest(
    val feedId: Long,
    val authorId: Long,
    val content: String
)

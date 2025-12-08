package com.example.rideflow.model

data class FeedSummaryBTO(
    val feedId: Long,
    val authorId: Long,
    val content: String,
    val createdAt: String
)

data class FeedDetailBTO(
    val feedId: Long,
    val authorId: Long,
    val content: String,
    val images: List<String>?,
    val createdAt: String,
    val comments: List<CommentBTO>?
)

data class FeedCreateRequest(
    val authorId: Long,
    val content: String,
    val images: List<String>?
)

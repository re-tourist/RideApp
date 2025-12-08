package com.example.rideflow.model

data class ClubSummaryBTO(
    val clubId: Long,
    val name: String,
    val memberCount: Int
)

data class ClubDetailBTO(
    val clubId: Long,
    val name: String,
    val description: String?,
    val members: List<ClubMemberBTO>?
)

data class ClubMemberBTO(
    val userId: Long,
    val nickname: String,
    val role: String?
)

data class ClubCreateRequest(
    val name: String,
    val description: String?
)

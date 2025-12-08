package com.example.rideflow.model

data class CompetitionSummaryBTO(
    val compId: Long,
    val title: String,
    val date: String,
    val location: String?
)

data class CompetitionDetailBTO(
    val compId: Long,
    val title: String,
    val description: String?,
    val date: String,
    val rules: String?
)

data class CompetitionRegisterRequest(
    val compId: Long,
    val userId: Long
)

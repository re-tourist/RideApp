package com.example.rideflow.model

data class ActivitySummaryBTO(
    val activityId: Long,
    val title: String,
    val startTime: String,
    val venue: String?
)

data class ActivityDetailBTO(
    val activityId: Long,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String?,
    val participants: List<Long>?
)

data class ActivityCreateRequest(
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String?,
    val venue: String?
)

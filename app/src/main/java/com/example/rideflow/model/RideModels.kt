package com.example.rideflow.model

data class RideRecordSummaryBTO(
    val rideId: Long,
    val date: String,
    val distanceKm: Double,
    val durationMin: Int
)

data class RideRecordDetailBTO(
    val rideId: Long,
    val path: List<GpsPointBTO>,
    val distanceKm: Double,
    val durationMin: Int,
    val avgSpeedKmh: Double,
    val calories: Int?
)

data class GpsPointBTO(
    val lat: Double,
    val lng: Double,
    val ts: Long
)

data class RideUploadRequest(
    val rideId: Long? = null,
    val path: List<GpsPointBTO>,
    val distanceKm: Double,
    val durationMin: Int
)

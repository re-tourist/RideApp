package com.example.rideflow.model

data class RouteSummaryBTO(
    val routeId: Long,
    val title: String,
    val distanceKm: Double,
    val elevationGainM: Double?
)

data class RouteDetailBTO(
    val routeId: Long,
    val title: String,
    val description: String?,
    val points: List<GpsPointBTO>
)

data class RouteCreateRequest(
    val title: String,
    val points: List<GpsPointBTO>,
    val description: String?
)

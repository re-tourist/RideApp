package com.example.rideflow.model

data class DiscoveryHomeBTO(
    val featuredRoutes: List<RouteSummaryBTO>?,
    val popularClubs: List<ClubSummaryBTO>?,
    val latestFeeds: List<FeedSummaryBTO>?
)

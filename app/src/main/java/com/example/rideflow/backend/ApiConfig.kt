package com.example.rideflow.backend

object ApiConfig {
    // 基础地址
    const val BASE_URL = "http://101.37.79.220:8080"

    // 登录、注册界面的 API 路径
    const val PATH_REGISTER = "/api/v1/auth/register"
    const val PATH_LOGIN    = "/api/v1/auth/login"

    // User / Profile
    const val PATH_USER_ME = "/api/v1/users/me"
    const val PATH_USER_ME_UPDATE = "/api/v1/users/me"
    const val PATH_USER_ME_STATS = "/api/v1/users/me/stats"
    const val PATH_USER_PROFILE = "/api/v1/users/"

    // Ride
    const val PATH_RIDE_LIST = "/api/v1/rides"
    const val PATH_RIDE_DETAIL = "/api/v1/rides/"
    const val PATH_RIDE_CREATE = "/api/v1/rides"
    const val PATH_RIDE_DELETE = "/api/v1/rides/"

    // Route
    const val PATH_ROUTE_LIST = "/api/v1/routes"
    const val PATH_ROUTE_DETAIL = "/api/v1/routes/"
    const val PATH_ROUTE_CREATE = "/api/v1/routes"
    const val PATH_ROUTE_FAVORITE = "/api/v1/routes/"
    const val PATH_MY_ROUTES = "/api/v1/users/me/routes"

    // Activity
    const val PATH_ACTIVITY_LIST = "/api/v1/activities"
    const val PATH_ACTIVITY_DETAIL = "/api/v1/activities/"
    const val PATH_ACTIVITY_CREATE = "/api/v1/activities"
    const val PATH_ACTIVITY_JOIN = "/api/v1/activities/"
    const val PATH_MY_ACTIVITIES = "/api/v1/users/me/activities"

    // Competition
    const val PATH_COMPETITION_LIST = "/api/v1/competitions"
    const val PATH_COMPETITION_DETAIL = "/api/v1/competitions/"
    const val PATH_COMPETITION_REGISTER = "/api/v1/competitions/"
    const val PATH_MY_COMPETITIONS = "/api/v1/users/me/competitions"

    // Club
    const val PATH_CLUB_LIST = "/api/v1/clubs"
    const val PATH_CLUB_DETAIL = "/api/v1/clubs/"
    const val PATH_CLUB_CREATE = "/api/v1/clubs"
    const val PATH_CLUB_JOIN = "/api/v1/clubs/"
    const val PATH_CLUB_MEMBERS = "/api/v1/clubs/"
    const val PATH_MY_CLUBS = "/api/v1/users/me/clubs"

    // Feed
    const val PATH_FEED_LIST = "/api/v1/feeds"
    const val PATH_FEED_DETAIL = "/api/v1/feeds/"
    const val PATH_FEED_CREATE = "/api/v1/feeds"
    const val PATH_FEED_DELETE = "/api/v1/feeds/"
    const val PATH_FEED_LIKE = "/api/v1/feeds/"

    // Comment
    const val PATH_COMMENT_LIST = "/api/v1/comments"
    const val PATH_COMMENT_CREATE = "/api/v1/comments"
    const val PATH_COMMENT_DELETE = "/api/v1/comments/"
    const val PATH_COMMENT_LIKE = "/api/v1/comments/"

    // Discovery
    const val PATH_DISCOVERY_HOME = "/api/v1/discovery/home"

    // 保留旧常量，防止已有代码引用失败
    const val PATH_PROFILE = "/api/v1/profile"
}

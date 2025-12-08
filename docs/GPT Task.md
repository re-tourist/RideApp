# GPT Task

我先把整体拆成 4 个大步骤，然后在每个步骤里**按文件名列清单**，你可以直接丢给 code agent 写代码。

------

## 第 0 步：统一约定（所有文件都要遵守）

- 所有接口返回统一包装：`ApiResponse<T>`
- 所有列表分页统一使用：`PageResult<T>`
- 所有网络调用统一通过：`HttpClient.get / post / put / delete`
- 所有需要登录的接口统一在 Header 里带 `Authorization: Bearer {token}`（由 HttpClient 负责）

------

## 第 1 步：`backend/ApiConfig.kt` 要实现什么

**目标**：集中维护所有后端路径，不直接在 ApiKit 里写字符串。

文件：`backend/ApiConfig.kt`

需要实现内容（常量名可以微调，但不要太离谱，方便搜索）：

1. **基础配置**
   - `const val BASE_URL: String`
     - 示例：`"http://101.37.79.220:8080"`
2. **Auth（已有，检查即可）**
   - `const val PATH_REGISTER = "/api/v1/auth/register"`  // POST
   - `const val PATH_LOGIN = "/api/v1/auth/login"`        // POST
3. **User / Profile**
   - `const val PATH_USER_ME = "/api/v1/users/me"`                // GET: 当前用户资料
   - `const val PATH_USER_ME_UPDATE = "/api/v1/users/me"`         // PUT: 更新资料
   - `const val PATH_USER_ME_STATS = "/api/v1/users/me/stats"`    // GET: 我的统计
   - （可留口子）`const val PATH_USER_PROFILE = "/api/v1/users/"` // GET: +id 查看他人
4. **Ride（骑行记录）**
   - `const val PATH_RIDE_LIST = "/api/v1/rides"`         // GET: 我的记录列表，带 ?page=
   - `const val PATH_RIDE_DETAIL = "/api/v1/rides/"`      // GET: +id
   - `const val PATH_RIDE_CREATE = "/api/v1/rides"`       // POST: 上传记录
   - （可选）`const val PATH_RIDE_DELETE = "/api/v1/rides/"` // DELETE: +id
5. **Route（路书）**
   - `const val PATH_ROUTE_LIST = "/api/v1/routes"`                 // GET: 列表
   - `const val PATH_ROUTE_DETAIL = "/api/v1/routes/"`              // GET: +id
   - `const val PATH_ROUTE_CREATE = "/api/v1/routes"`               // POST: 创建路书
   - `const val PATH_ROUTE_FAVORITE = "/api/v1/routes/"`            // POST/DELETE: +id + "/favorite"`
   - `const val PATH_MY_ROUTES = "/api/v1/users/me/routes"`         // GET: ?type=created|favorited
6. **Activity（活动）**
   - `const val PATH_ACTIVITY_LIST = "/api/v1/activities"`
   - `const val PATH_ACTIVITY_DETAIL = "/api/v1/activities/"`
   - `const val PATH_ACTIVITY_CREATE = "/api/v1/activities"`
   - `const val PATH_ACTIVITY_JOIN = "/api/v1/activities/"`         // +id + "/join"`
   - `const val PATH_MY_ACTIVITIES = "/api/v1/users/me/activities"` // GET: ?type=joined|created
7. **Competition（赛事）**
   - `const val PATH_COMPETITION_LIST = "/api/v1/competitions"`
   - `const val PATH_COMPETITION_DETAIL = "/api/v1/competitions/"`
   - `const val PATH_COMPETITION_REGISTER = "/api/v1/competitions/"`  // +id + "/register"`
   - `const val PATH_MY_COMPETITIONS = "/api/v1/users/me/competitions"`
8. **Club（俱乐部）**
   - `const val PATH_CLUB_LIST = "/api/v1/clubs"`
   - `const val PATH_CLUB_DETAIL = "/api/v1/clubs/"`
   - `const val PATH_CLUB_CREATE = "/api/v1/clubs"`
   - `const val PATH_CLUB_JOIN = "/api/v1/clubs/"`           // +id + "/join"`
   - `const val PATH_CLUB_MEMBERS = "/api/v1/clubs/"`        // +id + "/members"`
   - `const val PATH_MY_CLUBS = "/api/v1/users/me/clubs"`
9. **Feed（动态）**
   - `const val PATH_FEED_LIST = "/api/v1/feeds"`             // GET: ?type=follow|public&page=
   - `const val PATH_FEED_DETAIL = "/api/v1/feeds/"`
   - `const val PATH_FEED_CREATE = "/api/v1/feeds"`
   - `const val PATH_FEED_DELETE = "/api/v1/feeds/"`
   - `const val PATH_FEED_LIKE = "/api/v1/feeds/"`           // +id + "/like"`
10. **Comment（评论）**
    - `const val PATH_COMMENT_LIST = "/api/v1/comments"`      // GET: ?targetType=&targetId=
    - `const val PATH_COMMENT_CREATE = "/api/v1/comments"`
    - `const val PATH_COMMENT_DELETE = "/api/v1/comments/"`
    - `const val PATH_COMMENT_LIKE = "/api/v1/comments/"`     // +id + "/like"`
11. **Discovery（发现）**
    - `const val PATH_DISCOVERY_HOME = "/api/v1/discovery/home"`

> 说明：常量只负责路径前缀，对 URL 参数（`?page=&type=`）和拼接 `/{id}` 由 ApiKit 负责。

------

## 第 2 步：`model/ApiResponse.kt` 要实现什么

文件：`model/ApiResponse.kt`

要实现的内容：

1. **通用响应包装**

   ```kotlin
   data class ApiResponse<T>(
       val code: Int,
       val message: String,
       val data: T?
   )
   ```

2. **通用分页结果**

   ```kotlin
   data class PageResult<T>(
       val items: List<T>,
       val page: Int,
       val pageSize: Int,
       val total: Long
   )
   ```

3. （可选）封装一个判断是否成功的扩展函数：

   ```kotlin
   val ApiResponse<*>.isSuccess: Boolean
       get() = code == 0
   ```

------

## 第 3 步：新增的 BTO 文件要实现什么（放 `model/`）

下面按**文件名 → 需要包含的 data class** 列出。

### 3.1 `model/UserModels.kt`

1. `UserProfileBTO`
   - 字段：
     - `id: Long`
     - `phone: String?`
     - `nickname: String`
     - `avatarUrl: String?`
     - `gender: Int?`
     - `birthday: String?`
     - `location: String?`
     - `bio: String?`
     - `bikeType: String?`
     - `ridingLevel: String?`
     - `followerCount: Int`
     - `followingCount: Int`
2. `UserProfileUpdateRequest`
   - 用于 PUT `/users/me`
   - 字段（允许为 null，表示不修改）：
     - `nickname: String?`
     - `avatarUrl: String?`
     - `gender: Int?`
     - `birthday: String?`
     - `location: String?`
     - `bio: String?`
     - `bikeType: String?`
     - `ridingLevel: String?`
3. `UserStatsBTO`
   - 字段：
     - `totalDistanceKm: Double`
     - `totalDurationMinutes: Int`
     - `monthDistanceKm: Double`
     - `monthDurationMinutes: Int`
     - `rideDaysThisMonth: Int`
4. （可选）`FollowUserItemBTO` 给关注列表用
   - `id, nickname, avatarUrl, isMutual: Boolean`

------

### 3.2 `model/RideModels.kt`

1. `RideRecordSummaryBTO`
   - 列表卡片用：
     - `id: Long`
     - `startTime: String`
     - `durationMinutes: Int`
     - `distanceKm: Double`
     - `avgSpeed: Double`
     - `maxSpeed: Double`
     - `calories: Int`
2. `GpsPointBTO`
   - `lat: Double`
   - `lng: Double`
   - `elevation: Double?`
   - `timeOffsetSec: Int?`
3. `RideRecordDetailBTO`
   - 详情：
     - 上面 summary 里的字段
     - `title: String?`
     - `endTime: String`
     - `elevationGain: Int?`
     - `startLocation: String?`
     - `endLocation: String?`
     - `gpsTrack: List<GpsPointBTO>`
     - `weather: String?`
     - `isPublic: Boolean`
     - `routeId: Long?`
4. `RideUploadRequest`
   - 上传时用：
     - `title: String?`
     - `startTime: String`
     - `endTime: String`
     - `gpsTrack: List<GpsPointBTO>`
     - `routeId: Long?`

------

### 3.3 `model/RouteModels.kt`

1. `RouteSummaryBTO`
   - 列表：
     - `id: Long`
     - `title: String`
     - `distanceKm: Double`
     - `elevationGain: Int?`
     - `difficulty: Int`
     - `coverImage: String?`
     - `creatorId: Long`
     - `creatorNickname: String`
     - `favoriteCount: Int`
     - `isFavorited: Boolean`
     - `isMine: Boolean`
2. `RouteDetailBTO`
   - 详情：
     - `id, title, description: String?`
     - `distanceKm, elevationGain, difficulty`
     - `coverImage`
     - `creatorId, creatorNickname, creatorAvatarUrl`
     - `gpsTrack: List<GpsPointBTO>`
     - `tags: List<String>`
     - `isPublic: Boolean`
     - `isFavorited: Boolean`
     - `isMine: Boolean`
     - `favoriteCount: Int`
     - `createdAt: String`
3. `RouteCreateRequest`
   - `title: String`
   - `description: String?`
   - `gpsTrack: List<GpsPointBTO>`
   - `tags: List<String>`
   - `isPublic: Boolean`

------

### 3.4 `model/ActivityModels.kt`

1. `ActivitySummaryBTO`
   - `id: Long`
   - `title: String`
   - `coverImage: String?`
   - `startTime: String`
   - `endTime: String`
   - `city: String?`
   - `organizerName: String`
   - `maxParticipants: Int`
   - `currentParticipants: Int`
   - `status: String`        // registering/ongoing/finished
   - `hasJoined: Boolean`
2. `ActivityDetailBTO`
   - 上面 summary 字段 +
   - `description: String?`
   - `location: String?`
   - `lat: Double?`
   - `lng: Double?`
   - `organizerId: Long`
   - `isOrganizer: Boolean`
   - `participantsPreview: List<SimpleUserBTO>`（在 `UserModels.kt` 里定义 SimpleUserBTO）
3. `ActivityCreateRequest`
   - `title, description, startTime, endTime, location, lat, lng, maxParticipants`

------

### 3.5 `model/CompetitionModels.kt`

1. `CompetitionSummaryBTO`
   - `id: Long`
   - `title: String`
   - `coverImage: String?`
   - `category: String`
   - `city: String?`
   - `registerEnd: String`
   - `competitionStart: String`
   - `status: String`
   - `hasRegistered: Boolean`
2. `CompetitionDetailBTO`
   - summary 字段 +
   - `description: String?`
   - `registerStart: String`
   - `competitionEnd: String`
   - `rules: String?`
3. `CompetitionRegisterRequest`
   - `group: String?`

------

### 3.6 `model/ClubModels.kt`

1. `ClubSummaryBTO`
   - `id: Long`
   - `name: String`
   - `logoUrl: String?`
   - `city: String?`
   - `memberCount: Int`
   - `isMember: Boolean`
   - `isOwner: Boolean`
2. `ClubDetailBTO`
   - summary 字段 +
   - `description: String?`
   - `ownerId: Long`
   - `ownerNickname: String`
3. `ClubMemberBTO`
   - `user: SimpleUserBTO`
   - `role: String` // owner/admin/member
4. `ClubCreateRequest`
   - `name: String`
   - `logoUrl: String?`
   - `city: String?`
   - `description: String?`

------

### 3.7 `model/FeedModels.kt`

1. `FeedSummaryBTO`
   - `id: Long`
   - `user: SimpleUserBTO`
   - `content: String`
   - `images: List<String>`
   - `createdAt: String`
   - `likeCount: Int`
   - `commentCount: Int`
   - `hasLiked: Boolean`
   - `relatedRouteId: Long?`
   - `relatedRecordId: Long?`
2. `FeedDetailBTO`
   - 可以先等于 `FeedSummaryBTO`，后面如果详情字段更多再扩。
3. `FeedCreateRequest`
   - `content: String`
   - `images: List<String>`
   - `relatedRouteId: Long?`
   - `relatedRecordId: Long?`

------

### 3.8 `model/CommentModels.kt`

1. `CommentBTO`
   - `id: Long`
   - `user: SimpleUserBTO`
   - `targetType: String`
   - `targetId: Long`
   - `content: String`
   - `createdAt: String`
   - `likeCount: Int`
   - `hasLiked: Boolean`
2. `CommentCreateRequest`
   - `targetType: String`
   - `targetId: Long`
   - `content: String`

------

### 3.9 `model/DiscoveryModels.kt`（简单即可）

1. `DiscoveryHomeBTO`
   - `topCompetitions: List<CompetitionSummaryBTO>`
   - `hotRoutes: List<RouteSummaryBTO>`
   - `nearbyActivities: List<ActivitySummaryBTO>`
   - `hotClubs: List<ClubSummaryBTO>`
   - `nearbyRiders: List<SimpleUserBTO>`

------

## 第 4 步：新增的 ApiKit 文件要实现什么（放 `backend/`）

每个 ApiKit 的职责：
 **只做一件事——调用 HttpClient，拼 URL，传入请求体和 token，返回 `ApiResponse<...>`。**

### 4.1 `backend/ProfileApiKit.kt`

需要实现的函数：

1. `suspend fun getMyProfile(token: String): ApiResponse<UserProfileBTO>`
   - GET `ApiConfig.PATH_USER_ME`
2. `suspend fun updateMyProfile(token: String, body: UserProfileUpdateRequest): ApiResponse<UserProfileBTO>`
   - PUT `ApiConfig.PATH_USER_ME_UPDATE`
3. `suspend fun getMyStats(token: String): ApiResponse<UserStatsBTO>`
   - GET `ApiConfig.PATH_USER_ME_STATS`
4. （可选）`getUserProfile(id)`、`getUserStats(id)` 给查看他人用

------

### 4.2 `backend/RideApiKit.kt`

1. `suspend fun getMyRides(token: String, page: Int, pageSize: Int): ApiResponse<PageResult<RideRecordSummaryBTO>>`
   - GET `PATH_RIDE_LIST + "?page=$page&pageSize=$pageSize"`
2. `suspend fun getRideDetail(token: String, rideId: Long): ApiResponse<RideRecordDetailBTO>`
   - GET `PATH_RIDE_DETAIL + rideId`
3. `suspend fun uploadRide(token: String, body: RideUploadRequest): ApiResponse<RideRecordDetailBTO>`
   - POST `PATH_RIDE_CREATE`
4. （可选）`deleteRide(token, rideId)`
   - DELETE `PATH_RIDE_DELETE + rideId`

------

### 4.3 `backend/RouteApiKit.kt`

1. `suspend fun listRoutes(token: String?, page: Int, pageSize: Int, sort: String?, tag: String?): ApiResponse<PageResult<RouteSummaryBTO>>`
   - GET `PATH_ROUTE_LIST` + 查询参数
2. `suspend fun getRouteDetail(token: String?, routeId: Long): ApiResponse<RouteDetailBTO>`
   - GET `PATH_ROUTE_DETAIL + routeId`
3. `suspend fun createRoute(token: String, body: RouteCreateRequest): ApiResponse<RouteDetailBTO>`
   - POST `PATH_ROUTE_CREATE`
4. `suspend fun favoriteRoute(token: String, routeId: Long): ApiResponse<Unit>`
   - POST `PATH_ROUTE_FAVORITE + "$routeId/favorite"`
5. `suspend fun unfavoriteRoute(token: String, routeId: Long): ApiResponse<Unit>`
   - DELETE 同一路径
6. `suspend fun getMyRoutes(token: String, type: String): ApiResponse<List<RouteSummaryBTO>>`
   - GET `PATH_MY_ROUTES + "?type=$type"`  // type = "created"|"favorited"

------

### 4.4 `backend/ActivityApiKit.kt`

1. `listActivities(token, page, pageSize, city, status): ApiResponse<PageResult<ActivitySummaryBTO>>`
   - GET `PATH_ACTIVITY_LIST + query`
2. `getActivityDetail(token, id): ApiResponse<ActivityDetailBTO>`
   - GET `PATH_ACTIVITY_DETAIL + id`
3. `createActivity(token, body: ActivityCreateRequest): ApiResponse<ActivityDetailBTO>`
   - POST `PATH_ACTIVITY_CREATE`
4. `joinActivity(token, id): ApiResponse<Unit>`
   - POST `PATH_ACTIVITY_JOIN + "$id/join"`
5. `quitActivity(token, id): ApiResponse<Unit>`
   - DELETE 同一路径
6. `getMyActivities(token, type): ApiResponse<List<ActivitySummaryBTO>>`
   - GET `PATH_MY_ACTIVITIES + "?type=$type"`

------

### 4.5 `backend/CompetitionApiKit.kt`

1. `listCompetitions(...) : ApiResponse<PageResult<CompetitionSummaryBTO>>`
2. `getCompetitionDetail(token, id): ApiResponse<CompetitionDetailBTO>`
3. `registerCompetition(token, id, body: CompetitionRegisterRequest): ApiResponse<Unit>`
4. `cancelCompetitionRegistration(token, id): ApiResponse<Unit>`
5. `getMyCompetitions(token, type): ApiResponse<List<CompetitionSummaryBTO>>`

路径分别用 `PATH_COMPETITION_LIST / DETAIL / REGISTER / MY_COMPETITIONS` 拼接。

------

### 4.6 `backend/ClubApiKit.kt`

1. `listClubs(...) : ApiResponse<PageResult<ClubSummaryBTO>>`
2. `getClubDetail(token, id): ApiResponse<ClubDetailBTO>`
3. `createClub(token, body: ClubCreateRequest): ApiResponse<ClubDetailBTO>`
4. `joinClub(token, id): ApiResponse<Unit>`
5. `quitClub(token, id): ApiResponse<Unit>`
6. `getClubMembers(token, id): ApiResponse<List<ClubMemberBTO>>`
7. `getMyClubs(token, type): ApiResponse<List<ClubSummaryBTO>>`

------

### 4.7 `backend/FeedApiKit.kt`

1. `listFeeds(token, type, page, pageSize): ApiResponse<PageResult<FeedSummaryBTO>>`
   - `type`: follow / public
2. `getFeedDetail(token, id): ApiResponse<FeedDetailBTO>`
3. `createFeed(token, body: FeedCreateRequest): ApiResponse<FeedDetailBTO>`
4. `deleteFeed(token, id): ApiResponse<Unit>`
5. `likeFeed(token, id): ApiResponse<Unit>`
6. `unlikeFeed(token, id): ApiResponse<Unit>`

------

### 4.8 `backend/CommentApiKit.kt`

1. `listComments(token, targetType, targetId, page, pageSize): ApiResponse<PageResult<CommentBTO>>`
   - GET `PATH_COMMENT_LIST + "?targetType=$t&targetId=$id&page=..."`
2. `createComment(token, body: CommentCreateRequest): ApiResponse<CommentBTO>`
3. `deleteComment(token, id): ApiResponse<Unit>`
4. `likeComment(token, id): ApiResponse<Unit>`
5. `unlikeComment(token, id): ApiResponse<Unit>`

------

### 4.9 `backend/DiscoveryApiKit.kt`

1. `getDiscoveryHome(token: String?): ApiResponse<DiscoveryHomeBTO>`
   - GET `PATH_DISCOVERY_HOME`

------

这样一份清单基本就能**喂给任何 code agent**：

- 先生成 BTO 文件；
- 再生成 ApiKit；
- 然后你在 ViewModel / Repository 里调用即可。

如果你想从某一个模块先下手（比如 Profile 或 Ride），下一步我可以把该模块的**完整 Kotlin 代码草稿**直接写出来。
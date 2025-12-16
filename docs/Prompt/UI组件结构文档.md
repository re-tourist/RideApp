# UI组件结构文档

下面给你一份**最适合 Android 落地**的「我的 / Profile」页面组件层级拆解：**优先 Jetpack Compose**（现代安卓最省事、最好和 AI agent 协作），同时我也给出 **Flutter 对应结构**，方便你们如果是 Flutter 也能直接用。

------

# A. Jetpack Compose 组件层级拆解（推荐）

## 0）页面目标与约束（给 agent 的硬约束）

- **删除“编辑资料”按钮**（本页不出现）
- 顶部 **HUD 功能图标在上**，头像与用户信息区在下（竖直方向错位，不同高度层级）
- 增加：**个性签名**、**骑友**等更多入口（关注/粉丝/俱乐部/勋章等）
- 用户信息区整块可点：进入「完整个人主页」
- 结构参考 Keep：**顶部轻、下面重**（上展示入口，中数据，下功能列表）

------

## 1）Screen 入口

```kotlin
@Composable
fun ProfileScreen(
  state: ProfileUiState,
  onAction: (ProfileAction) -> Unit
)
```

### UI State（建议字段）

- user: avatarUrl, nickname, userId, level/segment, signature
- socialCounts: followingCount, followerCount, clubCount, badgeCount
- stats: weekDuration, monthDistance, monthCalories, monthRideCount（或本周/本月任选一套）
- menuItems: List
- notificationBadge: Int

------

## 2）整体布局骨架

```text
Scaffold
 ├─ topBar: ProfileTopHud(...)
 └─ content:
     LazyColumn (vertical scroll)
      ├─ item: ProfileHeaderCard(...)          // 头像+昵称+ID+签名（可点进主页）
      ├─ item: SocialQuickActionsRow(...)      // 骑友/粉丝/俱乐部/勋章（新增）
      ├─ item: RideStatsCard(...)              // 我的数据卡片（本周/本月）
      ├─ item: ShortcutGrid(optional)          // 可选：常用快捷入口（2x2）
      ├─ item: MenuSectionTitle("功能")         // 可选分组标题
      ├─ items: ProfileMenuList(...)           // 骑行记录/运动日历/我的活动/系统设置...
      └─ item: BottomSpacer
```

------

## 3）顶部 HUD：ProfileTopHud（功能图标在最上）

> 关键：HUD 只放图标，不和头像同行，不出现“编辑资料”。

```text
ProfileTopHud (TopAppBar / Box)
 ├─ LeftIcon(optional): AddFriend / QR / Device
 └─ RightIcons:
     ├─ IconButton(NotificationBell + badge)
     └─ IconButton(Settings)
```

交互：

- 点击通知：`onAction(OpenNotifications)`
- 点击设置：`onAction(OpenSettings)`
- 可选左侧：`onAction(OpenAddFriend)` / `onAction(OpenScanner)` / `onAction(OpenDeviceCenter)`

------

## 4）个人简略主页区：ProfileHeaderCard（头像在下方主视觉）

> 关键：头像与顶部 HUD 竖直错位；这一整块可点进“个人主页详情”。

```text
ProfileHeaderCard (Card)
 ├─ Row
 │   ├─ Avatar (圆形)
 │   └─ Column
 │       ├─ Nickname (标题)
 │       ├─ SubInfoRow: "用户ID: xxx" + Level/Segment (徽章样式)
 │       └─ Signature (新增，1~2行，弱强调)
 └─ RightChevronIcon (可选，提示可进入)
```

交互：

- 点击整卡：`onAction(OpenUserProfile(userId))`

------

## 5）社交与身份入口：SocialQuickActionsRow（新增“骑友”等）

> 这是 Keep 风格的核心：把“社交关系”放在 Profile 页上半段。

```text
SocialQuickActionsRow (Row, evenly spaced)
 ├─ SocialStatChip("骑友", followingCount) -> OpenFollowing
 ├─ SocialStatChip("粉丝", followerCount)  -> OpenFollowers
 ├─ SocialStatChip("俱乐部", clubCount)    -> OpenClubs
 └─ SocialStatChip("勋章", badgeCount)     -> OpenBadges
```

说明：

- “骑友”可以定义为「我关注的人」或「互关」；你们选一种保持一致即可。
- Chip 视觉：弱边框/弱底色，不要像传统按钮。

------

## 6）数据卡片：RideStatsCard（“我的数据”）

```text
RideStatsCard (Card)
 ├─ TitleRow: "我的数据" + (右侧箭头/更多)
 ├─ MiniBarChart(optional, placeholder bars)
 └─ MetricsRow (3~4列)
     ├─ MetricItem("本周时长", "xx 分钟")
     ├─ MetricItem("本月里程", "xx km")
     ├─ MetricItem("卡路里", "xx kcal")
     └─ MetricItem("次数", "xx 次") (可选)
```

交互：

- 点击卡片：`onAction(OpenStatsDetail(userId))`

------

## 7）功能列表：ProfileMenuList（下半部分工具区）

> 你们当前的“骑行偏好/骑行记录/运动日历/我的活动/系统设置”保留，但变成分组列表。

```text
ProfileMenuList (Card or Sectioned List)
 ├─ MenuItemRow(icon, "骑行记录", chevron) -> OpenRideHistory
 ├─ MenuItemRow(icon, "运动日历", chevron) -> OpenCalendar
 ├─ MenuItemRow(icon, "我的活动", chevron) -> OpenMyActivities
 ├─ MenuItemRow(icon, "我的动态", chevron) -> OpenMyPosts
 ├─ MenuItemRow(icon, "收藏与加油", chevron) -> OpenFavorites
 └─ MenuItemRow(icon, "系统设置", chevron) -> OpenSettings
```

建议分组（可选）：

- **骑行**：骑行记录 / 运动日历 / 数据报告
- **社交**：我的动态 / 骑友 / 俱乐部
- **系统**：设置 / 关于 / 帮助

------

## 8）事件与 Action 枚举（给 agent 的接口）

```text
ProfileAction:
 - OpenUserProfile(userId)
 - OpenNotifications
 - OpenSettings
 - OpenFollowing / OpenFollowers
 - OpenClubs / OpenBadges
 - OpenStatsDetail(userId)
 - OpenRideHistory
 - OpenCalendar
 - OpenMyActivities
 - OpenMyPosts
 - OpenFavorites
 - OpenAddFriend / OpenScanner / OpenDeviceCenter (optional)
```

------

# B. Flutter 组件层级拆解（如果你们是 Flutter）

```text
Scaffold
 ├─ appBar: PreferredSize(ProfileTopHud)
 └─ body: CustomScrollView
     ├─ SliverToBoxAdapter(ProfileHeaderCard)
     ├─ SliverToBoxAdapter(SocialQuickActionsRow)
     ├─ SliverToBoxAdapter(RideStatsCard)
     ├─ SliverToBoxAdapter(MenuSectionTitle)
     └─ SliverList(delegate: MenuItemRow...)
```

组件对应：

- `ProfileTopHud`: Row + IconButton + Badge
- `ProfileHeaderCard`: Card + Row + CircleAvatar + Column + signature
- `SocialQuickActionsRow`: Row + Expanded + InkWell
- `RideStatsCard`: Card + Grid/Row metrics
- `MenuItemRow`: ListTile/InkWell

------

# C. 给你的 AI agent 一句落地指令（建议复制粘贴到任务最前）

> “请严格按组件树实现 ProfileScreen：TopHud 仅含图标（通知/设置/可选扫码），删除编辑资料按钮；HUD 在上、头像与用户信息卡在下方且整卡可点进主页；新增个性签名与‘骑友/粉丝/俱乐部/勋章’四个社交入口；中部是‘我的数据’卡片；底部为分组功能列表。所有点击事件用 ProfileAction 统一上抛。”

------


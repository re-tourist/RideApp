# RideApp（RideFlow）

当前分支：`develop`（可发布）

当前发布版本：`v2.0.0`

RideFlow 是面向骑行者的 Android 应用，使用 Kotlin + Jetpack Compose 实现主要 UI 与导航，覆盖骑行、发现、社区、消息、我的等核心页面。

## 功能概览

- 骑行：骑行中界面（开始/暂停/结束）、地图展示（AMap2D）、基础指标展示
- 记录：骑行记录列表与统计展示
- 发现：赛事/活动/俱乐部/骑友/路书等入口与详情页面
- 社区：动态列表、帖子详情、俱乐部动态/详情、点赞/评论等交互
- 消息：消息模块与会话/详情页面（Room 本地库）
- 我的：个人主页、资料展示与编辑、设置入口、成就徽章页等
- 图片：部分页面支持图片选择与上传（阿里云 OSS）

更完整的现状盘点与模块说明见：[docs/项目开发现状总览.md](docs/项目开发现状总览.md)

## 技术栈

- 语言/框架：Kotlin、Android、Jetpack Compose（Material 3）
- 依赖注入：Koin
- 本地存储：Room、DataStore（Preferences）
- 图片加载：Coil
- 地图/定位：AMap2D（map2d/search/location）、Google Location Services
- 图片上传：阿里云 OSS SDK
- 网络/数据：当前版本主要通过 JDBC 直连 MySQL（后续建议迁移到后端 REST API）

## 快速开始

### 环境要求

- Android Studio（Koala+ 建议）
- JDK 17（建议）；项目编译目标为 JVM 11
- Android SDK：`minSdk 24`，`targetSdk 36`

### 配置（本地）

本项目会从根目录 `local.properties` 读取 OSS 配置（不会提交到仓库）：

```properties
# local.properties（示例）
OSS_ACCESS_KEY_ID=YOUR_KEY_ID
OSS_ACCESS_KEY_SECRET=YOUR_KEY_SECRET
OSS_BUCKET_NAME=rideapp
OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
```

地图使用 AMap API Key（通过 manifestPlaceholders 注入）。如需替换 Key，请在 `app/build.gradle.kts` 中调整相关配置。

### 运行

- 用 Android Studio 打开仓库根目录并等待 Gradle Sync 完成
- 运行 `app` 配置启动 `MainActivity`
- 首次启动需在设备上授予定位、前台服务等权限；地图与定位能力依赖设备/模拟器配置

### 命令行构建（可选）

```bash
./gradlew :app:assembleDebug
```

## 仓库结构（关键目录）

```text
app/src/main/java/com/example/rideflow/
  auth/         登录注册与会话
  backend/      数据访问与数据库连接（当前为 JDBC 直连）
  message/      消息模块（Room）
  navigation/   路由与导航图
  profile/      个人资料相关
  services/     前台服务（骑行采集示例）
  ui/           Compose 页面与组件
docs/           设计文档与现状总览
```

## 分支与发布

- `develop`：开发主干（当前已达到可发布状态）
- `main`：正式发布分支（用于对外发布/交付）
- `release/v2.0.0`：本次发布分支（从 `develop` 切出，用于发版与回归）

分支规范参考：[docs/工作须知/git-workflow.md](docs/工作须知/git-workflow.md)

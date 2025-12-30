# RideFlow 数据库与接口设计说明

本文档根据 `rideapp.sql` 和现有 App 功能，使用你要求的“数据库表格格式”和“接口分组格式”整理，便于直接拷贝到详细设计文档中使用。

---

## 1. 数据库设计

以下只列出项目中最核心、最常用的业务表。字段类型和含义来自实际的 `rideapp.sql`，备注列简要说明主键、外键和非空等约束。

### 表1. 用户表 (users)

| 字段名             | 数据类型                       | 用途               | 备注                                   |
| ------------------ | ------------------------------ | ------------------ | -------------------------------------- |
| user_id            | INT                            | 用户ID             | 主键，自增，非空                       |
| nickname           | VARCHAR(50)                    | 用户昵称           | 唯一，非空                             |
| email              | VARCHAR(100)                   | 邮箱               | 唯一，非空                             |
| password_hash      | VARCHAR(255)                   | 登录密码           | 非空（当前为明文存储，应改为加密）     |
| avatar_url         | VARCHAR(255)                   | 头像URL            | 可空                                   |
| bio                | TEXT                           | 个性签名           | 可空                                   |
| gender             | ENUM('male','female','other')  | 性别               | 默认值 'other'                         |
| birthday           | DATE                           | 生日               | 可空                                   |
| emergency_contact  | VARCHAR(100)                   | 紧急联系人         | 可空                                   |
| status             | TINYINT                        | 用户状态           | 默认0：正常，1：禁用，2：未激活        |
| email_verified     | TINYINT(1)                     | 邮箱是否验证       | 默认0                                  |
| last_login_at      | TIMESTAMP                      | 最后登录时间       | 可空                                   |
| created_at         | TIMESTAMP                      | 创建时间           | 默认 CURRENT_TIMESTAMP                 |
| updated_at         | TIMESTAMP                      | 更新时间           | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表2. 骑行者资料表 (rider_profiles)

| 字段名        | 数据类型     | 用途           | 备注                                              |
| ------------- | ------------ | -------------- | ------------------------------------------------- |
| user_id       | INT          | 用户ID         | 主键，外键引用 users.user_id，非空               |
| city          | VARCHAR(100) | 城市           | 可空                                              |
| level         | VARCHAR(50)  | 骑行等级/称号  | 可空，例如“菜鸟骑迹”“金牌骑客”等               |
| main_club_id  | INT          | 主俱乐部ID     | 可空，外键引用 clubs.club_id                     |
| updated_at    | TIMESTAMP    | 更新时间       | 默认 CURRENT_TIMESTAMP ON UPDATE                  |

---

### 表3. 用户关注关系表 (user_follows)

| 字段名           | 数据类型  | 用途         | 备注                                              |
| ---------------- | --------- | ------------ | ------------------------------------------------- |
| follower_user_id | INT       | 关注者用户ID | 主键的一部分，外键引用 users.user_id，非空       |
| followed_user_id | INT       | 被关注用户ID | 主键的一部分，外键引用 users.user_id，非空       |
| created_at       | TIMESTAMP | 关注时间     | 默认 CURRENT_TIMESTAMP                            |

---

### 表4. 俱乐部表 (clubs)

| 字段名        | 数据类型     | 用途           | 备注                                   |
| ------------- | ------------ | -------------- | -------------------------------------- |
| club_id       | INT          | 俱乐部ID       | 主键，自增，非空                       |
| name          | VARCHAR(100) | 俱乐部名称     | 唯一，非空                             |
| city          | VARCHAR(100) | 所在城市       | 可空，建立城市索引                     |
| logo_url      | VARCHAR(255) | 俱乐部Logo     | 可空                                   |
| members_count | INT          | 成员数量       | 默认0                                  |
| heat          | INT          | 俱乐部热度     | 默认0，用于排序                        |
| created_at    | TIMESTAMP    | 创建时间       | 默认 CURRENT_TIMESTAMP                 |
| updated_at    | TIMESTAMP    | 更新时间       | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表5. 俱乐部成员表 (club_members)

| 字段名   | 数据类型                        | 用途       | 备注                                                     |
| -------- | -------------------------------- | ---------- | -------------------------------------------------------- |
| club_id  | INT                              | 俱乐部ID   | 主键的一部分，外键引用 clubs.club_id，非空              |
| user_id  | INT                              | 用户ID     | 主键的一部分，外键引用 users.user_id，非空              |
| role     | ENUM('member','captain')         | 成员角色   | 默认 'member'，可取“队长/成员”                           |
| joined_at| TIMESTAMP                        | 加入时间   | 默认 CURRENT_TIMESTAMP                                   |

---

### 表6. 活动表 (activities)

| 字段名             | 数据类型                                      | 用途         | 备注                                   |
| ------------------ | --------------------------------------------- | ------------ | -------------------------------------- |
| activity_id        | INT                                           | 活动ID       | 主键，自增，非空                       |
| title              | VARCHAR(150)                                  | 活动标题     | 非空                                   |
| organizer          | VARCHAR(150)                                  | 主办方       | 可空                                   |
| event_date         | DATETIME                                      | 活动时间     | 非空                                   |
| registration_time  | VARCHAR(100)                                  | 报名时间文案 | 可空，仅展示用                         |
| location           | VARCHAR(150)                                  | 活动地点     | 可空                                   |
| checkin_location   | VARCHAR(150)                                  | 签到地点     | 可空                                   |
| event_type         | ENUM('骑行','越野跑','徒步','其他')           | 活动类型     | 默认 '骑行'                            |
| is_open            | TINYINT(1)                                    | 是否开放报名 | 默认1                                  |
| cover_image_url    | VARCHAR(255)                                  | 封面图片     | 可空                                   |
| description        | TEXT                                          | 活动描述     | 可空                                   |
| created_at         | TIMESTAMP                                     | 创建时间     | 默认 CURRENT_TIMESTAMP                 |
| updated_at         | TIMESTAMP                                     | 更新时间     | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表7. 赛事表 (races)

| 字段名             | 数据类型                                                | 用途         | 备注                                   |
| ------------------ | ------------------------------------------------------- | ------------ | -------------------------------------- |
| race_id            | INT                                                     | 赛事ID       | 主键，自增，非空                       |
| title              | VARCHAR(150)                                            | 赛事名称     | 非空                                   |
| organizer          | VARCHAR(150)                                            | 主办方       | 可空                                   |
| event_date         | DATETIME                                                | 比赛时间     | 非空                                   |
| registration_time  | VARCHAR(100)                                            | 报名时间文案 | 可空，仅展示用                         |
| location           | VARCHAR(150)                                            | 比赛地点     | 可空                                   |
| checkin_location   | VARCHAR(150)                                            | 签到地点     | 可空                                   |
| event_type         | ENUM('娱乐赛','竞速赛','骑行','其他')                   | 赛事类型     | 默认 '娱乐赛'                          |
| is_open            | TINYINT(1)                                              | 是否开放报名 | 默认1                                  |
| cover_image_url    | VARCHAR(255)                                            | 封面图片     | 可空                                   |
| description        | TEXT                                                    | 赛事描述     | 可空                                   |
| created_at         | TIMESTAMP                                               | 创建时间     | 默认 CURRENT_TIMESTAMP                 |
| updated_at         | TIMESTAMP                                               | 更新时间     | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表8. 用户赛事报名卡表 (user_registration_cards)

| 字段名             | 数据类型      | 用途           | 备注                                                   |
| ------------------ | ------------- | -------------- | ------------------------------------------------------ |
| card_id            | INT           | 报名卡ID       | 主键，自增，非空                                      |
| user_id            | INT           | 用户ID         | 唯一约束，外键引用 users.user_id，每个用户一张，非空  |
| name               | VARCHAR(50)   | 姓名           | 非空                                                   |
| gender             | VARCHAR(10)   | 性别           | 可空                                                   |
| phone              | VARCHAR(20)   | 手机           | 非空                                                   |
| id_type            | VARCHAR(32)   | 证件类型       | 非空，例如二代身份证/护照等                           |
| id_number          | VARCHAR(64)   | 证件号码       | 非空                                                   |
| birthday           | DATE          | 出生日期       | 非空                                                   |
| email              | VARCHAR(100)  | 邮箱           | 可空                                                   |
| address            | VARCHAR(255)  | 省市区         | 可空                                                   |
| detailed_address   | VARCHAR(255)  | 详细地址       | 可空                                                   |
| emergency_contact  | VARCHAR(50)   | 紧急联系人     | 可空                                                   |
| emergency_phone    | VARCHAR(20)   | 紧急联系人电话 | 可空                                                   |
| created_at         | TIMESTAMP     | 创建时间       | 默认 CURRENT_TIMESTAMP                                 |
| updated_at         | TIMESTAMP     | 更新时间       | 默认 CURRENT_TIMESTAMP ON UPDATE                       |

---

### 表9. 路书表 (routes)

| 字段名        | 数据类型                | 用途         | 备注                                   |
| ------------- | ----------------------- | ------------ | -------------------------------------- |
| route_id      | INT                     | 路书ID       | 主键，自增，非空                       |
| title         | VARCHAR(150)           | 路书名称     | 非空                                   |
| distance_km   | DECIMAL(8,2)           | 距离(公里)   | 非空                                   |
| elevation_m   | INT                     | 爬升(米)     | 默认0                                  |
| location      | VARCHAR(100)           | 所在城市     | 可空                                   |
| difficulty    | ENUM('简单','中等','困难') | 难度     | 默认 '简单'                            |
| cover_image_url | VARCHAR(255)         | 封面图片     | 可空                                   |
| created_at    | TIMESTAMP              | 创建时间     | 默认 CURRENT_TIMESTAMP                 |
| updated_at    | TIMESTAMP              | 更新时间     | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表10. 路书标签表 (route_tags)

| 字段名   | 数据类型     | 用途       | 备注                                              |
| -------- | ------------ | ---------- | ------------------------------------------------- |
| route_id | INT          | 路书ID     | 主键的一部分，外键引用 routes.route_id，非空     |
| tag_name | VARCHAR(50)  | 标签名称   | 主键的一部分，非空                               |

---

### 表11. 路书收藏表 (route_favorites)

| 字段名   | 数据类型  | 用途         | 备注                                              |
| -------- | --------- | ------------ | ------------------------------------------------- |
| route_id | INT       | 路书ID       | 主键的一部分，外键引用 routes.route_id，非空     |
| user_id  | INT       | 用户ID       | 主键的一部分，外键引用 users.user_id，非空       |
| created_at | TIMESTAMP | 收藏时间   | 默认 CURRENT_TIMESTAMP                            |

---

### 表12. 用户骑行记录表 (user_ride_records)

| 字段名          | 数据类型      | 用途               | 备注                                              |
| --------------- | ------------- | ------------------ | ------------------------------------------------- |
| record_id       | INT           | 骑行记录ID         | 主键，自增，非空                                  |
| user_id         | INT           | 用户ID             | 外键引用 users.user_id，非空                     |
| route_id        | INT           | 路书ID             | 可空，外键引用 routes.route_id                   |
| start_time      | DATETIME      | 开始时间           | 非空                                              |
| duration_seconds| INT           | 总时长(秒)         | 非空                                              |
| duration_sec    | INT           | 兼容字段           | 冗余字段，与旧逻辑兼容                           |
| distance_km     | DECIMAL(8,2)  | 骑行距离(公里)     | 非空                                              |
| avg_speed_kmh   | DECIMAL(5,2)  | 平均速度(km/h)     | 默认0.00                                          |
| calories        | INT           | 消耗热量           | 默认0                                             |
| note            | VARCHAR(255)  | 备注1              | 可空                                              |
| notes           | VARCHAR(255)  | 备注2              | 可空                                              |
| created_at      | TIMESTAMP     | 记录时间           | 默认 CURRENT_TIMESTAMP                            |
| climb           | INT           | 总爬升(米)         | 可空                                              |
| max_speed_kmh   | DECIMAL(5,2)  | 最快速度(km/h)     | 可空                                              |
| track_image_url | VARCHAR(255)  | 轨迹截图URL        | 可空                                              |

---

### 表13. 发现文章表 (articles)

| 字段名      | 数据类型      | 用途         | 备注                                              |
| ----------- | ------------- | ------------ | ------------------------------------------------- |
| article_id  | INT           | 文章ID       | 主键，自增，非空                                  |
| title       | VARCHAR(200)  | 标题         | 非空                                              |
| author_id   | INT           | 作者用户ID   | 外键引用 users.user_id，非空                     |
| image_url   | VARCHAR(255)  | 封面图片URL  | 可空                                              |
| publish_date| DATE          | 发布时间     | 可空                                              |
| views       | INT           | 浏览量       | 默认0                                             |
| created_at  | TIMESTAMP     | 创建时间     | 默认 CURRENT_TIMESTAMP                            |

---

### 表14. 社区动态表 (community_posts)

| 字段名        | 数据类型      | 用途           | 备注                                              |
| ------------- | ------------- | -------------- | ------------------------------------------------- |
| post_id       | INT           | 动态ID         | 主键，自增，非空                                  |
| author_user_id| INT           | 作者用户ID     | 外键引用 users.user_id，非空                     |
| club_id       | INT           | 俱乐部ID       | 可空，外键引用 clubs.club_id                     |
| author_type   | ENUM('user','club') | 作者类型 | 默认 'user'                                      |
| content_text  | TEXT          | 文本内容       | 可空                                              |
| image_url     | VARCHAR(255)  | 图片URL        | 可空                                              |
| created_at    | TIMESTAMP     | 创建时间       | 默认 CURRENT_TIMESTAMP                            |

---

### 表15. 动态评论表 (post_comments)

| 字段名      | 数据类型      | 用途         | 备注                                              |
| ----------- | ------------- | ------------ | ------------------------------------------------- |
| comment_id  | INT           | 评论ID       | 主键，自增，非空                                  |
| post_id     | INT           | 动态ID       | 外键引用 community_posts.post_id，非空           |
| user_id     | INT           | 评论用户ID   | 外键引用 users.user_id，非空                     |
| content     | VARCHAR(255)  | 评论内容     | 非空                                              |
| created_at  | TIMESTAMP     | 评论时间     | 默认 CURRENT_TIMESTAMP                            |

> 若实际脚本中字段略有不同，可在整理 Word 时按实际 SQL 做微调。

---

### 表16. 动态点赞表 (post_likes)

| 字段名   | 数据类型  | 用途       | 备注                                              |
| -------- | --------- | ---------- | ------------------------------------------------- |
| post_id  | INT       | 动态ID     | 主键的一部分，外键引用 community_posts.post_id   |
| user_id  | INT       | 用户ID     | 主键的一部分，外键引用 users.user_id             |
| created_at | TIMESTAMP | 点赞时间 | 默认 CURRENT_TIMESTAMP                            |

---

### 表17. 交易商品表 (trade_items)

| 字段名        | 数据类型      | 用途           | 备注                                              |
| ------------- | ------------- | -------------- | ------------------------------------------------- |
| item_id       | INT           | 商品ID         | 主键，自增，非空                                  |
| is_official   | TINYINT       | 是否官方商品   | 默认0，0=二手，1=官方                             |
| title         | VARCHAR(200)  | 商品标题       | 非空                                              |
| description   | TEXT          | 商品描述       | 可空                                              |
| price         | DECIMAL(10,2) | 商品价格       | 默认0.00                                          |
| image_url     | VARCHAR(255)  | 商品图片URL    | 可空                                              |
| external_url  | VARCHAR(255)  | 外部链接       | 可空，如咸鱼/淘宝/App内链接                      |
| seller_user_id| INT           | 卖家用户ID     | 可空，外键引用 users.user_id                      |
| category      | VARCHAR(50)   | 商品分类       | 可空，例如“整车”“配件”“骑行服”等               |
| is_published  | TINYINT       | 是否上架       | 默认1                                             |
| created_at    | TIMESTAMP     | 创建时间       | 默认 CURRENT_TIMESTAMP                            |

---

### 表18. 成就徽章表 (achievement_badges)

| 字段名           | 数据类型                                         | 用途           | 备注                                   |
| ---------------- | ------------------------------------------------ | -------------- | -------------------------------------- |
| badge_id         | INT                                              | 徽章ID         | 主键，自增，非空                       |
| code             | VARCHAR(50)                                     | 徽章编码       | 唯一，用于逻辑判断                     |
| name             | VARCHAR(100)                                    | 徽章名称       | 非空                                   |
| description      | VARCHAR(255)                                    | 徽章描述       | 可空                                   |
| icon_url         | VARCHAR(255)                                    | 图标URL        | 非空                                   |
| rule_type        | ENUM('first_ride','streak_days','total_rides','single_distance','night_rides','monthly_rides') | 规则类型 | 非空 |
| target_count     | INT                                              | 目标次数       | 默认0                                  |
| target_distance_km | DECIMAL(8,2)                                  | 目标距离       | 默认0.00                               |
| target_days      | INT                                              | 目标天数       | 默认0                                  |
| time_window_days | INT                                              | 时间窗口天数   | 默认0                                  |
| active           | TINYINT                                          | 是否启用       | 默认1                                  |
| created_at       | TIMESTAMP                                        | 创建时间       | 默认 CURRENT_TIMESTAMP                 |

---

### 表19. 骑行偏好分类表 (ride_preference_categories)

| 字段名        | 数据类型      | 用途           | 备注                                   |
| ------------- | ------------- | -------------- | -------------------------------------- |
| category_id   | INT           | 分类ID         | 主键，自增，非空                       |
| category_name | VARCHAR(50)   | 分类名称       | 唯一，例如“骑行类型”“骑行装备”等     |
| display_order | INT           | 排序值         | 默认0                                  |
| created_at    | TIMESTAMP     | 创建时间       | 默认 CURRENT_TIMESTAMP                 |
| updated_at    | TIMESTAMP     | 更新时间       | 默认 CURRENT_TIMESTAMP ON UPDATE       |

---

### 表20. 骑行偏好选项表 (ride_preference_options)

| 字段名      | 数据类型      | 用途           | 备注                                              |
| ----------- | ------------- | -------------- | ------------------------------------------------- |
| option_id   | INT           | 选项ID         | 主键，自增，非空                                  |
| category_id | INT           | 分类ID         | 外键引用 ride_preference_categories.category_id   |
| option_name | VARCHAR(50)   | 选项名称       | 非空，例如“公路骑行”“山地骑行”等                |
| display_order | INT         | 排序值         | 默认0                                             |
| created_at  | TIMESTAMP     | 创建时间       | 默认 CURRENT_TIMESTAMP                            |
| updated_at  | TIMESTAMP     | 更新时间       | 默认 CURRENT_TIMESTAMP ON UPDATE                  |

---

### 表21. 用户骑行偏好表 (user_ride_preferences)

| 字段名           | 数据类型  | 用途           | 备注                                                    |
| ---------------- | --------- | -------------- | ------------------------------------------------------- |
| user_preference_id | INT      | 记录ID         | 主键，自增，非空                                       |
| user_id          | INT       | 用户ID         | 外键引用 users.user_id，非空                          |
| option_id        | INT       | 偏好选项ID     | 外键引用 ride_preference_options.option_id，非空       |
| created_at       | TIMESTAMP | 创建时间       | 默认 CURRENT_TIMESTAMP                                  |

---

### 表22. 用户-活动关系表 (user_events)

| 字段名        | 数据类型                                                              | 用途           | 备注                                              |
| ------------- | --------------------------------------------------------------------- | -------------- | ------------------------------------------------- |
| user_event_id | INT                                                                   | 关系ID         | 主键，自增，非空                                  |
| user_id       | INT                                                                   | 用户ID         | 外键引用 users.user_id，非空                     |
| event_id      | INT                                                                   | 事件ID         | 外键引用 events.event_id，非空                   |
| relation      | ENUM('registered','favorite')                                         | 关联类型       | 默认 'registered'，报名/收藏                      |
| status        | ENUM('upcoming','in_progress','completed','cancelled')               | 状态           | 默认 'upcoming'                                   |
| registered_at | TIMESTAMP                                                             | 创建时间       | 默认 CURRENT_TIMESTAMP                            |
| notes         | VARCHAR(255)                                                          | 备注           | 可空                                              |

---

### 表23. 用户-赛事关系表 (user_races)

| 字段名        | 数据类型                                                              | 用途           | 备注                                              |
| ------------- | --------------------------------------------------------------------- | -------------- | ------------------------------------------------- |
| user_race_id  | INT                                                                   | 关系ID         | 主键，自增，非空                                  |
| user_id       | INT                                                                   | 用户ID         | 外键引用 users.user_id，非空                     |
| race_id       | INT                                                                   | 赛事ID         | 外键引用 races.race_id，非空                      |
| relation      | ENUM('registered','favorite')                                         | 关联类型       | 默认 'registered'                                 |
| status        | ENUM('upcoming','in_progress','completed','cancelled')               | 状态           | 默认 'upcoming'                                   |
| registered_at | TIMESTAMP                                                             | 创建时间       | 默认 CURRENT_TIMESTAMP                            |
| notes         | VARCHAR(255)                                                          | 备注           | 可空                                              |

---

### 表24. 用户-活动统计表 (user_event_stats)

| 字段名        | 数据类型      | 用途             | 备注                                              |
| ------------- | ------------- | ---------------- | ------------------------------------------------- |
| user_id       | INT           | 用户ID           | 主键的一部分，外键引用 users.user_id             |
| event_id      | INT           | 事件ID           | 主键的一部分，外键引用 events.event_id           |
| distance_km   | DECIMAL(8,2)  | 总里程(公里)     | 默认0.00                                          |
| duration_seconds | INT        | 总时长(秒)       | 默认0                                             |
| avg_speed_kmh | DECIMAL(8,2)  | 平均速度         | 可空                                              |
| calories      | INT           | 消耗热量         | 可空                                              |
| note          | VARCHAR(255)  | 备注             | 可空                                              |
| created_at    | TIMESTAMP     | 创建时间         | 默认 CURRENT_TIMESTAMP                            |

---

### 表25. 用户-赛事统计表 (user_race_stats)

| 字段名        | 数据类型      | 用途             | 备注                                              |
| ------------- | ------------- | ---------------- | ------------------------------------------------- |
| user_id       | INT           | 用户ID           | 主键的一部分，外键引用 users.user_id             |
| race_id       | INT           | 赛事ID           | 主键的一部分，外键引用 races.race_id             |
| distance_km   | DECIMAL(8,2)  | 总里程(公里)     | 默认0.00                                          |
| duration_seconds | INT        | 总时长(秒)       | 默认0                                             |
| avg_speed_kmh | DECIMAL(8,2)  | 平均速度         | 可空                                              |
| calories      | INT           | 消耗热量         | 可空                                              |
| note          | VARCHAR(255)  | 备注             | 可空                                              |
| created_at    | TIMESTAMP     | 创建时间         | 默认 CURRENT_TIMESTAMP                            |

---

## 5. 接口设计

本节采用你给出的接口分组格式，例如：

5.1 用户服务接口  
\login 用户登录(Body LoginDTO)  
  \register 用户注册(Body RegisterDTO)

下面按业务模块分组列出主要接口，仅作为接口设计示例，具体 DTO 字段可在详细设计中展开。

### 5.1 用户服务接口

\login 用户登录(Body LoginDTO)  
\register 用户注册(Body RegisterDTO)  
\getUserInfo 获取用户信息(Param int user_id)  
\updateProfile 更新用户资料(Body UserProfileDTO)  
\updatePassword 修改密码(Body PasswordDTO)  
\logout 用户退出登录(Header Authorization)  

### 5.2 俱乐部服务接口

\getClubList 获取俱乐部列表(Param ClubQueryDTO)  
\getClubDetail 获取俱乐部详情(Param int club_id)  
\joinClub 加入俱乐部(Body JoinClubDTO)  
\leaveClub 退出俱乐部(Body LeaveClubDTO)  
\setMainClub 设置主俱乐部(Body SetMainClubDTO)  

### 5.3 活动与赛事服务接口

\getActivityList 获取活动列表(Param ActivityQueryDTO)  
\getActivityDetail 获取活动详情(Param int activity_id)  
\getRaceList 获取赛事列表(Param RaceQueryDTO)  
\getRaceDetail 获取赛事详情(Param int race_id)  
\getMyEvents 获取“我的活动/赛事”列表(Param int user_id)  
\registerActivity 报名活动(Body ActivityRegisterDTO)  
\registerRace 报名赛事(Body RaceRegisterDTO)  
\favoriteEvent 收藏/取消收藏活动或赛事(Body EventFavoriteDTO)  

### 5.4 报名卡服务接口

\getRegistrationCard 获取当前用户报名卡(Param int user_id)  
\saveRegistrationCard 新增或更新报名卡(Body RegistrationCardDTO)  

### 5.5 路书与骑行记录服务接口

\getRouteList 获取路书列表(Param RouteQueryDTO)  
\getRouteDetail 获取路书详情(Param int route_id)  
\favoriteRoute 收藏/取消收藏路书(Body RouteFavoriteDTO)  
\getRideRecords 获取用户骑行记录列表(Param RideRecordQueryDTO)  
\getRideRecordDetail 获取骑行记录详情(Param long record_id)  
\createRideRecord 上传骑行记录(Body RideRecordCreateDTO)  

### 5.6 社区与发现服务接口

\getArticles 获取发现文章列表(Param ArticleQueryDTO)  
\getArticleDetail 获取文章详情(Param int article_id)  
\getCommunityPosts 获取社区动态列表(Param CommunityPostQueryDTO)  
\getCommunityPostDetail 获取动态详情(Param int post_id)  
\createCommunityPost 发布社区动态(Body CommunityPostCreateDTO)  
\commentPost 评论动态(Body CommentCreateDTO)  
\likePost 点赞/取消点赞动态(Body PostLikeDTO)  
\getTradeItems 获取交易商品列表(Param TradeItemQueryDTO)  
\getTradeItemDetail 获取商品详情(Param int item_id)  

### 5.7 成就与偏好服务接口

\getAchievementBadges 获取全部成就徽章定义()  
\getUserStats 获取用户骑行/活动统计(Param int user_id)  
\getUserPreferences 获取用户骑行偏好(Param int user_id)  
\updateUserPreferences 更新用户骑行偏好(Body UserPreferenceDTO)  

---

以上内容已经按你提供的两种格式重写：  
- 数据库部分使用“表X. 表名 (table_name) + 字段名/数据类型/用途/备注”格式；  
- 接口部分使用“5.X 模块接口 + \方法名 说明(参数说明)”格式。你可以直接复制到 Word 文档中，再根据需要补充 DTO 结构和更细的业务说明。 


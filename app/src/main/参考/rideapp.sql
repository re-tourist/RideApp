/*
 Navicat Premium Dump SQL

 Source Server         : 骑行
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44-0ubuntu0.22.04.1)
 Source Host           : 101.37.79.220:3306
 Source Schema         : rideapp

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44-0ubuntu0.22.04.1)
 File Encoding         : 65001

 Date: 18/12/2025 19:53:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for achievement_badges
-- ----------------------------
DROP TABLE IF EXISTS `achievement_badges`;
CREATE TABLE `achievement_badges`  (
  `badge_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rule_type` enum('first_ride','streak_days','total_rides','single_distance','night_rides','monthly_rides') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_count` int NULL DEFAULT 0,
  `target_distance_km` decimal(8, 2) NULL DEFAULT 0.00,
  `target_days` int NULL DEFAULT 0,
  `time_window_days` int NULL DEFAULT 0,
  `active` tinyint NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`badge_id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_badges_rule_type`(`rule_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '成就徽章定义' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of achievement_badges
-- ----------------------------
INSERT INTO `achievement_badges` VALUES (1, 'first_ride', '完成第一次骑行', '完成任意一次骑行记录', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'first_ride', 1, 0.00, 0, 0, 1, '2025-12-07 14:09:55');
INSERT INTO `achievement_badges` VALUES (2, 'streak_7', '连续骑行7天', '连续7天有骑行记录', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'streak_days', 0, 0.00, 7, 0, 1, '2025-12-07 14:09:55');
INSERT INTO `achievement_badges` VALUES (3, 'total_100', '累计骑行100次', '累计完成100次骑行', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'total_rides', 100, 0.00, 0, 0, 1, '2025-12-07 14:09:55');
INSERT INTO `achievement_badges` VALUES (4, 'single_42', '单次骑行超42公里', '任意单次骑行≥42km', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'single_distance', 0, 42.00, 0, 0, 1, '2025-12-07 14:09:55');
INSERT INTO `achievement_badges` VALUES (5, 'night_3', '夜骑3次', '22:00后完成3次骑行', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'night_rides', 3, 0.00, 0, 0, 1, '2025-12-07 14:09:55');
INSERT INTO `achievement_badges` VALUES (6, 'monthly_20', '月度20次', '一个月内完成20次骑行', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'monthly_rides', 20, 0.00, 0, 30, 1, '2025-12-07 14:09:55');

-- ----------------------------
-- Table structure for activities
-- ----------------------------
DROP TABLE IF EXISTS `activities`;
CREATE TABLE `activities`  (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_date` datetime NOT NULL,
  `location` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `event_type` enum('骑行','越野跑','徒步','其他') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '骑行',
  `is_open` tinyint(1) NULL DEFAULT 1,
  `cover_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`activity_id`) USING BTREE,
  INDEX `idx_activities_date`(`event_date` ASC) USING BTREE,
  INDEX `idx_activities_type`(`event_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activities
-- ----------------------------
INSERT INTO `activities` VALUES (3, '越野跑周末挑战赛', '2025-12-01 08:30:00', '上海市郊区', '越野跑', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/1e30d935247661a86ac4c5ab24dd26c.png', '周末越野跑挑战', '2025-12-04 23:00:54', '2025-12-09 23:42:14');
INSERT INTO `activities` VALUES (4, '111', '2025-11-11 11:11:00', '111', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/40343505983679401e1fbe48a30c3cb.png', '', '2025-12-09 11:10:54', '2025-12-09 23:42:24');
INSERT INTO `activities` VALUES (5, '111', '2025-11-11 11:11:00', '111', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/4c7d9c89b9a7ee6253037c5b1206cb5.png', '', '2025-12-09 11:11:21', '2025-12-09 23:44:37');

-- ----------------------------
-- Table structure for activity_tags
-- ----------------------------
DROP TABLE IF EXISTS `activity_tags`;
CREATE TABLE `activity_tags`  (
  `activity_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`activity_id`, `tag_name`) USING BTREE,
  CONSTRAINT `fk_activity_tags_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_tags
-- ----------------------------
INSERT INTO `activity_tags` VALUES (3, '周末活动');
INSERT INTO `activity_tags` VALUES (3, '娱乐赛');
INSERT INTO `activity_tags` VALUES (3, '挑战');
INSERT INTO `activity_tags` VALUES (3, '越野跑');
INSERT INTO `activity_tags` VALUES (4, '娱乐赛');
INSERT INTO `activity_tags` VALUES (5, '娱乐赛');

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `article_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_id` int NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `publish_date` date NULL DEFAULT NULL,
  `views` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`article_id`) USING BTREE,
  INDEX `author_id`(`author_id` ASC) USING BTREE,
  CONSTRAINT `articles_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '发现页文章' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of articles
-- ----------------------------
INSERT INTO `articles` VALUES (1, '骑车时别听歌，除非你...', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/06a5fee73fbac9e2b32da524f8fcbe4.png', '2025-11-20', 2444, '2025-12-04 23:01:07');
INSERT INTO `articles` VALUES (2, '告别耳内闷罐，这款耳机成了我的通勤与运动全能搭子', 2, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/13f1017e33c76449f8af3f71cea0002.png', '2025-11-20', 1876, '2025-12-04 23:01:07');

-- ----------------------------
-- Table structure for club_badges
-- ----------------------------
DROP TABLE IF EXISTS `club_badges`;
CREATE TABLE `club_badges`  (
  `club_id` int NOT NULL,
  `badge_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`club_id`, `badge_name`) USING BTREE,
  CONSTRAINT `fk_club_badges_club` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_badges
-- ----------------------------
INSERT INTO `club_badges` VALUES (1, '活跃', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg');
INSERT INTO `club_badges` VALUES (1, '荣誉', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg');
INSERT INTO `club_badges` VALUES (2, '荣誉', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg');
INSERT INTO `club_badges` VALUES (3, '活跃', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg');

-- ----------------------------
-- Table structure for club_members
-- ----------------------------
DROP TABLE IF EXISTS `club_members`;
CREATE TABLE `club_members`  (
  `club_id` int NOT NULL,
  `user_id` int NOT NULL,
  `role` enum('member','captain') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'member',
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`club_id`, `user_id`) USING BTREE,
  INDEX `idx_club_members_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_club_members_club` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_club_members_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club_members
-- ----------------------------
INSERT INTO `club_members` VALUES (1, 1, 'captain', '2025-11-01 09:00:00');
INSERT INTO `club_members` VALUES (1, 2, 'member', '2025-11-05 10:00:00');
INSERT INTO `club_members` VALUES (1, 3, 'member', '2025-11-06 10:00:00');
INSERT INTO `club_members` VALUES (2, 2, 'captain', '2025-11-03 09:00:00');
INSERT INTO `club_members` VALUES (3, 3, 'captain', '2025-11-04 09:00:00');

-- ----------------------------
-- Table structure for clubs
-- ----------------------------
DROP TABLE IF EXISTS `clubs`;
CREATE TABLE `clubs`  (
  `club_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `members_count` int NULL DEFAULT 0,
  `heat` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`club_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `idx_clubs_city`(`city` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '俱乐部' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clubs
-- ----------------------------
INSERT INTO `clubs` VALUES (1, '北京狂魔车队', '北京市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/club/5c852f57d7e9b60a838432c7f0526c8b.jpg', 1943, 42987, '2025-12-04 23:00:55', '2025-12-09 23:48:15');
INSERT INTO `clubs` VALUES (2, 'CAPU行者', '北京市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/club/3e63aa691b77b852a9c17e336a64de4d.jpg', 1258, 24658, '2025-12-04 23:00:55', '2025-12-09 23:49:36');
INSERT INTO `clubs` VALUES (3, '成都骑行吧', '成都市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/club/2c9e127243eeae82204dae2e7cc96d52.jpg', 1880, 96232, '2025-12-04 23:00:55', '2025-12-09 23:48:55');

-- ----------------------------
-- Table structure for community_posts
-- ----------------------------
DROP TABLE IF EXISTS `community_posts`;
CREATE TABLE `community_posts`  (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `author_user_id` int NOT NULL,
  `club_id` int NULL DEFAULT NULL,
  `author_type` enum('user','club') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user',
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `fk_posts_author`(`author_user_id` ASC) USING BTREE,
  INDEX `fk_posts_club`(`club_id` ASC) USING BTREE,
  INDEX `idx_posts_author_type`(`author_type` ASC) USING BTREE,
  CONSTRAINT `fk_posts_author` FOREIGN KEY (`author_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_posts_club` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_posts
-- ----------------------------
INSERT INTO `community_posts` VALUES (1, 1, NULL, 'user', '滨江夜骑，微风很舒服', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/8032121a67073f79ed86cc6b5bf1e09.png', '2025-12-05 08:10:00');
INSERT INTO `community_posts` VALUES (2, 2, NULL, 'user', '西郊爬坡训练，平均功率提升了', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/94acc13357724afb632e0040df2587a.png', '2025-12-05 08:20:00');
INSERT INTO `community_posts` VALUES (3, 3, NULL, 'user', '城市早骑，通勤顺路锻炼', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/city-morning.jpg', '2025-12-05 07:30:00');
INSERT INTO `community_posts` VALUES (4, 1, 1, 'club', '北京狂魔车队周末拉练，路线很燃', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/d0d454cdf63ec9fc40bba29a29de004.png', '2025-12-04 09:00:00');
INSERT INTO `community_posts` VALUES (5, 2, 2, 'club', 'CAPU行者夜骑分享，灯光很美', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/8032121a67073f79ed86cc6b5bf1e09.png', '2025-12-04 20:30:00');
INSERT INTO `community_posts` VALUES (6, 3, 3, 'club', '成都骑行吧晨练集合，欢迎一起', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/d4efdd6f9fcfbe8789bb53a6a7250f3.png', '2025-12-03 06:45:00');

-- ----------------------------
-- Table structure for event_tags
-- ----------------------------
DROP TABLE IF EXISTS `event_tags`;
CREATE TABLE `event_tags`  (
  `event_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`event_id`, `tag_name`) USING BTREE,
  INDEX `idx_event_tags_event_tag`(`event_id` ASC, `tag_name` ASC) USING BTREE,
  CONSTRAINT `event_tags_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event_tags
-- ----------------------------
INSERT INTO `event_tags` VALUES (1, '娱乐赛');
INSERT INTO `event_tags` VALUES (1, '挑战');
INSERT INTO `event_tags` VALUES (1, '骑行');
INSERT INTO `event_tags` VALUES (2, '竞速');
INSERT INTO `event_tags` VALUES (2, '竞速赛');
INSERT INTO `event_tags` VALUES (2, '骑行');
INSERT INTO `event_tags` VALUES (3, '周末活动');
INSERT INTO `event_tags` VALUES (3, '娱乐赛');
INSERT INTO `event_tags` VALUES (3, '挑战');
INSERT INTO `event_tags` VALUES (3, '越野跑');
INSERT INTO `event_tags` VALUES (4, '娱乐赛');
INSERT INTO `event_tags` VALUES (5, '娱乐赛');

-- ----------------------------
-- Table structure for events
-- ----------------------------
DROP TABLE IF EXISTS `events`;
CREATE TABLE `events`  (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_date` datetime NOT NULL,
  `location` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `event_type` enum('骑行','越野跑','徒步','其他') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '骑行',
  `is_open` tinyint(1) NULL DEFAULT 1,
  `cover_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`event_id`) USING BTREE,
  INDEX `idx_events_date`(`event_date` ASC) USING BTREE,
  INDEX `idx_events_type`(`event_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动/赛事' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of events
-- ----------------------------
INSERT INTO `events` VALUES (1, '迎风织金季·GBA青年自行车线上赛', '2025-11-08 09:00:00', '任意地点', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/06a5fee73fbac9e2b32da524f8fcbe4.png', '线上赛挑战', '2025-12-04 23:00:54', '2025-12-09 23:41:37');
INSERT INTO `events` VALUES (2, '2025“环八娄”自行车爬坡联赛（娄城）', '2025-11-29 08:00:00', '浙江省娄城市', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/13f1017e33c76449f8af3f71cea0002.png', '爬坡联赛', '2025-12-04 23:00:54', '2025-12-09 23:41:49');
INSERT INTO `events` VALUES (3, '越野跑周末挑战赛', '2025-12-01 08:30:00', '上海市郊区', '越野跑', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/1e30d935247661a86ac4c5ab24dd26c.png', '周末越野跑挑战', '2025-12-04 23:00:54', '2025-12-09 23:42:14');
INSERT INTO `events` VALUES (4, '111', '2025-11-11 11:11:00', '111', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/40343505983679401e1fbe48a30c3cb.png', '', '2025-12-09 11:10:54', '2025-12-09 23:42:24');
INSERT INTO `events` VALUES (5, '111', '2025-11-11 11:11:00', '111', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/4c7d9c89b9a7ee6253037c5b1206cb5.png', '', '2025-12-09 11:11:21', '2025-12-09 23:44:37');

-- ----------------------------
-- Table structure for post_comments
-- ----------------------------
DROP TABLE IF EXISTS `post_comments`;
CREATE TABLE `post_comments`  (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `fk_comments_post`(`post_id` ASC) USING BTREE,
  INDEX `fk_comments_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comments_post` FOREIGN KEY (`post_id`) REFERENCES `community_posts` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_comments
-- ----------------------------
INSERT INTO `post_comments` VALUES (1, 1, 2, '夜骑不错，注意安全', '2025-12-05 08:16:00');
INSERT INTO `post_comments` VALUES (2, 1, 3, '风很舒服，支持！', '2025-12-05 08:18:00');
INSERT INTO `post_comments` VALUES (3, 2, 1, '爬坡加油，稳步提升', '2025-12-05 08:26:00');
INSERT INTO `post_comments` VALUES (4, 4, 3, '周末拉练报名！', '2025-12-04 09:10:00');
INSERT INTO `post_comments` VALUES (5, 1, 1, '下次一起夜骑！', '2025-12-05 08:22:00');
INSERT INTO `post_comments` VALUES (6, 1, 3, '路线不错，注意补给', '2025-12-05 08:23:00');
INSERT INTO `post_comments` VALUES (7, 2, 2, '功率稳步提高，很棒', '2025-12-05 08:28:00');
INSERT INTO `post_comments` VALUES (8, 2, 3, '下次拉我一个', '2025-12-05 08:46:00');
INSERT INTO `post_comments` VALUES (9, 3, 1, '通勤顺路太香了', '2025-12-05 07:38:00');
INSERT INTO `post_comments` VALUES (10, 3, 2, '城市夜骑路况要看好', '2025-12-05 07:42:00');
INSERT INTO `post_comments` VALUES (11, 4, 1, '周末拉练报名+1', '2025-12-04 09:13:00');
INSERT INTO `post_comments` VALUES (12, 4, 2, '路线和配速发一下', '2025-12-04 09:16:00');
INSERT INTO `post_comments` VALUES (13, 5, 1, '夜骑灯光确实很美', '2025-12-04 21:11:00');
INSERT INTO `post_comments` VALUES (14, 5, 3, '速度控制好更安全', '2025-12-04 21:13:00');
INSERT INTO `post_comments` VALUES (15, 6, 3, '晨练集合几点？', '2025-12-03 06:53:00');
INSERT INTO `post_comments` VALUES (16, 1, 1, '下次一起夜骑！', '2025-12-05 08:22:00');
INSERT INTO `post_comments` VALUES (17, 1, 3, '路线不错，注意补给', '2025-12-05 08:23:00');
INSERT INTO `post_comments` VALUES (18, 2, 2, '功率稳步提高，很棒', '2025-12-05 08:28:00');
INSERT INTO `post_comments` VALUES (19, 2, 3, '下次拉我一个', '2025-12-05 08:46:00');
INSERT INTO `post_comments` VALUES (20, 3, 1, '通勤顺路太香了', '2025-12-05 07:38:00');
INSERT INTO `post_comments` VALUES (21, 3, 2, '城市夜骑路况要看好', '2025-12-05 07:42:00');
INSERT INTO `post_comments` VALUES (22, 4, 1, '周末拉练报名+1', '2025-12-04 09:13:00');
INSERT INTO `post_comments` VALUES (23, 4, 2, '路线和配速发一下', '2025-12-04 09:16:00');
INSERT INTO `post_comments` VALUES (24, 5, 1, '夜骑灯光确实很美', '2025-12-04 21:11:00');
INSERT INTO `post_comments` VALUES (25, 5, 3, '速度控制好更安全', '2025-12-04 21:13:00');
INSERT INTO `post_comments` VALUES (26, 6, 3, '晨练集合几点？', '2025-12-03 06:53:00');
INSERT INTO `post_comments` VALUES (27, 2, 1, '111', '2025-12-12 20:34:09');
INSERT INTO `post_comments` VALUES (28, 2, 1, 'test', '2025-12-12 20:40:48');
INSERT INTO `post_comments` VALUES (29, 6, 1, 'good', '2025-12-12 21:17:01');
INSERT INTO `post_comments` VALUES (30, 5, 1, 'gooooood', '2025-12-12 21:17:22');
INSERT INTO `post_comments` VALUES (31, 2, 8, 'good', '2025-12-16 15:06:08');
INSERT INTO `post_comments` VALUES (32, 2, 1, '999', '2025-12-16 19:43:57');
INSERT INTO `post_comments` VALUES (33, 2, 1, '999', '2025-12-16 19:44:08');
INSERT INTO `post_comments` VALUES (34, 5, 1, '6666', '2025-12-16 19:47:04');
INSERT INTO `post_comments` VALUES (35, 2, 11, '999', '2025-12-17 09:03:15');
INSERT INTO `post_comments` VALUES (36, 2, 11, '123456', '2025-12-17 09:03:27');
INSERT INTO `post_comments` VALUES (37, 2, 11, '12545568', '2025-12-17 09:14:28');
INSERT INTO `post_comments` VALUES (38, 2, 11, '1', '2025-12-17 09:14:34');
INSERT INTO `post_comments` VALUES (39, 2, 8, '123456', '2025-12-17 09:17:07');
INSERT INTO `post_comments` VALUES (40, 1, 8, 'fddf', '2025-12-17 09:28:03');

-- ----------------------------
-- Table structure for post_dislikes
-- ----------------------------
DROP TABLE IF EXISTS `post_dislikes`;
CREATE TABLE `post_dislikes`  (
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`, `user_id`) USING BTREE,
  INDEX `fk_dislikes_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_dislikes_post` FOREIGN KEY (`post_id`) REFERENCES `community_posts` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_dislikes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of post_dislikes
-- ----------------------------
INSERT INTO `post_dislikes` VALUES (2, 1, '2025-12-17 12:00:52');

-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
DROP TABLE IF EXISTS `post_likes`;
CREATE TABLE `post_likes`  (
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`, `user_id`) USING BTREE,
  INDEX `fk_likes_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_likes_post` FOREIGN KEY (`post_id`) REFERENCES `community_posts` (`post_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_likes
-- ----------------------------
INSERT INTO `post_likes` VALUES (1, 1, '2025-12-17 12:00:55');
INSERT INTO `post_likes` VALUES (1, 2, '2025-12-05 09:00:00');
INSERT INTO `post_likes` VALUES (1, 3, '2025-12-05 08:19:00');
INSERT INTO `post_likes` VALUES (1, 7, '2025-12-05 08:21:00');
INSERT INTO `post_likes` VALUES (1, 8, '2025-12-17 09:27:58');
INSERT INTO `post_likes` VALUES (2, 1, '2025-12-18 15:58:45');
INSERT INTO `post_likes` VALUES (2, 2, '2025-12-05 08:27:00');
INSERT INTO `post_likes` VALUES (2, 3, '2025-12-05 08:40:00');
INSERT INTO `post_likes` VALUES (2, 8, '2025-12-16 15:05:54');
INSERT INTO `post_likes` VALUES (2, 9, '2025-12-05 08:45:00');
INSERT INTO `post_likes` VALUES (2, 11, '2025-12-17 09:14:15');
INSERT INTO `post_likes` VALUES (3, 1, '2025-12-05 07:35:00');
INSERT INTO `post_likes` VALUES (3, 2, '2025-12-05 07:40:00');
INSERT INTO `post_likes` VALUES (4, 1, '2025-12-12 20:26:07');
INSERT INTO `post_likes` VALUES (4, 2, '2025-12-04 09:12:00');
INSERT INTO `post_likes` VALUES (4, 3, '2025-12-04 09:15:00');
INSERT INTO `post_likes` VALUES (5, 1, '2025-12-04 21:00:00');
INSERT INTO `post_likes` VALUES (5, 2, '2025-12-04 21:10:00');
INSERT INTO `post_likes` VALUES (5, 3, '2025-12-04 21:12:00');
INSERT INTO `post_likes` VALUES (5, 12, '2025-12-16 15:08:37');
INSERT INTO `post_likes` VALUES (6, 1, '2025-12-12 21:16:41');
INSERT INTO `post_likes` VALUES (6, 2, '2025-12-03 07:00:00');
INSERT INTO `post_likes` VALUES (6, 3, '2025-12-03 06:52:00');

-- ----------------------------
-- Table structure for race_tags
-- ----------------------------
DROP TABLE IF EXISTS `race_tags`;
CREATE TABLE `race_tags`  (
  `race_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`race_id`, `tag_name`) USING BTREE,
  CONSTRAINT `fk_race_tags_race` FOREIGN KEY (`race_id`) REFERENCES `races` (`race_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '赛事标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of race_tags
-- ----------------------------
INSERT INTO `race_tags` VALUES (1, '娱乐赛');
INSERT INTO `race_tags` VALUES (1, '挑战');
INSERT INTO `race_tags` VALUES (1, '骑行');
INSERT INTO `race_tags` VALUES (2, '竞速');
INSERT INTO `race_tags` VALUES (2, '竞速赛');
INSERT INTO `race_tags` VALUES (2, '骑行');

-- ----------------------------
-- Table structure for races
-- ----------------------------
DROP TABLE IF EXISTS `races`;
CREATE TABLE `races`  (
  `race_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_date` datetime NOT NULL,
  `location` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `event_type` enum('娱乐赛','竞速赛','骑行','其他') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '娱乐赛',
  `is_open` tinyint(1) NULL DEFAULT 1,
  `cover_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`race_id`) USING BTREE,
  INDEX `idx_races_date`(`event_date` ASC) USING BTREE,
  INDEX `idx_races_type`(`event_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '赛事' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of races
-- ----------------------------
INSERT INTO `races` VALUES (1, '迎风织金季·GBA青年自行车线上赛', '2025-11-08 09:00:00', '任意地点', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/06a5fee73fbac9e2b32da524f8fcbe4.png', '线上赛挑战', '2025-12-04 23:00:54', '2025-12-09 23:41:37');
INSERT INTO `races` VALUES (2, '2025“环八娄”自行车爬坡联赛（娄城）', '2025-11-29 08:00:00', '浙江省娄城市', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/posts/13f1017e33c76449f8af3f71cea0002.png', '爬坡联赛', '2025-12-04 23:00:54', '2025-12-09 23:41:49');

-- ----------------------------
-- Table structure for ride_preference_categories
-- ----------------------------
DROP TABLE IF EXISTS `ride_preference_categories`;
CREATE TABLE `ride_preference_categories`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `category_name`(`category_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ride_preference_categories
-- ----------------------------
INSERT INTO `ride_preference_categories` VALUES (1, '骑行类型', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_categories` VALUES (2, '骑行装备', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_categories` VALUES (3, '骑行路线', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_categories` VALUES (4, '骑行技巧', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_categories` VALUES (5, '骑行社区', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');

-- ----------------------------
-- Table structure for ride_preference_options
-- ----------------------------
DROP TABLE IF EXISTS `ride_preference_options`;
CREATE TABLE `ride_preference_options`  (
  `option_id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `option_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`option_id`) USING BTREE,
  UNIQUE INDEX `unique_category_option`(`category_id` ASC, `option_name` ASC) USING BTREE,
  INDEX `idx_preference_options_category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `ride_preference_options_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `ride_preference_categories` (`category_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ride_preference_options
-- ----------------------------
INSERT INTO `ride_preference_options` VALUES (1, 1, '公路骑行', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (2, 1, '山地骑行', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (3, 1, '通勤骑行', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (4, 1, '休闲骑行', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (5, 1, '长途骑行', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (6, 1, '竞赛骑行', 6, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (7, 2, '公路车', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (8, 2, '山地车', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (9, 2, '城市车', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (10, 2, '头盔', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (11, 2, '骑行服', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (12, 2, '骑行鞋', 6, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (13, 2, '手套', 7, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (14, 2, '护具', 8, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (15, 3, '城市道路', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (16, 3, '山间小路', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (17, 3, '沿海公路', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (18, 3, '乡村道路', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (19, 3, '专业赛道', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (20, 3, '公园绿道', 6, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (21, 4, '爬坡技巧', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (22, 4, '下坡安全', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (23, 4, '过弯技术', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (24, 4, '长途骑行', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (25, 4, '编队骑行', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (26, 4, '维修保养', 6, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (27, 5, '本地骑友', 1, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (28, 5, '品牌粉丝', 2, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (29, 5, '装备交流', 3, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (30, 5, '路线分享', 4, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (31, 5, '赛事讨论', 5, '2025-12-02 20:14:29', '2025-12-02 20:14:29');
INSERT INTO `ride_preference_options` VALUES (32, 5, '二手交易', 6, '2025-12-02 20:14:29', '2025-12-02 20:14:29');

-- ----------------------------
-- Table structure for rider_profiles
-- ----------------------------
DROP TABLE IF EXISTS `rider_profiles`;
CREATE TABLE `rider_profiles`  (
  `user_id` int NOT NULL,
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `main_club_id` int NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `fk_rider_profiles_club`(`main_club_id` ASC) USING BTREE,
  CONSTRAINT `fk_rider_profiles_club` FOREIGN KEY (`main_club_id`) REFERENCES `clubs` (`club_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_rider_profiles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rider_profiles
-- ----------------------------
INSERT INTO `rider_profiles` VALUES (1, '上海市', '普通', 1, '2025-12-05 20:12:47');
INSERT INTO `rider_profiles` VALUES (2, '北京市', '金牌骑客', 1, '2025-12-05 20:12:47');
INSERT INTO `rider_profiles` VALUES (3, '成都市', '菜鸟骑迹', 3, '2025-12-05 20:12:47');

-- ----------------------------
-- Table structure for route_favorites
-- ----------------------------
DROP TABLE IF EXISTS `route_favorites`;
CREATE TABLE `route_favorites`  (
  `route_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`, `user_id`) USING BTREE,
  INDEX `idx_rf_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `route_favorites_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `route_favorites_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路书收藏' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of route_favorites
-- ----------------------------
INSERT INTO `route_favorites` VALUES (1, 1, '2025-12-04 23:00:55');
INSERT INTO `route_favorites` VALUES (2, 2, '2025-12-04 23:00:55');

-- ----------------------------
-- Table structure for route_tags
-- ----------------------------
DROP TABLE IF EXISTS `route_tags`;
CREATE TABLE `route_tags`  (
  `route_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`route_id`, `tag_name`) USING BTREE,
  INDEX `idx_route_tags_event`(`route_id` ASC, `tag_name` ASC) USING BTREE,
  CONSTRAINT `route_tags_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路书标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of route_tags
-- ----------------------------
INSERT INTO `route_tags` VALUES (1, '休闲');
INSERT INTO `route_tags` VALUES (1, '骑行');
INSERT INTO `route_tags` VALUES (2, '爬坡');
INSERT INTO `route_tags` VALUES (2, '骑行');
INSERT INTO `route_tags` VALUES (3, '夜骑');
INSERT INTO `route_tags` VALUES (3, '骑行');

-- ----------------------------
-- Table structure for routes
-- ----------------------------
DROP TABLE IF EXISTS `routes`;
CREATE TABLE `routes`  (
  `route_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL,
  `elevation_m` int NULL DEFAULT 0,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `difficulty` enum('简单','中等','困难') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '简单',
  `cover_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`) USING BTREE,
  INDEX `idx_routes_location`(`location` ASC) USING BTREE,
  INDEX `idx_routes_difficulty`(`difficulty` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路书' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of routes
-- ----------------------------
INSERT INTO `routes` VALUES (1, '滨江环线', 32.50, 210, '上海市', '简单', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/route/1f208a8e980f612c72ca5ca40373c384.jpg', '2025-12-04 23:00:55', '2025-12-10 08:32:29');
INSERT INTO `routes` VALUES (2, '西郊爬坡挑战', 65.00, 980, '浙江省', '困难', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/route/3905914b2ec3cf0cf7674a267dcc0edb.jpg', '2025-12-04 23:00:55', '2025-12-10 08:32:42');
INSERT INTO `routes` VALUES (3, '城市夜骑', 18.30, 80, '上海市', '中等', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/route/482c97200a2a41bb9e876840b1c82eb3.jpg', '2025-12-04 23:00:55', '2025-12-10 08:32:52');
INSERT INTO `routes` VALUES (4, '测试', 11.11, 100, '南昌市', '简单', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/route/648617fd7b21449a0e2e2d0d3557f5dc.jpg', '2025-12-05 19:42:07', '2025-12-10 08:33:01');

-- ----------------------------
-- Table structure for trade_items
-- ----------------------------
DROP TABLE IF EXISTS `trade_items`;
CREATE TABLE `trade_items`  (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `is_official` tinyint NULL DEFAULT 0,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `price` decimal(10, 2) NULL DEFAULT 0.00,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `external_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `seller_user_id` int NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_published` tinyint NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`) USING BTREE,
  INDEX `fk_trade_seller`(`seller_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_trade_seller` FOREIGN KEY (`seller_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_items
-- ----------------------------
INSERT INTO `trade_items` VALUES (1, 0, '9成新碳纤维公路车架', '尺寸M，超轻，只用了半年，因为换车出售。可小刀。', 4500.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/3ba8f1964fff51db305e5d6966537153.jpg', 'xianyu://item/12345', 1, '整车', 1, '2025-12-05 09:00:00');
INSERT INTO `trade_items` VALUES (2, 0, 'Shimano 105套件（二手）', '飞轮、链条、牙盘全套，正常使用痕迹，功能完好。', 1500.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/0fa7a0d4e8da33d939daf6843a072846.jpg', 'xianyu://item/67890', 2, '配件', 1, '2025-12-05 09:05:00');
INSERT INTO `trade_items` VALUES (3, 0, '冬季骑行抓绒手套', '全新未拆封，L号，防水防风，多买了一副，便宜出。', 89.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/89f63fe94889606f2520e5413f5e405e.jpg', 'taobao://item/11223', 1, '配件', 1, '2025-12-05 09:10:00');
INSERT INTO `trade_items` VALUES (4, 1, 'RideFlow 2024新款速干骑行服套装', '骑行服，透气排汗，夏季必备。分类：骑行服', 399.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/4cec2ce0f03e2e48f31b36abe9fcb725.jpg', 'app://official/product/399', NULL, '骑行服', 1, '2025-12-05 09:20:00');
INSERT INTO `trade_items` VALUES (5, 1, '高性能GPS码表（R700型号）', '精准定位，超长续航，支持心率监测。分类：配件', 1899.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/8c01623046a896f57795599c7a6776a9.jpg', 'app://official/product/r700', NULL, '配件', 1, '2025-12-05 09:25:00');
INSERT INTO `trade_items` VALUES (6, 1, '山地越野头盔（Pro系列）', 'MIPS保护系统，轻量化设计，多色可选。分类：配件', 599.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/trade/40fb2b02b2334ddd4d34c3f1ce3a9d1a.jpg', 'app://official/product/prohelmet', NULL, '配件', 1, '2025-12-05 09:30:00');

-- ----------------------------
-- Table structure for user_achievement_progress
-- ----------------------------
DROP TABLE IF EXISTS `user_achievement_progress`;
CREATE TABLE `user_achievement_progress`  (
  `user_id` int NOT NULL,
  `badge_id` int NOT NULL,
  `current_count` int NULL DEFAULT 0,
  `current_distance_km` decimal(10, 2) NULL DEFAULT 0.00,
  `current_streak_days` int NULL DEFAULT 0,
  `progress_percent` decimal(5, 2) NULL DEFAULT 0.00,
  `is_unlocked` tinyint NULL DEFAULT 0,
  `unlocked_at` timestamp NULL DEFAULT NULL,
  `last_updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `badge_id`) USING BTREE,
  INDEX `idx_uap_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_uap_badge`(`badge_id` ASC) USING BTREE,
  CONSTRAINT `fk_uap_badge` FOREIGN KEY (`badge_id`) REFERENCES `achievement_badges` (`badge_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_uap_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户成就徽章进度' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_achievement_progress
-- ----------------------------
INSERT INTO `user_achievement_progress` VALUES (1, 1, 1, 0.00, 0, 100.00, 1, '2025-12-01 08:00:00', '2025-12-07 14:09:55');
INSERT INTO `user_achievement_progress` VALUES (1, 2, 0, 0.00, 3, 42.86, 0, NULL, '2025-12-07 14:09:55');
INSERT INTO `user_achievement_progress` VALUES (1, 3, 12, 0.00, 0, 12.00, 0, NULL, '2025-12-07 14:09:55');
INSERT INTO `user_achievement_progress` VALUES (1, 4, 1, 45.00, 0, 100.00, 1, '2025-12-10 10:00:00', '2025-12-07 14:09:55');
INSERT INTO `user_achievement_progress` VALUES (1, 5, 1, 0.00, 0, 33.33, 0, NULL, '2025-12-07 14:09:55');
INSERT INTO `user_achievement_progress` VALUES (1, 6, 5, 0.00, 0, 25.00, 0, NULL, '2025-12-07 14:09:57');

-- ----------------------------
-- Table structure for user_achievement_record_links
-- ----------------------------
DROP TABLE IF EXISTS `user_achievement_record_links`;
CREATE TABLE `user_achievement_record_links`  (
  `user_id` int NOT NULL,
  `badge_id` int NOT NULL,
  `record_id` int NOT NULL,
  `contribution_value` decimal(10, 2) NULL DEFAULT 0.00,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `badge_id`, `record_id`) USING BTREE,
  INDEX `idx_uarl_record`(`record_id` ASC) USING BTREE,
  INDEX `fk_uarl_badge`(`badge_id` ASC) USING BTREE,
  CONSTRAINT `fk_uarl_badge` FOREIGN KEY (`badge_id`) REFERENCES `achievement_badges` (`badge_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_uarl_record` FOREIGN KEY (`record_id`) REFERENCES `user_ride_records` (`record_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_uarl_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '成就进度与骑行记录关联（可选）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_achievement_record_links
-- ----------------------------

-- ----------------------------
-- Table structure for user_activities
-- ----------------------------
DROP TABLE IF EXISTS `user_activities`;
CREATE TABLE `user_activities`  (
  `user_activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `activity_id` int NOT NULL,
  `relation` enum('registered','favorite') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'registered',
  `status` enum('upcoming','in_progress','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'upcoming',
  `registered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_activity_id`) USING BTREE,
  UNIQUE INDEX `unique_user_activity_relation`(`user_id` ASC, `activity_id` ASC, `relation` ASC) USING BTREE,
  INDEX `idx_user_activities_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_activities_activity`(`activity_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_activities_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_activities_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-活动关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_activities
-- ----------------------------
INSERT INTO `user_activities` VALUES (9, 1, 4, 'favorite', 'upcoming', '2025-12-12 18:12:46', '????');
INSERT INTO `user_activities` VALUES (13, 1, 5, 'favorite', 'upcoming', '2025-12-17 09:27:02', '????');
INSERT INTO `user_activities` VALUES (14, 1, 3, 'favorite', 'upcoming', '2025-12-17 09:39:05', '????');

-- ----------------------------
-- Table structure for user_activity_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_activity_stats`;
CREATE TABLE `user_activity_stats`  (
  `user_id` int NOT NULL,
  `activity_id` int NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL DEFAULT 0.00,
  `duration_seconds` int NOT NULL DEFAULT 0,
  `avg_speed_kmh` decimal(8, 2) NULL DEFAULT NULL,
  `calories` int NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `activity_id`) USING BTREE,
  INDEX `idx_user_activity_stats_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_activity_stats_activity`(`activity_id` ASC) USING BTREE,
  CONSTRAINT `fk_stats_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`activity_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_stats_user_a` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-活动统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_activity_stats
-- ----------------------------
INSERT INTO `user_activity_stats` VALUES (1, 3, 0.00, 0, NULL, NULL, '活动3：收藏关注', '2025-12-09 10:40:31');

-- ----------------------------
-- Table structure for user_event_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_event_stats`;
CREATE TABLE `user_event_stats`  (
  `user_id` int NOT NULL,
  `event_id` int NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL DEFAULT 0.00,
  `duration_seconds` int NOT NULL DEFAULT 0,
  `avg_speed_kmh` decimal(8, 2) NULL DEFAULT NULL,
  `calories` int NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `event_id`) USING BTREE,
  INDEX `idx_user_event_stats_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_event_stats_event`(`event_id` ASC) USING BTREE,
  CONSTRAINT `fk_stats_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_stats_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_event_stats
-- ----------------------------
INSERT INTO `user_event_stats` VALUES (1, 1, 32.50, 5400, 21.67, 850, '活动1：夜骑完成', '2025-12-09 10:40:31');
INSERT INTO `user_event_stats` VALUES (1, 2, 18.30, 3600, 18.30, 600, '活动2：计划晨骑', '2025-12-09 10:40:31');
INSERT INTO `user_event_stats` VALUES (1, 3, 0.00, 0, NULL, NULL, '活动3：收藏关注', '2025-12-09 10:40:31');

-- ----------------------------
-- Table structure for user_events
-- ----------------------------
DROP TABLE IF EXISTS `user_events`;
CREATE TABLE `user_events`  (
  `user_event_id` int NOT NULL AUTO_INCREMENT COMMENT '用户-活动关系ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `event_id` int NOT NULL COMMENT '活动ID',
  `relation` enum('registered','favorite') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'registered' COMMENT '关联类型：报名/收藏',
  `status` enum('upcoming','in_progress','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'upcoming' COMMENT '状态：即将到来/进行中/已完成/已取消',
  `registered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注（可选）',
  PRIMARY KEY (`user_event_id`) USING BTREE,
  UNIQUE INDEX `unique_user_event_relation`(`user_id` ASC, `event_id` ASC, `relation` ASC) USING BTREE,
  INDEX `idx_user_events_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_events_event`(`event_id` ASC) USING BTREE,
  INDEX `idx_user_events_user_event`(`user_id` ASC, `event_id` ASC, `relation` ASC) USING BTREE,
  CONSTRAINT `fk_user_events_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_events_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与活动关系（我的活动）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_events
-- ----------------------------
INSERT INTO `user_events` VALUES (1, 1, 1, 'registered', 'completed', '2025-12-05 19:12:14', '已参加并完成');
INSERT INTO `user_events` VALUES (2, 1, 2, 'registered', 'upcoming', '2025-12-05 19:12:14', '已报名，待开始');
INSERT INTO `user_events` VALUES (7, 1, 3, 'favorite', 'upcoming', '2025-12-09 11:19:41', '????');
INSERT INTO `user_events` VALUES (8, 1, 5, 'favorite', 'upcoming', '2025-12-09 11:20:02', '????');

-- ----------------------------
-- Table structure for user_follows
-- ----------------------------
DROP TABLE IF EXISTS `user_follows`;
CREATE TABLE `user_follows`  (
  `follower_user_id` int NOT NULL,
  `followed_user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`follower_user_id`, `followed_user_id`) USING BTREE,
  INDEX `fk_user_follows_followed`(`followed_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_follows_followed` FOREIGN KEY (`followed_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_follows_follower` FOREIGN KEY (`follower_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_follows
-- ----------------------------
INSERT INTO `user_follows` VALUES (1, 1, '2025-12-16 19:43:41');
INSERT INTO `user_follows` VALUES (1, 2, '2025-12-16 19:43:36');
INSERT INTO `user_follows` VALUES (1, 3, '2025-12-16 20:27:47');
INSERT INTO `user_follows` VALUES (8, 1, '2025-12-16 17:05:05');
INSERT INTO `user_follows` VALUES (8, 2, '2025-12-17 09:41:43');
INSERT INTO `user_follows` VALUES (8, 3, '2025-12-16 16:07:09');
INSERT INTO `user_follows` VALUES (11, 2, '2025-12-17 09:02:41');

-- ----------------------------
-- Table structure for user_race_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_race_stats`;
CREATE TABLE `user_race_stats`  (
  `user_id` int NOT NULL,
  `race_id` int NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL DEFAULT 0.00,
  `duration_seconds` int NOT NULL DEFAULT 0,
  `avg_speed_kmh` decimal(8, 2) NULL DEFAULT NULL,
  `calories` int NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `race_id`) USING BTREE,
  INDEX `idx_user_race_stats_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_race_stats_race`(`race_id` ASC) USING BTREE,
  CONSTRAINT `fk_stats_race` FOREIGN KEY (`race_id`) REFERENCES `races` (`race_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_stats_user2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-赛事统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_race_stats
-- ----------------------------
INSERT INTO `user_race_stats` VALUES (1, 1, 32.50, 5400, 21.67, 850, '活动1：夜骑完成', '2025-12-09 10:40:31');
INSERT INTO `user_race_stats` VALUES (1, 2, 18.30, 3600, 18.30, 600, '活动2：计划晨骑', '2025-12-09 10:40:31');

-- ----------------------------
-- Table structure for user_races
-- ----------------------------
DROP TABLE IF EXISTS `user_races`;
CREATE TABLE `user_races`  (
  `user_race_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `race_id` int NOT NULL,
  `relation` enum('registered','favorite') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'registered',
  `status` enum('upcoming','in_progress','completed','cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'upcoming',
  `registered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_race_id`) USING BTREE,
  UNIQUE INDEX `unique_user_race_relation`(`user_id` ASC, `race_id` ASC, `relation` ASC) USING BTREE,
  INDEX `idx_user_races_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_races_race`(`race_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_races_race` FOREIGN KEY (`race_id`) REFERENCES `races` (`race_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_races_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户-赛事关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_races
-- ----------------------------
INSERT INTO `user_races` VALUES (1, 1, 1, 'registered', 'completed', '2025-12-05 19:12:14', '已参加并完成');
INSERT INTO `user_races` VALUES (2, 1, 2, 'registered', 'upcoming', '2025-12-05 19:12:14', '已报名，待开始');

-- ----------------------------
-- Table structure for user_ride_preferences
-- ----------------------------
DROP TABLE IF EXISTS `user_ride_preferences`;
CREATE TABLE `user_ride_preferences`  (
  `user_preference_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `option_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_preference_id`) USING BTREE,
  UNIQUE INDEX `unique_user_option`(`user_id` ASC, `option_id` ASC) USING BTREE,
  INDEX `idx_user_preferences_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_preferences_option_id`(`option_id` ASC) USING BTREE,
  CONSTRAINT `user_ride_preferences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_ride_preferences_ibfk_2` FOREIGN KEY (`option_id`) REFERENCES `ride_preference_options` (`option_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_ride_preferences
-- ----------------------------
INSERT INTO `user_ride_preferences` VALUES (44, 1, 23, '2025-12-06 17:07:10');
INSERT INTO `user_ride_preferences` VALUES (45, 1, 22, '2025-12-06 17:07:10');
INSERT INTO `user_ride_preferences` VALUES (46, 1, 28, '2025-12-06 17:07:10');
INSERT INTO `user_ride_preferences` VALUES (47, 1, 9, '2025-12-06 17:07:11');
INSERT INTO `user_ride_preferences` VALUES (48, 1, 1, '2025-12-06 17:07:11');
INSERT INTO `user_ride_preferences` VALUES (49, 1, 2, '2025-12-06 17:07:11');
INSERT INTO `user_ride_preferences` VALUES (50, 1, 3, '2025-12-06 17:07:11');
INSERT INTO `user_ride_preferences` VALUES (51, 1, 16, '2025-12-06 17:07:11');
INSERT INTO `user_ride_preferences` VALUES (100, 8, 8, '2025-12-17 09:04:29');
INSERT INTO `user_ride_preferences` VALUES (102, 8, 9, '2025-12-17 09:04:29');
INSERT INTO `user_ride_preferences` VALUES (103, 8, 12, '2025-12-17 09:04:29');
INSERT INTO `user_ride_preferences` VALUES (105, 8, 11, '2025-12-17 09:04:29');
INSERT INTO `user_ride_preferences` VALUES (106, 8, 14, '2025-12-17 09:04:29');
INSERT INTO `user_ride_preferences` VALUES (107, 8, 5, '2025-12-17 09:04:30');
INSERT INTO `user_ride_preferences` VALUES (108, 8, 16, '2025-12-17 09:04:30');
INSERT INTO `user_ride_preferences` VALUES (109, 11, 23, '2025-12-17 09:04:31');
INSERT INTO `user_ride_preferences` VALUES (110, 11, 25, '2025-12-17 09:04:31');
INSERT INTO `user_ride_preferences` VALUES (111, 11, 31, '2025-12-17 09:04:31');
INSERT INTO `user_ride_preferences` VALUES (112, 11, 8, '2025-12-17 09:04:32');
INSERT INTO `user_ride_preferences` VALUES (113, 11, 2, '2025-12-17 09:04:32');
INSERT INTO `user_ride_preferences` VALUES (114, 11, 3, '2025-12-17 09:04:32');
INSERT INTO `user_ride_preferences` VALUES (115, 11, 16, '2025-12-17 09:04:33');

-- ----------------------------
-- Table structure for user_ride_records
-- ----------------------------
DROP TABLE IF EXISTS `user_ride_records`;
CREATE TABLE `user_ride_records`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `route_id` int NULL DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `duration_seconds` int NOT NULL,
  `duration_sec` int NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL,
  `avg_speed_kmh` decimal(5, 2) NULL DEFAULT 0.00,
  `calories` int NULL DEFAULT 0,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `climb` int NULL DEFAULT NULL,
  `max_speed_kmh` decimal(5, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `route_id`(`route_id` ASC) USING BTREE,
  INDEX `idx_ride_records_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_ride_records_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `user_ride_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_ride_records_ibfk_2` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 818851204 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户骑行记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_ride_records
-- ----------------------------
INSERT INTO `user_ride_records` VALUES (1, 1, 1, '2025-11-21 19:00:00', 0, 5400, 32.50, 21.67, 850, NULL, '夜骑很爽', '2025-12-04 23:00:55', NULL, NULL);
INSERT INTO `user_ride_records` VALUES (2, 2, 2, '2025-11-22 08:00:00', 0, 10800, 65.00, 21.66, 1600, NULL, '爬坡辛苦', '2025-12-04 23:00:55', NULL, NULL);
INSERT INTO `user_ride_records` VALUES (3, 3, 3, '2025-11-23 20:00:00', 0, 3600, 18.30, 18.30, 600, NULL, '城市风光不错', '2025-12-04 23:00:55', NULL, NULL);
INSERT INTO `user_ride_records` VALUES (96398612, 11, NULL, '2025-12-10 00:52:37', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 08:52:39', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (97431832, 11, NULL, '2025-12-10 01:09:50', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 09:09:53', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (98194425, 1, NULL, '2025-12-10 09:22:33', 0, 5, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 09:22:34', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101425626, 11, NULL, '2025-12-10 02:16:24', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:16:26', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101443771, 11, NULL, '2025-12-10 02:16:42', 0, 8, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:16:44', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101503576, 1, NULL, '2025-12-10 02:17:42', 0, 13, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:17:45', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101774447, 11, NULL, '2025-12-10 10:22:13', 0, 49, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:22:13', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101779699, 11, NULL, '2025-12-10 10:22:18', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:22:19', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (101901621, 11, NULL, '2025-12-10 10:24:20', 0, 71, 0.08, 3.60, 4, NULL, NULL, '2025-12-10 10:24:20', 0, 5.25);
INSERT INTO `user_ride_records` VALUES (102513116, 11, NULL, '2025-12-10 10:34:31', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:34:33', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (102862332, 11, NULL, '2025-12-10 02:40:20', 0, 8, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:40:24', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (102944728, 11, NULL, '2025-12-10 02:41:43', 0, 17, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:41:46', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (103418061, 11, NULL, '2025-12-10 02:49:36', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:49:40', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (103423646, 11, NULL, '2025-12-10 02:49:42', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:49:48', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (103892224, 11, NULL, '2025-12-10 02:57:30', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:57:34', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (103897976, 11, NULL, '2025-12-10 02:57:36', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 10:57:42', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (104400404, 11, NULL, '2025-12-10 11:05:59', 0, 16, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:06:00', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (104413160, 11, NULL, '2025-12-10 11:06:11', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:06:13', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (104466393, 11, NULL, '2025-12-10 11:07:05', 0, 37, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:07:07', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (104570600, 11, NULL, '2025-12-10 11:08:49', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:08:50', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (107079789, 1, NULL, '2025-12-10 11:50:38', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:50:39', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (107090646, 1, NULL, '2025-12-10 11:50:49', 0, 6, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:50:50', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (107112978, 1, NULL, '2025-12-10 11:51:11', 0, 19, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 11:51:13', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (107180767, 1, NULL, '2025-12-10 11:52:19', 0, 55, 0.06, 3.90, 2, NULL, NULL, '2025-12-10 11:52:20', 0, 16.05);
INSERT INTO `user_ride_records` VALUES (108412885, 1, NULL, '2025-12-10 12:12:51', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 12:12:53', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (116037532, 11, NULL, '2025-12-10 06:19:56', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-10 14:19:56', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (563172296, 11, NULL, '2025-12-15 10:32:10', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 18:32:13', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (565878035, 1, NULL, '2025-12-15 19:17:16', 0, 18, 0.02, 4.10, 1, NULL, NULL, '2025-12-15 19:17:17', 0, 7.12);
INSERT INTO `user_ride_records` VALUES (566008307, 1, NULL, '2025-12-15 19:19:26', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 19:19:27', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (576081334, 1, NULL, '2025-12-15 22:07:19', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 22:07:20', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (577649998, 11, NULL, '2025-12-15 22:33:28', 0, 10, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 22:33:30', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (577852135, 1, NULL, '2025-12-15 22:36:50', 0, 11, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 22:36:52', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (577955975, 1, NULL, '2025-12-15 22:38:34', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 22:38:36', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (578309981, 11, NULL, '2025-12-15 22:44:28', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 22:44:30', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (581556357, 11, NULL, '2025-12-15 23:38:35', 0, 21, 0.03, 4.40, 1, NULL, NULL, '2025-12-15 23:38:35', 0, 5.69);
INSERT INTO `user_ride_records` VALUES (581559488, 11, NULL, '2025-12-15 23:38:38', 0, 37, 0.00, 0.00, 0, NULL, NULL, '2025-12-15 23:38:38', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (637264043, 12, NULL, '2025-12-16 07:07:02', 0, 20, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 15:07:02', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (642581256, 1, NULL, '2025-12-16 08:35:39', 0, 17, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 16:35:39', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (652318713, 11, NULL, '2025-12-16 11:17:57', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 19:17:59', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (653895593, 11, NULL, '2025-12-16 11:44:14', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 19:44:16', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (654786094, 11, NULL, '2025-12-16 11:59:04', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 19:59:07', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658168974, 11, NULL, '2025-12-16 20:55:27', 0, 6, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 20:55:27', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658172197, 11, NULL, '2025-12-16 20:55:30', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 20:55:31', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658178828, 11, NULL, '2025-12-16 20:55:37', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 20:55:37', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658188614, 11, NULL, '2025-12-16 20:55:47', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 20:55:47', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658353237, 11, NULL, '2025-12-16 20:58:31', 0, 25, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 20:58:32', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (658949345, 11, NULL, '2025-12-16 21:08:28', 0, 21, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 21:08:28', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (659260733, 1, NULL, '2025-12-16 21:13:39', 0, 164, 0.18, 3.90, 9, NULL, NULL, '2025-12-16 21:13:39', 0, 13.19);
INSERT INTO `user_ride_records` VALUES (659661863, 1, NULL, '2025-12-16 13:20:20', 0, 58, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 21:20:20', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (659901185, 1, NULL, '2025-12-16 13:24:19', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 21:24:20', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (661147521, 11, NULL, '2025-12-16 21:45:06', 0, 728, 0.00, 0.00, 0, NULL, NULL, '2025-12-16 21:45:06', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (699491936, 11, NULL, '2025-12-17 08:24:10', 0, 42, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:24:10', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (699681149, 11, NULL, '2025-12-17 08:27:19', 0, 180, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:27:20', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (699737931, 11, NULL, '2025-12-17 00:28:16', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:28:20', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (699890514, 11, NULL, '2025-12-17 08:30:49', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:30:49', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700280303, 11, NULL, '2025-12-17 08:37:18', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:37:19', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700286219, 11, NULL, '2025-12-17 08:37:24', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:37:25', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700336146, 11, NULL, '2025-12-17 08:38:14', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:38:15', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700357627, 11, NULL, '2025-12-17 08:38:36', 0, 5, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:38:36', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700609712, 11, NULL, '2025-12-17 08:42:48', 0, 20, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:42:48', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (700807518, 11, NULL, '2025-12-17 08:46:06', 0, 2, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:46:06', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (701637376, 1, NULL, '2025-12-17 00:59:56', 0, 22, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 08:59:58', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (701787707, 11, NULL, '2025-12-17 09:02:26', 0, 22, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:02:26', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (702330200, 11, NULL, '2025-12-17 09:11:28', 0, 7, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:11:29', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (702401331, 8, NULL, '2025-12-17 09:12:39', 0, 17, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:12:41', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (702430593, 8, NULL, '2025-12-17 09:13:09', 0, 8, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:13:09', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (702445472, 8, NULL, '2025-12-17 09:13:24', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:13:24', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (702570705, 8, NULL, '2025-12-17 09:15:29', 0, 7, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:15:29', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (703261803, 11, NULL, '2025-12-17 09:27:00', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:27:01', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (703289524, 11, NULL, '2025-12-17 09:27:28', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:27:28', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (703897782, 11, NULL, '2025-12-17 09:37:36', 0, 4, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:37:36', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (703946872, 11, NULL, '2025-12-17 09:38:25', 0, 1, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:38:25', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (704046264, 11, NULL, '2025-12-17 09:40:04', 0, 82, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:40:05', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (704313850, 8, NULL, '2025-12-17 09:44:32', 0, 20, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 09:44:33', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (705460654, 1, NULL, '2025-12-17 02:03:39', 0, 0, 0.00, 0.00, 0, NULL, NULL, '2025-12-17 10:04:11', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (811087845, 1, NULL, '2025-12-18 07:24:06', 0, 3, 0.00, 0.00, 0, NULL, NULL, '2025-12-18 15:24:07', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (811298996, 1, NULL, '2025-12-18 07:27:37', 0, 5, 0.00, 0.00, 0, NULL, NULL, '2025-12-18 15:27:37', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (813220565, 1, NULL, '2025-12-18 07:59:39', 0, 6, 0.00, 0.00, 0, NULL, NULL, '2025-12-18 15:59:39', 0, 0.00);
INSERT INTO `user_ride_records` VALUES (818851203, 1, NULL, '2025-12-18 09:33:10', 0, 15, 0.00, 0.00, 0, NULL, NULL, '2025-12-18 17:33:57', 0, 0.00);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增主键）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（明文存储）',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `gender` enum('male','female','other') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'other' COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `emergency_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用 2-未激活',
  `email_verified` tinyint(1) NULL DEFAULT 0 COMMENT '邮箱是否验证',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `nickname`(`nickname` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_users_email`(`email` ASC) USING BTREE,
  INDEX `idx_users_nickname`(`nickname` ASC) USING BTREE,
  INDEX `idx_users_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'testuser', 'test@example.com', '1234567', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 'ride and ride', 'female', '2025-12-03', '12345678900', 0, 1, '2025-12-17 11:42:06', '2025-11-27 20:58:42', '2025-12-17 11:42:06');
INSERT INTO `users` VALUES (2, 'admin', 'admin@example.com', 'admin123', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739073797.png', NULL, 'other', NULL, NULL, 0, 1, NULL, '2025-11-27 20:58:42', '2025-12-09 23:50:26');
INSERT INTO `users` VALUES (3, 'user1', 'user1@example.com', 'password1', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739073797.png', NULL, 'other', NULL, NULL, 0, 1, NULL, '2025-11-27 20:58:42', '2025-12-09 23:50:50');
INSERT INTO `users` VALUES (7, 'a', 'a@qq.com', 'aaaaaa', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739092619.png', NULL, 'other', NULL, NULL, 0, 0, '2025-11-28 00:06:10', '2025-11-28 00:06:10', '2025-12-09 23:50:57');
INSERT INTO `users` VALUES (8, '123456', '710963274@qq.com', '123456', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739092619.png', '1', 'male', '1000-01-01', '56588655633', 0, 0, '2025-12-17 09:07:38', '2025-12-02 12:09:25', '2025-12-17 09:07:38');
INSERT INTO `users` VALUES (9, 'ddd', 'aaa@gmail.com', 'sssssss', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739110342.png', NULL, 'other', NULL, NULL, 0, 0, '2025-12-02 20:24:26', '2025-12-02 20:24:26', '2025-12-09 23:51:09');
INSERT INTO `users` VALUES (10, '111', '111@gmail.com', '111111', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739127863.png', NULL, 'other', NULL, NULL, 0, 0, '2025-12-16 00:39:45', '2025-12-02 20:48:47', '2025-12-16 00:39:45');
INSERT INTO `users` VALUES (11, '1', '1@gmail.com', '123123', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/User%20Icon/1764739143896.png', NULL, 'other', NULL, NULL, 0, 0, '2025-12-17 09:26:48', '2025-12-09 19:31:59', '2025-12-17 09:26:48');
INSERT INTO `users` VALUES (12, 'as', 'test@qq.com', '123456', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-12-16 15:06:12', '2025-12-16 15:06:11', '2025-12-16 15:06:12');
INSERT INTO `users` VALUES (13, '123456/', 'test6@example.com', '123456', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-12-17 08:55:24', '2025-12-17 08:53:10', '2025-12-17 08:55:24');

SET FOREIGN_KEY_CHECKS = 1;

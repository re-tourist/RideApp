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

 Date: 06/12/2025 12:16:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
INSERT INTO `articles` VALUES (1, '骑车时别听歌，除非你...', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-11-20', 2444, '2025-12-04 23:01:07');
INSERT INTO `articles` VALUES (2, '告别耳内闷罐，这款耳机成了我的通勤与运动全能搭子', 2, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-11-20', 1876, '2025-12-04 23:01:07');

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
INSERT INTO `clubs` VALUES (1, '北京狂魔车队', '北京市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 1943, 42987, '2025-12-04 23:00:55', '2025-12-04 23:00:55');
INSERT INTO `clubs` VALUES (2, 'CAPU行者', '北京市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 1258, 24658, '2025-12-04 23:00:55', '2025-12-04 23:00:55');
INSERT INTO `clubs` VALUES (3, '成都骑行吧', '成都市', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', 1880, 96232, '2025-12-04 23:00:55', '2025-12-04 23:00:55');

-- ----------------------------
-- Table structure for community_posts
-- ----------------------------
DROP TABLE IF EXISTS `community_posts`;
CREATE TABLE `community_posts`  (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `author_user_id` int NOT NULL,
  `club_id` int NULL DEFAULT NULL,
  `content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `fk_posts_author`(`author_user_id` ASC) USING BTREE,
  INDEX `fk_posts_club`(`club_id` ASC) USING BTREE,
  CONSTRAINT `fk_posts_author` FOREIGN KEY (`author_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_posts_club` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`club_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_posts
-- ----------------------------
INSERT INTO `community_posts` VALUES (1, 1, NULL, '滨江夜骑，微风很舒服', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/bj-night.jpg', '2025-12-05 08:10:00');
INSERT INTO `community_posts` VALUES (2, 2, NULL, '西郊爬坡训练，平均功率提升了', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/hill-climb.jpg', '2025-12-05 08:20:00');
INSERT INTO `community_posts` VALUES (3, 3, NULL, '城市早骑，通勤顺路锻炼', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/city-morning.jpg', '2025-12-05 07:30:00');
INSERT INTO `community_posts` VALUES (4, 1, 1, '北京狂魔车队周末拉练，路线很燃', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/club-ride-1.jpg', '2025-12-04 09:00:00');
INSERT INTO `community_posts` VALUES (5, 2, 2, 'CAPU行者夜骑分享，灯光很美', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/club-ride-2.jpg', '2025-12-04 20:30:00');
INSERT INTO `community_posts` VALUES (6, 3, 3, '成都骑行吧晨练集合，欢迎一起', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/club-ride-3.jpg', '2025-12-03 06:45:00');

-- ----------------------------
-- Table structure for event_tags
-- ----------------------------
DROP TABLE IF EXISTS `event_tags`;
CREATE TABLE `event_tags`  (
  `event_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`event_id`, `tag_name`) USING BTREE,
  CONSTRAINT `event_tags_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of event_tags
-- ----------------------------
INSERT INTO `event_tags` VALUES (1, '挑战');
INSERT INTO `event_tags` VALUES (1, '骑行');
INSERT INTO `event_tags` VALUES (2, '竞速');
INSERT INTO `event_tags` VALUES (2, '骑行');
INSERT INTO `event_tags` VALUES (3, '挑战');
INSERT INTO `event_tags` VALUES (3, '越野跑');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动/赛事' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of events
-- ----------------------------
INSERT INTO `events` VALUES (1, '迎风织金季·GBA青年自行车线上赛', '2025-11-08 09:00:00', '任意地点', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '线上赛挑战', '2025-12-04 23:00:54', '2025-12-04 23:00:54');
INSERT INTO `events` VALUES (2, '2025“环八娄”自行车爬坡联赛（娄城）', '2025-11-29 08:00:00', '浙江省娄城市', '骑行', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '爬坡联赛', '2025-12-04 23:00:54', '2025-12-04 23:00:54');
INSERT INTO `events` VALUES (3, '越野跑周末挑战赛', '2025-12-01 08:30:00', '上海市郊区', '越野跑', 1, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '周末越野跑挑战', '2025-12-04 23:00:54', '2025-12-04 23:00:54');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_comments
-- ----------------------------
INSERT INTO `post_comments` VALUES (1, 1, 2, '夜骑不错，注意安全', '2025-12-05 08:16:00');
INSERT INTO `post_comments` VALUES (2, 1, 3, '风很舒服，支持！', '2025-12-05 08:18:00');
INSERT INTO `post_comments` VALUES (3, 2, 1, '爬坡加油，稳步提升', '2025-12-05 08:26:00');
INSERT INTO `post_comments` VALUES (4, 4, 3, '周末拉练报名！', '2025-12-04 09:10:00');

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
INSERT INTO `post_likes` VALUES (1, 1, '2025-12-05 08:15:00');
INSERT INTO `post_likes` VALUES (1, 2, '2025-12-05 09:00:00');
INSERT INTO `post_likes` VALUES (2, 1, '2025-12-05 08:25:00');
INSERT INTO `post_likes` VALUES (2, 3, '2025-12-05 08:40:00');
INSERT INTO `post_likes` VALUES (4, 1, '2025-12-04 10:00:00');
INSERT INTO `post_likes` VALUES (5, 1, '2025-12-04 21:00:00');
INSERT INTO `post_likes` VALUES (6, 2, '2025-12-03 07:00:00');

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
-- Table structure for route_downloads
-- ----------------------------
DROP TABLE IF EXISTS `route_downloads`;
CREATE TABLE `route_downloads`  (
  `route_id` int NOT NULL,
  `user_id` int NOT NULL,
  `downloaded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`, `user_id`) USING BTREE,
  INDEX `idx_route_downloads_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_route_downloads_route` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_route_downloads_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of route_downloads
-- ----------------------------
INSERT INTO `route_downloads` VALUES (1, 1, '2025-12-05 08:30:00');
INSERT INTO `route_downloads` VALUES (3, 1, '2025-12-05 08:45:00');

-- ----------------------------
-- Table structure for route_favorites
-- ----------------------------
DROP TABLE IF EXISTS `route_favorites`;
CREATE TABLE `route_favorites`  (
  `route_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`route_id`, `user_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `route_favorites_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `route_favorites_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路书收藏' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of route_favorites
-- ----------------------------
INSERT INTO `route_favorites` VALUES (1, 1, '2025-12-04 23:00:55');
INSERT INTO `route_favorites` VALUES (2, 2, '2025-12-04 23:00:55');
INSERT INTO `route_favorites` VALUES (3, 1, '2025-12-05 09:00:00');

-- ----------------------------
-- Table structure for route_tags
-- ----------------------------
DROP TABLE IF EXISTS `route_tags`;
CREATE TABLE `route_tags`  (
  `route_id` int NOT NULL,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`route_id`, `tag_name`) USING BTREE,
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
INSERT INTO `routes` VALUES (1, '滨江环线', 32.50, 210, '上海市', '简单', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-12-04 23:00:55', '2025-12-04 23:00:55');
INSERT INTO `routes` VALUES (2, '西郊爬坡挑战', 65.00, 980, '浙江省', '困难', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-12-04 23:00:55', '2025-12-04 23:00:55');
INSERT INTO `routes` VALUES (3, '城市夜骑', 18.30, 80, '上海市', '中等', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-12-04 23:00:55', '2025-12-05 19:42:13');
INSERT INTO `routes` VALUES (4, '测试', 11.11, 100, '南昌市', '简单', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '2025-12-05 19:42:07', '2025-12-05 19:42:11');

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
INSERT INTO `trade_items` VALUES (1, 0, '9成新碳纤维公路车架', '尺寸M，超轻，只用了半年，因为换车出售。可小刀。', 4500.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/frame.jpg', 'xianyu://item/12345', 1, '整车', 1, '2025-12-05 09:00:00');
INSERT INTO `trade_items` VALUES (2, 0, 'Shimano 105套件（二手）', '飞轮、链条、牙盘全套，正常使用痕迹，功能完好。', 1500.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/groupset.jpg', 'xianyu://item/67890', 2, '配件', 1, '2025-12-05 09:05:00');
INSERT INTO `trade_items` VALUES (3, 0, '冬季骑行抓绒手套', '全新未拆封，L号，防水防风，多买了一副，便宜出。', 89.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/gloves.jpg', 'taobao://item/11223', 1, '配件', 1, '2025-12-05 09:10:00');
INSERT INTO `trade_items` VALUES (4, 1, 'RideFlow 2024新款速干骑行服套装', '骑行服，透气排汗，夏季必备。分类：骑行服', 399.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/jersey.jpg', 'app://official/product/399', NULL, '骑行服', 1, '2025-12-05 09:20:00');
INSERT INTO `trade_items` VALUES (5, 1, '高性能GPS码表（R700型号）', '精准定位，超长续航，支持心率监测。分类：配件', 1899.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/computer.jpg', 'app://official/product/r700', NULL, '配件', 1, '2025-12-05 09:25:00');
INSERT INTO `trade_items` VALUES (6, 1, '山地越野头盔（Pro系列）', 'MIPS保护系统，轻量化设计，多色可选。分类：配件', 599.00, 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/helmet.jpg', 'app://official/product/prohelmet', NULL, '配件', 1, '2025-12-05 09:30:00');

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
  CONSTRAINT `fk_user_events_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_events_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与活动关系（我的活动）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_events
-- ----------------------------
INSERT INTO `user_events` VALUES (1, 1, 1, 'registered', 'completed', '2025-12-05 19:12:14', '已参加并完成');
INSERT INTO `user_events` VALUES (2, 1, 2, 'registered', 'upcoming', '2025-12-05 19:12:14', '已报名，待开始');
INSERT INTO `user_events` VALUES (3, 1, 3, 'favorite', 'upcoming', '2025-12-05 19:12:14', '收藏关注');

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
INSERT INTO `user_follows` VALUES (1, 2, '2025-12-05 20:35:08');
INSERT INTO `user_follows` VALUES (1, 3, '2025-12-05 20:35:08');

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
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_ride_preferences
-- ----------------------------
INSERT INTO `user_ride_preferences` VALUES (29, 1, 23, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (30, 1, 28, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (31, 1, 9, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (32, 1, 1, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (33, 1, 2, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (34, 1, 3, '2025-12-05 00:19:48');
INSERT INTO `user_ride_preferences` VALUES (35, 1, 16, '2025-12-05 00:19:48');

-- ----------------------------
-- Table structure for user_ride_records
-- ----------------------------
DROP TABLE IF EXISTS `user_ride_records`;
CREATE TABLE `user_ride_records`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `route_id` int NULL DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `duration_sec` int NOT NULL,
  `distance_km` decimal(8, 2) NOT NULL,
  `avg_speed_kmh` decimal(5, 2) NULL DEFAULT 0.00,
  `calories` int NULL DEFAULT 0,
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `route_id`(`route_id` ASC) USING BTREE,
  INDEX `idx_ride_records_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_ride_records_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `user_ride_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_ride_records_ibfk_2` FOREIGN KEY (`route_id`) REFERENCES `routes` (`route_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户骑行记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_ride_records
-- ----------------------------
INSERT INTO `user_ride_records` VALUES (1, 1, 1, '2025-11-21 19:00:00', 5400, 32.50, 21.67, 850, '夜骑很爽', '2025-12-04 23:00:55');
INSERT INTO `user_ride_records` VALUES (2, 2, 2, '2025-11-22 08:00:00', 10800, 65.00, 21.66, 1600, '爬坡辛苦', '2025-12-04 23:00:55');
INSERT INTO `user_ride_records` VALUES (3, 3, 3, '2025-11-23 20:00:00', 3600, 18.30, 18.30, 600, '城市风光不错', '2025-12-04 23:00:55');

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'testuser', 'test@example.com', '123456', 'https://rideapp.oss-cn-hangzhou.aliyuncs.com/images/%E5%87%89%E5%AE%AB%E6%98%A5%E6%97%A5.jpg', '222', 'male', '2025-12-03', '12345678900', 0, 1, '2025-12-06 12:08:27', '2025-11-27 20:58:42', '2025-12-06 12:08:27');
INSERT INTO `users` VALUES (2, 'admin', 'admin@example.com', 'admin123', NULL, NULL, 'other', NULL, NULL, 0, 1, NULL, '2025-11-27 20:58:42', '2025-11-27 20:58:42');
INSERT INTO `users` VALUES (3, 'user1', 'user1@example.com', 'password1', NULL, NULL, 'other', NULL, NULL, 0, 1, NULL, '2025-11-27 20:58:42', '2025-11-27 20:58:42');
INSERT INTO `users` VALUES (6, '1', '111@qq.com', '111111', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-11-28 00:01:50', '2025-11-27 21:42:01', '2025-11-28 00:01:50');
INSERT INTO `users` VALUES (7, 'a', 'a@qq.com', 'aaaaaa', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-11-28 00:06:10', '2025-11-28 00:06:10', '2025-11-28 00:06:10');
INSERT INTO `users` VALUES (8, '123456', '710963274@qq.com', '123456', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-12-03 00:50:28', '2025-12-02 12:09:25', '2025-12-03 00:50:28');
INSERT INTO `users` VALUES (9, 'ddd', 'aaa@gmail.com', 'sssssss', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-12-02 20:24:26', '2025-12-02 20:24:26', '2025-12-02 20:24:26');
INSERT INTO `users` VALUES (10, '111', '111@gmail.com', '111111', NULL, NULL, 'other', NULL, NULL, 0, 0, '2025-12-05 08:55:16', '2025-12-02 20:48:47', '2025-12-05 08:55:16');

SET FOREIGN_KEY_CHECKS = 1;

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

 Date: 12/12/2025 22:08:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;

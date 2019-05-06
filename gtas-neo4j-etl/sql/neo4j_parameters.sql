/*
 Navicat MariaDB Data Transfer

 Source Server         : GTAS-Local-DB
 Source Server Type    : MariaDB
 Source Server Version : 100033
 Source Host           : localhost:3306
 Source Schema         : gtas

 Target Server Type    : MariaDB
 Target Server Version : 100033
 File Encoding         : 65001

 Date: 25/04/2019 23:35:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for neo4j_parameters
-- ----------------------------
DROP TABLE IF EXISTS `neo4j_parameters`;
CREATE TABLE `neo4j_parameters`  (
  `id` int(11) NOT NULL,
  `last_proc_msg_crt_dtm` datetime(0) NOT NULL,
  `last_proc_msg_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of neo4j_parameters
-- ----------------------------
INSERT INTO `neo4j_parameters` VALUES (1, '2019-01-01 00:00:00', 1);

SET FOREIGN_KEY_CHECKS = 1;

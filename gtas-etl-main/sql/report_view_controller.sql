

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for report_view_controller
-- ----------------------------
DROP TABLE IF EXISTS `report_view_controller`;
CREATE TABLE `report_view_controller`  (
  `id` int(11) NOT NULL,
  `message_flight_idx_last_id` bigint(1) NULL DEFAULT NULL,
  `hit_detail_idx_last_id` bigint(1) NULL DEFAULT NULL,
  `hit_detail_idx_last_read_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_view_controller
-- ----------------------------
INSERT INTO `report_view_controller` VALUES (1, 0, 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;

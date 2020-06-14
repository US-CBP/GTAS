CREATE TABLE `country_group_join` (
                                      `country_group_id` bigint(20) unsigned NOT NULL,
                                      `country_id` bigint(20) unsigned NOT NULL,
                                      PRIMARY KEY (`country_group_id`,`country_id`),
                                      KEY `FKcp52ppqwq512hvit3ce0d9l5v` (`country_id`),
                                      CONSTRAINT `FKcp52ppqwq512hvit3ce0d9l5v` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`),
                                      CONSTRAINT `FKdciuwx2b961oycwewtrok314e` FOREIGN KEY (`country_group_id`) REFERENCES `country_group` (`id`)
)


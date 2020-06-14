CREATE TABLE `country_group` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                 `created_at` datetime DEFAULT NULL,
                                 `created_by` varchar(20) DEFAULT NULL,
                                 `updated_at` datetime DEFAULT NULL,
                                 `updated_by` varchar(20) DEFAULT NULL,
                                 `cg_label` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
)


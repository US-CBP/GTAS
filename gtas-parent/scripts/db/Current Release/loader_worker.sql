-- gtas.loader_worker definition

CREATE TABLE `loader_worker` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `bucketCount` int(11) DEFAULT NULL,
  `worker_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_4ho11t6qvpwfcagsr122u5hlf` (`worker_name`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
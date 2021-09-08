-- gtas.flight_loader definition

CREATE TABLE `flight_loader` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `id_tag` varchar(255) DEFAULT NULL,
  `loader_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6pvk902k0ve3htjj7mrw65o1l` (`id_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
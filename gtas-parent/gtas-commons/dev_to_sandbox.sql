-- --------------------------------------------------------
-- Host:                         gtas-dev.cyay6pqzzwmy.us-gov-west-1.rds.amazonaws.com
-- Server version:               10.0.24-MariaDB - MariaDB Server
-- Server OS:                    Linux
-- HeidiSQL Version:             9.4.0.5174
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for gtas
DROP DATABASE IF EXISTS `gtas`;
CREATE DATABASE IF NOT EXISTS `gtas` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `gtas`;

-- Dumping structure for table gtas.address
DROP TABLE IF EXISTS `address`;
CREATE TABLE IF NOT EXISTS `address` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `line1` varchar(255) NOT NULL,
  `line2` varchar(255) DEFAULT NULL,
  `line3` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.agency
DROP TABLE IF EXISTS `agency`;
CREATE TABLE IF NOT EXISTS `agency` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.airport
DROP TABLE IF EXISTS `airport`;
CREATE TABLE IF NOT EXISTS `airport` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `iata` varchar(3) DEFAULT NULL,
  `icao` varchar(4) DEFAULT NULL,
  `latitude` decimal(9,6) DEFAULT NULL,
  `longitude` decimal(9,6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `timezone` varchar(255) DEFAULT NULL,
  `utc_offset` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8108 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.apis_message
DROP TABLE IF EXISTS `apis_message`;
CREATE TABLE IF NOT EXISTS `apis_message` (
  `bag_count` int(11) DEFAULT NULL,
  `debarkation` varchar(255) DEFAULT NULL,
  `message_type` varchar(10) DEFAULT NULL,
  `transmission_date` datetime DEFAULT NULL,
  `transmission_source` varchar(255) DEFAULT NULL,
  `version` varchar(10) DEFAULT NULL,
  `embarkation` varchar(255) DEFAULT NULL,
  `port_of_first_arrival` varchar(255) DEFAULT NULL,
  `residence_country` varchar(255) DEFAULT NULL,
  `traveler_type` varchar(255) DEFAULT NULL,
  `id` bigint(20) unsigned NOT NULL,
  `installation_address` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_i9qgqr9rcgejn2xemiqr89rrt` (`installation_address`),
  CONSTRAINT `FK_i9qgqr9rcgejn2xemiqr89rrt` FOREIGN KEY (`installation_address`) REFERENCES `address` (`id`),
  CONSTRAINT `FK_q9ra20yen6nh35w7rlcyjajvp` FOREIGN KEY (`id`) REFERENCES `message` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.apis_message_flight
DROP TABLE IF EXISTS `apis_message_flight`;
CREATE TABLE IF NOT EXISTS `apis_message_flight` (
  `apis_message_id` bigint(20) unsigned NOT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`apis_message_id`,`flight_id`),
  KEY `FK_n98asyjlvqgig379lye01o6kh` (`flight_id`),
  CONSTRAINT `FK_n98asyjlvqgig379lye01o6kh` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FK_slssns7wben2clh21xqdjtcmo` FOREIGN KEY (`apis_message_id`) REFERENCES `apis_message` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.apis_message_passenger
DROP TABLE IF EXISTS `apis_message_passenger`;
CREATE TABLE IF NOT EXISTS `apis_message_passenger` (
  `apis_message_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`apis_message_id`,`passenger_id`),
  KEY `FK_mhpr74qe5bfgnp4it25m1r8vo` (`passenger_id`),
  CONSTRAINT `FK_3uc5c3h8luvp8q5tvj6j8jyo8` FOREIGN KEY (`apis_message_id`) REFERENCES `apis_message` (`id`),
  CONSTRAINT `FK_mhpr74qe5bfgnp4it25m1r8vo` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.apis_message_reporting_party
DROP TABLE IF EXISTS `apis_message_reporting_party`;
CREATE TABLE IF NOT EXISTS `apis_message_reporting_party` (
  `apis_message_id` bigint(20) unsigned NOT NULL,
  `reporting_party_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`apis_message_id`,`reporting_party_id`),
  KEY `FK_4n180bobme11q4jqmiv88hou7` (`reporting_party_id`),
  CONSTRAINT `FK_4n180bobme11q4jqmiv88hou7` FOREIGN KEY (`reporting_party_id`) REFERENCES `reporting_party` (`id`),
  CONSTRAINT `FK_d22blvqceucw0q8s386g64jq9` FOREIGN KEY (`apis_message_id`) REFERENCES `apis_message` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.apis_phone
DROP TABLE IF EXISTS `apis_phone`;
CREATE TABLE IF NOT EXISTS `apis_phone` (
  `apis_message_id` bigint(20) unsigned NOT NULL,
  `phone_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`apis_message_id`,`phone_id`),
  KEY `FK_8nvut1746o790aov8oe3ou43d` (`phone_id`),
  CONSTRAINT `FK_8nvut1746o790aov8oe3ou43d` FOREIGN KEY (`phone_id`) REFERENCES `phone` (`id`),
  CONSTRAINT `FK_ilbw65gg4bdxt70t6n986kb36` FOREIGN KEY (`apis_message_id`) REFERENCES `apis_message` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.app_configuration
DROP TABLE IF EXISTS `app_configuration`;
CREATE TABLE IF NOT EXISTS `app_configuration` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `opt` varchar(255) DEFAULT NULL,
  `val` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.attachment
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE IF NOT EXISTS `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` longblob,
  `content_type` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `passenger_id` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bqyc6athq9xl334mrjuy5m7r1` (`passenger_id`),
  CONSTRAINT `FK_bqyc6athq9xl334mrjuy5m7r1` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.audit_log
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `action_data` longtext,
  `actionStatus` varchar(32) NOT NULL,
  `action_type` varchar(32) NOT NULL,
  `action_message` varchar(255) DEFAULT NULL,
  `action_target` varchar(1024) NOT NULL,
  `timestamp` datetime NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pyjqqm7hglp6pnwp3h8whian8` (`user_id`),
  CONSTRAINT `FK_pyjqqm7hglp6pnwp3h8whian8` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3412 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.bag
DROP TABLE IF EXISTS `bag`;
CREATE TABLE IF NOT EXISTS `bag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `airline` varchar(255) DEFAULT NULL,
  `bag_identification` varchar(255) NOT NULL,
  `data_source` varchar(255) DEFAULT NULL,
  `destination` varchar(255) DEFAULT NULL,
  `destination_airport` varchar(255) DEFAULT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ikr7kpe2rqaulcebnyje8lh8y` (`flight_id`),
  KEY `FK_gq5fhhgswdq53il0lodbsaqs` (`passenger_id`),
  CONSTRAINT `FK_gq5fhhgswdq53il0lodbsaqs` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`),
  CONSTRAINT `FK_ikr7kpe2rqaulcebnyje8lh8y` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.carrier
DROP TABLE IF EXISTS `carrier`;
CREATE TABLE IF NOT EXISTS `carrier` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `iata` varchar(2) DEFAULT NULL,
  `icao` varchar(3) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=836 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.code_share_flight
DROP TABLE IF EXISTS `code_share_flight`;
CREATE TABLE IF NOT EXISTS `code_share_flight` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `marketing_flight_number` varchar(255) DEFAULT NULL,
  `operating_flight_id` bigint(20) DEFAULT NULL,
  `operating_flight_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.country
DROP TABLE IF EXISTS `country`;
CREATE TABLE IF NOT EXISTS `country` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `iso2` varchar(2) DEFAULT NULL,
  `iso3` varchar(3) DEFAULT NULL,
  `iso_numeric` varchar(3) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.credit_card
DROP TABLE IF EXISTS `credit_card`;
CREATE TABLE IF NOT EXISTS `credit_card` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `card_type` varchar(255) DEFAULT NULL,
  `expiration` date DEFAULT NULL,
  `number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ng2586g4ads4f0675aef7ovhu` (`card_type`,`number`,`expiration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for view gtas.daily_apis_counts
DROP VIEW IF EXISTS `daily_apis_counts`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `daily_apis_counts` (
	`id` BIGINT(20) UNSIGNED NOT NULL,
	`day` DATE NULL,
	`1am` BIGINT(21) NOT NULL,
	`2am` BIGINT(21) NOT NULL,
	`3am` BIGINT(21) NOT NULL,
	`4am` BIGINT(21) NOT NULL,
	`5am` BIGINT(21) NOT NULL,
	`6am` BIGINT(21) NOT NULL,
	`7am` BIGINT(21) NOT NULL,
	`8am` BIGINT(21) NOT NULL,
	`9am` BIGINT(21) NOT NULL,
	`10am` BIGINT(21) NOT NULL,
	`11am` BIGINT(21) NOT NULL,
	`12pm` BIGINT(21) NOT NULL,
	`1pm` BIGINT(21) NOT NULL,
	`2pm` BIGINT(21) NOT NULL,
	`3pm` BIGINT(21) NOT NULL,
	`4pm` BIGINT(21) NOT NULL,
	`5pm` BIGINT(21) NOT NULL,
	`6pm` BIGINT(21) NOT NULL,
	`7pm` BIGINT(21) NOT NULL,
	`8pm` BIGINT(21) NOT NULL,
	`9pm` BIGINT(21) NOT NULL,
	`10pm` BIGINT(21) NOT NULL,
	`11pm` BIGINT(21) NOT NULL,
	`12am` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view gtas.daily_pnr_counts
DROP VIEW IF EXISTS `daily_pnr_counts`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `daily_pnr_counts` (
	`id` BIGINT(20) UNSIGNED NOT NULL,
	`day` DATE NULL,
	`1am` BIGINT(21) NOT NULL,
	`2am` BIGINT(21) NOT NULL,
	`3am` BIGINT(21) NOT NULL,
	`4am` BIGINT(21) NOT NULL,
	`5am` BIGINT(21) NOT NULL,
	`6am` BIGINT(21) NOT NULL,
	`7am` BIGINT(21) NOT NULL,
	`8am` BIGINT(21) NOT NULL,
	`9am` BIGINT(21) NOT NULL,
	`10am` BIGINT(21) NOT NULL,
	`11am` BIGINT(21) NOT NULL,
	`12pm` BIGINT(21) NOT NULL,
	`1pm` BIGINT(21) NOT NULL,
	`2pm` BIGINT(21) NOT NULL,
	`3pm` BIGINT(21) NOT NULL,
	`4pm` BIGINT(21) NOT NULL,
	`5pm` BIGINT(21) NOT NULL,
	`6pm` BIGINT(21) NOT NULL,
	`7pm` BIGINT(21) NOT NULL,
	`8pm` BIGINT(21) NOT NULL,
	`9pm` BIGINT(21) NOT NULL,
	`10pm` BIGINT(21) NOT NULL,
	`11pm` BIGINT(21) NOT NULL,
	`12am` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for table gtas.dashboard_message_stats
DROP TABLE IF EXISTS `dashboard_message_stats`;
CREATE TABLE IF NOT EXISTS `dashboard_message_stats` (
  `id` bigint(20) NOT NULL,
  `dt_modified` datetime NOT NULL,
  `hour_8` int(11) NOT NULL,
  `hour_18` int(11) NOT NULL,
  `hour_11` int(11) NOT NULL,
  `hour_15` int(11) NOT NULL,
  `hour_5` int(11) NOT NULL,
  `hour_4` int(11) NOT NULL,
  `hour_14` int(11) NOT NULL,
  `message_type` varchar(255) NOT NULL,
  `hour_9` int(11) NOT NULL,
  `hour_19` int(11) NOT NULL,
  `hour_1` int(11) NOT NULL,
  `hour_7` int(11) NOT NULL,
  `hour_17` int(11) NOT NULL,
  `hour_6` int(11) NOT NULL,
  `hour_16` int(11) NOT NULL,
  `hour_10` int(11) NOT NULL,
  `hour_13` int(11) NOT NULL,
  `hour_3` int(11) NOT NULL,
  `hour_12` int(11) NOT NULL,
  `hour_20` int(11) NOT NULL,
  `hour_21` int(11) NOT NULL,
  `hour_23` int(11) NOT NULL,
  `hour_22` int(11) NOT NULL,
  `hour_2` int(11) NOT NULL,
  `hour_24` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.disposition
DROP TABLE IF EXISTS `disposition`;
CREATE TABLE IF NOT EXISTS `disposition` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  `status_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_u370wswbf8goqcqlphr4t83w` (`flight_id`),
  KEY `FK_nvdnrc80bxv5vfcuh97g16sp7` (`passenger_id`),
  KEY `FK_819n7u7x15kscsco2lc2tc9uj` (`status_id`),
  CONSTRAINT `FK_819n7u7x15kscsco2lc2tc9uj` FOREIGN KEY (`status_id`) REFERENCES `disposition_status` (`id`),
  CONSTRAINT `FK_nvdnrc80bxv5vfcuh97g16sp7` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`),
  CONSTRAINT `FK_u370wswbf8goqcqlphr4t83w` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.disposition_status
DROP TABLE IF EXISTS `disposition_status`;
CREATE TABLE IF NOT EXISTS `disposition_status` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.document
DROP TABLE IF EXISTS `document`;
CREATE TABLE IF NOT EXISTS `document` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `document_number` varchar(255) NOT NULL,
  `document_type` varchar(3) NOT NULL,
  `expiration_date` date DEFAULT NULL,
  `issuance_country` varchar(255) DEFAULT NULL,
  `issuance_date` date DEFAULT NULL,
  `passenger_id` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_r8t0wlglti63pxgp1qff1a05h` (`passenger_id`),
  CONSTRAINT `FK_r8t0wlglti63pxgp1qff1a05h` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.dwell_time
DROP TABLE IF EXISTS `dwell_time`;
CREATE TABLE IF NOT EXISTS `dwell_time` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arrival_time` datetime DEFAULT NULL,
  `departure_at` datetime DEFAULT NULL,
  `dwell_time` double DEFAULT NULL,
  `flying_from` varchar(255) DEFAULT NULL,
  `flying_to` varchar(255) DEFAULT NULL,
  `arrival_airport` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.email
DROP TABLE IF EXISTS `email`;
CREATE TABLE IF NOT EXISTS `email` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `domain` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fl58bno5ogt46e2nkfy5q81db` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.error_detail
DROP TABLE IF EXISTS `error_detail`;
CREATE TABLE IF NOT EXISTS `error_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `description` varchar(1024) NOT NULL,
  `details` longtext,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.filter
DROP TABLE IF EXISTS `filter`;
CREATE TABLE IF NOT EXISTS `filter` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `etas_end` int(11) DEFAULT NULL,
  `etas_start` int(11) DEFAULT NULL,
  `hourly_adj` int(10) NOT NULL DEFAULT '-5',
  `flight_direction` varchar(1) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iwfx6qrhvrl20cn38u6cebuhn` (`flight_direction`),
  KEY `FK_5b5q9mgp2iys4ss5d5tp30j9y` (`user_id`),
  CONSTRAINT `FK_5b5q9mgp2iys4ss5d5tp30j9y` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_iwfx6qrhvrl20cn38u6cebuhn` FOREIGN KEY (`flight_direction`) REFERENCES `flight_direction` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.filter_destination_airports
DROP TABLE IF EXISTS `filter_destination_airports`;
CREATE TABLE IF NOT EXISTS `filter_destination_airports` (
  `id` bigint(20) unsigned NOT NULL,
  `airport_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`,`airport_id`),
  KEY `FK_310i14wbeyfdbgwgawb4plary` (`airport_id`),
  CONSTRAINT `FK_310i14wbeyfdbgwgawb4plary` FOREIGN KEY (`airport_id`) REFERENCES `airport` (`id`),
  CONSTRAINT `FK_ir89r8epq9h2rvdmaedx5ndw9` FOREIGN KEY (`id`) REFERENCES `filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.filter_origin_airports
DROP TABLE IF EXISTS `filter_origin_airports`;
CREATE TABLE IF NOT EXISTS `filter_origin_airports` (
  `id` bigint(20) unsigned NOT NULL,
  `airport_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`,`airport_id`),
  KEY `FK_9kdg2q15i1w2vox9f2gf2pspp` (`airport_id`),
  CONSTRAINT `FK_9kdg2q15i1w2vox9f2gf2pspp` FOREIGN KEY (`airport_id`) REFERENCES `airport` (`id`),
  CONSTRAINT `FK_t17kr2v6s7bwg34aqjrxmogdv` FOREIGN KEY (`id`) REFERENCES `filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.flight
DROP TABLE IF EXISTS `flight`;
CREATE TABLE IF NOT EXISTS `flight` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `carrier` varchar(255) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `destination_country` varchar(3) DEFAULT NULL,
  `direction` varchar(1) NOT NULL,
  `eta` datetime DEFAULT NULL,
  `eta_date` date DEFAULT NULL,
  `etd` datetime DEFAULT NULL,
  `etd_date` date DEFAULT NULL,
  `flight_date` date NOT NULL,
  `flight_number` varchar(4) NOT NULL,
  `full_flight_number` varchar(255) DEFAULT NULL,
  `operating_flight` bit(1) DEFAULT NULL,
  `list_hit_count` int(11) NOT NULL,
  `origin` varchar(255) NOT NULL,
  `origin_country` varchar(3) DEFAULT NULL,
  `passenger_count` int(11) NOT NULL,
  `rule_hit_count` int(11) NOT NULL,
  `utc_eta` datetime DEFAULT NULL,
  `utc_etd` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ii2lo91fwjeaksteiy24gnnjs` (`carrier`,`flight_number`,`flight_date`,`origin`,`destination`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.flight_direction
DROP TABLE IF EXISTS `flight_direction`;
CREATE TABLE IF NOT EXISTS `flight_direction` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_o824hs8aub5bv4k4iimy8lya` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.flight_leg
DROP TABLE IF EXISTS `flight_leg`;
CREATE TABLE IF NOT EXISTS `flight_leg` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `leg_number` int(11) NOT NULL,
  `flight_id` bigint(20) unsigned DEFAULT NULL,
  `pnr_id` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_512rof22gyhj0l6bcat3iwn1x` (`flight_id`),
  KEY `FK_hdl1b3li9nuw9ax1k2ak1icgo` (`pnr_id`),
  CONSTRAINT `FK_512rof22gyhj0l6bcat3iwn1x` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FK_hdl1b3li9nuw9ax1k2ak1icgo` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.flight_passenger
DROP TABLE IF EXISTS `flight_passenger`;
CREATE TABLE IF NOT EXISTS `flight_passenger` (
  `flight_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`flight_id`,`passenger_id`),
  KEY `FK_jv5krh5m3lbyxtxem4piuaxe6` (`passenger_id`),
  CONSTRAINT `FK_jln16huo2u34rmxh1ntf7rvpt` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FK_jv5krh5m3lbyxtxem4piuaxe6` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.frequent_flyer
DROP TABLE IF EXISTS `frequent_flyer`;
CREATE TABLE IF NOT EXISTS `frequent_flyer` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `carrier` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oif2mvign1npxux0l5d10ac5e` (`carrier`,`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.hibernate_sequences
DROP TABLE IF EXISTS `hibernate_sequences`;
CREATE TABLE IF NOT EXISTS `hibernate_sequences` (
  `sequence_name` varchar(255) DEFAULT NULL,
  `sequence_next_hi_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.hits_summary
DROP TABLE IF EXISTS `hits_summary`;
CREATE TABLE IF NOT EXISTS `hits_summary` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `hit_type` varchar(255) DEFAULT NULL,
  `rule_hit_count` int(11) DEFAULT NULL,
  `wl_hit_count` int(11) DEFAULT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_no7y2pouyb5pkhc9hi38ol5ih` (`flight_id`),
  KEY `FK_m1ghrry86q1fimowmeettieeg` (`passenger_id`),
  CONSTRAINT `FK_m1ghrry86q1fimowmeettieeg` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`),
  CONSTRAINT `FK_no7y2pouyb5pkhc9hi38ol5ih` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.hit_detail
DROP TABLE IF EXISTS `hit_detail`;
CREATE TABLE IF NOT EXISTS `hit_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `hit_type` varchar(3) NOT NULL,
  `cond_text` text,
  `rule_id` bigint(20) NOT NULL,
  `hits_summary_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6qjmni9nw4r4kx8dn129vwhlf` (`hits_summary_id`),
  CONSTRAINT `FK_6qjmni9nw4r4kx8dn129vwhlf` FOREIGN KEY (`hits_summary_id`) REFERENCES `hits_summary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.knowledge_base
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE IF NOT EXISTS `knowledge_base` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `CREATION_DT` datetime NOT NULL,
  `KB_BLOB` longblob NOT NULL,
  `KB_NAME` varchar(20) NOT NULL,
  `RL_BLOB` longblob NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `KB_UNIQUE_NAME` (`KB_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.loader_audit_logs
DROP TABLE IF EXISTS `loader_audit_logs`;
CREATE TABLE IF NOT EXISTS `loader_audit_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `object_type` varchar(255) DEFAULT NULL,
  `object_value` varchar(4000) DEFAULT NULL,
  `object_key` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.message
DROP TABLE IF EXISTS `message`;
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `create_date` datetime NOT NULL,
  `error` varchar(4000) DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `hash_code` varchar(255) DEFAULT NULL,
  `raw` longtext,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_4wvrsj1r2gtvcc1q3kgdp74xt` (`hash_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3771 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.passenger
DROP TABLE IF EXISTS `passenger`;
CREATE TABLE IF NOT EXISTS `passenger` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `citizenship_country` varchar(255) DEFAULT NULL,
  `debark_country` varchar(255) DEFAULT NULL,
  `debarkation` varchar(255) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `dob` date DEFAULT NULL,
  `embark_country` varchar(255) DEFAULT NULL,
  `embarkation` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(2) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `days_visa_valid` int(11) DEFAULT NULL,
  `passenger_type` varchar(3) NOT NULL,
  `residency_country` varchar(255) DEFAULT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.phone
DROP TABLE IF EXISTS `phone`;
CREATE TABLE IF NOT EXISTS `phone` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `number` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jpobbsduo00bgyro8gurj7for` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr
DROP TABLE IF EXISTS `pnr`;
CREATE TABLE IF NOT EXISTS `pnr` (
  `bag_count` int(11) DEFAULT NULL,
  `baggage_unit` varchar(255) DEFAULT NULL,
  `baggage_weight` double DEFAULT NULL,
  `carrier` varchar(255) DEFAULT NULL,
  `date_booked` date DEFAULT NULL,
  `date_received` date DEFAULT NULL,
  `days_booked_before_travel` int(11) DEFAULT NULL,
  `departure_date` date DEFAULT NULL,
  `message_type` varchar(10) DEFAULT NULL,
  `transmission_date` datetime DEFAULT NULL,
  `transmission_source` varchar(255) DEFAULT NULL,
  `version` varchar(10) DEFAULT NULL,
  `form_of_payment` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `origin_country` varchar(3) DEFAULT NULL,
  `passenger_count` int(11) DEFAULT NULL,
  `record_locator` varchar(20) DEFAULT NULL,
  `resrvation_create_date` datetime DEFAULT NULL,
  `total_bag_count` int(11) DEFAULT NULL,
  `total_bag_weight` float DEFAULT NULL,
  `trip_duration` double DEFAULT NULL,
  `id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_j8v4dkklaftjeqctm6m55kmk4` FOREIGN KEY (`id`) REFERENCES `message` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_address
DROP TABLE IF EXISTS `pnr_address`;
CREATE TABLE IF NOT EXISTS `pnr_address` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `address_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`address_id`),
  KEY `FK_4rbphfnymi6chv6svp5can5xe` (`address_id`),
  CONSTRAINT `FK_2yub66t4ia6wj3vnukyiia1ac` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_4rbphfnymi6chv6svp5can5xe` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_agency
DROP TABLE IF EXISTS `pnr_agency`;
CREATE TABLE IF NOT EXISTS `pnr_agency` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `agency_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`agency_id`),
  KEY `FK_sfmxkkiov9jry9cjssqpkqg49` (`agency_id`),
  CONSTRAINT `FK_h4omfu5fjcw9q4bwgdv7859se` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_sfmxkkiov9jry9cjssqpkqg49` FOREIGN KEY (`agency_id`) REFERENCES `agency` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_codeshares
DROP TABLE IF EXISTS `pnr_codeshares`;
CREATE TABLE IF NOT EXISTS `pnr_codeshares` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `codeshare_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`codeshare_id`),
  KEY `FK_ii2yfcc4ucnngphh4cqrght2x` (`codeshare_id`),
  CONSTRAINT `FK_985l8cmmxxu9ir0fwu23fn5rm` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_ii2yfcc4ucnngphh4cqrght2x` FOREIGN KEY (`codeshare_id`) REFERENCES `code_share_flight` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_credit_card
DROP TABLE IF EXISTS `pnr_credit_card`;
CREATE TABLE IF NOT EXISTS `pnr_credit_card` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `credit_card_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`credit_card_id`),
  KEY `FK_74bqvo7dl3lng3jktqrmbkwku` (`credit_card_id`),
  CONSTRAINT `FK_2teqv170fgelm9wnxesreie9o` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_74bqvo7dl3lng3jktqrmbkwku` FOREIGN KEY (`credit_card_id`) REFERENCES `credit_card` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_dwelltime
DROP TABLE IF EXISTS `pnr_dwelltime`;
CREATE TABLE IF NOT EXISTS `pnr_dwelltime` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `dwell_id` bigint(20) NOT NULL,
  PRIMARY KEY (`pnr_id`,`dwell_id`),
  KEY `FK_86h3fnwgplgi2v2byoxlsr9lt` (`dwell_id`),
  CONSTRAINT `FK_86h3fnwgplgi2v2byoxlsr9lt` FOREIGN KEY (`dwell_id`) REFERENCES `dwell_time` (`id`),
  CONSTRAINT `FK_cjacc9ty7mekvly7sxbid632s` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_email
DROP TABLE IF EXISTS `pnr_email`;
CREATE TABLE IF NOT EXISTS `pnr_email` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `email_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`email_id`),
  KEY `FK_k3omxce5qlu134esf0qje8e8w` (`email_id`),
  CONSTRAINT `FK_13lvgdxvcm6p7r72rqbqhlhce` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_k3omxce5qlu134esf0qje8e8w` FOREIGN KEY (`email_id`) REFERENCES `email` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_flight
DROP TABLE IF EXISTS `pnr_flight`;
CREATE TABLE IF NOT EXISTS `pnr_flight` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`flight_id`),
  KEY `FK_1ab1ys8697pfiujp3tbjhj797` (`flight_id`),
  CONSTRAINT `FK_1ab1ys8697pfiujp3tbjhj797` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FK_7isyqrxuikafw77u7uashqwmh` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_frequent_flyer
DROP TABLE IF EXISTS `pnr_frequent_flyer`;
CREATE TABLE IF NOT EXISTS `pnr_frequent_flyer` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `ff_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`ff_id`),
  KEY `FK_5b6iaxsq23pk7v2k7322oqjik` (`ff_id`),
  CONSTRAINT `FK_5b6iaxsq23pk7v2k7322oqjik` FOREIGN KEY (`ff_id`) REFERENCES `frequent_flyer` (`id`),
  CONSTRAINT `FK_t18wdb651g4s5q0pf8ihtti5p` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_passenger
DROP TABLE IF EXISTS `pnr_passenger`;
CREATE TABLE IF NOT EXISTS `pnr_passenger` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`passenger_id`),
  KEY `FK_8srfwvfronky60cuw7j2p28jd` (`passenger_id`),
  CONSTRAINT `FK_8srfwvfronky60cuw7j2p28jd` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`),
  CONSTRAINT `FK_by1ox9lyap5vntr2by6mcy04e` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.pnr_phone
DROP TABLE IF EXISTS `pnr_phone`;
CREATE TABLE IF NOT EXISTS `pnr_phone` (
  `pnr_id` bigint(20) unsigned NOT NULL,
  `phone_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`pnr_id`,`phone_id`),
  KEY `FK_l5fyrupnqj312yilqvq2b9070` (`phone_id`),
  CONSTRAINT `FK_be3ciwpbxpok8pei4ubgpg8dm` FOREIGN KEY (`pnr_id`) REFERENCES `pnr` (`id`),
  CONSTRAINT `FK_l5fyrupnqj312yilqvq2b9070` FOREIGN KEY (`phone_id`) REFERENCES `phone` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.reporting_party
DROP TABLE IF EXISTS `reporting_party`;
CREATE TABLE IF NOT EXISTS `reporting_party` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `fax` varchar(255) DEFAULT NULL,
  `party_name` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nulctij18gdotl4jpallwaef2` (`party_name`,`telephone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.role
DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.rule
DROP TABLE IF EXISTS `rule`;
CREATE TABLE IF NOT EXISTS `rule` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `RULE_CRITERIA` varchar(1024) DEFAULT NULL,
  `RULE_DRL` varchar(4000) DEFAULT NULL,
  `RULE_INDX` int(11) DEFAULT NULL,
  `KB_REF` bigint(20) unsigned DEFAULT NULL,
  `UDR_RULE_REF` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fogppxa36uvmlcl2hw0s6i637` (`KB_REF`),
  KEY `FK_ejsrmyuiqlcxswsoxc8oppiol` (`UDR_RULE_REF`),
  CONSTRAINT `FK_ejsrmyuiqlcxswsoxc8oppiol` FOREIGN KEY (`UDR_RULE_REF`) REFERENCES `udr_rule` (`id`),
  CONSTRAINT `FK_fogppxa36uvmlcl2hw0s6i637` FOREIGN KEY (`KB_REF`) REFERENCES `knowledge_base` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.rule_meta
DROP TABLE IF EXISTS `rule_meta`;
CREATE TABLE IF NOT EXISTS `rule_meta` (
  `ID` bigint(20) NOT NULL,
  `DESCRIPTION` varchar(1024) DEFAULT NULL,
  `ENABLE_FLAG` varchar(1) NOT NULL,
  `END_DT` datetime DEFAULT NULL,
  `HIT_SHARE_FLAG` varchar(1) NOT NULL,
  `HIGH_PRIORITY_FLAG` varchar(1) NOT NULL,
  `START_DT` datetime NOT NULL,
  `TITLE` varchar(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.seat
DROP TABLE IF EXISTS `seat`;
CREATE TABLE IF NOT EXISTS `seat` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `apis` bit(1) NOT NULL,
  `number` varchar(255) NOT NULL,
  `flight_id` bigint(20) unsigned NOT NULL,
  `passenger_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_n9a334x3ys6t4foyg70lq12nm` (`number`,`apis`,`passenger_id`,`flight_id`),
  KEY `FK_bobutwxpl4xf5kbe03u9ksaxm` (`flight_id`),
  KEY `FK_mwvalhy5l0xicmttnkcslac0n` (`passenger_id`),
  CONSTRAINT `FK_bobutwxpl4xf5kbe03u9ksaxm` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`id`),
  CONSTRAINT `FK_mwvalhy5l0xicmttnkcslac0n` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.udr_rule
DROP TABLE IF EXISTS `udr_rule`;
CREATE TABLE IF NOT EXISTS `udr_rule` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `DEL_ID` bigint(20) NOT NULL,
  `DEL_FLAG` varchar(1) NOT NULL,
  `EDIT_DT` datetime NOT NULL,
  `TITLE` varchar(20) NOT NULL,
  `UDR_BLOB` blob,
  `version` bigint(20) DEFAULT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `EDITED_BY` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UDR_UNIQUE_AUTHOR_TITLE` (`AUTHOR`,`TITLE`,`DEL_ID`),
  KEY `FK_p893uc1skp174w2li4thi4n12` (`EDITED_BY`),
  CONSTRAINT `FK_cm9edxbcmiemg87iacara4ff0` FOREIGN KEY (`AUTHOR`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_p893uc1skp174w2li4thi4n12` FOREIGN KEY (`EDITED_BY`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` varchar(255) NOT NULL,
  `active` int(11) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.user_query
DROP TABLE IF EXISTS `user_query`;
CREATE TABLE IF NOT EXISTS `user_query` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_dt` datetime NOT NULL,
  `deleted_dt` datetime DEFAULT NULL,
  `query_description` varchar(100) DEFAULT NULL,
  `query_text` longtext NOT NULL,
  `query_title` varchar(20) NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `deleted_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gvotjllimj5wbulqhf3t4e832` (`created_by`),
  KEY `FK_a5apm3u1vsqiheqhnxt1bceft` (`deleted_by`),
  CONSTRAINT `FK_a5apm3u1vsqiheqhnxt1bceft` FOREIGN KEY (`deleted_by`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_gvotjllimj5wbulqhf3t4e832` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.user_role
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_it77eq964jhfqtu54081ebtio` (`role_id`),
  CONSTRAINT `FK_apcc8lxk2xnug8377fatvbn04` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FK_it77eq964jhfqtu54081ebtio` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.watch_list
DROP TABLE IF EXISTS `watch_list`;
CREATE TABLE IF NOT EXISTS `watch_list` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `WL_EDIT_DTTM` datetime NOT NULL,
  `WL_ENTITY` varchar(20) NOT NULL,
  `WL_NAME` varchar(64) NOT NULL,
  `WL_EDITOR` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `WL_UNIQUE_NAME` (`WL_NAME`),
  KEY `FK_ny0lcuj5tmbps38e6vpppwhb5` (`WL_EDITOR`),
  CONSTRAINT `FK_ny0lcuj5tmbps38e6vpppwhb5` FOREIGN KEY (`WL_EDITOR`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.white_list
DROP TABLE IF EXISTS `white_list`;
CREATE TABLE IF NOT EXISTS `white_list` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(20) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL,
  `citizenship_country` varchar(255) DEFAULT NULL,
  `DEL_FLAG` varchar(1) NOT NULL,
  `dob` date DEFAULT NULL,
  `document_number` varchar(255) NOT NULL,
  `document_type` varchar(3) NOT NULL,
  `expiration_date` date DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(2) DEFAULT NULL,
  `issuance_country` varchar(255) DEFAULT NULL,
  `issuance_date` date DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `residency_country` varchar(255) DEFAULT NULL,
  `editor` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dmmaochjj2kgievy614cq0bvu` (`editor`),
  CONSTRAINT `FK_dmmaochjj2kgievy614cq0bvu` FOREIGN KEY (`editor`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table gtas.wl_item
DROP TABLE IF EXISTS `wl_item`;
CREATE TABLE IF NOT EXISTS `wl_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ITM_DATA` varchar(1024) NOT NULL,
  `ITM_RL_DATA` varchar(1024) DEFAULT NULL,
  `ITM_WL_REF` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gusix9e4ueq476l9on27yh1dk` (`ITM_WL_REF`),
  CONSTRAINT `FK_gusix9e4ueq476l9on27yh1dk` FOREIGN KEY (`ITM_WL_REF`) REFERENCES `watch_list` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for view gtas.ytd_airport_stats
DROP VIEW IF EXISTS `ytd_airport_stats`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `ytd_airport_stats` (
	`ID` BIGINT(20) UNSIGNED NOT NULL,
	`AIRPORT` VARCHAR(3) NULL COLLATE 'utf8_general_ci',
	`FLIGHTS` BIGINT(21) NOT NULL,
	`RULEHITS` DECIMAL(32,0) NULL,
	`WATCHLISTHITS` DECIMAL(32,0) NULL
) ENGINE=MyISAM;

-- Dumping structure for view gtas.ytd_rules
DROP VIEW IF EXISTS `ytd_rules`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `ytd_rules` (
	`id` BIGINT(20) UNSIGNED NOT NULL,
	`RuleName` VARCHAR(20) NOT NULL COLLATE 'utf8_general_ci',
	`RuleHits` BIGINT(21) NOT NULL,
	`CreatedBy` VARCHAR(255) NOT NULL COLLATE 'utf8_general_ci',
	`CreatedOn` VARCHAR(40) NULL COLLATE 'utf8mb4_general_ci',
	`LastUpdatedBy` VARCHAR(255) NOT NULL COLLATE 'utf8_general_ci',
	`LastEditedOn` VARCHAR(40) NULL COLLATE 'utf8mb4_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view gtas.ytd_rule_hit_counts
DROP VIEW IF EXISTS `ytd_rule_hit_counts`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `ytd_rule_hit_counts` (
	`ruleid` BIGINT(20) UNSIGNED NOT NULL,
	`ruleref` BIGINT(20) UNSIGNED NOT NULL,
	`hits` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view gtas.daily_apis_counts
DROP VIEW IF EXISTS `daily_apis_counts`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `daily_apis_counts`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `daily_apis_counts` AS select `message`.`id` AS `id`,cast((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour) as date) AS `day`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 0),1,NULL)) AS `1am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 1),1,NULL)) AS `2am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 2),1,NULL)) AS `3am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 3),1,NULL)) AS `4am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 4),1,NULL)) AS `5am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 5),1,NULL)) AS `6am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 6),1,NULL)) AS `7am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 7),1,NULL)) AS `8am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 8),1,NULL)) AS `9am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 9),1,NULL)) AS `10am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 10),1,NULL)) AS `11am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 11),1,NULL)) AS `12pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 12),1,NULL)) AS `1pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 13),1,NULL)) AS `2pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 14),1,NULL)) AS `3pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 15),1,NULL)) AS `4pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 16),1,NULL)) AS `5pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 17),1,NULL)) AS `6pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 18),1,NULL)) AS `7pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 19),1,NULL)) AS `8pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 20),1,NULL)) AS `9pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 21),1,NULL)) AS `10pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 22),1,NULL)) AS `11pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 23),1,NULL)) AS `12am` from `message` where ((`message`.`create_date` >= date_format((curdate() - 1),'%Y-%m-%d %T')) and (`message`.`create_date` < date_format((curdate() + 1),'%Y-%m-%d %T')) and (`message`.`raw` like '%PAXLST%')) group by cast((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour) as date);

-- Dumping structure for view gtas.daily_pnr_counts
DROP VIEW IF EXISTS `daily_pnr_counts`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `daily_pnr_counts`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `daily_pnr_counts` AS select `message`.`id` AS `id`,cast((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour) as date) AS `day`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 0),1,NULL)) AS `1am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 1),1,NULL)) AS `2am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 2),1,NULL)) AS `3am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 3),1,NULL)) AS `4am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 4),1,NULL)) AS `5am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 5),1,NULL)) AS `6am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 6),1,NULL)) AS `7am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 7),1,NULL)) AS `8am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 8),1,NULL)) AS `9am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 9),1,NULL)) AS `10am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 10),1,NULL)) AS `11am`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 11),1,NULL)) AS `12pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 12),1,NULL)) AS `1pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 13),1,NULL)) AS `2pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 14),1,NULL)) AS `3pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 15),1,NULL)) AS `4pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 16),1,NULL)) AS `5pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 17),1,NULL)) AS `6pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 18),1,NULL)) AS `7pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 19),1,NULL)) AS `8pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 20),1,NULL)) AS `9pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 21),1,NULL)) AS `10pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 22),1,NULL)) AS `11pm`,count(if((hour((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour)) = 23),1,NULL)) AS `12am` from `message` where ((`message`.`create_date` >= date_format((curdate() - 1),'%Y-%m-%d %T')) and (`message`.`create_date` < date_format((curdate() + 1),'%Y-%m-%d %T')) and (`message`.`raw` like '%PNRGOV%')) group by cast((`message`.`create_date` + interval (select `app_configuration`.`val` from `app_configuration` where (`app_configuration`.`opt` like '%HOURLY_ADJ%')) hour) as date);

-- Dumping structure for view gtas.ytd_airport_stats
DROP VIEW IF EXISTS `ytd_airport_stats`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `ytd_airport_stats`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `ytd_airport_stats` AS select `a`.`id` AS `ID`,`a`.`iata` AS `AIRPORT`,count(0) AS `FLIGHTS`,sum(`f`.`rule_hit_count`) AS `RULEHITS`,sum(`f`.`list_hit_count`) AS `WATCHLISTHITS` from (`flight` `f` join `airport` `a`) where ((`a`.`country` = 'USA') and ((trim(`a`.`iata`) = trim(`f`.`origin`)) or (trim(`a`.`iata`) = trim(`f`.`destination`)))) group by `a`.`iata` order by count(0) desc limit 5;

-- Dumping structure for view gtas.ytd_rules
DROP VIEW IF EXISTS `ytd_rules`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `ytd_rules`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `ytd_rules` AS select `udr`.`id` AS `id`,`rm`.`TITLE` AS `RuleName`,`r`.`hits` AS `RuleHits`,`udr`.`AUTHOR` AS `CreatedBy`,date_format(`rm`.`START_DT`,'%d %b %Y') AS `CreatedOn`,`udr`.`EDITED_BY` AS `LastUpdatedBy`,date_format(`udr`.`EDIT_DT`,'%d %b %Y') AS `LastEditedOn` from ((`udr_rule` `udr` join `rule_meta` `rm`) join `ytd_rule_hit_counts` `r`) where ((`udr`.`id` = `r`.`ruleref`) and (`rm`.`ID` = `r`.`ruleid`));

-- Dumping structure for view gtas.ytd_rule_hit_counts
DROP VIEW IF EXISTS `ytd_rule_hit_counts`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `ytd_rule_hit_counts`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `ytd_rule_hit_counts` AS select `r`.`id` AS `ruleid`,`r`.`UDR_RULE_REF` AS `ruleref`,count(`hd`.`id`) AS `hits` from (`rule` `r` join `hit_detail` `hd` on((`hd`.`rule_id` = `r`.`id`))) group by `r`.`id`;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

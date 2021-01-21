CREATE TABLE `pnr_saved_segment` (
    `Pnr_id` bigint(20) unsigned NOT NULL,
    `savedSegments_id` bigint(20) unsigned NOT NULL,
    UNIQUE KEY `UK_9so91cbdda7v2kyfav9sln9q6` (`savedSegments_id`),
    KEY `FKntem6gytcwbnjian99yptqhap` (`Pnr_id`),
    CONSTRAINT `FK25sh9hujqtrvasdr64afvqije` FOREIGN KEY (`savedSegments_id`) REFERENCES `saved_segment` (`id`),
    CONSTRAINT `FKntem6gytcwbnjian99yptqhap` FOREIGN KEY (`Pnr_id`) REFERENCES `pnr` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1


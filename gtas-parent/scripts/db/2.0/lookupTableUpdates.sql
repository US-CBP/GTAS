

use `gtas`;
alter table `airport`
	ADD COLUMN IF NOT EXISTS `created_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `created_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `archived` bit(1) DEFAULT NULL;


alter table `carrier`
	ADD COLUMN IF NOT EXISTS `created_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `created_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `archived` bit(1) DEFAULT NULL;


alter table `country`
	ADD COLUMN IF NOT EXISTS `created_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `created_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `archived` bit(1) DEFAULT NULL;



alter table `credit_card_type`
	ADD COLUMN IF NOT EXISTS `created_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `created_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_at` datetime DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `updated_by` varchar(20) DEFAULT NULL,
	ADD COLUMN IF NOT EXISTS `archived` bit(1) DEFAULT NULL;

update `airport` set `updated_at` = NOW() where 1=1;
update `carrier` set `updated_at` = NOW() where 1=1;
update `country` set `updated_at` = NOW() where 1=1;
update `credit_card_type` set `updated_at` = NOW() where 1=1;
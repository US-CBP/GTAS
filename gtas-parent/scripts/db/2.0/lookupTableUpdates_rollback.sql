 use gtas;
 ALTER TABLE `airport`
	DROP COLUMN  IF EXISTS `created_at`,
	DROP COLUMN IF EXISTS `created_by`,
	DROP COLUMN IF EXISTS `updated_at`,
	DROP COLUMN IF EXISTS `updated_by`,
	DROP COLUMN IF EXISTS `archived`;

 ALTER TABLE `carrier`
	DROP COLUMN  IF EXISTS `created_at`,
	DROP COLUMN IF EXISTS `created_by`,
	DROP COLUMN IF EXISTS `updated_at`,
	DROP COLUMN IF EXISTS `updated_by`,
	DROP COLUMN IF EXISTS `archived`;

 ALTER TABLE `country`
	DROP COLUMN  IF EXISTS `created_at`,
	DROP COLUMN IF EXISTS `created_by`,
	DROP COLUMN IF EXISTS `updated_at`,
	DROP COLUMN IF EXISTS `updated_by`,
	DROP COLUMN IF EXISTS `archived`;

 ALTER TABLE `credit_card_type`
	DROP COLUMN  IF EXISTS `created_at`,
	DROP COLUMN IF EXISTS `created_by`,
	DROP COLUMN IF EXISTS `updated_at`,
	DROP COLUMN IF EXISTS `updated_by`,
	DROP COLUMN IF EXISTS `archived`;

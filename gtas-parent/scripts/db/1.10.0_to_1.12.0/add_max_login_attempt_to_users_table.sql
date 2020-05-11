ALTER TABLE gtas.`user`
    ADD COLUMN IF NOT EXISTS `reset_token` VARCHAR(255) NULL DEFAULT NULL AFTER `password`,
    ADD COLUMN IF NOT EXISTS `consecutgive_failed_login_attempts` INT(11) NULL DEFAULT NULL AFTER `active`;
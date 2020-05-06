ALTER TABLE gtas.`user`
    DROP COLUMN IF EXISTS `reset_token`,
    DROP COLUMN IF EXISTS `consecutive_failed_login_attempts`;

ALTER TABLE gtas.`booking_detail`
    ADD COLUMN IF NOT EXISTS `local_eta` datetime NULL DEFAULT NULL AFTER `eta`,
    ADD COLUMN IF NOT EXISTS `local_etd` datetime NULL DEFAULT NULL AFTER `etd`;
ALTER TABLE gtas.`mutable_flight_details`
    ADD COLUMN IF NOT EXISTS `full_utc_etd_timestamp` datetime NULL DEFAULT NULL AFTER `full_etd_timestamp`,
    ADD COLUMN IF NOT EXISTS `full_utc_eta_timestamp` datetime NULL DEFAULT NULL AFTER `full_eta_timestamp`;
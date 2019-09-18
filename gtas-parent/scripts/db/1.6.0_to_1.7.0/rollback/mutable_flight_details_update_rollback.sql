ALTER TABLE gtas.`mutable_flight_details`
    DROP COLUMN IF EXISTS `full_utc_eta_timestamp`,
    DROP COLUMN IF EXISTS `full_utc_etd_timestamp`;

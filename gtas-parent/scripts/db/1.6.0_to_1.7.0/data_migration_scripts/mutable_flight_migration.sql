UPDATE gtas.mutable_flight_details mfd
SET mfd.full_utc_eta_timestamp = mfd.full_eta_timestamp
WHERE mfd.flight_id = mfd.flight_id
  AND mfd.full_utc_eta_timestamp IS NULL;

UPDATE gtas.mutable_flight_details mfd
SET mfd.full_utc_etd_timestamp = mfd.full_etd_timestamp
WHERE mfd.flight_id = mfd.flight_id
  AND mfd.full_utc_etd_timestamp IS NULL;
-- Adds pnr_ref_number colum to the PASSENGER_TRIP_DETAILS table
ALTER TABLE `passenger_trip_details`
	ADD COLUMN IF NOT EXISTS `pnr_ref_number` VARCHAR(255) NULL DEFAULT NULL AFTER `ptd_id`;
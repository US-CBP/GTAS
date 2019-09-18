-- Restore  PASSENGER_TRIP_DETAILS table
ALTER TABLE `passenger_trip_details`
	DROP COLUMN IF EXISTS `pnr_ref_number`;
  
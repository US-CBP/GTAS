
-- Add id_tag to the FLIGHT table
ALTER TABLE `flight`
	ADD COLUMN IF NOT EXISTS `id_tag` VARCHAR(255) NULL DEFAULT NULL AFTER `full_flight_number`;
CREATE OR REPLACE VIEW flight_countdown_view AS
SELECT id as 'fcdv_flight_id',
       (CASE WHEN flight.direction= 'I'
                 THEN mutable_flight_details.full_utc_eta_timestamp
             WHEN flight.direction= 'O'
                 THEN mutable_flight_details.full_utc_etd_timestamp
             ELSE mutable_flight_details.full_utc_eta_timestamp END )
          AS 'fcdv_countdown_timer'
from flight, mutable_flight_details where flight_id = id;

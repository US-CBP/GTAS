select c.*,
	   f.`id` "flight.id",
       fd.`eta_date` "flight.eta_date", -- 
       f.`etd_date` "flight.etd_date", 
       f.`etd_date` "flight.flight_date", 
       f.`flight_number` "flight.flight_number", 
       f.`full_flight_number` "flight.full_flight_number", 
       f.`origin` "flight.origin", 
       f.`origin_country` "flight.origin_country",
              f.`direction` "flight.direction", -- 
       seat.`number` "passenger.seat_number",
       
       p.`id` "passenger.paxid",
       pd.`pd_age` "passenger.age",
       pd.`pd_nationality` "passenger.nationality",
       td.`debark_country` "passenger.debark_country", --
       td.`debarkation` "passenger.debarkation",
       debark_ar.longitude "passenger.debarkation.lon", --
       debark_ar.latitude "passenger.debarkation.lat", --
       embark_ar.longitude "passenger.embarkation.lon", --
       embark_ar.latitude "passenger.embarkation.lat", --
       pd.`dob` "passenger.dob",
       td.`embark_country` "passenger.embark_country",
       td.`embarkation` "passenger.embarkation",
       pd.`pd_first_name` "passenger.first_name",
       pd.`pd_last_name` "passenger.last_name",
       pd.`pd_gender` "passenger.gender",
       pd.`pd_middle_name` "passenger.middle_name",
       td.`days_visa_valid` "passenger.days_visa_valid",
       pd.`pd_passenger_type` "passenger.passenger_type",
       td.`ref_number` "passenger.ref_number",
       pd.`pd_residency_country` "passenger.residency_country",
       pd.`pd_suffix` "passenger.suffix",
       td.`travel_frequency` "passenger.travel_frequency"
        from hits_summary c
LEFT JOIN flight f 
              ON (c.flight_id = f.id )
left join `mutable_flight_details` fd
		on (f.id = fd.flight_id)
		       LEFT JOIN passenger p
              ON (c.hs_passenger_id = p.id)
       JOIN `passenger_details` pd
		ON (p.id = pd.pd_passenger_id)
	JOIN `passenger_trip_details` td
		ON (td.ptd_id = p.id)
       left join `airport` debark_ar
       		  on (td.debarkation is not null and td.debarkation != '' and debark_ar.iata=td.debarkation)
        left join `airport` embark_ar
       		  on (td.embarkation is not null and td.embarkation != '' and embark_ar.iata=td.embarkation)
       left join `seat` seat 
              on seat.flight_id=f.ID and seat.passenger_id=p.id
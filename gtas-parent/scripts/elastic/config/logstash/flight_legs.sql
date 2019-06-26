select  
	(case when pax_b.pax_id is not null then concat(pax_b.pax_id,'+',bd.flight_id) else concat(flight_p.`passenger_id`,'+',flight_p.`flight_id`) end) "id",   leg.id "leg.id", leg.leg_number,
    (case when leg.`flight_id` is not null then true else false end) "is_flight_prime",
    (case when leg.`flight_id` is null then booking_detail.full_flight_number else prime_flight.`full_flight_number` end) "flight_number",
    (case when leg.`flight_id` is null then booking_detail.origin else prime_flight.`origin` end) "origin",
		(case when leg.`flight_id` is null then booking_detail.destination else prime_flight.`destination` end) "destination",
		(case when leg.`flight_id` is null then booking_detail.etd else mutable_f_detail.`full_etd_timestamp` end) "etd",
		(case when leg.`flight_id` is null then booking_detail.eta else mutable_f_detail.`full_eta_timestamp` end) "eta",
		(case when leg.`flight_id` is null then booking_detail.origin_country else prime_flight.`origin_country` end) "origin_country",
		(case when leg.`flight_id` is null then booking_detail.destination_country else prime_flight.`destination_country` end) "destination_country",
		leg.message_id,
		(case when leg.`flight_id` is null then booking_d_origin_ar.longitude else prime_f_origin_ar.longitude end) "flight_leg.origin.lon",
    (case when leg.`flight_id` is null then booking_d_origin_ar.latitude  else  prime_f_origin_ar.latitude end) "flight_leg.origin.lat",
    (case when leg.`flight_id` is null then booking_d_destination_ar.longitude else prime_f_destination_ar.longitude end) "flight_leg.destination.lon",
    (case when leg.`flight_id` is null then booking_d_destination_ar.latitude else prime_f_destination_ar.latitude end) "flight_leg.destination.lat",
    
    (case when leg.`flight_id` is null then concat(booking_d_origin_ar.latitude, ',' ,booking_d_origin_ar.longitude)  else  concat(prime_f_origin_ar.latitude, ',' , prime_f_origin_ar.latitude) end) "flight_leg.origin.coordinates",

       (case when leg.`flight_id` is null then concat(booking_d_destination_ar.latitude, ',' ,booking_d_destination_ar.longitude) else concat(prime_f_destination_ar.longitude, ',',prime_f_destination_ar.latitude) end) "flight_leg.destination.coordinates"
    
    from flight_leg leg
		
	left join flight prime_flight
	    on (prime_flight.id= leg.flight_id)   
	 
	left join flight_leg bd 
		on (bd.message_id = leg.message_id and bd.flight_id is not null)
	
	left join booking_detail booking_detail
		on(leg.`bookingDetail_id` = booking_detail.id)
	
	left join `flight_passenger` flight_p
		on (flight_p.`flight_id` = prime_flight.id)
	
	left join mutable_flight_details mutable_f_detail
        on (mutable_f_detail.flight_id = prime_flight.id)
	
	left join pax_booking pax_b
				on (pax_b.booking_detail_id = booking_detail.id)
 	
 	left join `airport` prime_f_origin_ar 
		on (prime_f_origin_ar.iata=prime_flight.origin and prime_f_origin_ar.country=prime_flight.origin_country)

    left join `airport` booking_d_origin_ar 
		on (booking_d_origin_ar.iata=booking_detail.origin and booking_d_origin_ar.country=booking_detail.origin_country)
	
    left join `airport` prime_f_destination_ar 
		on (prime_f_destination_ar.iata=prime_flight.destination and prime_f_destination_ar.country=prime_flight.destination_country)

    left join `airport` booking_d_destination_ar 
		on (booking_d_destination_ar.iata=booking_detail.destination and booking_d_destination_ar.country=booking_detail.destination_country)
       
	order by id, leg.leg_number, bd.message_id
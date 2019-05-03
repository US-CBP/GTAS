SELECT c.*, 
	   f.`id` "flight.id",
       fd.`eta_date` "flight.eta_date", 
       f.`etd_date` "flight.etd_date", 
       f.`etd_date` "flight.flight_date", 
       f.`flight_number` "flight.flight_number", 
       f.`full_flight_number` "flight.full_flight_number", 
       f.`origin` "flight.origin", 
       f.`origin_country` "flight.origin_country", 
--        f.`rule_hit_count` "flight.rule_hit_count", 
       -- f.`passenger_count` "flight.passenger_count",
       f.`direction` "flight.direction",
       seat.`number` "passenger.seat_number",
       
       p.`id` "passenger.paxid",
       pd.`pd_age` "passenger.age",
       pd.`pd_nationality` "passenger.nationality",
       td.`debark_country` "passenger.debark_country",
       td.`debarkation` "passenger.debarkation",
       debark_ar.longitude "passenger.debarkation.lon",
       debark_ar.latitude "passenger.debarkation.lat",
       embark_ar.longitude "passenger.embarkation.lon",
       embark_ar.latitude "passenger.embarkation.lat",
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
       td.`travel_frequency` "passenger.travel_frequency",
       -- p.`watchlistCheckTimestamp` "passenger.watchlistCheckTimestamp",
       
       hd.`id` "hit_disposition.id",
       hd.`status` "hit_disposition.status",
	   hd.created_at "hit_disposition.created_at",
	   hd.created_by "hit_disposition.created_by",
	   hd.updated_at "hit_disposition.updated_at",
	   hd.updated_by "hit_disposition.updated_by",
	   hd.description "hit_disposition.description",
	   hd.hit_id "hit_disposition.hit_id",
	   hd.valid "hit_disposition.valid",
	   hd.rule_cat_id "hit_disposition.rule_cat_id",
	   
	   hdc.id "hit_disposition.comment.id",
	   hdc.created_at "hit_disposition.comment.created_at",
	   hdc.created_by "hit_disposition.comment.created_by",
	   hdc.updated_at "hit_disposition.comment.updated_at",
	   hdc.updated_by "hit_disposition.comment.updated_by",
	   hdc.comments "hit_disposition.comment.comments",
	   hdc.hit_id "hit_disposition.comment.hit_id",
	   hdc.`hit_disp_id` "hit_disposition.comment.hit_disp_id",
	   
	   h_summary.`created_date` "h_summary_created_date",
	   h_summary.`hit_type` "h_summary_hit_type",
	   h_summary.`id` "h_summary.id",
	   h_summary.`rule_hit_count` "h_summary_rule_hit_count",
	   h_summary.`wl_hit_count` "h_summary_wl_hit_count",
	   h_summary.`flight_id` "h_summary_flight_id",
	   h_summary.`passenger_id` "h_summary_passenger_id",
	   
	   h_detail.`cond_text` "h_detail_cond_text",
	   h_detail.`created_date` "h_detail_created_date",
	   h_detail.`description` "h_detail_description",
	   h_detail.`hit_type` "h_detail_hit_type",
	   h_detail.`hits_summary_id` "h_detail_hits_summary_id",
	   h_detail.`id` "h_detail.id",
	   h_detail.`rule_id` "h_detail_rule_id",
	   h_detail.`title` "h_detail_title",

          	pax_watchlist.`id` "pax_watchlist.id",
	pax_watchlist.`last_run_timestamp` "pax_watchlist.last_run_timestamp",
	pax_watchlist.`passenger_id` "pax_watchlist.passenger_id",
	pax_watchlist.`percent_match` "pax_watchlist.percent_match",
	pax_watchlist.`verified_status` "pax_watchlist.verified_status",
	pax_watchlist.`watchlist_item_id` "pax_watchlist.watchlist_item_id"
	   	   
FROM   cases c 
       LEFT JOIN flight f 
              ON (c.flightid = f.id )
       left join `mutable_flight_details` fd
		on (f.id = fd.flight_id)
       LEFT JOIN passenger p
              ON (c.paxId = p.id)
       JOIN `passenger_details` pd
		ON (p.id = pd.pd_passenger_id)
	JOIN `passenger_trip_details` td
		ON (td.ptd_id = p.id)
       LEFT JOIN `hits_disposition` hd
       		  ON (hd.`case_id` = c.`id`)
       LEFT JOIN `hits_disposition_comments` hdc
       		  ON (hd.`id` = hdc.`hit_disp_id`)
       left join `hits_summary` h_summary
       		  on (c.`paxId` = h_summary.`passenger_id` and c.`flightId` = h_summary.`flight_id`)
       left join `hit_detail` h_detail
       		  on (h_summary.`id` = h_detail.`hits_summary_id`)
       left join `airport` debark_ar
       		  on (debark_ar.iata=td.debarkation and debark_ar.country=td.debark_country)
        left join `airport` embark_ar
       		  on (embark_ar.iata=td.embarkation and embark_ar.country=td.embark_country)
       left join `pax_watchlist_link` pax_watchlist
		on (pax_watchlist.passenger_id=p.id)
       left join `seat` seat 
              on seat.flight_id=f.ID and seat.passenger_id=p.id
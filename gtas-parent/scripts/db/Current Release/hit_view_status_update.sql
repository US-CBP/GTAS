     alter table hit_view_status 
         add column direction varchar(1);
 
     alter table hit_view_status 
         add column full_utc_eta_timestamp datetime;
 
     alter table hit_view_status 
         add column full_utc_etd_timestamp datetime;
         
       create index hvs_query_eta on hit_view_status(full_utc_eta_timestamp) ;
       create index hvs_query_etd on hit_view_status(full_utc_etd_timestamp) ;
       
       
       update hit_view_status hvs 
	join passenger p on hv_passenger_id  = p.id 
	join flight_passenger fp on fp.passenger_id  = p.id 
	join flight f on fp.flight_id = f.id 
	join mutable_flight_details mfd on f.id = mfd.flight_id 
		set hvs.direction = f.direction,
	hvs.full_utc_eta_timestamp =  mfd.full_utc_eta_timestamp,
	hvs.full_utc_etd_timestamp =  mfd.full_utc_etd_timestamp;
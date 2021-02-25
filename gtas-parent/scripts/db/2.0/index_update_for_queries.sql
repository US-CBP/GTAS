create index booking_detail_destination_index
    on booking_detail (destination);

create index booking_detail_origin_index
    on booking_detail (origin);

create index type_number_index
    on credit_card (card_type, number);

-- auto-generated definition
create index doc_num_index
    on  (document_number);

-- auto-generated definition
create index dwell_arrival_index
    on dwell_time (arrival_airport);

-- auto-generated definition
create index domain_address_index
    on email (domain, address);


-- auto-generated definition
create index passenger_query
    on passenger_details (dob, pd_gender, pd_last_name, pd_first_name);

-- auto-generated definition
create index phone_number_index
    on phone (number, flight_id);

    
create index passenger_status 
	on data_retention_status (drs_passenger_id,drs_masked_pnr,drs_masked_apis,drs_has_pnr_message,drs_has_apis_message,drs_deleted_PNR,drs_deleted_apis);



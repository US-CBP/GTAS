CREATE VIEW neo4j_vw AS
    SELECT pit.idTag,
           pd.pd_first_name       as first_name,
           pd.pd_middle_name      as middle_name,
           pd.pd_last_name        as last_name,
           pd.pd_nationality      as citizenship_country,
           pd.dob,
           pd.pd_gender           as gender,
           pd.pd_title               title,
           pd.pd_suffix              suffix,
           a.line1,
           a.line2,
           a.line3,
           a.city,
           a.state,
           a.postal_code,
           a.country,
           f.carrier,
           f.direction,
           f.destination,
           f.destination_country,
           mfd.eta_date,
           f.etd_date,
           f.flight_number,
           f.full_flight_number,
           f.origin,
           f.origin_country,
           fpc.fp_count           as passenger_count,
           fhw.fhw_hit_count      as flight_hit_watchlist_count,
           fhr.fhr_hit_count      as flight_hit_rule_count,
           d.document_type,
           d.document_number,
           d.expiration_date,
           d.issuance_country,
           d.issuance_date,
           ph.number              as phone_number,
           cc.number              as credit_card_number,
           cc.card_type,
           cc.expiration,
           cc.account_holder,
           em.address,
           em.domain,
           pit.created_at            pid_tag_creat_date,
           a.id                   as address_id,
           d.id                   as document_id,
           f.id                   as flight_id,
           ph.id                  as phone_id,
           cc.id                  as credit_card_id,
           em.id                  as email_id,
           p.updated_at           as passenger_update_date,
           a.updated_at           as address_update_date,
           em.updated_at          as email_update_date,
           ptd.created_at         as passenger_td_crt_dtm,
           ptd.updated_at         as passenger_td_upd_dtm,
           ptd.debark_country,
           ptd.debarkation,
           ptd.embark_country,
           ptd.embarkation,
           ptd.days_visa_valid,
           ptd.ref_number,
           ptd.travel_frequency,
           pnr.record_locator     as pnr_record_locator,
           mfd.full_eta_timestamp as full_eta_dtm,
           mfd.full_etd_timestamp as full_etd_dtm,
           msg.id                 as gtas_message_id,
           p.id                   as gtas_passenger_id,
           msg.create_date        as gtas_message_create_dtm,
           'PNR'                  as "message_type"

    FROM gtas.message msg
INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
INNER JOIN gtas.pnr pnr ON msg.id = pnr.id
INNER JOIN gtas.pnr_flight pfl ON pnr.id = pfl.pnr_id
INNER JOIN gtas.pnr_passenger ppr ON pnr.id = ppr.pnr_id
INNER JOIN gtas.passenger p ON ppr.passenger_id = p.id
INNER JOIN gtas.passenger_id_tag pit ON pit.pax_id = p.id
INNER JOIN gtas.flight f ON pfl.flight_id = f.id
INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
INNER JOIN gtas.passenger_details pd ON p.id = pd.pd_passenger_id
INNER JOIN gtas.passenger_trip_details ptd ON p.id = ptd.ptd_id
LEFT JOIN  gtas.flight_passenger_count fpc ON fpc.fp_flight_id = f.id
LEFT JOIN gtas.flight_hit_watchlist fhw ON fhw.fhw_flight_id = f.id
LEFT JOIN gtas.flight_hit_rule fhr ON fhr.fhr_flight_id = f.id
LEFT JOIN gtas.document d ON p.id = d.passenger_id
LEFT JOIN gtas.pnr_address pda ON pda.pnr_id = pnr.id
LEFT JOIN gtas.address a ON a.id = pda.address_id
LEFT JOIN gtas.pnr_phone pnp ON pnp.pnr_id = pnr.id
LEFT JOIN gtas.phone ph ON ph.id = pnp.phone_id
LEFT JOIN gtas.pnr_credit_card pnc ON pnc.pnr_id = pnr.id
LEFT JOIN gtas.credit_card cc ON pnc.credit_card_id = cc.id
LEFT JOIN gtas.pnr_email pne ON pne.pnr_id = pnr.id
LEFT JOIN gtas.email em ON pne.email_id = em.id	
WHERE mst.ms_status = 'ANALYZED'
AND pit.idTag IS NOT NULL 
AND pd.pd_first_name IS NOT NULL 
AND pd.pd_last_name IS NOT NULL
AND pd.pd_gender IS NOT NULL 
AND pd.dob IS NOT NULL 
AND f.origin IS NOT NULL 
AND f.destination IS NOT NULL 
AND f.carrier IS NOT NULL 
AND f.flight_number IS NOT NULL 
AND f.etd_date IS NOT NULL
AND f.full_flight_number IS NOT NULL 
AND f.etd_date IS NOT NULL
AND (mst.ms_analyzed_timestamp >= (SELECT last_proc_msg_crt_dtm FROM neo4j_parameters njp WHERE njp.id =1))
AND (mst.ms_message_id > (SELECT last_proc_msg_id FROM neo4j_parameters njp WHERE njp.id =1))

    UNION ALL

    SELECT pit.idTag,
           pd.pd_first_name       as first_name,
           pd.pd_middle_name      as middle_name,
           pd.pd_last_name        as last_name,
           pd.pd_nationality      as citizenship_country,
           pd.dob,
           pd.pd_gender           as gender,
           pd.pd_title               title,
           pd.pd_suffix              suffix,
           null,
           null,
           null,
           null,
           null,
           null,
           null,
           f.carrier,
           f.direction,
           f.destination,
           f.destination_country,
           mfd.eta_date,
           f.etd_date,
           f.flight_number,
           f.full_flight_number,
           f.origin,
           f.origin_country,
           fpc.fp_count           as passenger_count,
           fhw.fhw_hit_count      as flight_hit_watchlist_count,
           fhr.fhr_hit_count      as flight_hit_rule_count,
           d.document_type,
           d.document_number,
           d.expiration_date,
           d.issuance_country,
           d.issuance_date,
           null,
           null,
           null,
           null,
           null,
           null,
           null,
           pit.created_at            pid_tag_creat_date,
           null,
           d.id                   as document_id,
           f.id                   as flight_id,
           null,
           null,
           null,
           p.updated_at           as passenger_update_date,
           null,
           null,
           ptd.created_at         as passenger_td_crt_dtm,
           ptd.updated_at         as passenger_td_upd_dtm,
           ptd.debark_country,
           ptd.debarkation,
           ptd.embark_country,
           ptd.embarkation,
           ptd.days_visa_valid,
           ptd.ref_number,
           ptd.travel_frequency,
           flpx.ref_number        as pnr_record_locator,
           mfd.full_eta_timestamp as full_eta_dtm,
           mfd.full_etd_timestamp as full_etd_dtm,
           msg.id                 as gtas_message_id,
           p.id                   as gtas_passenger_id,
           msg.create_date        as gtas_message_create_dtm,
           'APIS'                 as "message_type"
    FROM gtas.message msg
 INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
 INNER JOIN gtas.apis_message apm ON msg.id = apm.id
 INNER JOIN gtas.apis_message_flight_pax amfx ON apm.id = amfx.apis_message_id
 INNER JOIN gtas.flight_pax flpx ON amfx.flight_pax_id = flpx.id
 INNER JOIN gtas.flight f ON flpx.flight_id= f.id
 INNER JOIN gtas.passenger p ON flpx.passenger_id = p.id
 INNER JOIN gtas.passenger_id_tag pit ON pit.pax_id = p.id
 INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
 INNER JOIN gtas.passenger_details pd ON p.id = pd.pd_passenger_id
 INNER JOIN gtas.passenger_trip_details ptd ON p.id = ptd.ptd_id
 LEFT JOIN  gtas.flight_passenger_count fpc ON fpc.fp_flight_id = f.id
 LEFT JOIN gtas.flight_hit_watchlist fhw ON fhw.fhw_flight_id = f.id
 LEFT JOIN gtas.flight_hit_rule fhr ON fhr.fhr_flight_id = f.id
 LEFT JOIN gtas.document d ON p.id = d.passenger_id
    WHERE mst.ms_status = 'ANALYZED'
      AND pit.idTag IS NOT NULL
      AND pd.pd_first_name IS NOT NULL
      AND pd.pd_last_name IS NOT NULL
      AND pd.pd_gender IS NOT NULL
      AND pd.dob IS NOT NULL
      AND f.origin IS NOT NULL
      AND f.destination IS NOT NULL
      AND f.carrier IS NOT NULL
      AND f.flight_number IS NOT NULL
      AND f.etd_date IS NOT NULL
      AND f.full_flight_number IS NOT NULL
      AND f.etd_date IS NOT NULL
      AND (mst.ms_analyzed_timestamp >= (SELECT last_proc_msg_crt_dtm FROM neo4j_parameters njp WHERE njp.id =1))
      AND (mst.ms_message_id > (SELECT last_proc_msg_id FROM neo4j_parameters njp WHERE njp.id =1))
    ORDER BY gtas_message_id, flight_id, gtas_passenger_id
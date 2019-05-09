
 CREATE VIEW neo4j_hit_vw AS
     SELECT hs.id           as gtas_hit_summary_id,
            hs.created_date as hit_summary_create_date,
            hs.hit_type,
            hs.rule_hit_count,
            hs.wl_hit_count,
            hd.id           as gtas_hit_detail_id,
            hd.description,
            hd.title,
            hd.cond_text,
            hd.rule_id,
            hd.created_date as hit_detail_create_date,
            pit.idTag,
            f.id            as flight_id,
            f.origin,
            f.destination,
            f.carrier,
            f.direction,
            f.origin_country,
            f.destination_country,
            mfd.eta_date,
            f.etd_date,
            f.flight_number,
            f.full_flight_number,
            msg.id          as gtas_message_id,
            p.id            as gtas_passenger_id

     FROM gtas.message msg
INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
INNER JOIN gtas.pnr pnr ON msg.id = pnr.id
INNER JOIN gtas.pnr_flight pfl ON pnr.id = pfl.pnr_id
INNER JOIN gtas.pnr_passenger ppr ON pnr.id = ppr.pnr_id
INNER JOIN gtas.passenger p ON ppr.passenger_id = p.id
INNER JOIN gtas.passenger_id_tag pit ON pit.pax_id = p.id
INNER JOIN gtas.flight f ON pfl.flight_id = f.id
INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
INNER JOIN gtas.hits_summary hs ON p.id = hs.passenger_id 
INNER JOIN gtas.hit_detail hd ON hs.id = hd.hits_summary_id
WHERE mst.ms_status = 'ANALYZED'
AND (mst.ms_analyzed_timestamp >= (SELECT last_proc_msg_crt_dtm FROM neo4j_parameters njp WHERE njp.id =1))
AND (mst.ms_message_id > (SELECT last_proc_msg_id FROM neo4j_parameters njp WHERE njp.id =1))

     UNION

     SELECT hs.id           as gtas_hit_summary_id,
            hs.created_date as hit_summary_create_date,
            hs.hit_type,
            hs.rule_hit_count,
            hs.wl_hit_count,
            hd.id           as gtas_hit_detail_id,
            hd.description,
            hd.title,
            hd.cond_text,
            hd.rule_id,
            hd.created_date as hit_detail_create_date,
            pit.idTag,
            f.id            as flight_id,
            f.origin,
            f.destination,
            f.carrier,
            f.direction,
            f.origin_country,
            f.destination_country,
            mfd.eta_date,
            f.etd_date,
            f.flight_number,
            f.full_flight_number,
            msg.id          as gtas_message_id,
            p.id            as gtas_passenger_id

     FROM gtas.message msg
 INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
 INNER JOIN gtas.apis_message apm ON msg.id = apm.id
 INNER JOIN gtas.apis_message_flight_pax amfx ON apm.id = amfx.apis_message_id
 INNER JOIN gtas.flight_pax flpx ON amfx.flight_pax_id = flpx.id
 INNER JOIN gtas.flight f ON flpx.flight_id= f.id
 INNER JOIN gtas.passenger p ON flpx.passenger_id = p.id
 INNER JOIN gtas.passenger_id_tag pit ON pit.pax_id = p.id
 INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
 INNER JOIN gtas.hits_summary hs ON p.id = hs.passenger_id 
INNER JOIN gtas.hit_detail hd ON hs.id = hd.hits_summary_id
     WHERE mst.ms_status = 'ANALYZED'
AND (mst.ms_analyzed_timestamp >= (SELECT last_proc_msg_crt_dtm FROM neo4j_parameters njp WHERE njp.id =1))
AND (mst.ms_message_id > (SELECT last_proc_msg_id FROM neo4j_parameters njp WHERE njp.id =1))
     ORDER BY gtas_message_id,flight_id,gtas_passenger_id, gtas_hit_detail_id

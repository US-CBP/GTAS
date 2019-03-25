CREATE VIEW neo4j_hit_vw AS
SELECT 
	hs.id as gtas_hit_summary_id,
	hs.created_date as hit_summary_create_date,
	hs.hit_type,
	hs.rule_hit_count,
	hs.wl_hit_count,
	hd.id as gtas_hit_detail_id,
	hd.description,
	hd.title,
	hd.cond_text,
	hd.rule_id,
	hd.created_date as hit_detail_create_date,
	pit.idTag,
	fl.full_flight_number,
	fl.flight_date,
	fl.id as flight_id

FROM gtas.hits_summary hs
LEFT JOIN gtas.hit_detail hd ON hs.id = hd.hits_summary_id
LEFT JOIN gtas.flight fl ON hs.flight_id = fl.id
LEFT JOIN gtas.passenger_id_tag pit ON pit.pax_id = hs.passenger_id 
WHERE hs.created_date > (SELECT last_hit_summary_crt_dtm from gtas.neo4j_parameters  WHERE id=1) 
ORDER BY pit.idTag,hd.id,fl.id 
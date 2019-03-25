SELECT pit.idTag,
	p.first_name,
	p.middle_name,
	p.last_name,
	p.citizenship_country,
	p.dob,
	p.gender,
	p.title,
	p.suffix, 
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
	f.eta_date,
	f.etd_date,
	f.full_flight_number,
	f.origin,
	f.origin_country,
	fpc.fp_count as passenger_count,
	fhw.fhw_hit_count as flight_hit_watchlist_count,
	fhr.fhr_hit_count as flight_hit_rule_count,
	d.document_type,
	d.document_number,
	d.expiration_date,
	d.issuance_country,
	d.issuance_date,
	f.flight_date,
	ph.number as phone_number,
	cc.number as credit_card_number,
	cc.card_type,
	cc.expiration,
	cc.account_holder,
	em.address,
	em.domain,
	pit.created_at pid_tag_creat_date,
	a.id as address_id,
	d.id as document_id,
	f.id as flight_id,
	ph.id as phone_id,
	cc.id as credit_card_id,
	em.id as email_id,
	p.updated_at as passenger_update_date,
  a.updated_at as address_update_date,
	em.updated_at as email_update_date

	
FROM gtas.passenger_id_tag pit
INNER JOIN gtas.passenger p ON pit.pax_id = p.id 
INNER JOIN gtas.flight_passenger fp ON fp.passenger_id = p.id
INNER JOIN gtas.flight f ON f.id = fp.flight_id
LEFT JOIN  gtas.flight_passenger_count fpc ON fpc.fp_flight_id = fp.flight_id
LEFT JOIN gtas.flight_hit_watchlist fhw ON fhw.fhw_flight_id = f.id
LEFT JOIN gtas.flight_hit_rule fhr ON fhr.fhr_flight_id = f.id
LEFT JOIN gtas.document d ON p.id = d.passenger_id
LEFT JOIN gtas.pnr_passenger pp ON pp.passenger_id =  p.id
LEFT JOIN gtas.pnr pnr ON pnr.id = pp.pnr_id
LEFT JOIN gtas.pnr_address pda ON pda.pnr_id = pnr.id
LEFT JOIN gtas.address a ON a.id = pda.address_id
LEFT JOIN gtas.pnr_phone pnp ON pnp.pnr_id = pnr.id
LEFT JOIN gtas.phone ph ON ph.id = pnp.phone_id
LEFT JOIN gtas.pnr_credit_card pnc ON pnc.pnr_id = pnr.id
LEFT JOIN gtas.credit_card cc ON pnc.credit_card_id = cc.id
LEFT JOIN gtas.pnr_email pne ON pne.pnr_id = pnr.id
LEFT JOIN gtas.email em ON pne.email_id = em.id
WHERE pit.idTag IS NOT NULL 
AND p.first_name IS NOT NULL AND p.last_name IS NOT NULL AND p.gender IS NOT NULL AND p.citizenship_country IS NOT NULL AND p.dob IS NOT NULL
AND f.full_flight_number IS NOT NULL AND f.flight_date IS NOT NULL
AND (pit.created_at > (SELECT last_proc_pid_tag_dtm FROM neo4j_parameters njp WHERE njp.id =1)
OR p.updated_at >  (SELECT last_passenger_upd_dtm FROM neo4j_parameters njp WHERE njp.id =1)
OR em.updated_at >  (SELECT last_email_upd_dtm FROM neo4j_parameters njp WHERE njp.id =1)
OR a.updated_at >  (SELECT last_address_upd_dtm FROM neo4j_parameters njp WHERE njp.id =1)
)
ORDER BY pit.created_at asc, pit.idTag, a.id, d.id, f.id, ph.id, cc.id, em.id 
SELECT DISTINCT(flight_id) as id , carrier, full_flight_number, direction,origin,destination, full_utc_etd_timestamp,passenger_count,apis_count, pnr_count
FROM message_delivery_comp_vw mtc
LEFT JOIN 
(
SELECT 
flight_id as flight_id_a,
COUNT(id) as apis_count
FROM message_delivery_comp_vw
WHERE  message_type  = 'APIS'
GROUP BY  flight_id  
) AS a
ON a.flight_id_a = mtc.flight_id
LEFT JOIN
(
SELECT 
flight_id as flight_id_p,
COUNT(id) as pnr_count
FROM message_delivery_comp_vw
WHERE  message_type  = 'PNR'
GROUP BY flight_id  
) AS p
ON p.flight_id_p = mtc.flight_id
ORDER BY full_utc_etd_timestamp DESC
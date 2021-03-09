DROP VIEW IF EXISTS gtas.message_flight_idx_vw;
CREATE VIEW gtas.message_flight_idx_vw AS
(SELECT  
msg.id,
'APIS' as 'message_type', 
msg.create_date as message_create_dtm, 
apm.transmission_date as transmission_date, 
f.id_tag as flight_id_tag, 
f.id as flight_id, 
f.flight_number as flight_number, 
f.full_flight_number as full_flight_number, 
f.carrier as carrier, 
f.direction as direction, 
(CASE
	WHEN f.direction = 'I' THEN f.destination
	WHEN f.direction = 'O' THEN f.origin
END) AS airport,

(CASE
	WHEN f.direction = 'I' THEN 'Inbound'
	WHEN f.direction = 'O' THEN 'Outbound'
END) AS direction_desc,

(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS flight_date,

CAST(year(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED INTEGER ) AS flight_year,

CAST(MONTH(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED INTEGER) AS flight_month,

MONTHNAME(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS flight_month_name,

CAST(week(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS UNSIGNED INTEGER) AS flight_week,

CAST(DAY(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED INTEGER)  AS flight_day,

CAST(HOUR(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED INTEGER)  AS flight_hour,

f.origin as origin,
(select CONCAT(iata,'-',name) from airport where iata = f.origin) AS full_origin,
f.origin_country as origin_country,
f.destination as destination,
(select CONCAT(iata,'-',name) from airport where iata = f.destination) AS full_destination,
f.destination_country as destination_country,
CONCAT(f.origin, ', ',  (select name from country where iso3 =f.origin_country),' - ',f.destination,', ',  (select name from country where iso3 = f.destination_country ) ) as route,
 mfd.full_utc_etd_timestamp as full_utc_etd_timestamp,
 mfd.full_utc_eta_timestamp as full_utc_eta_timestamp,
 CAST(CASE
	WHEN f.direction = 'I' THEN fpc.fp_count
	WHEN f.direction = 'O' THEN 0
END AS UNSIGNED  INTEGER) AS inbound_passenger_count,

CAST(CASE
	WHEN f.direction = 'I' THEN 0
	WHEN f.direction = 'O' THEN fpc.fp_count
END AS UNSIGNED  INTEGER) AS outbound_passenger_count,

 fpc.fp_count as passenger_count,

 TIMESTAMPDIFF(HOUR, apm.transmission_date,
 (CASE
	WHEN f.direction = 'I' THEN mfd.full_utc_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_utc_etd_timestamp
END)) as msg_trans_comp_hrs,

TIMESTAMPDIFF(MINUTE, apm.transmission_date,
 (CASE
	WHEN f.direction = 'I' THEN mfd.full_utc_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_utc_etd_timestamp
END)) as msg_trans_comp_mins

FROM gtas.message msg
INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
INNER JOIN gtas.apis_message apm ON msg.id = apm.id
INNER JOIN gtas.apis_message_flight amf ON apm.id = amf.apis_message_id
INNER JOIN gtas.flight f ON amf.flight_id= f.id
INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
INNER JOIN  gtas.flight_passenger_count fpc ON fpc.fp_flight_id = f.id AND f.id_tag IS NOT NULL
WHERE mst.ms_status NOT IN ('RECEIVED','PARSED','FAILED_PARSING','FAILED_LOADING','FAILED_PRE_PARSE')
AND mst.message_flight_idx_flag IS NULL
AND (f.direction = 'I' OR f.direction = 'O')
ORDER BY msg.id, f.id
LIMIT 5000
)

 UNION ALL

(SELECT
msg.id,
'PNR' as 'message_type',
msg.create_date as message_create_dtm,
pnr.transmission_date as transmission_date,
f.id_tag as flight_id_tag,
f.id as flight_id,
f.flight_number as flight_number,
f.full_flight_number as full_flight_number,
f.carrier as carrier,
f.direction as direction,
(CASE
	WHEN f.direction = 'I' THEN f.destination
	WHEN f.direction = 'O' THEN f.origin
END) AS airport,
(CASE
	WHEN f.direction = 'I' THEN 'Inbound'
	WHEN f.direction = 'O' THEN 'Outbound'
END) AS direction_desc,

(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS flight_date,

CAST(year(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED  INTEGER) AS flight_year,

CAST(MONTH(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED  INTEGER) AS flight_month,

MONTHNAME(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS flight_month_name,

CAST(week(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END)  AS UNSIGNED  INTEGER) AS flight_week,

CAST(DAY(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED  INTEGER)  AS flight_day,

CAST(HOUR(CASE
	WHEN f.direction = 'I' THEN mfd.eta_date
	WHEN f.direction = 'O' THEN f.etd_date
END) AS UNSIGNED  INTEGER)  AS flight_hour,

f.origin as origin,
(select CONCAT(iata,'-',name) from airport where iata = f.origin) AS full_origin,
f.origin_country as origin_country,
f.destination as destination,
(select CONCAT(iata,'-',name) from airport where iata = f.destination) AS full_destination,
f.destination_country as destination_country,
CONCAT(f.origin, ', ',  (select name from country where iso3 =f.origin_country),' - ',f.destination,', ',  (select name from country where iso3 = f.destination_country ) ) as route,
mfd.full_utc_etd_timestamp as full_utc_etd_timestamp,
mfd.full_utc_eta_timestamp as full_utc_eta_timestamp,
CAST(CASE
	WHEN f.direction = 'I' THEN fpc.fp_count
	WHEN f.direction = 'O' THEN 0
END AS UNSIGNED  INTEGER) AS inbound_passenger_count,

CAST(CASE
	WHEN f.direction = 'I' THEN 0
	WHEN f.direction = 'O' THEN fpc.fp_count
END AS UNSIGNED  INTEGER) AS outbound_passenger_count,

fpc.fp_count as passenger_count,

TIMESTAMPDIFF(HOUR, pnr.transmission_date,
 (CASE
	WHEN f.direction = 'I' THEN mfd.full_utc_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_utc_etd_timestamp
END)) as msg_trans_comp_hrs,

TIMESTAMPDIFF(MINUTE, pnr.transmission_date,
 (CASE
	WHEN f.direction = 'I' THEN mfd.full_utc_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_utc_etd_timestamp
END)) as msg_trans_comp_mins

FROM gtas.message msg
INNER JOIN gtas.message_status mst ON msg.id = mst.ms_message_id
INNER JOIN gtas.pnr pnr ON msg.id = pnr.id
INNER JOIN gtas.pnr_flight pfl ON pnr.id = pfl.pnr_id
INNER JOIN gtas.flight f ON pfl.flight_id = f.id
INNER JOIN gtas.mutable_flight_details mfd ON f.id = mfd.flight_id
INNER JOIN  gtas.flight_passenger_count fpc ON fpc.fp_flight_id = f.id AND f.id_tag IS NOT NULL
WHERE mst.ms_status NOT IN ('RECEIVED','PARSED','FAILED_PARSING','FAILED_LOADING','FAILED_PRE_PARSE')
AND mst.message_flight_idx_flag IS NULL
AND (f.direction = 'I' OR f.direction = 'O')
ORDER BY msg.id, f.id
LIMIT 10000
)
ORDER BY id ASC
Limit 15000
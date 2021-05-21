DROP VIEW IF EXISTS hit_detail_idx_vw;
CREATE VIEW hit_detail_idx_vw AS
(
SELECT
	hd.id as id,
	pd.pd_last_name as last_name,
	pd.pd_first_name as first_name,
	pd.pd_middle_name as middle_name,
	hd.hit_type as hit_type,
	hd.title as title,
  f.id_tag as flight_id_tag,
	f.id as flight_id,
  DATE(CASE
	WHEN f.direction = 'I' THEN mfd.full_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_etd_timestamp
END)  AS flight_date,	

(CASE
	WHEN f.direction = 'I' THEN mfd.full_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_etd_timestamp
END)  AS flight_timestamp,	

CONVERT(YEAR(CASE
	WHEN f.direction = 'I' THEN mfd.full_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_etd_timestamp
END), CHAR)  AS flight_year,	

CONVERT(WEEK(CASE
	WHEN f.direction = 'I' THEN mfd.full_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_etd_timestamp
END), CHAR) as flight_week,
		
CAST(HOUR(CASE
	WHEN f.direction = 'I' THEN mfd.full_eta_timestamp
	WHEN f.direction = 'O' THEN mfd.full_etd_timestamp
END) AS CHAR CHARACTER SET utf8) AS flight_hour,


	hd.hitEnum as hit_source_type,
	hvs.hv_status as hit_status,
	hm.hm_author as hit_author,
	DATE(hvs.updated_at) as hit_updated_date,
	hvs.updated_at as hit_updated_timestamp,
	hc.category as hit_category,
	hc.description as hit_category_desc,
	hc.severity as hit_category_severity,
	ug.ug_name as user_group,
	f.direction as direction,
  (CASE
	 WHEN f.direction = 'I' THEN f.destination
	WHEN f.direction = 'O' THEN f.origin
END) AS airport,
(CASE
	WHEN f.direction = 'I' THEN 'Inbound'
	WHEN f.direction = 'O' THEN 'Outbound'
END ) AS direction_desc,
f.carrier as carrier,
f.flight_number as flight_number,
f.origin as origin,
f.destination as destination,
mfd.full_eta_timestamp as eta_timestamp,
mfd.full_etd_timestamp as etd_timestamp


FROM
hit_detail hd
INNER JOIN hit_maker hm ON hd.hm_id = hm.id
INNER JOIN hit_view_status hvs ON hd.id = hvs.hv_hit_detail
INNER JOIN passenger_details pd ON hd.passenger = pd.pd_passenger_id
INNER JOIN flight f ON f.id = hd.flight
INNER JOIN mutable_flight_details mfd ON mfd.flight_id = f.id
INNER JOIN hit_category hc ON hm.hm_hit_category = hc.id
INNER JOIN gtas.user u ON hm.hm_author = u.user_id
INNER JOIN ug_user_join uuj ON u.user_id = uuj.user_id
INNER JOIN user_group ug ON uuj.ug_id = ug.id
WHERE hd.id > (SELECT hit_detail_idx_last_id FROM report_view_controller WHERE id =1)
ORDER BY id
LIMIT 10000
)
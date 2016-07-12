-- Dumping structure for view gtas.daily_apis_counts
DROP VIEW IF EXISTS `daily_apis_counts`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `daily_apis_counts`;
CREATE VIEW `daily_apis_counts` AS SELECT
        id,
        DATE(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR)) AS 'day',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=0,1,NULL)) AS '1am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=1,1,NULL)) AS '2am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=2,1,NULL)) AS '3am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=3,1,NULL)) AS '4am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=4,1,NULL)) AS '5am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=5,1,NULL)) AS '6am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=6,1,NULL)) AS '7am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=7,1,NULL)) AS '8am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=8,1,NULL)) AS '9am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=9,1,NULL)) AS '10am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=10,1,NULL)) AS '11am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=11,1,NULL)) AS '12pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=12,1,NULL)) AS '1pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=13,1,NULL)) AS '2pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=14,1,NULL)) AS '3pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=15,1,NULL)) AS '4pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=16,1,NULL)) AS '5pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=17,1,NULL)) AS '6pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=18,1,NULL)) AS '7pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=19,1,NULL)) AS '8pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=20,1,NULL)) AS '9pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=21,1,NULL)) AS '10pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=22,1,NULL)) AS '11pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=23,1,NULL)) AS '12am'
    FROM message
    WHERE create_date >= DATE_FORMAT((CURRENT_DATE()-1),'%Y-%m-%d %T') AND create_date < DATE_FORMAT((CURRENT_DATE()+1),'%Y-%m-%d %T')
    AND raw LIKE 
	 '%PAXLST%'
    GROUP BY day ;


-- Dumping structure for view gtas.daily_pnr_counts
DROP VIEW IF EXISTS `daily_pnr_counts`;
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `daily_pnr_counts`;
CREATE VIEW `daily_pnr_counts` AS SELECT
        id,
        DATE(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR)) AS 'day',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=0,1,NULL)) AS '1am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=1,1,NULL)) AS '2am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=2,1,NULL)) AS '3am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=3,1,NULL)) AS '4am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=4,1,NULL)) AS '5am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=5,1,NULL)) AS '6am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=6,1,NULL)) AS '7am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=7,1,NULL)) AS '8am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=8,1,NULL)) AS '9am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=9,1,NULL)) AS '10am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=10,1,NULL)) AS '11am',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=11,1,NULL)) AS '12pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=12,1,NULL)) AS '1pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=13,1,NULL)) AS '2pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=14,1,NULL)) AS '3pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=15,1,NULL)) AS '4pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=16,1,NULL)) AS '5pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=17,1,NULL)) AS '6pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=18,1,NULL)) AS '7pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=19,1,NULL)) AS '8pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=20,1,NULL)) AS '9pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=21,1,NULL)) AS '10pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=22,1,NULL)) AS '11pm',
        COUNT(IF(HOUR(DATE_ADD(create_date, INTERVAL (SELECT val FROM app_configuration WHERE opt LIKE '%HOURLY_ADJ%') HOUR))=23,1,NULL)) AS '12am'
    FROM message
    WHERE create_date >= DATE_FORMAT((CURRENT_DATE()-1),'%Y-%m-%d %T') AND create_date < DATE_FORMAT((CURRENT_DATE()+1),'%Y-%m-%d %T')
    AND raw LIKE 
	 '%PNRGOV%'
    GROUP BY day ;


DROP VIEW IF EXISTS ytd_rule_hit_counts;
DROP TABLE IF EXISTS ytd_rule_hit_counts;
DROP VIEW IF EXISTS ytd_rules;
DROP TABLE IF EXISTS ytd_rules;

    CREATE VIEW ytd_rule_hit_counts AS
    Select
      r.id as 'ruleid',
      r.UDR_RULE_REF as 'ruleref',
      COUNT(hd.id) as 'hits'
      FROM rule as r JOIN hit_detail as hd ON hd.rule_id = r.id GROUP BY r.id;


    CREATE VIEW ytd_rules AS
    SELECT
    	  udr.id as 'id',
        rm.TITLE as 'RuleName',
        r.hits as 'RuleHits',
        udr.author as 'CreatedBy',
        DATE_FORMAT(rm.START_DT,'%d %b %Y') as 'CreatedOn',
        udr.edited_by as 'LastUpdatedBy',
        DATE_FORMAT(udr.edit_dt,'%d %b %Y')  as 'LastEditedOn'

        FROM udr_rule as udr, rule_meta rm,
        ytd_rule_hit_counts as r
        WHERE udr.id = r.ruleref AND rm.ID = r.ruleid;

DROP VIEW IF EXISTS ytd_airport_stats;
DROP TABLE IF EXISTS ytd_airport_stats;

    CREATE VIEW ytd_airport_stats AS
    SELECT a.id as 'ID', a.iata 'AIRPORT', COUNT(*) 'FLIGHTS', SUM(f.rule_hit_count) 'RULEHITS', SUM(f.list_hit_count) 'WATCHLISTHITS'
        FROM flight f, airport a
        WHERE a.country = 'USA'
        AND (TRIM(a.iata) = TRIM( f.origin )
        OR TRIM(a.iata) = TRIM(f.destination))
        GROUP BY a.iata
        ORDER BY COUNT(*) DESC
        LIMIT 5;

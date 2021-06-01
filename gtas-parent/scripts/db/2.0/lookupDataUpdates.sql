
use gtas;

-- -----
-- UPDATE EXISTING COUNTRY NAMES WHERE THE ISO3 MATCHES (COUNTRY RESTORE ONLY)
-- -----
UPDATE country_restore SET NAME = 'Hong Kong' WHERE ISO3 = 'HKG';
UPDATE country_restore SET NAME = 'Macau' WHERE ISO3 = 'MAC';
UPDATE country_restore SET NAME = 'Republic of the Congo' WHERE ISO3 = 'COG';
UPDATE country_restore SET NAME = 'Democratic Republic of the Congo' WHERE ISO3 = 'COD';
UPDATE country_restore SET NAME = 'Falkland Islands' WHERE ISO3 = 'FLK';
UPDATE country_restore SET NAME = 'Iran' WHERE ISO3 = 'IRN';
UPDATE country_restore SET NAME = 'North Korea' WHERE ISO3 = 'PRK';
UPDATE country_restore SET NAME = 'South Korea' WHERE ISO3 = 'KOR';
UPDATE country_restore SET NAME = 'Laos' WHERE ISO3 = 'LAO';
UPDATE country_restore SET NAME = 'Macedonia' WHERE ISO3 = 'MKD';
UPDATE country_restore SET NAME = 'Micronesia' WHERE ISO3 = 'FSM';
UPDATE country_restore SET NAME = 'Palestine' WHERE ISO3 = 'PSE';
UPDATE country_restore SET NAME = 'Russia' WHERE ISO3 = 'RUS';
UPDATE country_restore SET NAME = 'Saint-Martin' WHERE ISO3 = 'MAF';
UPDATE country_restore SET NAME = 'Saint Vincent and the Grenadines' WHERE ISO3 = 'VCT';
UPDATE country_restore SET NAME = 'Syria' WHERE ISO3 = 'SYR';
UPDATE country_restore SET NAME = 'Taiwan' WHERE ISO3 = 'TWN';
UPDATE country_restore SET NAME = 'Tanzania' WHERE ISO3 = 'TZA';
UPDATE country_restore SET NAME = 'Venezuela' WHERE ISO3 = 'VEN';
UPDATE country_restore SET NAME = 'Vietnam' WHERE ISO3 = 'VNM';
UPDATE country_restore SET NAME = 'Virgin Islands' WHERE ISO3 = 'VIR';


-- -----
-- INSERT NEW COUNTRIES IF NOT EXISTS
-- -----

insert into country_restore (name, iso2, iso3, iso_numeric) 
(
select 'Curaçao', 'CW', 'CUW', '531' from country_restore 
WHERE NOT EXISTS (select iso3 from country_restore where iso3 = "CUW")
limit 1
);
insert into country (name, iso2, iso3, iso_numeric, originId, updated_at) 
(
select 'Curaçao', 'CW', 'CUW', '531', id, NOW() from country_restore 
WHERE iso3 = "CUW"
AND NOT EXISTS (select iso3 from country where iso3 = "CUW")
limit 1
);


insert into country_restore (name, iso2, iso3, iso_numeric) 
(
select 'Bonaire, Sint Eustatius and Saba', 'BQ', 'BES', '535' from country_restore 
WHERE NOT EXISTS (select iso3 from country_restore where iso3 = "BES")
limit 1
);
insert into country (name, iso2, iso3, iso_numeric, originId, updated_at) 
(
select 'Bonaire, Sint Eustatius and Saba', 'BQ', 'BES', '535', id, NOW() from country_restore 
WHERE iso3 = "BES"
AND NOT EXISTS (select iso3 from country where iso3 = "BES")
limit 1
);


insert into country_restore (name, iso2, iso3, iso_numeric) 
(
select 'Sint Maarten', 'SW', 'SXM', '534' from country_restore 
WHERE NOT EXISTS (select iso3 from country_restore where iso3 = "SXM")
limit 1
);
insert into country (name, iso2, iso3, iso_numeric, originId, updated_at) 
(
select 'Sint Maarten', 'SW', 'SXM', '534', id, NOW() from country_restore 
WHERE iso3 = "SXM"
AND NOT EXISTS (select iso3 from country where iso3 = "SXM")
limit 1
);



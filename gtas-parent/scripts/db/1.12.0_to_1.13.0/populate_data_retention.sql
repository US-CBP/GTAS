insert into data_retention_status (created_by, drs_deleted_apis, drs_deleted_PNR, drs_has_apis_message, drs_has_pnr_message, drs_masked_apis, drs_masked_pnr, drs_passenger_id)
select 'update', false, false, false, false, false, false,  id from gtas.passenger;

update data_retention_status
set drs_has_pnr_message = true where drs_passenger_id in (select passenger_id from pnr_passenger);

update data_retention_status
set drs_has_apis_message = true where drs_passenger_id in (select passenger_id from apis_message_passenger);
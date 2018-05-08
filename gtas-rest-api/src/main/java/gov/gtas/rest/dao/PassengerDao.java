package gov.gtas.rest.dao;

import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.PassengerResponse;

public interface PassengerDao extends BaseDao<PassengerRequest,PassengerResponse> {
	
	public PassengerResponse findPassengerByName(PassengerRequest passengerRequest);

}

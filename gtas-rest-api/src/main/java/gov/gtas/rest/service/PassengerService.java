package gov.gtas.rest.service;

import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.PassengerResponse;

public interface PassengerService extends BaseService<PassengerRequest,PassengerResponse> {

	public PassengerResponse findPassengerByName(PassengerRequest passengerRequest);
}

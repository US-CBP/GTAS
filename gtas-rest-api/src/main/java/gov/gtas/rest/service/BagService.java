package gov.gtas.rest.service;

import gov.gtas.rest.request.BagRequest;
import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.BagResponse;
import gov.gtas.rest.response.PassengerResponse;

public interface BagService extends BaseService<PassengerRequest,PassengerResponse> {

	public BagResponse findBagByFlightIdPassengerId(BagRequest bagRequest);
}

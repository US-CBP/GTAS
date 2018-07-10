package gov.gtas.rest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.rest.dao.PassengerDao;
import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.PassengerResponse;
import gov.gtas.rest.service.PassengerService;

@Service
public class PassengerServiceImpl implements PassengerService{

	
	@Autowired
	private PassengerDao passengerDao;
	
	
	@Override
	public PassengerResponse findPassengerByName(PassengerRequest passengerRequest) {
			
		return passengerDao.findPassengerByName(passengerRequest);
	}

}

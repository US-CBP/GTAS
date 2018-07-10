package gov.gtas.rest.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.rest.dao.BagDao;
import gov.gtas.rest.request.BagRequest;
import gov.gtas.rest.response.BagResponse;
import gov.gtas.rest.service.BagService;


@Service
public class BagServiceImpl implements BagService {

	@Autowired
	private BagDao bagDao;
	
	@Override
	public BagResponse findBagByFlightIdPassengerId(BagRequest bagRequest) {
		
		return bagDao.findBagByflightIdPassengerId(bagRequest);
	}

}

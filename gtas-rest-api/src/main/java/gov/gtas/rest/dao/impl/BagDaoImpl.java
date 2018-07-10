package gov.gtas.rest.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.gtas.rest.dao.BagDao;
import gov.gtas.rest.mapper.BagMapper;
import gov.gtas.rest.model.Bag;
import gov.gtas.rest.request.BagRequest;
import gov.gtas.rest.response.BagResponse;


@Repository
public class BagDaoImpl implements BagDao {

	@Autowired
	private BagMapper bagMapper;
	
	
	@Override
	public BagResponse findByID(BagRequest r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BagResponse findBagByflightIdPassengerId(BagRequest bagRequest) {
		
		List<Bag> bagList = bagMapper.findBagByFlightIdPassengerId(bagRequest.getModel().getFlightId(), bagRequest.getModel().getPassengerId());
		BagResponse bagResponse = new BagResponse();
		bagResponse.setBags(bagList);
		return bagResponse;
	}

}

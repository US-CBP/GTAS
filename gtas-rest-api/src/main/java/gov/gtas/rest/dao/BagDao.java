package gov.gtas.rest.dao;

import gov.gtas.rest.request.BagRequest;
import gov.gtas.rest.response.BagResponse;

public interface BagDao extends BaseDao<BagRequest,BagResponse> {
	
	public BagResponse findBagByflightIdPassengerId(BagRequest bagRequest);

}

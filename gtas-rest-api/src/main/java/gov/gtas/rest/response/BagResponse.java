package gov.gtas.rest.response;

import java.util.List;

import gov.gtas.rest.model.Bag;

public class BagResponse extends BaseResponse<Bag>{

	protected List<Bag> bags;

	public List<Bag> getBags() {
		return bags;
	}

	public void setBags(List<Bag> bags) {
		this.bags = bags;
	}
	
	
	
}

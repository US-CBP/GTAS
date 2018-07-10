package gov.gtas.rest.request;

import gov.gtas.rest.model.Bag;

public class BagRequest extends BaseRequest<Bag> {
	
	public BagRequest()
	{
		this.model = new Bag();
	}

}

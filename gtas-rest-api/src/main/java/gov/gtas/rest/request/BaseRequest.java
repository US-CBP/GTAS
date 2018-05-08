package gov.gtas.rest.request;

import gov.gtas.rest.model.BaseModel;

public class BaseRequest <T extends BaseModel> {
	
	protected T model;

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}


	
	
	

}

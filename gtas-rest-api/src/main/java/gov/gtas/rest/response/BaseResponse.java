package gov.gtas.rest.response;

import java.util.ArrayList;
import java.util.List;

import gov.gtas.rest.model.BaseModel;
import gov.gtas.rest.validator.ValidationResult;


public class BaseResponse<T extends BaseModel> {
	


	protected ValidationResult validationResult;
	
	public BaseResponse()
	{
		validationResult = new ValidationResult();
	}
	
	
	public ValidationResult getValidationResult() {
		return validationResult;
	}
	public void setValidationResult(ValidationResult validationResult) {
		this.validationResult = validationResult;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}

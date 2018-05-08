package gov.gtas.rest.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

	private String validationMessage;
	private Boolean hasValidationError = true;
	
	private List<FieldValidationMessage> fieldValidationMessageList;
	
	public ValidationResult()
	{
		fieldValidationMessageList = new ArrayList<FieldValidationMessage>();
	}
	
	public String getValidationMessage() {
		return validationMessage;
	}
	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
	public List<FieldValidationMessage> getFieldValidationMessageList() {
		return fieldValidationMessageList;
	}
	public void setFieldValidationMessageList(List<FieldValidationMessage> fieldValidationMessageList) {
		this.fieldValidationMessageList = fieldValidationMessageList;
	}

	public Boolean getHasValidationError() {
		return hasValidationError;
	}

	public void setHasValidationError(Boolean hasValidationError) {
		this.hasValidationError = hasValidationError;
	}
	
	

	
}

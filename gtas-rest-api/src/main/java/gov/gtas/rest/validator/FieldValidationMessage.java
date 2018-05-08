package gov.gtas.rest.validator;


public class FieldValidationMessage {
	
	private String field;
	private String fieldValidationMessage;
	
	public FieldValidationMessage(String field, String fieldValidationMessage )
	{
		this.field = field;
		this.fieldValidationMessage = fieldValidationMessage;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getFieldValidationMessage() {
		return fieldValidationMessage;
	}
	public void setFieldValidationMessage(String fieldValidationMessage) {
		this.fieldValidationMessage = fieldValidationMessage;
	}
	
	
	
	

}

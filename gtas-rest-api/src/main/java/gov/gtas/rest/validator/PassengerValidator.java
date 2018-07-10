package gov.gtas.rest.validator;

import org.springframework.stereotype.Component;

import gov.gtas.rest.model.Passenger;

@Component
public class PassengerValidator {
	
	public ValidationResult validatePassenger(Passenger passenger)
	{
		ValidationResult validationResult = new ValidationResult();
		
		if( (passenger.getFirstName()==null || passenger.getFirstName().trim().isEmpty()) || (passenger.getLastName()==null || passenger.getLastName().isEmpty()))
		{
			validationResult.getFieldValidationMessageList().add(new FieldValidationMessage("First Name/Last Name","First Name and Last Name is required for this request."));
		}
		 
		
		 if(validationResult.getFieldValidationMessageList().isEmpty() )
		 {
				validationResult.setHasValidationError(false);
		 }
		 else {
			 validationResult.setHasValidationError(true);
			 validationResult.setValidationMessage("Validation Error");
		 }
		 
		return validationResult;
	
	}

}

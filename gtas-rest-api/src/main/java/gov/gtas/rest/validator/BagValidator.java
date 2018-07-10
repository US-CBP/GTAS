package gov.gtas.rest.validator;

import org.springframework.stereotype.Component;

import gov.gtas.rest.model.Bag;

@Component
public class BagValidator {
	
	public ValidationResult validateEvent(Bag bag)
	{
		ValidationResult validationResult = new ValidationResult();
		
		if(bag.getFlightId()==null)
		{
			validationResult.getFieldValidationMessageList().add(new FieldValidationMessage("FlightId","The supplied flight Id is null"));
		}
		 if(bag.getPassengerId()==null)
		 {
			 validationResult.getFieldValidationMessageList().add(new FieldValidationMessage("PassengerId","The supplied Passenger Id is null"));
		 }
		
		 if(validationResult.getFieldValidationMessageList().isEmpty() )
				validationResult.setHasValidationError(false);
		 else
			 validationResult.setValidationMessage("Validation Error");
		 
		return validationResult;
	
	}

}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import gov.gtas.constant.CommonErrorConstants;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class CommonValidationException extends CommonServiceException {
	private static final long serialVersionUID = 4913437813095082766L;

	private Errors validationErrors;

	public CommonValidationException(String message, Errors errors) {
		super(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, message);
		this.validationErrors = errors;
	}

	@Override
	public String getMessage() {
		StringBuilder msg = new StringBuilder(super.getMessage());
		msg.append("\n");
		List<FieldError> fieldErrors = validationErrors.getFieldErrors();
		for (FieldError err : fieldErrors) {
			msg.append(err.getField()).append(" is invalid\n");
		}
		List<ObjectError> globalErrors = validationErrors.getGlobalErrors();
		for (ObjectError err : globalErrors) {
			msg.append(err.getDefaultMessage()).append("\n");
		}
		return msg.toString();
	}

	/**
	 * @return the validationErrors
	 */
	public Errors getValidationErrors() {
		return validationErrors;
	}

}

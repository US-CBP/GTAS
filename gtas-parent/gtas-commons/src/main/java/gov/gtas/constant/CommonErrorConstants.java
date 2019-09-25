/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

/**
 * Common Error Constants for GTAS.
 */
public class CommonErrorConstants {
	public static final String CONSTRAINT_VIOLATION_EXCEPTION_NAME = "ConstraintViolationException";

	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR CODES
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * The error handler uses this to indicate that the error code passed to it is
	 * Unknown.
	 */
	public static final String UNKNOWN_ERROR_CODE = "UNKNOWN_ERROR_CODE";
	/*
	 * This is the error code for an internal system error indicating invalid
	 * program logic causing a service API method being called with a null argument.
	 */
	public static final String NULL_ARGUMENT_ERROR_CODE = "NULL_ARGUMENT";
	/*
	 * This is the error code for an internal system error indicating invalid
	 * program logic causing a service API method being called with an invalid
	 * argument.
	 */
	public static final String INVALID_ARGUMENT_ERROR_CODE = "INVALID_ARGUMENT";
	/*
	 * This is the error code indicating that the user indicated by a supplied
	 * userId cannot be found.
	 */
	public static final String INVALID_USER_ID_ERROR_CODE = "INVALID_USER_ID";

	/*
	 * This error code indicates that an input JSON field has an incorrect format.
	 */
	public static final String INPUT_JSON_FORMAT_ERROR_CODE = "INPUT_FORMAT_ERROR";
	/*
	 * This error code indicates that an input JSON failed validation.
	 */
	public static final String JSON_INPUT_VALIDATION_ERROR_CODE = "JSON_VALIDATION_ERROR";

	/*
	 * This is the error code indicating that the a query returned no result.
	 */
	public static final String QUERY_RESULT_EMPTY_ERROR_CODE = "QUERY_RESULT_EMPTY";
	/*
	 * This is the error code indicating that the record to be updated does not
	 * exist in the database.
	 */
	public static final String UPDATE_RECORD_MISSING_ERROR_CODE = "UPDATE_RECORD_MISSING";

	/*
	 * This is the error code for an Unexpected internal system error.
	 */
	public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR";

	// //////////////////////////////////////////////////////////////////////////////////////
	// ERROR Messages
	// //////////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error message for an internal system error indicating invalid
	 * program logic causing a service API method being called with a null argument.
	 */
	public static final String NULL_ARGUMENT_ERROR_MESSAGE = "The parameter '%s' provided for the operation/function '%s' should not be null.";
	/*
	 * This is the error message for an internal system error indicating invalid
	 * program logic causing a service API method being called with an invalid
	 * argument.
	 */
	public static final String INVALID_ARGUMENT_ERROR_MESSAGE = "The parameter '%s' in '%s' is invalid.";
	/*
	 * This is the error message indicating that the user indicated by a supplied
	 * userId cannot be found.
	 */
	public static final String INVALID_USER_ID_ERROR_MESSAGE = "The user id '%s' does not represent a valid user.";

	/*
	 * This error message indicates that an input JSON field has an incorrect
	 * format.
	 */
	public static final String INPUT_JSON_FORMAT_ERROR_MESSAGE = "Parse error for values %s with type %s for '%s'.";

	/*
	 * This error message indicates that an input JSON failed validation.
	 */
	public static final String JSON_INPUT_VALIDATION_ERROR_MESSAGE = "The JSON input is invalid.";

	/*
	 * This is the error message indicating that the record to be updated does not
	 * exist in the database.
	 */
	public static final String UPDATE_RECORD_MISSING_ERROR_MESSAGE = "Cannot find UDR record to update (title=%s, userId=%s, id=%s).";

	/*
	 * This is the error message indicating that the a query returned no result.
	 */
	public static final String QUERY_RESULT_EMPTY_ERROR_MESSAGE = "Query for %s using '%s' returned no result.";
	/*
	 * This is the error message for an Unexpected internal system error.
	 */
	public static final String SYSTEM_ERROR_MESSAGE = "There was an Internal System Error with ID %s. Please contact the HelpDesk for details.";
	/*
	 * The error handler uses this to indicate that the error code passed to it is
	 * Unknown.
	 */
	public static final String UNKNOWN_ERROR_CODE_MESSAGE = "The system generated an unknown error code '%s'.";
}

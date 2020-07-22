/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.validation.util;

import gov.gtas.enumtype.ConditionEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.OperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.constants.Constants;
import gov.gtas.querybuilder.mappings.*;
import gov.gtas.querybuilder.model.UserQuery;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryValidationUtils {
	private static final Logger logger = LoggerFactory.getLogger(QueryValidationUtils.class);

	public static Errors validateQueryObject(QueryObject queryObject) {
		String objectName = Constants.QUERYOBJECT_OBJECTNAME;
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(QueryObject.class, objectName);

		logger.info("Validating " + objectName);
		if (queryObject == null) {
			errors.reject("", "query is null");
			return errors;
		}

		// validate user query
		validate(queryObject, errors);

		return errors;
	}

	public static Errors validateQueryRequest(UserQuery userQuery)
			throws JsonParseException, JsonMappingException, IOException {
		String objectName = Constants.USERQUERY_OBJECTNAME;
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(UserQuery.class, objectName);

		logger.debug("Validating " + objectName);
		if (userQuery != null) {

			if (userQuery.getCreatedBy() == null || StringUtils.isEmpty(userQuery.getCreatedBy().getUserId())) {
				errors.reject("", "userId must be provided");
			}
			if (StringUtils.isEmpty(userQuery.getTitle())) {
				errors.reject("", "Title cannot be empty");
			}
			if (StringUtils.isEmpty(userQuery.getQueryText())) {
				errors.reject("", "Query cannot be empty");
			} else {
				ObjectMapper mapper = new ObjectMapper();
				QueryObject queryObject = mapper.readValue(userQuery.getQueryText(), QueryObject.class);
				validate(queryObject, errors);
			}

		}

		return errors;
	}

	private static void validate(QueryEntity queryEntity, Errors errors) {

		if (queryEntity instanceof QueryObject) {
			QueryObject queryObject = (QueryObject) queryEntity;
			String condition = queryObject.getCondition();

			// validate condition
			boolean validCondition = false;
			for (ConditionEnum e : ConditionEnum.values()) {
				if (e.toString().equalsIgnoreCase(condition)) {
					validCondition = true;
					break;
				}
			}
			if (!validCondition) {
				errors.reject("", "condition: \'" + condition + "\' is invalid");
			}

			// validate rules
			List<QueryEntity> rules = queryObject.getRules();
			if (rules != null) {
				for (QueryEntity rule : rules) {

					validate(rule, errors);
				}
			}

		} else if (queryEntity instanceof QueryTerm) {
			QueryTerm queryTerm = (QueryTerm) queryEntity;

			String entity = queryTerm.getEntity();
			String field = queryTerm.getField();
			String type = queryTerm.getType();
			String operator = queryTerm.getOperator();

			// validate entity
			boolean validEntity = false;
			for (EntityEnum e : EntityEnum.values()) {
				if (e.getEntityName().equalsIgnoreCase(entity)) {
					validEntity = true;
					break;
				}
			}
			if (!validEntity) {
				errors.reject("", "entity: \'" + entity + "\' is invalid");
			}

			if (validEntity) { // only validate the field if the entity is valid
				// validate field name
				boolean validField = false;
				switch (entity.toUpperCase()) {
				case Constants.ADDRESS:
					validField = validateField(AddressMapping.values(), field);
					break;
				case Constants.CREDITCARD:
					validField = validateField(CreditCardMapping.values(), field);
					break;
				case Constants.SEAT:
					validField = validateField(SeatMapping.values(), field);
					break;
				case Constants.DOCUMENT:
					validField = validateField(DocumentMapping.values(), field);
					break;
				case Constants.EMAIL:
					validField = validateField(EmailMapping.values(), field);
					break;
				case Constants.FLIGHT:
					validField = validateField(FlightMapping.values(), field)
							|| validateField(MutableFlightInformationMapping.values(), field);
					break;
				case Constants.BOOKINGDETAIL:
					validField = validateField(BookingDetailMapping.values(), field);
					break;
				case Constants.FREQUENTFLYER:
					validField = validateField(FrequentFlyerMapping.values(), field);
					break;
				case Constants.HITS:
					validField = validateField(HitsMapping.values(), field);
					break;
				case Constants.PASSENGER:
					validField = validateField(PassengerMapping.values(), field)
							|| validateField(PassengerDetailsMapping.values(), field);
					break;
				case Constants.PHONE:
					validField = validateField(PhoneMapping.values(), field);
					break;
				case Constants.PNR:
					validField = validateField(PNRMapping.values(), field);
					break;
				case Constants.DWELLTIME:
					validField = validateField(DwellTimeMapping.values(), field);
					break;
				case Constants.AGENCY:
					validField = validateField(TravelAgencyMapping.values(), field);
					break;
				case Constants.BAG:
					validField = validateField(BagMapping.values(), field);
					break;
				case Constants.FLIGHTPAX:
					validField = validateField(FlightPaxMapping.values(), field);
					break;
				case Constants.PAYMENTFORM:
					validField = validateField(PaymentFormMapping.values(), field);
					break;
				}
				if (!validField) {
					errors.reject("", "entity: \'" + entity + "\' has an invalid field value, field: \'" + field
							+ "\' is invalid");
				}
			}

			// validate type
			boolean validType = false;
			for (TypeEnum t : TypeEnum.values()) {
				if (t.getType().equalsIgnoreCase(type)) {
					validType = true;
					break;
				}
			}
			if (!validType) {
				errors.reject("",
						"entity: \'" + entity + "\' has an invalid type value, type: \'" + type + "\' is invalid");
			}

			// validate operator
			boolean validOperator = false;
			for (OperatorEnum o : OperatorEnum.values()) {
				if (o.toString().equalsIgnoreCase(operator)) {
					validOperator = true;
					break;
				}
			}

			if (!validOperator) {
				errors.reject("", "entity: \'" + entity + "\' has an invalid operator value, operator: \'" + operator
						+ "\' is invalid");
			} else {
				// validate value
				// ignore the value on these four operators because they shouldn't
				// have one and will not be used if it's provided
				if (!OperatorEnum.IS_EMPTY.toString().equalsIgnoreCase(operator)
						&& !OperatorEnum.IS_NOT_EMPTY.toString().equalsIgnoreCase(operator)
						&& !OperatorEnum.IS_NULL.toString().equalsIgnoreCase(operator)
						&& !OperatorEnum.IS_NOT_NULL.toString().equalsIgnoreCase(operator)) {

					// validate that there are two values if the operator is BETWEEN
					if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)) {

						if (queryTerm.getValue() == null || queryTerm.getValue().length != 2) {
							errors.reject("", "entity: \'" + entity
									+ "\' has an incorrect number of arguments, BETWEEN operator must have two parameters in value attribute");
						}
					}
					// for IN or NOT_IN operator, verify that values is not null and it has at least
					// one parameter
					else if (OperatorEnum.IN.toString().equalsIgnoreCase(operator)
							|| OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)) {

						if (queryTerm.getValue() == null || queryTerm.getValue().length == 0) {
							errors.reject("", "entity: \'" + entity + "\' has an incorrect number of arguments, "
									+ operator + " operator must have at least one parameter in value attribute");
						}
					} else {

						// verify that value is not null
						if (queryTerm.getValue() == null) {
							errors.reject("", "entity: \'" + entity + "\' has a null attribute value for field: \'"
									+ field + "\'");
						} else if (queryTerm.getValue().length != 1) {
							errors.reject("", "entity: \'" + entity + "\' must have one value for field: \'" + field
									+ "\' and operator: \'" + operator + "\'");
						}
					}
				}
			}

		} else {
			errors.reject("", "Invalid query");
		}

	}

	public static String getErrorString(Errors errors) {
		StringBuilder errorMsg = new StringBuilder();

		if (errors != null && errors.hasErrors()) {
			int count = 0;
			for (ObjectError e : errors.getAllErrors()) {
				if (count > 0) {
					errorMsg.append("; ");
				}
				errorMsg.append(e.getDefaultMessage());
				count++;
			}
		}

		return errorMsg.toString();
	}

	private static boolean validateField(IEntityMapping[] entityEnum, String field) {
		boolean validField = false;

		for (IEntityMapping e : entityEnum) {
			if (e.getFieldName().equals(field)) {
				validField = true;
				break;
			}
		}

		return validField;
	}
}

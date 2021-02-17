/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.OperatorEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.constants.Constants;
import gov.gtas.querybuilder.exceptions.InvalidQueryRepositoryException;
import gov.gtas.querybuilder.model.QueryRequestWithMetaData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class parses the QueryEntity and generates a JPQL Statement
 */
public class JPQLGenerator {
	
	private static final String NOT_BETWEEN = "NOT_BETWEEN";
	private static final String BETWEEN = "BETWEEN";
	private static final String NOT_EQUAL = "NOT_EQUAL";
	private static final String EQUAL = "EQUAL";
	private static final String LESS = "LESS";
	private static final String GREATER_OR_EQUAL = "GREATER_OR_EQUAL";
	private static final String GREATER = "GREATER";
	private static final String LESS_OR_EQUAL = "LESS_OR_EQUAL";
	private static final Logger logger = LoggerFactory.getLogger(JPQLGenerator.class);

	private JPQLGenerator() {};
	
	public static String generateQuery(QueryRequestWithMetaData queryRequest, EntityEnum queryType)
			throws InvalidQueryRepositoryException {
		QueryEntity queryEntity = queryRequest.getQuery();
		String query = "";
		
		if (queryEntity != null && queryType != null) {
			String queryPrefix;
			List<EntityEnum> joinEntities = new ArrayList<>();
			StringBuilder where = new StringBuilder();
			String join = "";
			MutableBoolean seatCondition = new MutableBoolean();
			MutableBoolean paymentFormCondition = new MutableBoolean();
			MutableInt positionalParameter = new MutableInt();
			MutableInt level = new MutableInt();
			logger.debug("Parsing QueryObject...");
			QueryObject queryObject = (QueryObject) queryEntity;
			
			convertAgeToDob(queryObject, queryRequest.getUtcMinuteOffset());
			
			generateWhereCondition(queryEntity, queryType, joinEntities, where, seatCondition, positionalParameter,
					level, paymentFormCondition);
			
			logger.debug("Set join to outer or left join depending on query having only 'AND' conditions");
			String joinType = " " + queryRequest.getJoinCondition() + " ";

			if (queryType == EntityEnum.FLIGHT) {
				
				
				//  force inner join as these all must be present.
				queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + " " + Constants.FROM
						+ " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
						Constants.JOIN + EntityEnum.FLIGHT.getAlias() + ".passengers " + EntityEnum.PASSENGER.getAlias() +
						" join p.dataRetentionStatus drsps ";

				if (seatCondition.isTrue()) {
					joinEntities.add(EntityEnum.PASSENGER);
				}

				if (!joinEntities.isEmpty()) {
					// remove Flight from the List because it is already
					// part of the queryPrefix statement
					joinEntities.remove(EntityEnum.FLIGHT);
					if (!joinEntities.isEmpty()) {
						// add join to PNR if there is a PNR
						// entity in the query
						if (hasPNREntity(joinEntities)) {
							joinEntities.remove(EntityEnum.PNR);
							joinEntities.add(0, EntityEnum.PNR);
						}
					}
					join = generateJoinCondition(joinEntities, queryType, joinType);
				}

				boolean hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);

				if (hasFormOfPayment) {
					join += joinType + EntityEnum.PNR.getAlias() + ".flights pnfl ";
				}

				if (seatCondition.isTrue()) {
					join += joinType + " p.seatAssignments s ";
				}
				if (paymentFormCondition.isTrue()) {
					// joins to pnr -> paymentForms through flight
					join += joinType + " pnr.paymentForms pf ";

				}

				query = queryPrefix + join + " " + Constants.WHERE + " " + where;
			}
			else if (queryType == EntityEnum.PASSENGER) {
				where.append(" and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))");

				queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.PASSENGER.getAlias() + Constants.ID + ", "
						+ EntityEnum.PASSENGER.getAlias() + ", p.flight " + Constants.FROM + " "
						+ EntityEnum.PASSENGER.getEntityName() + " " + EntityEnum.PASSENGER.getAlias()
						+ " join p.flight f "
						+ " join p.dataRetentionStatus drsps  ";

				if (!joinEntities.isEmpty()) {

					// remove Flight and Passenger from the List because it is already
					// part of the queryPrefix statement
					joinEntities.remove(EntityEnum.FLIGHT);
					joinEntities.remove(EntityEnum.PASSENGER);

					// add join to PNR if there is a PNR
					// entity in the query
					if (hasPNREntity(joinEntities)) {
						joinEntities.remove(EntityEnum.PNR);
						joinEntities.add(0, EntityEnum.PNR);
					}

					join = generateJoinCondition(joinEntities, queryType, joinType);
				}

				boolean hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);

				if (hasFormOfPayment) {
					join +=  joinType + EntityEnum.PNR.getAlias() + ".flights pnfl ";
				}

				if (seatCondition.isTrue()) {
					join += joinType + " p.seatAssignments s ";
				}

				if (paymentFormCondition.isTrue()) {
					join +=  joinType + " pnr.paymentForms pf ";
				}

				query = queryPrefix + join + " " + Constants.WHERE + " " + where;
			}
			logger.info("Parsed Query: " + query);
		}

		return query;
	}

	private static void convertAgeToDob(QueryObject queryObject, int utcMinuteOffset) {
		for (QueryEntity qe : queryObject.getRules()) {
			if (qe instanceof QueryTerm) {
				QueryTerm qt = (QueryTerm) qe;
				if (qt.getEntity().equalsIgnoreCase(EntityEnum.PASSENGER.getEntityName()) &&
					qt.getField().equalsIgnoreCase("passengerDetails.age")) {
				
					String replacementField = "passengerDetails.dob";			
					String replacementType = "date";
					qt.setField(replacementField);
					qt.setType(replacementType);
				
					//Handle UTC offsets.
					LocalDateTime localDatetime = LocalDateTime.now();
					localDatetime = localDatetime.minusMinutes(utcMinuteOffset);
					
					LocalDate localDate = localDatetime.toLocalDate(); 
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					switch (qt.getOperator()) {
						case LESS:
							// Because dates are stored as the "greatest date" being the date in the future, 
							// and greatest age are stored as "greatest date" in the past, these must be swapped.
							qt.setOperator(GREATER);
							int lessAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate lessTime = localDate.minusYears(lessAge + 1);
							String[] lessReplacementValue = new String[] {lessTime.format(formatter)};
							qt.setValue(lessReplacementValue);	
							break;
						case LESS_OR_EQUAL:
							// Because dates are stored as the "greatest date" being the date in the future, 
							// and greatest age are stored as "greatest date" in the past, these must be swapped.
							qt.setOperator(GREATER_OR_EQUAL);
							int lessEqualAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate lessEqualTime = localDate.minusYears(lessEqualAge + 1);
							String[] lessEqualReplacementValue = new String[] {lessEqualTime.format(formatter)};
							qt.setValue(lessEqualReplacementValue);							
							break;						
						case GREATER_OR_EQUAL:
							// Because dates are stored as the "greatest date" being the date in the future, 
							// and greatest age are stored as "greatest date" in the past, these must be swapped.
							qt.setOperator(LESS_OR_EQUAL);
							int greaterOrEqualAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate greaterOrEqualTime = localDate.minusYears(greaterOrEqualAge);
							String[] greaterOrEqualVal = new String[] {greaterOrEqualTime.format(formatter)};
							qt.setValue(greaterOrEqualVal);
							break;
						case GREATER:
							// Because dates are stored as the "greatest date" being the date in the future, 
							// and greatest age are stored as "greatest date" in the past, these must be swapped.
							qt.setOperator(LESS);
							int greaterAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate greaterTime = localDate.minusYears(greaterAge);
							String[] greaterValue = new String[] {greaterTime.format(formatter)};
							qt.setValue(greaterValue);						
							break;				
						case EQUAL:
							int equalAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate equalAgeStart = localDate.minusYears(equalAge + 1);
							LocalDate equalAgeFinish = localDate.minusYears(equalAge);
							// Because Age can represent any date within a year equality is updated to 
							// matching any birthday within a year of the date.
							qt.setOperator(BETWEEN);
							String[] replacementValueEqual = new String[] {equalAgeStart.format(formatter), equalAgeFinish.format(formatter)};
							qt.setValue(replacementValueEqual);
							break;
						case NOT_EQUAL:
							int notEqualAge = Integer.parseInt(qt.getValue()[0]);
							LocalDate notEqualAgeStart = localDate.minusYears(notEqualAge + 1);
							LocalDate notEqualAgeFinish = localDate.minusYears(notEqualAge);
							// Because Age can represent any date within a year equality is updated to 
							// matching any birthday within a year of the date.
							qt.setOperator(NOT_BETWEEN);
							String[] replacementValueNotEqual = new String[] {notEqualAgeStart.format(formatter), notEqualAgeFinish.format(formatter)};
							qt.setValue(replacementValueNotEqual);
							break;
						default: 
							String message = "No conversion from age to date for query operator of " + qt.getOperator();
							logger.info(message);
							break;	
					}
				}
			} else if (qe instanceof QueryObject) {
				convertAgeToDob(queryObject,utcMinuteOffset);
			}
		}
	}

	/**
	 * This method recursively parses the query entity and generates the where
	 * clause of the query
	 *
	 * @param queryEntity
	 *            contains the user's ad-hoc query
	 * @param queryType
	 *            indicates whether the user is querying against the flight or
	 *            passenger data
	 * @param joinEntities
	 *            contains the list of entities that will later be used to generate
	 *            the join condition
	 * @param where
	 *            the generated where clause
	 * @param positionalParameter
	 *            parameter's position in where clause
	 * @param level
	 *            used to group conditions
	 */
	private static void generateWhereCondition(QueryEntity queryEntity, EntityEnum queryType,
			List<EntityEnum> joinEntities, StringBuilder where, MutableBoolean seatCondition,
			MutableInt positionalParameter, MutableInt level, MutableBoolean paymentFormCondition) {
		QueryObject queryObject;
		QueryTerm queryTerm;
		String condition;

		if (queryEntity instanceof QueryObject) {
			queryObject = (QueryObject) queryEntity;
			condition = queryObject.getCondition();
			level.increment();
			List<QueryEntity> rules = queryObject.getRules();
			where.append("(");
			int index = 0;
			for (QueryEntity rule : rules) {

				if (index > 0) {
					where.append(" ").append(condition).append(" ");
				}
				generateWhereCondition(rule, queryType, joinEntities, where, seatCondition, positionalParameter, level,
						paymentFormCondition);
				index++;
			}
			where.append(")");
		} else if (queryEntity instanceof QueryTerm) {
			queryTerm = (QueryTerm) queryEntity;

			String field = queryTerm.getField();
			String operator = queryTerm.getOperator();
			EntityEnum entityEnum = EntityEnum.getEnum(queryTerm.getEntity());
			OperatorEnum opEnum = OperatorEnum.getEnum(operator);

			// add entity to data structure if not already present
			// will be used later for generating the join condition
			if (!(entityEnum == EntityEnum.PNR && (field.equalsIgnoreCase(Constants.SEAT)))
					&& !joinEntities.contains(entityEnum)) {
				joinEntities.add(entityEnum);
			}

			if (seatCondition.isFalse() && field.equalsIgnoreCase(Constants.SEAT)) {
				seatCondition.setTrue();
			}

			if (paymentFormCondition.isFalse() && field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
				paymentFormCondition.setTrue();
			}

			if (entityEnum == EntityEnum.HITS) {
				if (field.equalsIgnoreCase(Constants.IS_RULE_HIT)
						|| field.equalsIgnoreCase(Constants.IS_WATCHLIST_HIT)) {

					joinEntities.remove(entityEnum);
					String value = (queryTerm.getValue() != null && queryTerm.getValue().length == 1)
							? queryTerm.getValue()[0]
							: "0";

					if (queryType == EntityEnum.FLIGHT) {
						// 1 - YES, 0 - NO
						if (value.equals("1")) {
							where.append(Constants.EXISTS_HITS_PREFIX);
						} else {
							where.append(Constants.NOT_EXISTS_HITS_PREFIX);
						}

						where.append(" ").append(EntityEnum.HITS.getAlias()).append(Constants.HITS_FLIGHT_REF)
								.append(" ").append(Constants.FROM).append(" ").append(EntityEnum.HITS.getEntityName())
								.append(" ").append(EntityEnum.HITS.getAlias()).append(" ").append(Constants.WHERE)
								.append(" ").append(EntityEnum.HITS.getAlias()).append(Constants.HITS_FLIGHT_REF)
								.append(" = ").append(EntityEnum.FLIGHT.getAlias()).append(Constants.ID).append(" ");

						if (field.equalsIgnoreCase(Constants.IS_RULE_HIT)) {
							where.append(Constants.AND + " ").append(EntityEnum.HITS.getAlias()).append(".")
									.append(Constants.RULE_HIT_TYPE).append(")");
						} else {
							where.append(Constants.AND + " (").append(EntityEnum.HITS.getAlias()).append(".")
									.append(Constants.PASSENGER_HIT_TYPE).append(" ").append(Constants.OR).append(" ")
									.append(EntityEnum.HITS.getAlias()).append(".").append(Constants.DOCUMENT_HIT_TYPE)
									.append("))");
						}
					} else if (queryType == EntityEnum.PASSENGER) {
						if (value.equals("1")) {
							where.append(Constants.EXISTS_HITS_PREFIX);
						} else {
							where.append(Constants.NOT_EXISTS_HITS_PREFIX);
						}

						where.append(" ").append(EntityEnum.HITS.getAlias()).append(Constants.HITS_PASSENGER_REF)
								.append(" ").append(Constants.FROM).append(" ").append(EntityEnum.HITS.getEntityName())
								.append(" ").append(EntityEnum.HITS.getAlias()).append(" ").append(Constants.WHERE)
								.append(" ").append(EntityEnum.HITS.getAlias()).append(Constants.HITS_PASSENGER_REF)
								.append(" = ").append(EntityEnum.PASSENGER.getAlias()).append(Constants.ID).append(" ");

						if (field.equalsIgnoreCase(Constants.IS_RULE_HIT)) {
							where.append(Constants.AND + " ").append(EntityEnum.HITS.getAlias()).append(".")
									.append(Constants.RULE_HIT_TYPE).append(")");
						} else {
							where.append(Constants.AND + " (").append(EntityEnum.HITS.getAlias()).append(".")
									.append(Constants.PASSENGER_HIT_TYPE).append(" ").append(Constants.OR).append(" ")
									.append(EntityEnum.HITS.getAlias()).append(".").append(Constants.DOCUMENT_HIT_TYPE)
									.append("))");
						}
					}
				} else if (field.equalsIgnoreCase(Constants.HITS_ID)) {
					positionalParameter.increment(); // parameter position in the query

					if (queryType == EntityEnum.FLIGHT) {
						where.append("(").append(EntityEnum.FLIGHT.getAlias()).append(Constants.ID).append(" = ")
								.append(EntityEnum.HITS.getAlias()).append(Constants.HITS_FLIGHT_REF).append(" ")
								.append(Constants.AND).append(" ").append(EntityEnum.HITS.getAlias())
								.append(Constants.ID).append(" ").append(opEnum.getOperator()).append(" ?")
								.append(positionalParameter).append(")");
					} else if (queryType == EntityEnum.PASSENGER) {
						where.append("(").append(EntityEnum.PASSENGER.getAlias()).append(Constants.ID).append(" = ")
								.append(EntityEnum.HITS.getAlias()).append(Constants.HITS_PASSENGER_REF).append(" ")
								.append(Constants.AND).append(" ").append(EntityEnum.HITS.getAlias())
								.append(Constants.ID).append(" ").append(opEnum.getOperator()).append(" ?")
								.append(positionalParameter).append(")");
					}
				}
			} else if (entityEnum == EntityEnum.BOOKING_DETAIL) {

				// These four operators don't have any value ex. where firstname IS NULL
				if (OperatorEnum.IS_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NULL.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_NULL.toString().equalsIgnoreCase(operator)) {

					where.append(EntityEnum.BOOKING_DETAIL.getAlias()).append(".").append(field).append(" ")
							.append(opEnum.getOperator());
				} else if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_BETWEEN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (values != null && values.size() == 2) {
						positionalParameter.increment(); // parameter position in the query

						where.append(EntityEnum.BOOKING_DETAIL.getAlias()).append(".").append(field).append(" ")
								.append(opEnum.getOperator()).append(" ?").append(positionalParameter);
						positionalParameter.increment();
						where.append(" " + Constants.AND + " ?").append(positionalParameter);
					}

				} else if (OperatorEnum.IN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)) {
					positionalParameter.increment(); // parameter position in the query

					where.append(EntityEnum.BOOKING_DETAIL.getAlias()).append(".").append(field).append(" ")
							.append(opEnum.getOperator()).append(" (?").append(positionalParameter).append(")");
				} else {
					positionalParameter.increment(); // parameter position in the query

					where.append(EntityEnum.BOOKING_DETAIL.getAlias()).append(".").append(field).append(" ")
							.append(opEnum.getOperator()).append(" ?").append(positionalParameter);

				}

			} else if (entityEnum.toString().equalsIgnoreCase(Constants.EMAIL) ||
					entityEnum.toString().equalsIgnoreCase(Constants.CREDITCARD) ||
					entityEnum.toString().equalsIgnoreCase(Constants.DOCUMENT)) {

				/* Due to a lack of direct connection between passenger and email/creditcard/etc...
				 * IN/NOT_IN (and equals/not equals) behaviour does not work as intended through HQL, particularly because the order in
				 * which the HQL separates the groupings goes from smallest -> largest, which allows for bad data
				 * Example: A PNR 1, has 2 emails with domains: Gmail (2), and Hotmal (3). If Not In Gmail is used
				 * 1 -> 2 would be false, but 1 -> 3 is true. This returns pnr 1 when it DOES contain gmail (which
				 * in turn returns a passenger erroneously)
				 * */

				if (OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_EQUAL.toString().equalsIgnoreCase(operator)
							) {
					positionalParameter.increment();
					//The inner join being made here swaps the value of the in or not in operator value
					//in order to produce valid results, as we take the intersection of the haves vs have-nots
					String whereClauseBridgeEntity;
					String whereClauseBridgeEntityAlias;
					// not in produces equivalent problems for not equal

					//Different where clauses for document vs email/credit card
					if(entityEnum.toString().equalsIgnoreCase((Constants.DOCUMENT))) {
						whereClauseBridgeEntityAlias = "p";
						whereClauseBridgeEntity = "Passenger";
					} else {
						whereClauseBridgeEntityAlias = "pnr";
						whereClauseBridgeEntity = "Pnr";
					}
					//Construct special inner select statement in where clause with conditions
					where.append(entityEnum.getAlias() + "." + field + " ");
					where.append("not in" + (" (?") + positionalParameter + ") ");
					where.append(Constants.AND + " " + whereClauseBridgeEntityAlias + ".id not in (");
					where.append(Constants.SELECT + " " + whereClauseBridgeEntityAlias +".id from " + whereClauseBridgeEntity + " " + whereClauseBridgeEntityAlias);
					where.append(Constants.JOIN + whereClauseBridgeEntityAlias + entityEnum.getEntityReference() + " " + entityEnum.getAlias() + " ");
					where.append(Constants.WHERE + " " + entityEnum.getAlias() + "." + field + " " + "in" + " (?" + positionalParameter + "))");

				} else {
					//default where
					positionalParameter.increment();
					where.append(entityEnum.getAlias()).append(".").append(field).append(" ")
							.append(opEnum.getOperator()).append(" ?").append(positionalParameter);
				}

			} else {
				// These four operators don't have any value ex. where firstname IS NULL
				if (OperatorEnum.IS_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NULL.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_NULL.toString().equalsIgnoreCase(operator)) {

					if (field.equalsIgnoreCase(Constants.SEAT)) {
						if (entityEnum == EntityEnum.PASSENGER) {
							where.append("(s.apis = true");
						} else if (entityEnum == EntityEnum.PNR) {
							where.append("(s.apis = false");
						}
						where.append(" and s.number ").append(opEnum.getOperator()).append(")");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType ").append(opEnum.getOperator())
								.append(" ?").append(positionalParameter).append(")");
					} else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" f.id in pnfl.id  and ").append(entityEnum.getAlias()).append(".").append(field)
								.append(" ").append(opEnum.getOperator()).append(" ?").append(positionalParameter);
					} else {
						where.append(entityEnum.getAlias()).append(".").append(field).append(" ")
								.append(opEnum.getOperator());
					}
				} else if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_BETWEEN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (values != null && values.size() == 2) {
						positionalParameter.increment(); // parameter position in the query

						where.append(entityEnum.getAlias()).append(".").append(field).append(" ")
								.append(opEnum.getOperator()).append(" ?").append(positionalParameter);
						positionalParameter.increment();
						where.append(" " + Constants.AND + " ?").append(positionalParameter);
					}
				} else if (OperatorEnum.IN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)) {
					positionalParameter.increment(); // parameter position in the query

					if (field.equalsIgnoreCase(Constants.SEAT)) {
						if (entityEnum == EntityEnum.PASSENGER) {
							where.append("(s.apis = true");
						} else if (entityEnum == EntityEnum.PNR) {
							where.append("(s.apis = false");
						}
						where.append(" and p.flight.id = s.flight.id and s.number ").append(opEnum.getOperator())
								.append(" (?").append(positionalParameter).append("))");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType ").append(opEnum.getOperator())
								.append(" ?").append(positionalParameter).append(")");
					} else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" p.flight.id in pnfl.id  and ").append(entityEnum.getAlias()).append(".")
								.append(field).append(" ").append(opEnum.getOperator()).append(" ?")
								.append(positionalParameter);
					} else {
						where.append(entityEnum.getAlias()).append(".").append(field).append(" ")
								.append(opEnum.getOperator()).append(" (?").append(positionalParameter).append(")");
					}
				} else {
					positionalParameter.increment(); // parameter position in the query

					if (field.equalsIgnoreCase(Constants.SEAT)) {
						if (entityEnum == EntityEnum.PASSENGER) {
							where.append("(s.apis = true");
						} else if (entityEnum == EntityEnum.PNR) {
							where.append("(s.apis = false");
						}
						where.append(" and p.flight.id = s.flight.id and s.number ").append(opEnum.getOperator())
								.append(" ?").append(positionalParameter).append(")");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType ").append(opEnum.getOperator())
								.append(" ?").append(positionalParameter).append(")");
					} else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" p.flight.id in pnfl.id  and ").append(entityEnum.getAlias()).append(".")
								.append(field).append(" ").append(opEnum.getOperator()).append(" ?")
								.append(positionalParameter);
					} else {
						where.append(entityEnum.getAlias()).append(".").append(field).append(" ")
								.append(opEnum.getOperator()).append(" ?").append(positionalParameter);
					}
				}
			}
		}

	}

	private static String generateJoinCondition(List<EntityEnum> entity, EntityEnum queryType, String joinType)
			throws InvalidQueryRepositoryException {
		StringBuilder joinCondition = new StringBuilder();

		if (entity == null) {
			throw new InvalidQueryRepositoryException("No Entity specified for join", null);
		}

		for (EntityEnum entityEnum : entity) {
			joinCondition.append(getJoinCondition(entityEnum, queryType, joinType));
		}

		return joinCondition.toString();
	}

	private static String getJoinCondition(EntityEnum entity, EntityEnum queryType, String joinType)
			throws InvalidQueryRepositoryException {

		String joinCondition = "";

		switch (entity.getEntityName().toUpperCase()) {
		case Constants.ADDRESS:
			joinCondition = joinType + EntityEnum.PNR.getAlias() + EntityEnum.ADDRESS.getEntityReference()
					+ " " + EntityEnum.ADDRESS.getAlias();
			break;
		case Constants.AGENCY:
			joinCondition = joinType + EntityEnum.PNR.getAlias()
					+ EntityEnum.TRAVEL_AGENCY.getEntityReference() + " " + EntityEnum.TRAVEL_AGENCY.getAlias();
			break;
		case Constants.DWELLTIME:
			joinCondition = joinType + EntityEnum.PNR.getAlias() + EntityEnum.DWELL_TIME.getEntityReference()
					+ " " + EntityEnum.DWELL_TIME.getAlias();
			break;
		case Constants.PAYMENTFORM:
			joinCondition = joinType + EntityEnum.PNR.getAlias()
					+ EntityEnum.FORM_OF_PAYMENT.getEntityReference() + " " + EntityEnum.FORM_OF_PAYMENT.getAlias();
			break;
		case Constants.CREDITCARD:
			joinCondition = joinType + EntityEnum.PNR.getAlias()
					+ EntityEnum.CREDIT_CARD.getEntityReference() + " " + EntityEnum.CREDIT_CARD.getAlias();
			break;
		case Constants.DOCUMENT:
			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.DOCUMENT.getEntityReference() + " " + EntityEnum.DOCUMENT.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.DOCUMENT.getEntityReference() + " " + EntityEnum.DOCUMENT.getAlias();
			}
			break;
		case Constants.SEAT:
				joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.SEAT.getEntityReference() + " " + EntityEnum.SEAT.getAlias();
		    break;
		case Constants.EMAIL:
			joinCondition = joinType + EntityEnum.PNR.getAlias() + EntityEnum.EMAIL.getEntityReference()
					+ " " + EntityEnum.EMAIL.getAlias();
			break;
		case Constants.FLIGHT:
			joinCondition = joinType + EntityEnum.PASSENGER.getAlias() + EntityEnum.FLIGHT.getEntityReference()
					+ " " + EntityEnum.FLIGHT.getAlias();
			break;
		case Constants.BOOKINGDETAIL:

			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
			}
			break;
		case Constants.FREQUENTFLYER:
			joinCondition = joinType + EntityEnum.PNR.getAlias()
					+ EntityEnum.FREQUENT_FLYER.getEntityReference() + " " + EntityEnum.FREQUENT_FLYER.getAlias();
			break;
		case Constants.HITS:
			joinCondition = ", " + EntityEnum.HITS.getEntityName() + " " + EntityEnum.HITS.getAlias();
			break;
		case Constants.PASSENGER:
			joinCondition = Constants.JOIN + EntityEnum.FLIGHT.getAlias() + EntityEnum.PASSENGER.getEntityReference()
					+ " " + EntityEnum.PASSENGER.getAlias();
			break;
		case Constants.PHONE:
			joinCondition = joinType + EntityEnum.PNR.getAlias() + EntityEnum.PHONE.getEntityReference()
					+ " " + EntityEnum.PHONE.getAlias();
			break;
		case Constants.SAVED_SEGMENT:
			joinCondition = joinType + EntityEnum.PNR.getAlias() + EntityEnum.SAVED_SEGMENT.getEntityReference()
					+ " " + EntityEnum.SAVED_SEGMENT.getAlias();
			break;
		case Constants.PNR:
			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = joinType + EntityEnum.FLIGHT.getAlias() + EntityEnum.PNR.getEntityReference()
						+ " " + EntityEnum.PNR.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.PNR.getEntityReference() + " " + EntityEnum.PNR.getAlias();
			}
			break;
		case Constants.BAG:
			// TO-DO: Revisit This For Flight Queries, currently both Bag and Fpax only work
			// with passenger queries
			joinCondition = joinType + EntityEnum.PASSENGER.getAlias() + EntityEnum.BAG.getEntityReference()
					+ " " + EntityEnum.BAG.getAlias();
			break;
		case Constants.FLIGHTPAX:
			joinCondition = joinType + EntityEnum.PASSENGER.getAlias()
					+ EntityEnum.FLIGHT_PAX.getEntityReference() + " " + EntityEnum.FLIGHT_PAX.getAlias();
			break;
		default:
			throw new InvalidQueryRepositoryException("Invalid Entity: " + entity.getEntityName(), null);
		}

		return joinCondition;
	}

	private static boolean hasPNREntity(List<EntityEnum> entity) {

		if (entity != null && !entity.isEmpty()) {

			for (EntityEnum entityEnum : entity) {
				if (entityEnum == EntityEnum.ADDRESS
						|| entityEnum == EntityEnum.CREDIT_CARD
						|| entityEnum == EntityEnum.EMAIL
						|| entityEnum == EntityEnum.FREQUENT_FLYER
						|| entityEnum == EntityEnum.PHONE
						|| entityEnum == EntityEnum.PNR
						|| entityEnum == EntityEnum.TRAVEL_AGENCY
						|| entityEnum == EntityEnum.DWELL_TIME
						|| entityEnum == EntityEnum.FORM_OF_PAYMENT
						|| entityEnum == EntityEnum.SAVED_SEGMENT) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("SameParameterValue") // Meant to be extended.
	private static boolean hasField(QueryObject queryObject, String fieldName) {
		boolean result = false;
		QueryEntity queryEntity;

		List<QueryEntity> rules = queryObject.getRules();
		if (rules != null) {
			for (QueryEntity rule : rules) {
				queryEntity = rule;
				if (queryEntity instanceof QueryTerm) {
					QueryTerm queryTerm = (QueryTerm) queryEntity;
					String field = queryTerm.getField();
					if (field != null && field.equalsIgnoreCase(fieldName))
						result = true;

				}

			}

		}

		return result;
	}

	/*
	 * private static boolean isDwellQuery(List<EntityEnum> entity) { if (entity !=
	 * null && !entity.isEmpty()) { Iterator<EntityEnum> it = entity.iterator();
	 * 
	 * while (it.hasNext()) { EntityEnum entityEnum = it.next();
	 * 
	 * if (entityEnum == EntityEnum.DWELL_TIME) { return true; } } } return false; }
	 */
}

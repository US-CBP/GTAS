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
	private static final Logger logger = LoggerFactory.getLogger(JPQLGenerator.class);

	public static String generateQuery(QueryEntity queryEntity, EntityEnum queryType)
			throws InvalidQueryRepositoryException {
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

			generateWhereCondition(queryEntity, queryType, joinEntities, where, seatCondition, positionalParameter,
					level, paymentFormCondition);

			if (queryType == EntityEnum.FLIGHT) {
				queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + " " + Constants.FROM
						+ " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias() +
						Constants.LEFT_JOIN + EntityEnum.FLIGHT.getAlias() + ".passengers " + EntityEnum.PASSENGER.getAlias();

				if (seatCondition.isTrue()) {
					joinEntities.add(EntityEnum.PASSENGER);
				}

				/*
				 * if (paymentFormCondition.isTrue()) { // PNR doesn't need to be added in the
				 * same way if paymentForm is a requirement //
				 * joinEntities.remove(EntityEnum.PNR); }
				 */
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
					join = generateJoinCondition(joinEntities, queryType);
				}

				boolean hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);

				if (hasFormOfPayment) {
					join += " join " + EntityEnum.PNR.getAlias() + ".flights pnfl ";
				}

				if (seatCondition.isTrue()) {
					join += " left join p.seatAssignments s ";
				}
				if (paymentFormCondition.isTrue()) {
					// joins to pnr -> paymentForms through flight
					join += " left join pnr.paymentForms pf ";

				}

				query = queryPrefix + join + " " + Constants.WHERE + " " + where;
			} else if (queryType == EntityEnum.PASSENGER) {
				where.append(" and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))");

				queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.PASSENGER.getAlias() + Constants.ID + ", "
						+ EntityEnum.PASSENGER.getAlias() + ", p.flight " + Constants.FROM + " "
						+ EntityEnum.PASSENGER.getEntityName() + " " + EntityEnum.PASSENGER.getAlias()
						+ " left join p.flight f ";

				// if (paymentFormCondition.isTrue()) {
				// // joinEntities.remove(EntityEnum.PNR);
				// }

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

					join = generateJoinCondition(joinEntities, queryType);
				}

				boolean hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);

				if (hasFormOfPayment) {
					join += " join " + EntityEnum.PNR.getAlias() + ".flights pnfl ";
				}

				if (seatCondition.isTrue()) {
					join += " left join p.seatAssignments s ";
				}

				if (paymentFormCondition.isTrue()) {
					join += " left join pnr.paymentForms pf ";
				}

				query = queryPrefix + join + " " + Constants.WHERE + " " + where;
			}
			logger.info("Parsed Query: " + query);
		}

		return query;
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
					where.append(Constants.LEFT_JOIN + whereClauseBridgeEntityAlias + entityEnum.getEntityReference() + " " + entityEnum.getAlias() + " ");
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

	private static String generateJoinCondition(List<EntityEnum> entity, EntityEnum queryType)
			throws InvalidQueryRepositoryException {
		StringBuilder joinCondition = new StringBuilder();

		if (entity == null) {
			throw new InvalidQueryRepositoryException("No Entity specified for join", null);
		}

		for (EntityEnum entityEnum : entity) {
			joinCondition.append(getJoinCondition(entityEnum, queryType));
		}

		return joinCondition.toString();
	}

	public static String getJoinCondition(EntityEnum entity, EntityEnum queryType)
			throws InvalidQueryRepositoryException {

		String joinCondition = "";

		switch (entity.getEntityName().toUpperCase()) {
		case Constants.ADDRESS:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias() + EntityEnum.ADDRESS.getEntityReference()
					+ " " + EntityEnum.ADDRESS.getAlias();
			break;
		case Constants.AGENCY:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias()
					+ EntityEnum.TRAVEL_AGENCY.getEntityReference() + " " + EntityEnum.TRAVEL_AGENCY.getAlias();
			break;
		case Constants.DWELLTIME:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias() + EntityEnum.DWELL_TIME.getEntityReference()
					+ " " + EntityEnum.DWELL_TIME.getAlias();
			break;
		case Constants.PAYMENTFORM:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias()
					+ EntityEnum.FORM_OF_PAYMENT.getEntityReference() + " " + EntityEnum.FORM_OF_PAYMENT.getAlias();
			break;
		case Constants.CREDITCARD:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias()
					+ EntityEnum.CREDIT_CARD.getEntityReference() + " " + EntityEnum.CREDIT_CARD.getAlias();
			break;
		case Constants.DOCUMENT:
			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.DOCUMENT.getEntityReference() + " " + EntityEnum.DOCUMENT.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.DOCUMENT.getEntityReference() + " " + EntityEnum.DOCUMENT.getAlias();
			}
			break;
		case Constants.SEAT:
				joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.SEAT.getEntityReference() + " " + EntityEnum.SEAT.getAlias();
		    break;
		case Constants.EMAIL:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias() + EntityEnum.EMAIL.getEntityReference()
					+ " " + EntityEnum.EMAIL.getAlias();
			break;
		case Constants.FLIGHT:
			joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.FLIGHT.getEntityReference()
					+ " " + EntityEnum.FLIGHT.getAlias();
			break;
		case Constants.BOOKINGDETAIL:

			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = Constants.LEFT_JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = Constants.LEFT_JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
			}
			break;
		case Constants.FREQUENTFLYER:
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias()
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
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PNR.getAlias() + EntityEnum.PHONE.getEntityReference()
					+ " " + EntityEnum.PHONE.getAlias();
			break;
		case Constants.PNR:
			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = Constants.LEFT_JOIN + EntityEnum.FLIGHT.getAlias() + EntityEnum.PNR.getEntityReference()
						+ " " + EntityEnum.PNR.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = Constants.LEFT_JOIN + EntityEnum.PASSENGER.getAlias()
						+ EntityEnum.PNR.getEntityReference() + " " + EntityEnum.PNR.getAlias();
			}
			break;
		case Constants.BAG:
			// TO-DO: Revisit This For Flight Queries, currently both Bag and Fpax only work
			// with passenger queries
			joinCondition = Constants.LEFT_JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.BAG.getEntityReference()
					+ " " + EntityEnum.BAG.getAlias();
			break;
		case Constants.FLIGHTPAX:
			joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias()
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
				if (entityEnum == EntityEnum.ADDRESS || entityEnum == EntityEnum.CREDIT_CARD
						|| entityEnum == EntityEnum.EMAIL || entityEnum == EntityEnum.FREQUENT_FLYER
						|| entityEnum == EntityEnum.PHONE || entityEnum == EntityEnum.PNR
						|| entityEnum == EntityEnum.TRAVEL_AGENCY || entityEnum == EntityEnum.DWELL_TIME
						|| entityEnum == EntityEnum.FORM_OF_PAYMENT) {
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

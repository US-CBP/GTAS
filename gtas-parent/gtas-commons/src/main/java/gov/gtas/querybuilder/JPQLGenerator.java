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
import java.util.Iterator;
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

	/**
	 * 
	 * @param queryObject
	 * @param queryType
	 * @return
	 * @throws InvalidQueryRepositoryException
	 */
	public static String generateQuery(QueryEntity queryEntity, EntityEnum queryType)
			throws InvalidQueryRepositoryException {
		String query = "";

		if (queryEntity != null && queryType != null) {
			String queryPrefix = "";
			List<EntityEnum> joinEntities = new ArrayList<>();
			StringBuilder where = new StringBuilder();
			String join = "";
			// Since flight and passenger are no longer mapped together, this manual join is
			// done via connecting through the Flight_Passenger table. The trailing "and" is
			// required since this is a
			// where clause itself and allows for the addition of normal where clauses after
			// it.
			String crossJoinForFlightPax = "";
			MutableBoolean seatCondition = new MutableBoolean();
			MutableBoolean paymentFormCondition = new MutableBoolean();
			MutableInt positionalParameter = new MutableInt();
			MutableInt level = new MutableInt();
			logger.debug("Parsing QueryObject...");
			
			QueryObject queryObject = (QueryObject)queryEntity;
			

			generateWhereCondition(queryEntity, queryType, joinEntities, where, seatCondition, positionalParameter,
					level, paymentFormCondition);

		
			
			if (queryType == EntityEnum.FLIGHT) {
				queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + " " + Constants.FROM
						+ " " + EntityEnum.FLIGHT.getEntityName() + " " + EntityEnum.FLIGHT.getAlias();

				if (seatCondition.isTrue()) {
					// joinEntities.add(EntityEnum.PASSENGER); //TO-DO Seemingly redundant at this
					// point due to redesign, consider removal
				}

				if (paymentFormCondition.isTrue()) {
					// PNR doesn't need to be added in the same way if paymentForm is a requirement
					// joinEntities.remove(EntityEnum.PNR);
				}

				if (!joinEntities.isEmpty()) {

					// remove Flight from the List because it is already
					// part of the queryPrefix statement
					joinEntities.remove(EntityEnum.FLIGHT);

					if (!joinEntities.isEmpty()) {

						// remove Passenger if there is a Document entity
						// because that has a join with Passenger already
						if (joinEntities.contains(EntityEnum.DOCUMENT)) {
							joinEntities.remove(EntityEnum.PASSENGER);
						}

						// add join to PNR if there is a PNR
						// entity in the query
						if (hasPNREntity(joinEntities)) {
							joinEntities.remove(EntityEnum.PNR);
							joinEntities.add(0, EntityEnum.PNR);
						}

						if (joinEntities.contains(EntityEnum.PASSENGER) || joinEntities.contains(EntityEnum.DOCUMENT) || joinEntities.contains(EntityEnum.BOOKING_DETAIL)) {
							// If document, needs new prefix in order to use join p.documents
							// join flights.passengers does not work as they are not mapped to one another
							// by hibernate, so the join must be removed if it exists
							joinEntities.remove(EntityEnum.PASSENGER);
							// prefix for flight selection must be altered in order to incorporate the
							// appropriate self joins on passenger due to lack of hibernate managed mapping
							// The flight side is unique in both the prefix is not already consistent of a
							// join (like passenger was) but also the cross join is altered to only select
							// on flight
														
							queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.FLIGHT.getAlias() + " "
									+ Constants.FROM + " " + EntityEnum.PASSENGER.getEntityName() + " "
									+ EntityEnum.PASSENGER.getAlias() + ", " + EntityEnum.FLIGHT.getEntityName() + " "
									+ EntityEnum.FLIGHT.getAlias();
							// the subsequent crossjoin that the adjusted prefix is only required if
							// document, or a passenger join is present.
							crossJoinForFlightPax = Constants.EXISTS + "(" + Constants.SELECT + " fp " + Constants.FROM
									+ " FlightPassenger fp " + Constants.WHERE + " " + EntityEnum.PASSENGER.getAlias()
									+ Constants.ID + " = fp.passengerId " + Constants.AND + " "
									+ EntityEnum.FLIGHT.getAlias() + Constants.ID + " = fp.flightId) " + Constants.AND;
						}
					}

					join = generateJoinCondition(joinEntities, queryType);
				}

				boolean  hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);
				
				if(hasFormOfPayment)
				{
					join += " join "+ EntityEnum.PNR.getAlias() +".flights pnfl ";
				}
				
				if (seatCondition.isTrue()) {
					join += " left join p.seatAssignments s ";
				}
				if (paymentFormCondition.isTrue()) {
					// joins to pnr -> paymentForms through flight
					join += " left join pnr.paymentForms pf ";
				
				}
				

				query = queryPrefix + join + " " + Constants.WHERE + " " + crossJoinForFlightPax + " " + where;
			} else if (queryType == EntityEnum.PASSENGER) {


					/*
					 * queryPrefix = Constants.SELECT_DISTINCT + " " +
					 * EntityEnum.PASSENGER.getAlias() + Constants.ID + ", " +
					 * EntityEnum.PASSENGER.getAlias() + ", " + EntityEnum.FLIGHT.getAlias() + " " +
					 * Constants.FROM + " " + EntityEnum.PASSENGER.getEntityName() + " " +
					 * EntityEnum.PASSENGER.getAlias() + Constants.JOIN +
					 * EntityEnum.PASSENGER.getAlias() + EntityEnum.FLIGHT.getEntityReference() +
					 * " " + EntityEnum.FLIGHT.getAlias();
					 */

					// Replacement prefix for the new structure that does not have flight/passengers
					// mapped together by hibernate, this means we have to do self joins in essence.
					queryPrefix = Constants.SELECT_DISTINCT + " " + EntityEnum.PASSENGER.getAlias() + Constants.ID
							+ ", " + EntityEnum.PASSENGER.getAlias() + ", " + EntityEnum.FLIGHT.getAlias() + " "
							+ Constants.FROM + " " + EntityEnum.PASSENGER.getEntityName() + " "
							+ EntityEnum.PASSENGER.getAlias() + ", " + EntityEnum.FLIGHT.getEntityName() + " "
							+ EntityEnum.FLIGHT.getAlias();
					
					
					// Cross join is always required for Passenger side queries
					crossJoinForFlightPax = Constants.EXISTS + "(" + Constants.SELECT + " fp " + Constants.FROM
							+ " FlightPassenger fp " + Constants.WHERE + " " + EntityEnum.PASSENGER.getAlias()
							+ Constants.ID + " = fp.passengerId " + Constants.AND + " " + EntityEnum.FLIGHT.getAlias()
							+ Constants.ID + " = fp.flightId) " + Constants.AND;
			
					if (paymentFormCondition.isTrue()) {
						// joinEntities.remove(EntityEnum.PNR);
					}

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
					
					boolean  hasFormOfPayment = hasField(queryObject, Constants.FORM_OF_PAYMENT);
					
					if(hasFormOfPayment)
					{
						join += " join "+ EntityEnum.PNR.getAlias() +".flights pnfl ";
					}
					

					if (seatCondition.isTrue()) {
						join += " left join p.seatAssignments s ";
					}

					if (paymentFormCondition.isTrue()) {
						join += " left join pnr.paymentForms pf ";
					}

					query = queryPrefix + join + " " + Constants.WHERE + " " + crossJoinForFlightPax + " " + where;
					/*
					 * if(isDwellQuery(joinEntities)){ query +=
					 * " and dwell.location = f.destination"; }
					 */

				
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
	 * @throws InvalidQueryRepositoryException
	 */
	private static void generateWhereCondition(QueryEntity queryEntity, EntityEnum queryType,
			List<EntityEnum> joinEntities, StringBuilder where, MutableBoolean seatCondition,
			MutableInt positionalParameter, MutableInt level, MutableBoolean paymentFormCondition)
			throws InvalidQueryRepositoryException {
		QueryObject queryObject = null;
		QueryTerm queryTerm = null;
		String condition = null;

		if (queryEntity instanceof QueryObject) {
			queryObject = (QueryObject) queryEntity;
			condition = queryObject.getCondition();
			level.increment();

			List<QueryEntity> rules = queryObject.getRules();

			if (level.intValue() > 1) {
				where.append("(");
			}

			int index = 0;
			for (QueryEntity rule : rules) {

				if (index > 0) {
					where.append(" " + condition + " ");
				}
				generateWhereCondition(rule, queryType, joinEntities, where, seatCondition, positionalParameter, level,
						paymentFormCondition);
				index++;
			}

			if (level.intValue() > 1) {
				where.append(")");
			}
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

						where.append(" " + EntityEnum.HITS.getAlias() + Constants.HITS_FLIGHT_REF + " " + Constants.FROM
								+ " " + EntityEnum.HITS.getEntityName() + " " + EntityEnum.HITS.getAlias() + " "
								+ Constants.WHERE + " " + EntityEnum.HITS.getAlias() + Constants.HITS_FLIGHT_REF + " = "
								+ EntityEnum.FLIGHT.getAlias() + Constants.ID + " ");

						if (field.equalsIgnoreCase(Constants.IS_RULE_HIT)) {
							where.append(Constants.AND + " " + EntityEnum.HITS.getAlias() + "."
									+ Constants.RULE_HIT_TYPE + ")");
						} else {
							where.append(Constants.AND + " (" + EntityEnum.HITS.getAlias() + "."
									+ Constants.PASSENGER_HIT_TYPE + " " + Constants.OR + " "
									+ EntityEnum.HITS.getAlias() + "." + Constants.DOCUMENT_HIT_TYPE + "))");
						}
					} else if (queryType == EntityEnum.PASSENGER) {
						if (value.equals("1")) {
							where.append(Constants.EXISTS_HITS_PREFIX);
						} else {
							where.append(Constants.NOT_EXISTS_HITS_PREFIX);
						}

						where.append(" " + EntityEnum.HITS.getAlias() + Constants.HITS_PASSENGER_REF + " "
								+ Constants.FROM + " " + EntityEnum.HITS.getEntityName() + " "
								+ EntityEnum.HITS.getAlias() + " " + Constants.WHERE + " " + EntityEnum.HITS.getAlias()
								+ Constants.HITS_PASSENGER_REF + " = " + EntityEnum.PASSENGER.getAlias() + Constants.ID
								+ " ");

						if (field.equalsIgnoreCase(Constants.IS_RULE_HIT)) {
							where.append(Constants.AND + " " + EntityEnum.HITS.getAlias() + "."
									+ Constants.RULE_HIT_TYPE + ")");
						} else {
							where.append(Constants.AND + " (" + EntityEnum.HITS.getAlias() + "."
									+ Constants.PASSENGER_HIT_TYPE + " " + Constants.OR + " "
									+ EntityEnum.HITS.getAlias() + "." + Constants.DOCUMENT_HIT_TYPE + "))");
						}
					}
				} else if (field.equalsIgnoreCase(Constants.HITS_ID)) {
					positionalParameter.increment(); // parameter position in the query

					if (queryType == EntityEnum.FLIGHT) {
						where.append("(" + EntityEnum.FLIGHT.getAlias() + Constants.ID + " = "
								+ EntityEnum.HITS.getAlias() + Constants.HITS_FLIGHT_REF + " " + Constants.AND + " "
								+ EntityEnum.HITS.getAlias() + Constants.ID + " " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					} else if (queryType == EntityEnum.PASSENGER) {
						where.append("(" + EntityEnum.PASSENGER.getAlias() + Constants.ID + " = "
								+ EntityEnum.HITS.getAlias() + Constants.HITS_PASSENGER_REF + " " + Constants.AND + " "
								+ EntityEnum.HITS.getAlias() + Constants.ID + " " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					}
				}
			}

			else if (entityEnum == EntityEnum.BOOKING_DETAIL) {

				// These four operators don't have any value ex. where firstname IS NULL
				if (OperatorEnum.IS_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_EMPTY.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NULL.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.IS_NOT_NULL.toString().equalsIgnoreCase(operator)) {

					where.append(EntityEnum.BOOKING_DETAIL.getAlias() + "." + field + " " + opEnum.getOperator());
				} else if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_BETWEEN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (values != null && values.size() == 2) {
						positionalParameter.increment(); // parameter position in the query

						where.append(EntityEnum.BOOKING_DETAIL.getAlias() + "." + field + " " + opEnum.getOperator()
								+ " ?" + positionalParameter);
						positionalParameter.increment();
						where.append(" " + Constants.AND + " ?" + positionalParameter);
					}

				} else if (OperatorEnum.IN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)) {
					positionalParameter.increment(); // parameter position in the query

					where.append(EntityEnum.BOOKING_DETAIL.getAlias() + "." + field + " " + opEnum.getOperator() + " (?"
							+ positionalParameter + ")");
				} else {
					positionalParameter.increment(); // parameter position in the query

					where.append(EntityEnum.BOOKING_DETAIL.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
							+ positionalParameter);

				}

			}

			else {
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
						where.append(" and s.number " + opEnum.getOperator() + ")");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					} 
					else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" f.id in pnfl.id  and "+entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
								+ positionalParameter);
					}
					else {
						where.append(entityEnum.getAlias() + "." + field + " " + opEnum.getOperator());
					}
				} else if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_BETWEEN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (values != null && values.size() == 2) {
						positionalParameter.increment(); // parameter position in the query

						where.append(entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
								+ positionalParameter);
						positionalParameter.increment();
						where.append(" " + Constants.AND + " ?" + positionalParameter);
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
						where.append(" and f.id = s.flight.id and s.number " + opEnum.getOperator() + " (?"
								+ positionalParameter + "))");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					} 
					else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" f.id in pnfl.id  and "+entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
								+ positionalParameter);
					}
					
					else {
						where.append(entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " (?"
								+ positionalParameter + ")");
					}
				} else {
					positionalParameter.increment(); // parameter position in the query

					if (field.equalsIgnoreCase(Constants.SEAT)) {
						if (entityEnum == EntityEnum.PASSENGER) {
							where.append("(s.apis = true");
						} else if (entityEnum == EntityEnum.PNR) {
							where.append("(s.apis = false");
						}
						where.append(" and f.id = s.flight.id and s.number " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					} else if (field.equalsIgnoreCase(Constants.PAYMENTFORMS)) {
						where.append("(pnr.id = pf.pnr.id and pf.paymentType " + opEnum.getOperator() + " ?"
								+ positionalParameter + ")");
					} else if (field.equalsIgnoreCase(Constants.FORM_OF_PAYMENT)) {
						where.append(" f.id in pnfl.id  and "+entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
								+ positionalParameter);
					} else {
						where.append(entityEnum.getAlias() + "." + field + " " + opEnum.getOperator() + " ?"
								+ positionalParameter);
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

		Iterator<EntityEnum> it = entity.iterator();

		while (it.hasNext()) {
			EntityEnum entityEnum = it.next();

			joinCondition.append(getJoinCondition(entityEnum, queryType));
		}

		return joinCondition.toString();
	}

	private static String getJoinCondition(EntityEnum entity, EntityEnum queryType)
			throws InvalidQueryRepositoryException {
		String joinCondition = "";

		switch (entity.getEntityName().toUpperCase()) {
		case Constants.ADDRESS:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.ADDRESS.getEntityReference() + " "
					+ EntityEnum.ADDRESS.getAlias();
			break;
		case Constants.AGENCY:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.TRAVEL_AGENCY.getEntityReference()
					+ " " + EntityEnum.TRAVEL_AGENCY.getAlias();
			break;
		case Constants.DWELLTIME:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.DWELL_TIME.getEntityReference()
					+ " " + EntityEnum.DWELL_TIME.getAlias();
			break;
		case Constants.CREDITCARD:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.CREDIT_CARD.getEntityReference()
					+ " " + EntityEnum.CREDIT_CARD.getAlias();
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
		case Constants.EMAIL:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.EMAIL.getEntityReference() + " "
					+ EntityEnum.EMAIL.getAlias();
			break;
		case Constants.FLIGHT:
			joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.FLIGHT.getEntityReference()
					+ " " + EntityEnum.FLIGHT.getAlias();
			break;
		 case Constants.BOOKINGDETAIL:
             
              if(queryType == EntityEnum.FLIGHT) {
            	  joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
              } else if(queryType == EntityEnum.PASSENGER) {
                  joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.BOOKING_DETAIL.getEntityReference() + " " + EntityEnum.BOOKING_DETAIL.getAlias();
              }
              break;
		case Constants.FREQUENTFLYER:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.FREQUENT_FLYER.getEntityReference()
					+ " " + EntityEnum.FREQUENT_FLYER.getAlias();
			break;
		case Constants.HITS:
			joinCondition = ", " + EntityEnum.HITS.getEntityName() + " " + EntityEnum.HITS.getAlias();
			break;
		case Constants.PASSENGER:
			joinCondition = Constants.JOIN + EntityEnum.FLIGHT.getAlias() + EntityEnum.PASSENGER.getEntityReference()
					+ " " + EntityEnum.PASSENGER.getAlias();
			break;
		case Constants.PHONE:
			joinCondition = Constants.JOIN + EntityEnum.PNR.getAlias() + EntityEnum.PHONE.getEntityReference() + " "
					+ EntityEnum.PHONE.getAlias();
			break;
		case Constants.PNR:
			if (queryType == EntityEnum.FLIGHT) {
				joinCondition = Constants.JOIN + EntityEnum.FLIGHT.getAlias() + EntityEnum.PNR.getEntityReference()
						+ " " + EntityEnum.PNR.getAlias();
			} else if (queryType == EntityEnum.PASSENGER) {
				joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.PNR.getEntityReference()
						+ " " + EntityEnum.PNR.getAlias();
			}
			break;
		case Constants.BAG:
			// TO-DO: Revisit This For Flight Queries, currently both Bag and Fpax only work
			// with passenger queries
			joinCondition = Constants.JOIN + EntityEnum.PASSENGER.getAlias() + EntityEnum.BAG.getEntityReference() + " "
					+ EntityEnum.BAG.getAlias();
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
			Iterator<EntityEnum> it = entity.iterator();

			while (it.hasNext()) {
				EntityEnum entityEnum = it.next();

				if (entityEnum == EntityEnum.ADDRESS || entityEnum == EntityEnum.CREDIT_CARD
						|| entityEnum == EntityEnum.EMAIL || entityEnum == EntityEnum.FREQUENT_FLYER
						|| entityEnum == EntityEnum.PHONE || entityEnum == EntityEnum.PNR
						|| entityEnum == EntityEnum.TRAVEL_AGENCY || entityEnum == EntityEnum.DWELL_TIME) {
					return true;
				}
			}
		}

		return false;
	}
	
	
	
	private static boolean hasField(QueryObject queryObject, String fieldName) {
		boolean result = false;
		QueryEntity queryEntity = null;

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

	private static boolean isDwellQuery(List<EntityEnum> entity) {
		if (entity != null && !entity.isEmpty()) {
			Iterator<EntityEnum> it = entity.iterator();

			while (it.hasNext()) {
				EntityEnum entityEnum = it.next();

				if (entityEnum == EntityEnum.DWELL_TIME) {
					return true;
				}
			}
		}

		return false;

	}
}

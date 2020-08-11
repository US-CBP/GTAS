/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.bo.FlightPassengerLink;
import gov.gtas.model.RuleHitDetail;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.rule.builder.pnr.*;

import java.text.ParseException;
import java.util.*;

import static gov.gtas.rule.builder.RuleTemplateConstants.*;
import static gov.gtas.rule.builder.util.ConditionBuilderUtils.handleMultipleObjectTypeOnSameRule;

/**
 * Generates the "when" part of a DRL rule. procedure: new RuleConditionBuilder(
 */
public class RuleConditionBuilder {

	private List<DocumentConditionBuilder> documentConditionBuilder = new ArrayList<>();
	private List<BagConditionBuilder> bagConditionBuilder = new ArrayList<>();
	private List<SeatConditionBuilder> seatConditionBuilders = new ArrayList<>();

	private PnrRuleConditionBuilder pnrRuleConditionBuilder;
	private SeatConditionBuilder pnrSeatConditionBuilder;
	private SeatConditionBuilder apisSeatConditionBuilder;
	private FlightPaxConditionBuilder flightPaxConditionBuilder;
	private MutableFlightDetailsConditionBuilder mutableFlightDetailsConditionBuilder;
	private PassengerConditionBuilder passengerConditionBuilder;
	private PassengerDetailsConditionBuilder detailsConditionBuilder;
	private PassengerTripDetailsConditionBuilder tripDetailsConditionBuilder;
	private FlightConditionBuilder flightConditionBuilder;

	private String passengerVariableName = PASSENGER_VARIABLE_NAME;
	private String flightVariableName = FLIGHT_VARIABLE_NAME;

	private StringBuilder conditionDescriptionBuilder;

	private boolean flightCriteriaPresent;

	private Map<UUID, EntityConditionBuilder> conditionBuilderMap = new HashMap<>();

	public RuleConditionBuilder(List<QueryTerm> queryTermList) {

		/*
		 * Constructor for the Simple Rules:<br> (i.e., One Passenger, one document, one
		 * flight.)
		 * 
		 * @param entityVariableNameMap a lookup for variable name to use when
		 * generating rules<br> For example, to get the variable for passenger lookup
		 * using the key EntityEnum.PASSENGER.
		 */
		Map<UUID, EntityEnum> uuidEntityEnumMap = new HashMap<>();
		Set<UUID> orderedUUIDList = new LinkedHashSet<>();
		for (QueryTerm qt : queryTermList) {
			EntityEnum entity = EntityEnum.fromString(qt.getEntity()).orElse(EntityEnum.NOT_LISTED);
			uuidEntityEnumMap.putIfAbsent(qt.getUuid(), entity);
			orderedUUIDList.add(qt.getUuid());
		}
		int groupNumber;
		EntityConditionBuilder ecb;
		for (UUID uuid : orderedUUIDList) {
			EntityEnum entityEnum = uuidEntityEnumMap.get(uuid);
			switch (entityEnum) {
			case DOCUMENT:
			case BAG:
			case SEAT:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				break;
			case EMAIL:
			case FLIGHT:
			case FREQUENT_FLYER:
			case HITS:
			case PHONE:
			case PASSENGER:
			case FLIGHT_PAX:
			case PNR:
			case ADDRESS:
			case BOOKING_DETAIL:
			case CREDIT_CARD:
			case TRAVEL_AGENCY:
			case DWELL_TIME:
			case NOT_LISTED:
			default:
				break;
			}
		}
		this.pnrRuleConditionBuilder = new PnrRuleConditionBuilder(queryTermList);
		this.pnrSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.SEAT_VARIABLE_NAME);
		this.apisSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.SEAT_VARIABLE_NAME + "2");
		this.flightPaxConditionBuilder = new FlightPaxConditionBuilder(FLIGHT_PAX_VARIABLE_NAME);
		this.passengerConditionBuilder = new PassengerConditionBuilder(passengerVariableName);
		this.detailsConditionBuilder = new PassengerDetailsConditionBuilder(
				RuleTemplateConstants.PASSENGER_DETAILS_VARIABLE_NAME);
		this.tripDetailsConditionBuilder = new PassengerTripDetailsConditionBuilder(
				RuleTemplateConstants.PASSENGER_TRIP_VARIABLE_NAME);
		this.flightConditionBuilder = new FlightConditionBuilder(flightVariableName, passengerVariableName);
		this.mutableFlightDetailsConditionBuilder = new MutableFlightDetailsConditionBuilder("$mfd");
	}

	private EntityConditionBuilder getEntityConditionBuilder(EntityEnum entityEnum, int groupNumber) {
		EntityConditionBuilder ecb;
		String drlVariableName = DOLLAR_SIGN + entityEnum.getAlias() + groupNumber;
		switch (entityEnum) {
		case DOCUMENT:
			ecb = new DocumentConditionBuilder(drlVariableName);
			ecb.setGroupNumber(groupNumber);
			documentConditionBuilder.add((DocumentConditionBuilder) ecb);
			break;
		case BAG:
			ecb = new BagConditionBuilder(drlVariableName);
			ecb.setGroupNumber(groupNumber);
			bagConditionBuilder.add((BagConditionBuilder) ecb);
			break;
		case SEAT:
			ecb = new SeatConditionBuilder(drlVariableName);
			ecb.setGroupNumber(groupNumber);
			seatConditionBuilders.add((SeatConditionBuilder)ecb);
			break;
		case EMAIL:
		case FREQUENT_FLYER:
		case PHONE:
		case TRAVEL_AGENCY:
		case DWELL_TIME:
		case ADDRESS:
		case BOOKING_DETAIL:
		case CREDIT_CARD:
		case PNR:
		case FLIGHT_PAX:
		case HITS:
		case PASSENGER:
		case FLIGHT:
		case NOT_LISTED:
		default:
			throw new RuntimeException("Attempted to make list condition builder on unsupported field!");
		}
		return ecb;
	}

	/**
	 * @return the flightCriteriaPresent
	 */
	private boolean isFlightCriteriaPresent() {
		return flightCriteriaPresent;
	}

	/**
	 * Appends the generated "when" part (i.e., the LHS) of the rule to the rule
	 * document.
	 *
	 * @param parentStringBuilder
	 *            the rule document builder.
	 */
	public void buildConditionsAndApppend(final StringBuilder parentStringBuilder) {

		generateLinkConditions();

		// Make the case where multiple objects of the same type are reasoned against
		// e.g. 2 bags, 2 credit cards.
		handleMultiVariableObjects();

		// order MATTERS! Changing order can have a MASSIVE impact on rule performance
		// as well as cause compile errors.
		parentStringBuilder.append(flightPaxConditionBuilder.build());
		parentStringBuilder.append(passengerConditionBuilder.build());
		parentStringBuilder.append(flightConditionBuilder.build());
		parentStringBuilder.append(addFlightPassengerLink());
		parentStringBuilder.append(mutableFlightDetailsConditionBuilder.build());
		parentStringBuilder.append(detailsConditionBuilder.build());
		parentStringBuilder.append(tripDetailsConditionBuilder.build());
		for (DocumentConditionBuilder dcb : documentConditionBuilder) {
			parentStringBuilder.append(dcb.build());
		}
		parentStringBuilder.append(apisSeatConditionBuilder.build());
		pnrRuleConditionBuilder.buildConditionsAndApppend(parentStringBuilder, passengerConditionBuilder);

		for (BagConditionBuilder bcb : bagConditionBuilder) {
			parentStringBuilder.append(bcb.build());
		}
		for (SeatConditionBuilder scb : seatConditionBuilders) {
			parentStringBuilder.append(scb.build());
		}
		// order doesn't matter
		pnrRuleConditionBuilder.reset();
		tripDetailsConditionBuilder.reset();
		detailsConditionBuilder.reset();
		passengerConditionBuilder.reset();
		documentConditionBuilder = new ArrayList<>();
		flightConditionBuilder.reset();
		flightPaxConditionBuilder.reset();
		pnrSeatConditionBuilder.reset();
		apisSeatConditionBuilder.reset();
		bagConditionBuilder = new ArrayList<>();
		seatConditionBuilders = new ArrayList<>();
		flightConditionBuilder.reset();
		mutableFlightDetailsConditionBuilder.reset();
	}

	private void handleMultiVariableObjects() {
		handleMultipleObjectTypeOnSameRule(documentConditionBuilder);
		handleMultipleObjectTypeOnSameRule(bagConditionBuilder);
	}

	protected String addFlightPassengerLink() {
		return RuleTemplateConstants.FLIGHT_PASSENGER_LINK_VARIABLE_NAME + ":"
				+ FlightPassengerLink.class.getSimpleName() + "(passengerId == $p.id, flightId == $f.id)\n";
	}

	/**
	 * Creates linking passenger criteria for documents and flights, and also seats,
	 * passengers and flights.
	 */
	private void generateLinkConditions() {

		if (pnrRuleConditionBuilder.hasSeats()) {
			this.flightCriteriaPresent = true;
		}
		if (!apisSeatConditionBuilder.isEmpty()) {
			apisSeatConditionBuilder.addApisCondition(true);
			this.flightCriteriaPresent = true;
		}

		// If there are bag or flightpax conditions add link to passenger builder
		// Add link to flight condition builder because of flight id existence in each.
		if (!bagConditionBuilder.isEmpty()) {
			for (BagConditionBuilder bcb : bagConditionBuilder) {
				if (!bcb.isEmpty()) {
					this.flightCriteriaPresent = true;
				}
			}
		}

		if (!flightPaxConditionBuilder.isEmpty()) {
			passengerConditionBuilder.addLinkByIdCondition(flightPaxConditionBuilder.getPassengerIdLinkExpression());
			flightConditionBuilder
					.addConditionAsString("id == " + flightPaxConditionBuilder.getFlightIdLinkExpression());
			this.flightCriteriaPresent = true;
		}

	}

	/**
	 * Adds a rule condition to the builder.
	 *
	 * @param trm
	 *            the condition to add.
	 */
	public void addRuleCondition(final QueryTerm trm) {
		// add the hit reason description
		if (conditionDescriptionBuilder == null) {
			conditionDescriptionBuilder = new StringBuilder();
		} else {
			conditionDescriptionBuilder.append(RuleHitDetail.HIT_REASON_SEPARATOR);
		}

		try {
			RuleConditionBuilderHelper.addConditionDescription(trm, conditionDescriptionBuilder);

			TypeEnum attributeType = TypeEnum.getEnum(trm.getType());
			CriteriaOperatorEnum opCode = CriteriaOperatorEnum.getEnum(trm.getOperator());
			String field = trm.getField();
			// All entities that are a part of a list live int he conditionBuilderMap.
			EntityConditionBuilder ebd;
			EntityEnum entity = EntityEnum.fromString(trm.getEntity()).orElse(EntityEnum.NOT_LISTED);
			switch (entity) {
			case PASSENGER:
				if (RuleTemplateConstants.SEAT_ENTITY_NAME.equalsIgnoreCase(field)) {
					apisSeatConditionBuilder.addCondition(opCode, RuleTemplateConstants.SEAT_ATTRIBUTE_NAME,
							attributeType, trm.getValue());
				} else if (RuleTemplateConstants.passDetailsMap.keySet().contains(field.toUpperCase())) {
					// front end path from query builder
					detailsConditionBuilder.addCondition(opCode,
							RuleTemplateConstants.passDetailsMap.get(field.toUpperCase()), attributeType,
							trm.getValue());
				} else if (PASSENGER_DETAILS_SET.contains(field.toUpperCase())) {
					// front end path from watchlist
					detailsConditionBuilder.addCondition(opCode, field, attributeType, trm.getValue());
				} else if (RuleTemplateConstants.passTripDetailsMap.keySet().contains(field.toUpperCase())) {
					tripDetailsConditionBuilder.addCondition(opCode,
							RuleTemplateConstants.passTripDetailsMap.get(field.toUpperCase()), attributeType,
							trm.getValue());
				} else {
					throw new RuntimeException(
							"ERROR: PASSENGER HAS NO INFORMATION FOR RULE. CHECK DETAILS OR TRIP IMPLEMENTATION");
				}
				break;
			case FLIGHT:
				if (RuleTemplateConstants.flightMutableDetailsMap.containsKey(field.toUpperCase())) {
					mutableFlightDetailsConditionBuilder.addCondition(opCode,
							flightMutableDetailsMap.get(trm.getField().toUpperCase()), attributeType, trm.getValue());
				} else if (FLIGHT_MUTABLE_DETAILS.contains(field.toUpperCase())) {
					mutableFlightDetailsConditionBuilder.addCondition(opCode, trm.getField(), attributeType,
							trm.getValue());
				} else {
					flightConditionBuilder.addCondition(opCode, trm.getField(), attributeType, trm.getValue());
					this.flightCriteriaPresent = true;
				}
				break;
			case FLIGHT_PAX:
				flightPaxConditionBuilder.addCondition(opCode, trm.getField(), attributeType, trm.getValue());
				break;
			case DOCUMENT:
			case SEAT:
			case BAG:
				ebd = conditionBuilderMap.get(trm.getUuid());
				ebd.addCondition(opCode, trm.getField(), attributeType, trm.getValue());
				break;
			/*
			 *
			 * Several Entities are PNR specific and handled in the pnr rule condition
			 * builders.
			 */
			case PNR:
			case FREQUENT_FLYER:
			case PHONE:
			case EMAIL:
			case FORM_OF_PAYMENT:
			case TRAVEL_AGENCY:
			case DWELL_TIME:
			case ADDRESS:
			case BOOKING_DETAIL:
			case CREDIT_CARD:
				pnrRuleConditionBuilder.addRuleCondition(attributeType, opCode, trm);
				break;
			case HITS:
			case NOT_LISTED:
			default:
				throw new RuntimeException("This field is not currently implemented for rules!" + trm.getEntity());
			}
		} catch (ParseException pe) {
			StringBuilder bldr = new StringBuilder("[");
			for (String val : trm.getValue()) {
				bldr.append(val).append(",");
			}
			bldr.append("]");
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_CODE, bldr.toString(), trm.getType(),
					"Engine Rule Creation");
		} catch (NullPointerException | IllegalArgumentException ex) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
					String.format("QueryTerm (entity=%s, field=%s, operator=%s, type=%s)", trm.getEntity(),
							trm.getField(), trm.getOperator(), trm.getType()),
					"Engine Rule Creation");

		}

	}

	private static final String ACTION_PASSENGER_HIT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, null, \"%s\"));\n";
	private static final String ACTION_PASSENGER_HIT_WITH_FLIGHT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, %s, \"%s\"));\n";

	List<String> addRuleAction(StringBuilder ruleStringBuilder, UdrRule parent) {
		String cause = conditionDescriptionBuilder.toString().replace("\"", "'");
		ruleStringBuilder.append("then\n");
		if (isFlightCriteriaPresent()) {
			ruleStringBuilder.append(String.format(ACTION_PASSENGER_HIT_WITH_FLIGHT, "%dL", // the UDR ID may not be
																							// available
					"%dL", // the rule ID may not be available
					parent.getTitle(), this.passengerVariableName, this.flightVariableName, cause));
		} else {
			// the UDR ID and/or the rule id may not be available at
			// this stage so we add defer adding these
			ruleStringBuilder.append(String.format(ACTION_PASSENGER_HIT, "%dL", "%dL", // the rule ID may not be
																						// available
					parent.getTitle(), this.passengerVariableName, cause));

		}
		ruleStringBuilder.append("end\n");
		conditionDescriptionBuilder = null;
		return Arrays.asList(cause.split(RuleHitDetail.HIT_REASON_SEPARATOR));
	}

	private static final String ACTION_WATCHLIST_HIT = "resultList.add(new RuleHitDetail(%s, \"%s\", %s, \"%s\"));\n";

	public List<String> addWatchlistRuleAction(StringBuilder ruleStringBuilder, EntityEnum entity) {
		String cause = conditionDescriptionBuilder.toString().replace("\"", "'");
		ruleStringBuilder.append("then\n");

		HitTypeEnum hitType = (entity == EntityEnum.PASSENGER) ? HitTypeEnum.WATCHLIST_PASSENGER
				: HitTypeEnum.WATCHLIST_DOCUMENT;

		// the watch list item id id may not be available at
		// this stage so we add defer adding it
		ruleStringBuilder.append(String.format(ACTION_WATCHLIST_HIT, "%dL", // the watch list item ID may not be
																			// available
				hitType.toString(), this.passengerVariableName, cause));

		ruleStringBuilder.append("end\n");
		conditionDescriptionBuilder = null;
		return Arrays.asList(cause.split(RuleHitDetail.HIT_REASON_SEPARATOR));
	}
}

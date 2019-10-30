/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

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

/**
 * Generates the "when" part of a DRL rule. procedure: new RuleConditionBuilder(
 */
public class RuleConditionBuilder {

	private List<DocumentConditionBuilder> documentConditionBuilder = new ArrayList<>();
	private List<BagConditionBuilder> bagConditionBuilder = new ArrayList<>();

	private PnrRuleConditionBuilder pnrRuleConditionBuilder;
	private SeatConditionBuilder pnrSeatConditionBuilder;
	private SeatConditionBuilder apisSeatConditionBuilder;
	private PaymentFormConditionBuilder paymentFormConditionBuilder;
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
		this.paymentFormConditionBuilder = new PaymentFormConditionBuilder(
				RuleTemplateConstants.PAYMENT_FORM_VARIABLE_NAME);
		this.pnrSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.SEAT_VARIABLE_NAME, false);
		this.apisSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.SEAT_VARIABLE_NAME + "2", true);
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
			documentConditionBuilder.add((DocumentConditionBuilder) ecb);
			break;
		case BAG:
			ecb = new BagConditionBuilder(drlVariableName);
			bagConditionBuilder.add((BagConditionBuilder) ecb);
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

		// order does matter here. Some variables rely on other variables
		// being initialized.
		boolean isPassengerConditionCreated = !passengerConditionBuilder.isEmpty() | !flightConditionBuilder.isEmpty();

		pnrRuleConditionBuilder.buildConditionsAndApppend(parentStringBuilder);
		for (BagConditionBuilder bcb : bagConditionBuilder) {
			parentStringBuilder.append(bcb.build());
		}
		parentStringBuilder.append(flightPaxConditionBuilder.build()).append(apisSeatConditionBuilder.build())
				.append(detailsConditionBuilder.build()).append(tripDetailsConditionBuilder.build());
		for (DocumentConditionBuilder dcb : documentConditionBuilder) {
			parentStringBuilder.append(dcb.build());
		}
		parentStringBuilder.append(paymentFormConditionBuilder.build());
		if (isPassengerConditionCreated) {
			parentStringBuilder.append(passengerConditionBuilder.build()).append(flightConditionBuilder.build())
					.append(mutableFlightDetailsConditionBuilder.build());
		}
		pnrRuleConditionBuilder.addPnrLinkConditions(parentStringBuilder, isPassengerConditionCreated,
				passengerConditionBuilder);

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
		paymentFormConditionBuilder.reset();
		flightConditionBuilder.reset();
		mutableFlightDetailsConditionBuilder.reset();
	}

	/**
	 * Creates linking passenger criteria for documents and flights, and also seats,
	 * passengers and flights.
	 */
	private void generateLinkConditions() {

		if (pnrRuleConditionBuilder.hasSeats()) {
			passengerConditionBuilder
					.addConditionAsString("id == " + pnrRuleConditionBuilder.getSeatVarName() + ".passenger.id");
			flightConditionBuilder
					.addConditionAsString("id == " + pnrRuleConditionBuilder.getSeatVarName() + ".flight.id");
			this.flightCriteriaPresent = true;
		}
		if (!apisSeatConditionBuilder.isEmpty()) {
			apisSeatConditionBuilder.addApisCondition();
			passengerConditionBuilder
					.addConditionAsString("id == " + RuleTemplateConstants.SEAT_VARIABLE_NAME + "2.passenger.id");
			flightConditionBuilder
					.addConditionAsString("id == " + RuleTemplateConstants.SEAT_VARIABLE_NAME + "2.flight.id");
			this.flightCriteriaPresent = true;
		}

		if (!documentConditionBuilder.isEmpty()) {
			for (DocumentConditionBuilder dcb : documentConditionBuilder) {
				// add a link condition to the passenger builder.
				passengerConditionBuilder.addLinkByIdCondition(dcb.getPassengerIdLinkExpression());
			}
		}
		// If there are bag or flightpax conditions add link to passenger builder
		// Add link to flight condition builder because of flight id existence in each.
		if (!bagConditionBuilder.isEmpty()) {
			for (BagConditionBuilder bcb : bagConditionBuilder) {
				if (!bcb.isEmpty()) {
					passengerConditionBuilder.addLinkByIdCondition(bcb.getPassengerIdLinkExpression());
					flightConditionBuilder.addConditionAsString("id == " + bcb.getFlightIdLinkExpression());
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
		if (!detailsConditionBuilder.isEmpty()) {
			passengerConditionBuilder.addLinkByIdCondition(detailsConditionBuilder.getPassengerIdLinkExpression());
		}
		if (!tripDetailsConditionBuilder.isEmpty()) {
			passengerConditionBuilder.addLinkByIdCondition(tripDetailsConditionBuilder.getPassengerIdLinkExpression());
		}
		if (!mutableFlightDetailsConditionBuilder.isEmpty()) {
			flightPaxConditionBuilder.addConditionAsString("id > 0"); // gets all rows
			passengerConditionBuilder.addLinkByIdCondition(flightPaxConditionBuilder.getPassengerIdLinkExpression());
			flightConditionBuilder
					.addConditionAsString("id == " + flightPaxConditionBuilder.getFlightIdLinkExpression());
			mutableFlightDetailsConditionBuilder.addConditionAsString("flightId == $f.id");
			this.flightCriteriaPresent = true;
		}

		// add FlightPax as a join table for flights and passengers where no other
		// passenger join possibility exists.
		// This replaces the addition of 'Passenger in f.passengers' clause that now no
		// longer works due to database changes.
		if (!flightConditionBuilder.isEmpty() && (bagConditionBuilder.isEmpty()) && (apisSeatConditionBuilder.isEmpty())
				&& (flightPaxConditionBuilder.isEmpty()) && (!pnrRuleConditionBuilder.hasSeats())) {
			flightPaxConditionBuilder.addConditionAsString("id > 0"); // gets all rows
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

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.rule.builder.pnr.BagConditionBuilder;
import gov.gtas.rule.builder.pnr.FlightPaxConditionBuilder;
import gov.gtas.rule.builder.pnr.PnrRuleConditionBuilder;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static gov.gtas.rule.builder.RuleTemplateConstants.PASSENGER_DETAILS_SET;

/**
 * Generates the "when" part of a DRL rule.
 * procedure:
 * new RuleConditionBuilder( 
 */
public class RuleConditionBuilder {

    private PassengerConditionBuilder passengerConditionBuilder;
    private PassengerDetailsConditionBuilder detailsConditionBuilder;
    private PassengerTripDetailsConditionBuilder tripDetailsConditionBuilder;
    private DocumentConditionBuilder documentConditionBuilder;
    private FlightConditionBuilder flightConditionBuilder;

    private PnrRuleConditionBuilder pnrRuleConditionBuilder;

    private SeatConditionBuilder pnrSeatConditionBuilder;
    private SeatConditionBuilder apisSeatConditionBuilder;
    
    private BagConditionBuilder bagConditionBuilder;
    private FlightPaxConditionBuilder flightPaxConditionBuilder;
    private PaymentFormConditionBuilder paymentFormConditionBuilder;

    private String passengerVariableName;
    private String flightVariableName;

    private StringBuilder conditionDescriptionBuilder;

    private boolean flightCriteriaPresent;

    /**
     * Constructor for the Simple Rules:<br>
     * (i.e., One Passenger, one document, one flight.)
     * 
     * @param entityVariableNameMap
     *            a lookup for variable name to use when generating rules<br>
     *            For example, to get the variable for passenger lookup using
     *            the key EntityEnum.PASSENGER.
     * 
     */
    public RuleConditionBuilder(
            final Map<EntityEnum, String> entityVariableNameMap) {

        this.passengerVariableName = entityVariableNameMap
                .get(EntityEnum.PASSENGER);
        this.flightVariableName = entityVariableNameMap.get(EntityEnum.FLIGHT);
        final String documentVariableName = entityVariableNameMap
                .get(EntityEnum.DOCUMENT);

        this.passengerConditionBuilder = new PassengerConditionBuilder(
                passengerVariableName);
        this.documentConditionBuilder = new DocumentConditionBuilder(
                documentVariableName, passengerVariableName);

        this.detailsConditionBuilder = new PassengerDetailsConditionBuilder(RuleTemplateConstants.PASSENGER_DETAILS_VARIABLE_NAME);

        this.tripDetailsConditionBuilder = new PassengerTripDetailsConditionBuilder(RuleTemplateConstants.PASSENGER_TRIP_VARIABLE_NAME);

        this.documentConditionBuilder = new DocumentConditionBuilder(
                documentVariableName, passengerVariableName);
        this.flightConditionBuilder = new FlightConditionBuilder(
                flightVariableName, passengerVariableName);

        this.pnrSeatConditionBuilder = new SeatConditionBuilder(
                RuleTemplateConstants.SEAT_VARIABLE_NAME, false);
        this.apisSeatConditionBuilder = new SeatConditionBuilder(
                RuleTemplateConstants.SEAT_VARIABLE_NAME+"2", true);
        
        this.bagConditionBuilder = new BagConditionBuilder(
        		RuleTemplateConstants.BAG_VARIABLE_NAME, passengerVariableName);
        this.flightPaxConditionBuilder = new FlightPaxConditionBuilder(
        		RuleTemplateConstants.FLIGHT_PAX_VARIABLE_NAME, passengerVariableName);
        
        this.pnrRuleConditionBuilder = new PnrRuleConditionBuilder(
                entityVariableNameMap);
        
        this.paymentFormConditionBuilder = new PaymentFormConditionBuilder(RuleTemplateConstants.PAYMENT_FORM_VARIABLE_NAME);
    }

    /**
     * @return the flightCriteriaPresent
     */
    public boolean isFlightCriteriaPresent() {
        return flightCriteriaPresent;
    }

    /**
     * Appends the generated "when" part (i.e., the LHS) of the rule to the rule
     * document.
     * 
     * @param parentStringBuilder
     *            the rule document builder.
     * @throws ParseException
     *             if the UDR has invalid formatting.
     */
    public void buildConditionsAndApppend(
            final StringBuilder parentStringBuilder) {

        generateLinkConditions();

        //order doesn't matter.
        parentStringBuilder.append(bagConditionBuilder.build())
        .append(flightPaxConditionBuilder.build())
        .append(apisSeatConditionBuilder.build())
        .append(detailsConditionBuilder.build())
        .append(tripDetailsConditionBuilder.build())
        .append(pnrSeatConditionBuilder.build())
        .append(documentConditionBuilder.build())
        .append(passengerConditionBuilder.build())
        .append(flightConditionBuilder.build())
        .append(paymentFormConditionBuilder.build());

        boolean isPassengerConditionCreated = !passengerConditionBuilder
                .isEmpty() | !flightConditionBuilder.isEmpty();
        
        pnrRuleConditionBuilder.buildConditionsAndApppend(parentStringBuilder,
                isPassengerConditionCreated, passengerConditionBuilder);

        //order doesn't matter
        tripDetailsConditionBuilder.reset();
        detailsConditionBuilder.reset();
        passengerConditionBuilder.reset();
        documentConditionBuilder.reset();
        flightConditionBuilder.reset();
        pnrSeatConditionBuilder.reset();
        apisSeatConditionBuilder.reset();
        bagConditionBuilder.reset();
        flightConditionBuilder.reset();
        paymentFormConditionBuilder.reset();

    }

    /**
     * Creates linking passenger criteria for documents and flights,
     * and also seats, passengers and flights.
     * 
     */
    private void generateLinkConditions() {
        if(!pnrSeatConditionBuilder.isEmpty()) {
            pnrSeatConditionBuilder.addApisCondition();
            passengerConditionBuilder.addConditionAsString("id == "+RuleTemplateConstants.SEAT_VARIABLE_NAME+".passenger.id");
            flightConditionBuilder.addConditionAsString("id == "+RuleTemplateConstants.SEAT_VARIABLE_NAME+".flight.id");
            this.flightCriteriaPresent = true;
        }
        if(!apisSeatConditionBuilder.isEmpty()) {
            apisSeatConditionBuilder.addApisCondition();
            passengerConditionBuilder.addConditionAsString("id == "+RuleTemplateConstants.SEAT_VARIABLE_NAME+"2.passenger.id");
            flightConditionBuilder.addConditionAsString("id == "+RuleTemplateConstants.SEAT_VARIABLE_NAME+"2.flight.id");
            this.flightCriteriaPresent = true;
        }

        if (!documentConditionBuilder.isEmpty()) {
            // add a link condition to the passenger builder.
            passengerConditionBuilder
                    .addLinkByIdCondition(documentConditionBuilder
                            .getPassengerIdLinkExpression());
        }
        //If there are bag or flightpax conditions add link to passenger builder
        //Add link to flight condition builder because of flight id existence in each. 
        if(!bagConditionBuilder.isEmpty()){
        	passengerConditionBuilder.addLinkByIdCondition(bagConditionBuilder.getPassengerIdLinkExpression());
        	flightConditionBuilder.addConditionAsString("id == "+bagConditionBuilder.getFlightIdLinkExpression());
        	this.flightCriteriaPresent = true;
        }
        
        if(!flightPaxConditionBuilder.isEmpty()){
        	passengerConditionBuilder.addLinkByIdCondition(flightPaxConditionBuilder.getPassengerIdLinkExpression());
        	flightConditionBuilder.addConditionAsString("id == "+flightPaxConditionBuilder.getFlightIdLinkExpression());
        	this.flightCriteriaPresent = true;
        }
        if (!detailsConditionBuilder.isEmpty()){
        	passengerConditionBuilder.addLinkByIdCondition(detailsConditionBuilder.getPassengerIdLinkExpression());
        }
        if (!tripDetailsConditionBuilder.isEmpty()){
        	passengerConditionBuilder.addLinkByIdCondition(tripDetailsConditionBuilder.getPassengerIdLinkExpression());
        }
        
        
        // add FlightPax as a join table for flights and passengers where no other passenger join possibility exists.
        // This replaces the addition of 'Passenger in f.passengers' clause that now no longer works due to database changes.
        if (!flightConditionBuilder.isEmpty() && (bagConditionBuilder.isEmpty()) && 
                (apisSeatConditionBuilder.isEmpty()) && (flightPaxConditionBuilder.isEmpty()) && (pnrSeatConditionBuilder.isEmpty()))
        {
            flightPaxConditionBuilder.addConditionAsString("id > 0"); // gets all rows
            passengerConditionBuilder.addLinkByIdCondition(flightPaxConditionBuilder.getPassengerIdLinkExpression());
            flightConditionBuilder.addConditionAsString("id == "+flightPaxConditionBuilder.getFlightIdLinkExpression());
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
            conditionDescriptionBuilder
                    .append(RuleHitDetail.HIT_REASON_SEPARATOR);
        }

        try {
            RuleConditionBuilderHelper.addConditionDescription(trm,
                    conditionDescriptionBuilder);

            EntityEnum entity = EntityEnum.getEnum(trm.getEntity());
            TypeEnum attributeType = TypeEnum.getEnum(trm.getType());
            CriteriaOperatorEnum opCode = CriteriaOperatorEnum.getEnum(trm
                    .getOperator());
            String field = trm.getField();
            switch (entity) {
                case PASSENGER:
                    if (RuleTemplateConstants.SEAT_ENTITY_NAME.equalsIgnoreCase(field)) {
                        apisSeatConditionBuilder.addCondition(opCode, RuleTemplateConstants.SEAT_ATTRIBUTE_NAME, attributeType, trm.getValue());
                    } else if (RuleTemplateConstants.passDetailsMap.keySet().contains(field.toUpperCase())) {
                        //front end path from query builder
                        detailsConditionBuilder.addCondition(opCode, RuleTemplateConstants.passDetailsMap.get(field.toUpperCase()),
                                attributeType, trm.getValue());
                    } else if (PASSENGER_DETAILS_SET.contains(field.toUpperCase())) {
                        //front end path from watchlist
                        detailsConditionBuilder.addCondition(opCode, field, attributeType, trm.getValue());
                    } else if (RuleTemplateConstants.passTripDetailsMap.keySet().contains(field.toUpperCase())) {
                        tripDetailsConditionBuilder.addCondition(opCode, RuleTemplateConstants.passTripDetailsMap.get(field.toUpperCase()),
                                attributeType, trm.getValue());
                    } else {
                        throw new RuntimeException("ERROR: PASSENGER HAS NO INFORMATION FOR RULE. CHECK DETAILS OR TRIP IMPLEMENTATION");
                    }
                    break;
                case DOCUMENT:
                    documentConditionBuilder.addCondition(opCode, trm.getField(),
                            attributeType, trm.getValue());
                    break;
                case FLIGHT:
                    flightConditionBuilder.addCondition(opCode, trm.getField(),
                            attributeType, trm.getValue());
                    this.flightCriteriaPresent = true;
                    break;
                case BAG:
                    bagConditionBuilder.addCondition(opCode, trm.getField(),
                            attributeType, trm.getValue());
                    break;
                case FLIGHT_PAX:
                    flightPaxConditionBuilder.addCondition(opCode, trm.getField(),
                            attributeType, trm.getValue());
                    break;
                default:
                    // try and add PNR related conditions if they exist.
                    if (entity == EntityEnum.PNR && field.equalsIgnoreCase(RuleTemplateConstants.SEAT_ENTITY_NAME)) {
                        pnrSeatConditionBuilder.addCondition(opCode, RuleTemplateConstants.SEAT_ATTRIBUTE_NAME, attributeType, trm.getValue());
                    } else if (entity == EntityEnum.PNR && field.equalsIgnoreCase(RuleTemplateConstants.PAYMENT_FORM_FIELD_ALIAS)) {
                        paymentFormConditionBuilder.addCondition(opCode, RuleTemplateConstants.PAYMENT_TYPE_ATTRIBUTE_NAME, attributeType, trm.getValue());
                        pnrRuleConditionBuilder.getPnrConditionBuilder().addConditionAsString("id == " + RuleTemplateConstants.PAYMENT_FORM_VARIABLE_NAME + ".pnr.id");
                    } else {
                        pnrRuleConditionBuilder.addRuleCondition(entity, attributeType,
                                opCode, trm);
                    }
                    break;
            }
        } catch (ParseException pe) {
            StringBuilder bldr = new StringBuilder("[");
            for (String val : trm.getValue()) {
                bldr.append(val).append(",");
            }
            bldr.append("]");
            throw ErrorHandlerFactory.getErrorHandler().createException(
                    CommonErrorConstants.INPUT_JSON_FORMAT_ERROR_CODE,
                    bldr.toString(), trm.getType(), "Engine Rule Creation");
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw ErrorHandlerFactory
                    .getErrorHandler()
                    .createException(
                            CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                            String.format(
                                    "QueryTerm (entity=%s, field=%s, operator=%s, type=%s)",
                                    trm.getEntity(), trm.getField(),
                                    trm.getOperator(), trm.getType()),
                            "Engine Rule Creation");

        }

    }

    private static final String ACTION_PASSENGER_HIT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, null, \"%s\"));\n";
    private static final String ACTION_PASSENGER_HIT_WITH_FLIGHT = "resultList.add(new RuleHitDetail(%s, %s, \"%s\", %s, %s, \"%s\"));\n";

    public List<String> addRuleAction(StringBuilder ruleStringBuilder,
            UdrRule parent, Rule rule, String passengerVariableName) {
        String cause = conditionDescriptionBuilder.toString()
                .replace("\"", "'");
        ruleStringBuilder.append("then\n");
        if (isFlightCriteriaPresent()) {
            ruleStringBuilder.append(String.format(
                    ACTION_PASSENGER_HIT_WITH_FLIGHT,
                    "%dL", // the UDR ID may not be available
                    "%dL", // the rule ID may not be available
                    parent.getTitle(), this.passengerVariableName,
                    this.flightVariableName, cause));
        } else {
            // the UDR ID and/or the rule id may not be available at
            // this stage so we add defer adding these
            ruleStringBuilder.append(String.format(ACTION_PASSENGER_HIT, "%dL", 
                    "%dL", // the rule ID may not be available
                    parent.getTitle(), this.passengerVariableName, cause));

        }
        ruleStringBuilder.append("end\n");
        conditionDescriptionBuilder = null;
        return Arrays.asList(cause.split(RuleHitDetail.HIT_REASON_SEPARATOR));
    }

    private static final String ACTION_WATCHLIST_HIT = "resultList.add(new RuleHitDetail(%s, \"%s\", %s, \"%s\"));\n";
    public List<String> addWatchlistRuleAction(StringBuilder ruleStringBuilder, EntityEnum entity, String title,
            String passengerVariableName) {
        String cause = conditionDescriptionBuilder.toString()
                .replace("\"", "'");
        ruleStringBuilder.append("then\n");
        
        HitTypeEnum hitType = (entity == EntityEnum.PASSENGER) ? HitTypeEnum.P : HitTypeEnum.D;
         
        // the watch list item id id may not be available at
        // this stage so we add defer adding it
        ruleStringBuilder.append(String.format(ACTION_WATCHLIST_HIT,
                "%dL", // the watch list item ID may not be available
                 hitType.toString(), this.passengerVariableName, cause));

        ruleStringBuilder.append("end\n");
        conditionDescriptionBuilder = null;
        return Arrays.asList(cause.split(RuleHitDetail.HIT_REASON_SEPARATOR));
    }
}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_ATTRIBUTE_ID;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_PNR_ID;
import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrBookingDetailLink;
import gov.gtas.bo.match.PnrCreditCardLink;
import gov.gtas.bo.match.PnrEmailLink;
import gov.gtas.bo.match.PnrFrequentFlyerLink;
import gov.gtas.bo.match.PnrPassengerLink;
import gov.gtas.bo.match.PnrPhoneLink;
import gov.gtas.bo.match.PnrTravelAgencyLink;
import gov.gtas.bo.match.PnrDwellTimeLink;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.rule.builder.EntityConditionBuilder;
import gov.gtas.rule.builder.PassengerConditionBuilder;
import gov.gtas.rule.builder.RuleTemplateConstants;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class PnrRuleConditionBuilder {
    private AddressConditionBuilder addressConditionBuilder;
    private PhoneConditionBuilder phoneConditionBuilder;
    private EmailConditionBuilder emailConditionBuilder;
    private CreditCardConditionBuilder creditCardConditionBuilder;
    private FrequentFlyerConditionBuilder frequentFlyerConditionBuilder;
    private TravelAgencyConditionBuilder travelAgencyConditionBuilder;
    private DwellTimeConditionBuilder dwellTimeConditionBuilder;
    private BagConditionBuilder bagConditionBuilder;
    private FlightPaxConditionBuilder flightPaxConditionBuilder;
    private PnrConditionBuilder pnrConditionBuilder;
    private BookingDetailConditionBuilder bookingDetailConditionBuilder;
    public PnrRuleConditionBuilder(
            final Map<EntityEnum, String> entityVariableNameMap) {
        for (Entry<EntityEnum, String> entry : entityVariableNameMap.entrySet()) {
            switch (entry.getKey()) {
            case PNR:
                this.pnrConditionBuilder = new PnrConditionBuilder(
                        entry.getValue());
                break;
            case DWELL_TIME:
                this.dwellTimeConditionBuilder = new DwellTimeConditionBuilder(
                        entry.getValue());
                break;
            case TRAVEL_AGENCY:
                this.travelAgencyConditionBuilder = new TravelAgencyConditionBuilder(
                        entry.getValue());
                break;
            case ADDRESS:
                this.addressConditionBuilder = new AddressConditionBuilder(
                        entry.getValue());
                break;
            case PHONE:
                this.phoneConditionBuilder = new PhoneConditionBuilder(
                        entry.getValue());
                break;
            case EMAIL:
                this.emailConditionBuilder = new EmailConditionBuilder(
                        entry.getValue());
                break;
            case BOOKING_DETAIL:
                this.bookingDetailConditionBuilder = new BookingDetailConditionBuilder(
                        entry.getValue());
                break;
            case FREQUENT_FLYER:
                this.frequentFlyerConditionBuilder = new FrequentFlyerConditionBuilder(
                        entry.getValue());
                break;
            case CREDIT_CARD:
                this.creditCardConditionBuilder = new CreditCardConditionBuilder(
                        entry.getValue());
                break;
            default:
                break;
            }
        }
    }

    /**
     * Creates linking criteria for PNR related objects.
     * 
     * @param linkStringBuilder
     *            the string builder to accumulate the linking conditions.
     * @return true if at least one linking condition was generated.
     */
    private String generatePnrLinks() {
        final StringBuilder linkStringBuilder = new StringBuilder();
        final String pnrVarName = pnrConditionBuilder.getDrlVariableName();
        if (!addressConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    addressConditionBuilder.getLinkVariableName(),
                    PnrAddressLink.class.getSimpleName(), pnrVarName,
                    addressConditionBuilder);
        }
        if (!phoneConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    phoneConditionBuilder.getLinkVariableName(),
                    PnrPhoneLink.class.getSimpleName(), pnrVarName,
                    phoneConditionBuilder);
        }
        if (!bookingDetailConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
            		bookingDetailConditionBuilder.getLinkVariableName(),
                    PnrBookingDetailLink.class.getSimpleName(), pnrVarName,
                    bookingDetailConditionBuilder);
        }
        if (!emailConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    emailConditionBuilder.getLinkVariableName(),
                    PnrEmailLink.class.getSimpleName(), pnrVarName,
                    emailConditionBuilder);
        }
        if (!creditCardConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    creditCardConditionBuilder.getLinkVariableName(),
                    PnrCreditCardLink.class.getSimpleName(), pnrVarName,
                    creditCardConditionBuilder);
        }
        if (!frequentFlyerConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    frequentFlyerConditionBuilder.getLinkVariableName(),
                    PnrFrequentFlyerLink.class.getSimpleName(), pnrVarName,
                    frequentFlyerConditionBuilder);
        }
        if (!travelAgencyConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
                    travelAgencyConditionBuilder.getLinkVariableName(),
                    PnrTravelAgencyLink.class.getSimpleName(), pnrVarName,
                    travelAgencyConditionBuilder);
        }
        if (!dwellTimeConditionBuilder.isEmpty()) {
            addLinkCondition(linkStringBuilder,
            		dwellTimeConditionBuilder.getLinkVariableName(),
                    PnrDwellTimeLink.class.getSimpleName(), pnrVarName,
                    dwellTimeConditionBuilder);
        }
        return linkStringBuilder.toString();
    }

    private void addLinkCondition(final StringBuilder parentStringBuilder,
            final String linkVarName, final String linkClassName,
            final String pnrVarName, final EntityConditionBuilder entBuilder) {
        parentStringBuilder.append(linkVarName)
                .append(RuleTemplateConstants.COLON_CHAR).append(linkClassName)
                .append(RuleTemplateConstants.LEFT_PAREN_CHAR)
                .append(LINK_PNR_ID).append(" == ").append(pnrVarName).append(".id, ")
                .append(LINK_ATTRIBUTE_ID).append(" == ")
                .append(entBuilder.getDrlVariableName()).append(".id)\n");

    }

    /**
     * Appends the generated "when" part (i.e., the LHS) of the rule to the rule
     * document.
     * 
     * @param parentStringBuilder
     *            the rule document builder.
     */
    public void buildConditionsAndApppend(
            final StringBuilder parentStringBuilder,
            final boolean passengerConditionExists,
            final PassengerConditionBuilder passengerBuilder) {

        parentStringBuilder.append(addressConditionBuilder.build());
        parentStringBuilder.append(phoneConditionBuilder.build());
        parentStringBuilder.append(emailConditionBuilder.build());
        parentStringBuilder.append(creditCardConditionBuilder.build());
        parentStringBuilder.append(travelAgencyConditionBuilder.build());
        parentStringBuilder.append(frequentFlyerConditionBuilder.build());
        parentStringBuilder.append(dwellTimeConditionBuilder.build());
        parentStringBuilder.append(bookingDetailConditionBuilder.build());
        
        String linkConditions = generatePnrLinks();
        if (pnrConditionBuilder.isEmpty()
                && StringUtils.isNotEmpty(linkConditions)) {
            pnrConditionBuilder.addConditionAsString(StringUtils.EMPTY);
        }
        parentStringBuilder.append(pnrConditionBuilder.build());
        parentStringBuilder.append(linkConditions);

        // add a passenger link condition if PNR conditions exist
        if (!passengerConditionExists && !pnrConditionBuilder.isEmpty()) {
            passengerBuilder.addConditionAsString(StringUtils.EMPTY);
            parentStringBuilder.append(passengerBuilder.build());
        }
        //assert at this point passenger condition exists!
        if (!pnrConditionBuilder.isEmpty()) {
            addLinkCondition(parentStringBuilder,
                    passengerBuilder.getDrlVariableName()+RuleTemplateConstants.LINK_VARIABLE_SUFFIX,
                    PnrPassengerLink.class.getSimpleName(), pnrConditionBuilder.getDrlVariableName(),
                    passengerBuilder);
        }

        addressConditionBuilder.reset();
        phoneConditionBuilder.reset();
        emailConditionBuilder.reset();
        creditCardConditionBuilder.reset();
        travelAgencyConditionBuilder.reset();
        frequentFlyerConditionBuilder.reset();
        pnrConditionBuilder.reset();
        dwellTimeConditionBuilder.reset();
        bookingDetailConditionBuilder.reset();
    }

    /**
     * Adds a rule condition to the builder.
     * 
     * @param trm
     *            the condition to add.
     */
    public void addRuleCondition(final EntityEnum entity,
            final TypeEnum attributeType, final CriteriaOperatorEnum opCode,
            final QueryTerm trm) {
        try {
            switch (entity) {
            case PNR:
                pnrConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case ADDRESS:
                addressConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case BOOKING_DETAIL:
                bookingDetailConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case PHONE:
                phoneConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case EMAIL:
                emailConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case CREDIT_CARD:
                creditCardConditionBuilder.addCondition(opCode, trm.getField(),
                        attributeType, trm.getValue());
                break;
            case TRAVEL_AGENCY:
                travelAgencyConditionBuilder.addCondition(opCode,
                        trm.getField(), attributeType, trm.getValue());
                break;
             case DWELL_TIME:
            	dwellTimeConditionBuilder.addCondition(opCode,
                        trm.getField(), attributeType, trm.getValue());
                break;
            case FREQUENT_FLYER:
                frequentFlyerConditionBuilder.addCondition(opCode,
                        trm.getField(), attributeType, trm.getValue());
                break;
            default:
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
}

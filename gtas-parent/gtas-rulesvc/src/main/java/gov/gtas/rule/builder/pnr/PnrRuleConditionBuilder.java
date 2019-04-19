/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrBookingLink;
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
import gov.gtas.rule.builder.*;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

import static gov.gtas.rule.builder.RuleTemplateConstants.*;

public class PnrRuleConditionBuilder {
    private AddressConditionBuilder addressConditionBuilder;
    private PhoneConditionBuilder phoneConditionBuilder;
    private EmailConditionBuilder emailConditionBuilder;
    private CreditCardConditionBuilder creditCardConditionBuilder;
    private FrequentFlyerConditionBuilder frequentFlyerConditionBuilder;
    private TravelAgencyConditionBuilder travelAgencyConditionBuilder;
    private DwellTimeConditionBuilder dwellTimeConditionBuilder;
    private PnrConditionBuilder pnrConditionBuilder;
    private BookingDetailConditionBuilder bookingDetailConditionBuilder;
    private PaymentFormConditionBuilder paymentFormConditionBuilder;
    private SeatConditionBuilder pnrSeatConditionBuilder;


    public PnrRuleConditionBuilder(int groupName) {
        paymentFormConditionBuilder = new PaymentFormConditionBuilder(RuleTemplateConstants.PAYMENT_FORM_VARIABLE_NAME + groupName);
        pnrSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.PNR_SEAT + groupName, false);
        for (EntityEnum entry : EntityEnum.values()) {
            switch (entry) {
                case PNR:
                    this.pnrConditionBuilder = new PnrConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case DWELL_TIME:
                    this.dwellTimeConditionBuilder = new DwellTimeConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case TRAVEL_AGENCY:
                    this.travelAgencyConditionBuilder = new TravelAgencyConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case ADDRESS:
                    this.addressConditionBuilder = new AddressConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case PHONE:
                    this.phoneConditionBuilder = new PhoneConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case EMAIL:
                    this.emailConditionBuilder = new EmailConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case BOOKING_DETAIL:
                    this.bookingDetailConditionBuilder = new BookingDetailConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case FREQUENT_FLYER:
                    this.frequentFlyerConditionBuilder = new FrequentFlyerConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                case CREDIT_CARD:
                    this.creditCardConditionBuilder = new CreditCardConditionBuilder(
                            entry.getAlias() + groupName);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Creates linking criteria for PNR related objects.
     * <p>
     * the string builder to accumulate the linking conditions.
     *
     * @return true if at least one linking condition was generated.
     */
    private String generatePnrLinks() {
        final StringBuilder linkStringBuilder = new StringBuilder();
        final String pnrVarName = pnrConditionBuilder.getDrlVariableName();

        if (!paymentFormConditionBuilder.isEmpty()) {
            this.getPnrConditionBuilder().addConditionAsString("id == " + paymentFormConditionBuilder.getDrlVariableName() + ".pnr.id");
        }

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
                    PnrBookingLink.class.getSimpleName(), pnrVarName,
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
     * @param parentStringBuilder the rule document builder.
     */
    public void buildConditionsAndApppend(
            final StringBuilder parentStringBuilder,
            final boolean passengerConditionExists,
            final PassengerConditionBuilder passengerBuilder) {

        if (!pnrSeatConditionBuilder.isEmpty()) {
            pnrSeatConditionBuilder.addApisCondition();
            parentStringBuilder.append(pnrSeatConditionBuilder.build());
        }
        parentStringBuilder.append(paymentFormConditionBuilder.build());
        parentStringBuilder.append(addressConditionBuilder.build());
        parentStringBuilder.append(phoneConditionBuilder.build());
        parentStringBuilder.append(emailConditionBuilder.build());
        parentStringBuilder.append(creditCardConditionBuilder.build());
        parentStringBuilder.append(travelAgencyConditionBuilder.build());
        parentStringBuilder.append(frequentFlyerConditionBuilder.build());
        parentStringBuilder.append(dwellTimeConditionBuilder.build());
        parentStringBuilder.append(bookingDetailConditionBuilder.build());

    }

    public void addPnrLinkConditions(StringBuilder parentStringBuilder, boolean passengerConditionExists, PassengerConditionBuilder passengerBuilder) {
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
                    passengerBuilder.getDrlVariableName() + RuleTemplateConstants.LINK_VARIABLE_SUFFIX,
                    PnrPassengerLink.class.getSimpleName(), pnrConditionBuilder.getDrlVariableName(),
                    passengerBuilder);
        }
    }

    public void reset() {
        addressConditionBuilder.reset();
        phoneConditionBuilder.reset();
        emailConditionBuilder.reset();
        creditCardConditionBuilder.reset();
        travelAgencyConditionBuilder.reset();
        frequentFlyerConditionBuilder.reset();
        pnrConditionBuilder.reset();
        dwellTimeConditionBuilder.reset();
        bookingDetailConditionBuilder.reset();
        paymentFormConditionBuilder.reset();
        pnrSeatConditionBuilder.reset();
    }

    /**
     * Adds a rule condition to the builder.
     *
     * @param trm the condition to add.
     */
    public void addRuleCondition(final EntityEnum entity,
                                 final TypeEnum attributeType, final CriteriaOperatorEnum opCode,
                                 final QueryTerm trm) {
        try {
            switch (entity) {
                case PNR:
                    switch (trm.getField().toLowerCase()) {
                        case PAYMENT_FORM_FIELD_ALIAS_LOWERCASE:
                            paymentFormConditionBuilder.addCondition(opCode, RuleTemplateConstants.PAYMENT_TYPE_ATTRIBUTE_NAME, attributeType, trm.getValue());
                            break;
                        case RuleTemplateConstants.SEAT_ENTITY_NAME_LOWERCASE:
                            pnrSeatConditionBuilder.addCondition(opCode, RuleTemplateConstants.SEAT_ATTRIBUTE_NAME, attributeType, trm.getValue());
                            break;
                        default:
                            pnrConditionBuilder.addCondition(opCode, trm.getField(),
                                    attributeType, trm.getValue());
                            break;
                    }
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
                    throw new RuntimeException("UnImplemented PNR Field! Error!");
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

    private PnrConditionBuilder getPnrConditionBuilder() {
        return pnrConditionBuilder;
    }

    public boolean hasSeats() {
        return !pnrSeatConditionBuilder.isEmpty();
    }

    public String getSeatVarName() {
        return pnrSeatConditionBuilder.getDrlVariableName();
    }
}

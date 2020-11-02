/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import gov.gtas.bo.match.*;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.rule.builder.*;

import java.text.ParseException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import static gov.gtas.rule.builder.RuleTemplateConstants.*;
import static gov.gtas.rule.builder.util.ConditionBuilderUtils.handleMultipleObjectTypeOnSameRule;

public class PnrRuleConditionBuilder {
	private List<AddressConditionBuilder> addressConditionBuilder = new ArrayList<>();
	private List<PhoneConditionBuilder> phoneConditionBuilder = new ArrayList<>();
	private List<EmailConditionBuilder> emailConditionBuilder = new ArrayList<>();
	private List<CreditCardConditionBuilder> creditCardConditionBuilder = new ArrayList<>();
	private List<FrequentFlyerConditionBuilder> frequentFlyerConditionBuilder = new ArrayList<>();
	private List<TravelAgencyConditionBuilder> travelAgencyConditionBuilder = new ArrayList<>();
	private List<DwellTimeConditionBuilder> dwellTimeConditionBuilder = new ArrayList<>();
	private List<BookingDetailConditionBuilder> bookingDetailConditionBuilder = new ArrayList<>();
	private List<PaymentFormConditionBuilder> paymentFormConditionBuilders = new ArrayList<>();

	private PnrConditionBuilder pnrConditionBuilder;
	private SeatConditionBuilder pnrSeatConditionBuilder;

	private Map<UUID, EntityConditionBuilder> conditionBuilderMap = new HashMap<>();

	public PnrRuleConditionBuilder(List<QueryTerm> queryTermList) {

		Map<UUID, EntityEnum> uuidEntityEnumMap = new HashMap<>();
		Set<UUID> orderedUUIDList = new LinkedHashSet<>();
		for (QueryTerm qt : queryTermList) {
			EntityEnum entity = EntityEnum.fromString(qt.getEntity()).orElse(EntityEnum.NOT_LISTED);
			uuidEntityEnumMap.putIfAbsent(qt.getUuid(), entity);
			orderedUUIDList.add(qt.getUuid());
		}
		int groupNumber = 0;
		pnrSeatConditionBuilder = new SeatConditionBuilder(RuleTemplateConstants.PNR_SEAT + groupNumber);
		this.pnrConditionBuilder = new PnrConditionBuilder(DOLLAR_SIGN + EntityEnum.PNR.getAlias() + groupNumber);
		EntityConditionBuilder ecb;

		for (UUID uuid : orderedUUIDList) {
			EntityEnum entityEnum = uuidEntityEnumMap.get(uuid);
			switch (entityEnum) {
			case DWELL_TIME:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				dwellTimeConditionBuilder.add((DwellTimeConditionBuilder) ecb);
				break;
			case FORM_OF_PAYMENT:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				paymentFormConditionBuilders.add((PaymentFormConditionBuilder) ecb);
				break;
			case TRAVEL_AGENCY:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				travelAgencyConditionBuilder.add((TravelAgencyConditionBuilder) ecb);
				break;
			case ADDRESS:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				addressConditionBuilder.add((AddressConditionBuilder) ecb);
				break;
			case PHONE:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				phoneConditionBuilder.add((PhoneConditionBuilder) ecb);
				break;
			case EMAIL:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				emailConditionBuilder.add((EmailConditionBuilder) ecb);
				break;
			case BOOKING_DETAIL:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				bookingDetailConditionBuilder.add((BookingDetailConditionBuilder) ecb);
				break;
			case FREQUENT_FLYER:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				frequentFlyerConditionBuilder.add((FrequentFlyerConditionBuilder) ecb);
				break;
			case CREDIT_CARD:
				groupNumber = conditionBuilderMap.size() + 1;
				ecb = getEntityConditionBuilder(entityEnum, groupNumber);
				ecb.setGroupNumber(groupNumber);
				conditionBuilderMap.put(uuid, ecb);
				creditCardConditionBuilder.add((CreditCardConditionBuilder) ecb);
				break;
			case PNR:
			default:
				break;
			}
		}
	}

	private EntityConditionBuilder getEntityConditionBuilder(EntityEnum entityEnum, int groupNumber) {
		EntityConditionBuilder ecb;
		String drlVariableName = DOLLAR_SIGN + entityEnum.getAlias() + groupNumber;
		switch (entityEnum) {
		case ADDRESS:
			ecb = new AddressConditionBuilder(drlVariableName);
			break;
		case BOOKING_DETAIL:
			ecb = new BookingDetailConditionBuilder(drlVariableName);
			break;
		case CREDIT_CARD:
			ecb = new CreditCardConditionBuilder(drlVariableName);
			break;
		case EMAIL:
			ecb = new EmailConditionBuilder(drlVariableName);
			break;
		case FREQUENT_FLYER:
			ecb = new FrequentFlyerConditionBuilder(drlVariableName);
			break;
		case PHONE:
			ecb = new PhoneConditionBuilder(drlVariableName);
			break;
		case TRAVEL_AGENCY:
			ecb = new TravelAgencyConditionBuilder(drlVariableName);
			break;
		case DWELL_TIME:
			ecb = new DwellTimeConditionBuilder(drlVariableName);
			break;
		case FORM_OF_PAYMENT:
			ecb = new PaymentFormConditionBuilder(drlVariableName);
			break;
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
	 * Creates linking criteria for PNR related objects.
	 * <p>
	 * the string builder to accumulate the linking conditions.
	 *
	 * @return true if at least one linking condition was generated.
	 */
	private String generatePnrLinks() {
		final StringBuilder linkStringBuilder = new StringBuilder();
		final String pnrVarName = pnrConditionBuilder.getDrlVariableName();


		if (!addressConditionBuilder.isEmpty()) {
			for (AddressConditionBuilder acb : addressConditionBuilder) {
				addLinkCondition(linkStringBuilder, acb.getLinkVariableName(), PnrAddressLink.class.getSimpleName(),
						pnrVarName);
			}
		}
		if (!phoneConditionBuilder.isEmpty()) {
			for (PhoneConditionBuilder pcb : phoneConditionBuilder) {
				addLinkCondition(linkStringBuilder, pcb.getLinkVariableName(), PnrPhoneLink.class.getSimpleName(),
						pnrVarName);
			}
		}
		if (!bookingDetailConditionBuilder.isEmpty()) {
			for (BookingDetailConditionBuilder bdcb : bookingDetailConditionBuilder) {
				addLinkCondition(linkStringBuilder, bdcb.getLinkVariableName(), PnrBookingLink.class.getSimpleName(),
						pnrVarName);
			}
		}
		if (!emailConditionBuilder.isEmpty()) {
			for (EmailConditionBuilder ecb : emailConditionBuilder) {
				addLinkCondition(linkStringBuilder, ecb.getLinkVariableName(), PnrEmailLink.class.getSimpleName(),
						pnrVarName);
			}
		}
		if (!creditCardConditionBuilder.isEmpty()) {
			for (CreditCardConditionBuilder creditCardBuilder : creditCardConditionBuilder) {
				addLinkCondition(linkStringBuilder, creditCardBuilder.getLinkVariableName(),
						PnrCreditCardLink.class.getSimpleName(), pnrVarName);
			}
		}

		if (!frequentFlyerConditionBuilder.isEmpty()) {
			for (FrequentFlyerConditionBuilder ffCb : frequentFlyerConditionBuilder) {
				addLinkCondition(linkStringBuilder, ffCb.getLinkVariableName(),
						PnrFrequentFlyerLink.class.getSimpleName(), pnrVarName);
			}
		}
		if (!travelAgencyConditionBuilder.isEmpty()) {
			for (TravelAgencyConditionBuilder travelCb : travelAgencyConditionBuilder) {
				addLinkCondition(linkStringBuilder, travelCb.getLinkVariableName(),
						PnrTravelAgencyLink.class.getSimpleName(), pnrVarName);
			}
		}
		if (!dwellTimeConditionBuilder.isEmpty()) {
			for (DwellTimeConditionBuilder dtCb : dwellTimeConditionBuilder) {
				addLinkCondition(linkStringBuilder, dtCb.getLinkVariableName(), PnrDwellTimeLink.class.getSimpleName(),
						pnrVarName);

			}
		}
		if (!paymentFormConditionBuilders.isEmpty()) {
			for (PaymentFormConditionBuilder pfcb : paymentFormConditionBuilders) {
				addLinkCondition(linkStringBuilder, pfcb.getLinkVariableName(),
						PnrFormOfPaymentLink.class.getSimpleName(), pnrVarName);
			}
		}
		return linkStringBuilder.toString();
	}

	private void addLinkCondition(final StringBuilder parentStringBuilder, final String linkVarName,
			final String linkClassName, final String pnrVarName) {
		parentStringBuilder.append(linkVarName).append(RuleTemplateConstants.COLON_CHAR).append(linkClassName)
				.append(RuleTemplateConstants.LEFT_PAREN_CHAR).append(LINK_PNR_ID).append(" == ").append(pnrVarName)
				.append(".id)\n");
	}


	/**
	 * Appends the generated "when" part (i.e., the LHS) of the rule to the rule
	 * document.
	 *
	 * @param parentStringBuilder
	 *            the rule document builder.
	 */
	public void buildConditionsAndApppend(final StringBuilder parentStringBuilder,
			PassengerConditionBuilder passengerBuilder) {
		// Make and ignore the link conditions. (We need to have the objects first)
		String linkConditions = generatePnrLinks();
		handleMultiVariableObjects();
		// make pnr object.
		if (pnrConditionBuilder.isEmpty() && StringUtils.isNotEmpty(linkConditions)) {
			pnrConditionBuilder.addConditionAsString(StringUtils.EMPTY);
		}

		parentStringBuilder.append(pnrConditionBuilder.build());
		addPnrLinkConditions(parentStringBuilder);

		// Link passenger to PNR
		if (!pnrConditionBuilder.isEmpty() || StringUtils.isNotEmpty(linkConditions)) {
			parentStringBuilder.append("$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)\n");
		}

		if (!pnrSeatConditionBuilder.isEmpty()) {
			pnrSeatConditionBuilder.addApisCondition(false);
			parentStringBuilder.append(pnrSeatConditionBuilder.build());
		}
		for (AddressConditionBuilder acb : addressConditionBuilder) {
			parentStringBuilder.append(acb.build());
		}
		for (PhoneConditionBuilder pCb : phoneConditionBuilder) {
			parentStringBuilder.append(pCb.build());
		}
		for (EmailConditionBuilder eCb : emailConditionBuilder) {
			parentStringBuilder.append(eCb.build());
		}
		for (CreditCardConditionBuilder ccCb : creditCardConditionBuilder) {
			parentStringBuilder.append(ccCb.build());
		}
		for (TravelAgencyConditionBuilder taCb : travelAgencyConditionBuilder) {
			parentStringBuilder.append(taCb.build());
		}
		for (FrequentFlyerConditionBuilder ffCb : frequentFlyerConditionBuilder) {
			parentStringBuilder.append(ffCb.build());
		}
		for (DwellTimeConditionBuilder dtCb : dwellTimeConditionBuilder) {
			parentStringBuilder.append(dtCb.build());
		}
		for (PaymentFormConditionBuilder pfcb : paymentFormConditionBuilders) {
			parentStringBuilder.append(pfcb.build());
		}
		for (BookingDetailConditionBuilder bdCb : bookingDetailConditionBuilder) {
			parentStringBuilder.append(bdCb.build());
		}
	}

	public void addPnrLinkConditions(StringBuilder parentStringBuilder) {
		String linkConditions = generatePnrLinks();
		if (pnrConditionBuilder.isEmpty() && StringUtils.isNotEmpty(linkConditions)) {
			pnrConditionBuilder.addConditionAsString(StringUtils.EMPTY);
		}
		parentStringBuilder.append(linkConditions);
	}

	public void reset() {
		addressConditionBuilder = new ArrayList<>();
		phoneConditionBuilder = new ArrayList<>();
		emailConditionBuilder = new ArrayList<>();
		creditCardConditionBuilder = new ArrayList<>();
		travelAgencyConditionBuilder = new ArrayList<>();
		frequentFlyerConditionBuilder = new ArrayList<>();
		dwellTimeConditionBuilder = new ArrayList<>();
		bookingDetailConditionBuilder = new ArrayList<>();
		paymentFormConditionBuilders = new ArrayList<>();
		pnrConditionBuilder.reset();
		pnrSeatConditionBuilder.reset();
	}

	/**
	 * Adds a rule condition to the builder.
	 *
	 * @param trm
	 *            the condition to add.
	 */
	public void addRuleCondition(final TypeEnum attributeType, final CriteriaOperatorEnum opCode, final QueryTerm trm) {
		EntityEnum entity = EntityEnum.fromString(trm.getEntity()).orElse(EntityEnum.NOT_LISTED);
		try {
			switch (entity) {
			case PNR:
				if (SEAT_ENTITY_NAME_LOWERCASE.equals(trm.getField().toLowerCase())) {
					pnrSeatConditionBuilder.addCondition(opCode, SEAT_ATTRIBUTE_NAME,
							attributeType, trm.getValue());
				} else {
					pnrConditionBuilder.addCondition(opCode, trm.getField(), attributeType, trm.getValue());
				}
				break;
			case FORM_OF_PAYMENT:
			case ADDRESS:
			case BOOKING_DETAIL:
			case PHONE:
			case EMAIL:
			case CREDIT_CARD:
			case TRAVEL_AGENCY:
			case DWELL_TIME:
			case FREQUENT_FLYER:
				conditionBuilderMap.get(trm.getUuid()).addCondition(opCode, trm.getField(), attributeType,
						trm.getValue());
				break;
			case HITS:
			case FLIGHT:
			case PASSENGER:
			case BAG:
			case FLIGHT_PAX:
			case DOCUMENT:
			case NOT_LISTED:
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

	private void handleMultiVariableObjects() {
		handleMultipleObjectTypeOnSameRule(addressConditionBuilder);
		handleMultipleObjectTypeOnSameRule(phoneConditionBuilder);
		handleMultipleObjectTypeOnSameRule(emailConditionBuilder);
		handleMultipleObjectTypeOnSameRule(creditCardConditionBuilder);
		handleMultipleObjectTypeOnSameRule(frequentFlyerConditionBuilder);
		handleMultipleObjectTypeOnSameRule(travelAgencyConditionBuilder);
		handleMultipleObjectTypeOnSameRule(dwellTimeConditionBuilder);
		handleMultipleObjectTypeOnSameRule(bookingDetailConditionBuilder);
		handleMultipleObjectTypeOnSameRule(paymentFormConditionBuilders);

	}

	public PnrConditionBuilder getPnrConditionBuilder() {
		return pnrConditionBuilder;
	}

	public boolean hasSeats() {
		return !pnrSeatConditionBuilder.isEmpty();
	}

	public SeatConditionBuilder getPnrSeatBuilder() {
		return pnrSeatConditionBuilder;
	}

	public String getSeatVarName() {
		return pnrSeatConditionBuilder.getDrlVariableName();
	}
}

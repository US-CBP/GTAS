/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.COMMA_CHAR;
import static gov.gtas.rule.builder.RuleTemplateConstants.DOUBLE_QUOTE_CHAR;
import static gov.gtas.rule.builder.RuleTemplateConstants.FALSE_STRING;
import static gov.gtas.rule.builder.RuleTemplateConstants.LEFT_PAREN_CHAR;
import static gov.gtas.rule.builder.RuleTemplateConstants.REGEX_WILDCARD;
import static gov.gtas.rule.builder.RuleTemplateConstants.RIGHT_PAREN_CHAR;
import static gov.gtas.rule.builder.RuleTemplateConstants.SPACE_CHAR;
import static gov.gtas.rule.builder.RuleTemplateConstants.TRUE_STRING;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.AddressMapping;
import gov.gtas.querybuilder.mappings.BookingDetailMapping;
import gov.gtas.querybuilder.mappings.CreditCardMapping;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.EmailMapping;
import gov.gtas.querybuilder.mappings.FlightMapping;
import gov.gtas.querybuilder.mappings.FrequentFlyerMapping;
import gov.gtas.querybuilder.mappings.IEntityMapping;
import gov.gtas.querybuilder.mappings.PNRMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.querybuilder.mappings.PhoneMapping;
import gov.gtas.querybuilder.mappings.TravelAgencyMapping;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.Date;

public class RuleConditionBuilderHelper {

    private static String convertJsonStringVal(final TypeEnum type,
            final String value, final boolean beginsWithWildcard,
            final boolean endsWithWildcard) throws ParseException {
        String ret = null;
        switch (type) {
        case BOOLEAN:
            if (value.charAt(0) == 'Y') {
                ret = TRUE_STRING;
            } else if (value.charAt(0) == 'N') {
                ret = FALSE_STRING;
            } else if (TRUE_STRING.equalsIgnoreCase(value)) {
                ret = TRUE_STRING;
            } else if (FALSE_STRING.equalsIgnoreCase(value)) {
                ret = FALSE_STRING;
            } else if (value.charAt(0) == '1') {
                ret = TRUE_STRING;
            } else if (value.charAt(0) == '0') {
                ret = FALSE_STRING;
            } else {
                throw new RuntimeException("Unsupported boolean value");
            }
            break;
        case STRING:
            //added value.toUpperCase() since Loader converts strings to upper case.
            //Thus string equality will be case insensitive
            if (beginsWithWildcard || endsWithWildcard) {
                ret = createRegex(value.toUpperCase(), beginsWithWildcard, endsWithWildcard);
            } else {
                ret = DOUBLE_QUOTE_CHAR + value.toUpperCase() + DOUBLE_QUOTE_CHAR;
            }
            break;
        case DATE:
            Date date = DateCalendarUtils.parseJsonDate(value);
            ret = DOUBLE_QUOTE_CHAR
                    + DateCalendarUtils.formatRuleEngineDate(date)
                    + DOUBLE_QUOTE_CHAR;
            break;
        case TIME:
            date = DateCalendarUtils.parseJsonDate(value);
            ret = DOUBLE_QUOTE_CHAR
                    + DateCalendarUtils.formatRuleEngineDateTime(date)
                    + DOUBLE_QUOTE_CHAR;
            break;
        case DATETIME:
            date = DateCalendarUtils.parseJsonDate(value);
            ret = DOUBLE_QUOTE_CHAR
                    + DateCalendarUtils.formatRuleEngineDateTime(date)
                    + DOUBLE_QUOTE_CHAR;
            break;
        case INTEGER:
        case LONG:
        case DOUBLE:
            ret = value;
            break;
        }
        return ret;
    }

    /**
     * Creates a regular expression that begins/ends or both begins and ends
     * with wild cards.
     * 
     * @param value the string to put in the regex.
     * @param beginsWithWildcard if true a wildcard is inserted at the start of the regex.
     * @param endsWithWildcard if true a wildcard is appended to the regex.
     * @return
     */
    private static String createRegex(String value,
            final boolean beginsWithWildcard, final boolean endsWithWildcard) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(DOUBLE_QUOTE_CHAR);
        if (beginsWithWildcard) {
            bldr.append(REGEX_WILDCARD);
        }
        //TODO escape the regex characters in the value string (e.g., '.')
        bldr.append(value);
        if (endsWithWildcard) {
            bldr.append(REGEX_WILDCARD);
        }
        bldr.append(DOUBLE_QUOTE_CHAR);
        return bldr.toString();
    }

    public static void addConditionValue(final TypeEnum type, final String val,
            final StringBuilder bldr, final boolean beginsWithWildcard,
            final boolean endsWithWildcard) throws ParseException {
        bldr.append(convertJsonStringVal(type, val, beginsWithWildcard,
                endsWithWildcard));
    }

    public static void addConditionValue(final TypeEnum type, final String val,
            final StringBuilder bldr) throws ParseException {
        bldr.append(convertJsonStringVal(type, val, false, false));
    }

    public static void addConditionValues(final TypeEnum type,
            final String[] values, StringBuilder bldr) throws ParseException {
        bldr.append(LEFT_PAREN_CHAR);
        if (values != null && values.length > 0) {
            bldr.append(convertJsonStringVal(type, values[0], false, false));
            for (int i = 1; i < values.length; ++i) {
                bldr.append(COMMA_CHAR)
                        .append(SPACE_CHAR)
                        .append(convertJsonStringVal(type, values[i], false,
                                false));
            }
        }
        bldr.append(RIGHT_PAREN_CHAR);
    }

    /**
     * Creates a friendly description for the criterion.
     * 
     * @param cond
     *            the JSON query term
     * @param bldr
     *            the string builder to use for constructing the description.
     * @throws ParseException
     *             on error.
     */
    public static void addConditionDescription(final QueryTerm cond,
            StringBuilder bldr) throws ParseException {
        CriteriaOperatorEnum opCode = CriteriaOperatorEnum.getEnum(cond.getOperator());
        TypeEnum attributeType = TypeEnum.getEnum(cond.getType());

        EntityEnum entity = EntityEnum.getEnum(cond.getEntity());
        bldr.append(entity.getEntityName()).append(SPACE_CHAR)
                .append(getFieldName(entity, cond.getField()))
                .append(SPACE_CHAR).append(opCode.getDisplayName())
                .append(SPACE_CHAR);

        String[] values = cond.getValue();
        if (values != null && values.length > 0) {
            switch (opCode) {
            case IN:
            case NOT_IN:
            case BETWEEN:
            case NOT_BETWEEN:
                addConditionValues(attributeType, values, bldr);
                break;
            case IS_NULL:
            case IS_NOT_NULL:
            	break;
            default:
                // single value
                addConditionValue(attributeType, values[0], bldr);
                break;
            }
        }
    }

    /**
     * Gets the friendly English like name for the field of the entity.
     * 
     * @param entity
     *            the entity containing the field
     * @param field
     *            the field whose name is to be fetched
     * @return the friendly name of the field.
     */
    private static String getFieldName(EntityEnum entity, String field) {
        String ret = field;
        switch (entity) {
        case PASSENGER:
            ret = extractFriendlyName(PassengerMapping.values(), field);
            break;
        case FLIGHT:
            ret = extractFriendlyName(FlightMapping.values(), field);
            break;
        case DOCUMENT:
            ret = extractFriendlyName(DocumentMapping.values(), field);
            break;
        case PNR:
            ret = extractFriendlyName(PNRMapping.values(), field);
            break;
        case PHONE:
            ret = extractFriendlyName(PhoneMapping.values(), field);
            break;
        case ADDRESS:
            ret = extractFriendlyName(AddressMapping.values(), field);
            break;
        case FREQUENT_FLYER:
            ret = extractFriendlyName(FrequentFlyerMapping.values(), field);
            break;
        case TRAVEL_AGENCY:
            ret = extractFriendlyName(TravelAgencyMapping.values(), field);
            break;
        case CREDIT_CARD:
            ret = extractFriendlyName(CreditCardMapping.values(), field);
            break;
        case BOOKING_DETAIL:
            ret = extractFriendlyName(BookingDetailMapping.values(), field);
            break;
        case EMAIL:
            ret = extractFriendlyName(EmailMapping.values(), field);
            break;
        case HITS:
            // NA
            break;
        }
        return ret;
    }

    private static String extractFriendlyName(
            final IEntityMapping[] mappingValues, String field) {
        String ret = field;
        for (IEntityMapping fldEnum : mappingValues) {
            if (field.equalsIgnoreCase(fldEnum.getFieldName())) {
                ret = fldEnum.getFriendlyName();
            }
        }
        return ret;
    }
}

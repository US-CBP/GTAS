/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.ADDRESS_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.CREDIT_CARD_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.DOCUMENT_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.EMAIL_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FLIGHT_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FREQUENT_FLYER_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import static gov.gtas.rule.builder.RuleTemplateConstants.PASSENGER_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PHONE_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PNR_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.TRAVEL_AGENCY_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.DWELL_TIME_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FLIGHT_PAX_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.BAG_VARIABLE_NAME;
import static gov.gtas.util.DateCalendarUtils.addOneDayToDate;
import static gov.gtas.util.DateCalendarUtils.formatRuleEngineDate;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility functions to construct Drools rules
 */
public class EngineRuleUtils {
    /**
     * Creates a single engine rule from a minterm.
     * 
     * @param ruleData
     *            the minterm.
     * @param parent
     *            the parent UDR rule.
     * @param indx
     *            the ordering index of the rule with respect to the parent.
     * @return the engine rule created.
     * @throws ParseException
     *             parse exception.
     */
    public static Rule createEngineRule(List<QueryTerm> ruleData,
            UdrRule parent, int indx) {

        StringBuilder stringBuilder = new StringBuilder();
        RuleConditionBuilder ruleConditionBuilder = new RuleConditionBuilder(
                createEngineRuleVariableMap());

        Rule ret = new Rule(parent, indx, null);
        addRuleHeader(parent, ret, stringBuilder);
        for (QueryTerm trm : ruleData) {
            ruleConditionBuilder.addRuleCondition(trm);
        }
        ruleConditionBuilder.buildConditionsAndApppend(stringBuilder);
        List<String> causes = ruleConditionBuilder.addRuleAction(stringBuilder,
                parent, ret, RuleTemplateConstants.PASSENGER_VARIABLE_NAME);

        ret.setRuleDrl(stringBuilder.toString());
        ret.addRuleCriteria(causes);

        return ret;
    }
    /**
     * Creates the header including the rule title. 
     * @param parent
     * @param rule
     * @param bldr
     */
    private static void addRuleHeader(UdrRule parent, Rule rule,
            StringBuilder bldr) {
        bldr.append("rule \"").append(parent.getTitle()).append(":")
                .append(parent.getAuthor().getUserId()).append(":")
                .append(rule.getRuleIndex()).append("\"").append(NEW_LINE)
                .append("date-effective \"")
                .append(formatRuleEngineDate(parent.getMetaData().getStartDt()))
                .append("\"").append(NEW_LINE);
        Date endDate = parent.getMetaData().getEndDt();
        if(endDate != null){
            bldr.append("date-expires \"").append(formatRuleEngineDate(addOneDayToDate(endDate)))
            .append("\"").append(NEW_LINE);
        }
        bldr.append("when\n");
    }
    /**
     * Creates a map of rule variables to use when generating engine rules.
     * 
     * @return the rule variable map.
     */
    public static Map<EntityEnum, String> createEngineRuleVariableMap() {
        Map<EntityEnum, String> ret = new HashMap<EntityEnum, String>();
        ret.put(EntityEnum.PASSENGER, PASSENGER_VARIABLE_NAME);
        ret.put(EntityEnum.DOCUMENT, DOCUMENT_VARIABLE_NAME);
        ret.put(EntityEnum.FLIGHT, FLIGHT_VARIABLE_NAME);
        ret.put(EntityEnum.PNR, PNR_VARIABLE_NAME);
        ret.put(EntityEnum.ADDRESS, ADDRESS_VARIABLE_NAME);
        ret.put(EntityEnum.PHONE, PHONE_VARIABLE_NAME);
        ret.put(EntityEnum.EMAIL, EMAIL_VARIABLE_NAME);
        ret.put(EntityEnum.FREQUENT_FLYER, FREQUENT_FLYER_VARIABLE_NAME);
        ret.put(EntityEnum.TRAVEL_AGENCY, TRAVEL_AGENCY_VARIABLE_NAME);
        ret.put(EntityEnum.CREDIT_CARD, CREDIT_CARD_VARIABLE_NAME);
        ret.put(EntityEnum.DWELL_TIME, DWELL_TIME_VARIABLE_NAME);
        ret.put(EntityEnum.FLIGHT_PAX, FLIGHT_PAX_VARIABLE_NAME);
        ret.put(EntityEnum.BAG, BAG_VARIABLE_NAME);
        return ret;
    }

}

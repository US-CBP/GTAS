/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import static gov.gtas.util.DateCalendarUtils.addOneDayToDate;
import static gov.gtas.util.DateCalendarUtils.formatRuleEngineDate;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @return the engine rule created. parse exception.
	 */

	private static final Logger logger = LoggerFactory.getLogger(EngineRuleUtils.class);

	public static Rule createEngineRule(List<QueryTerm> ruleData, UdrRule parent, int indx) {

		StringBuilder stringBuilder = new StringBuilder();
		RuleConditionBuilder ruleConditionBuilder = new RuleConditionBuilder(ruleData);

		Rule ret = new Rule(parent, indx, null);
		addRuleHeader(parent, ret, stringBuilder);
		for (QueryTerm trm : ruleData) {
			ruleConditionBuilder.addRuleCondition(trm);
		}
		ruleConditionBuilder.buildConditionsAndApppend(stringBuilder);
		List<String> causes = ruleConditionBuilder.addRuleAction(stringBuilder, parent);

		logger.info("\nDRL string for UDR rule: \n" + stringBuilder.toString() + "\n\n");
		ret.setRuleDrl(stringBuilder.toString());
		ret.addRuleCriteria(causes);

		return ret;
	}

	/**
	 * Creates the header including the rule title.
	 * 
	 * @param parent
	 * @param rule
	 * @param bldr
	 */
	private static void addRuleHeader(UdrRule parent, Rule rule, StringBuilder bldr) {
		bldr.append("rule \"").append(parent.getTitle()).append(":").append(parent.getAuthor().getUserId()).append(":")
				.append(rule.getRuleIndex()).append("\"").append(NEW_LINE).append("date-effective \"")
				.append(formatRuleEngineDate(parent.getMetaData().getStartDt())).append("\"").append(NEW_LINE);
		Date endDate = parent.getMetaData().getEndDt();
		if (endDate != null) {
			bldr.append("date-expires \"").append(formatRuleEngineDate(addOneDayToDate(endDate))).append("\"")
					.append(NEW_LINE);
		}
		bldr.append("when\n");
	}
}

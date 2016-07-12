/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.rule.builder.EngineRuleUtils;
import gov.gtas.rule.builder.RuleConditionBuilder;
import gov.gtas.rule.builder.RuleTemplateConstants;

import java.util.List;

/**
 * Helper class for the UDR service.
 */
public class WatchlistRuleCreationUtil {
    public static List<String> createWatchlistRule(EntityEnum entity, WatchlistTerm[] wlData,
            String title, StringBuilder ruleOutput) {
        RuleConditionBuilder ruleConditionBuilder = new RuleConditionBuilder(
                EngineRuleUtils.createEngineRuleVariableMap());

        ruleOutput.append("rule \"").append(title).append(":%d\"")
                .append(NEW_LINE).append("when\n");
        for (WatchlistTerm wlterm : wlData) {
            QueryTerm trm = new QueryTerm(entity.getEntityName(),
                    wlterm.getField(), wlterm.getType(),
                    CriteriaOperatorEnum.EQUAL.toString(), new String[]{wlterm.getValue()});
            ruleConditionBuilder.addRuleCondition(trm);
        }
        ruleConditionBuilder.buildConditionsAndApppend(ruleOutput);
        List<String> causes = ruleConditionBuilder.addWatchlistRuleAction(ruleOutput, entity, 
                title, RuleTemplateConstants.PASSENGER_VARIABLE_NAME);

        return causes;
    }

}

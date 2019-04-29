/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.rule.builder.RuleConditionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for the UDR service.
 */
public class WatchlistRuleCreationUtil {
    public static List<String> createWatchlistRule(EntityEnum entity, WatchlistTerm[] wlData,
            String title, StringBuilder ruleOutput) {

        ruleOutput.append("rule \"").append(title).append(":%d\"")
                .append(NEW_LINE).append("when\n");
        List<QueryTerm> queryTerms = new ArrayList<>();
        UUID qtUUID = UUID.randomUUID(); //UUID is how the rule engine tells which group an object is in.
        for (WatchlistTerm wlterm : wlData) {
            QueryTerm trm = new QueryTerm(entity.getEntityName(),
                    wlterm.getField(), wlterm.getType(),
                    CriteriaOperatorEnum.EQUAL.toString(), new String[]{wlterm.getValue()});
            trm.setUuid(qtUUID);
            queryTerms.add(trm);
        }
        RuleConditionBuilder ruleConditionBuilder = new RuleConditionBuilder(queryTerms);
        for (QueryTerm qt : queryTerms) {
            ruleConditionBuilder.addRuleCondition(qt);
        }
        ruleConditionBuilder.buildConditionsAndApppend(ruleOutput);

        return ruleConditionBuilder.addWatchlistRuleAction(ruleOutput, entity
        );
    }

}

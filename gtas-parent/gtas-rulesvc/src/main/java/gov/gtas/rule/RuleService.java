/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;

import java.util.Map;

/**
 * The interface for the Rule Engine.
 */
public interface RuleService {
	/**
	 * Execute the rule engine on the specified request for the specified rule file.
	 * (Note: the file should be on the class path.)
	 * 
	 * @param ruleFilePath
	 *            the path name of the rule file to invoke the engine on.
	 * @param req
	 *            the rule request message.
	 * @return the result of the rule engine invocation.
	 */
	RuleServiceResult invokeAdhocRules(String ruleFilePath, RuleServiceRequest req);

	/**
	 * Execute the rule engine on the specified request for the default
	 * KnowledgeBase.
	 * 
	 * @param req
	 *            the rule request message.
	 * @param kbName
	 *            The name of the knowledge base to use for executing the request.
	 * @return the result of the rule engine invocation.
	 */
	RuleServiceResult invokeRuleEngine(RuleServiceRequest req, String kbName, Map<String, KIEAndLastUpdate> rules);

}

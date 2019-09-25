/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import java.util.List;

/**
 * Interface definition for rule engine execution result objects.
 */
public interface RuleServiceResult {
	/**
	 * Gets the list of Passenger IDs "hit" by the rules.
	 * 
	 * @return the list of hits.
	 */
	List<RuleHitDetail> getResultList();

	/**
	 * Gets the statistics of the rule engine execution.
	 * 
	 * @return rule engine execution statistics.
	 */
	RuleExecutionStatistics getExecutionStatistics();
}

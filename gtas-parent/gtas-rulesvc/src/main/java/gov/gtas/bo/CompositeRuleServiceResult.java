/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class CompositeRuleServiceResult that combines Udr and watchlist results.
 */
public class CompositeRuleServiceResult implements RuleServiceResult, Serializable {
	private static final long serialVersionUID = 6373119898883595702L;

	private List<RuleHitDetail> resultList;
	private RuleExecutionStatistics executionStatistics;
	private RuleExecutionStatistics wlExecutionStatistics;

	/**
	 * Instantiates a new composite rule service result.
	 *
	 * @param udrResults
	 *            the udr results
	 * @param watchlistResults
	 *            the watchlist results
	 */
	public CompositeRuleServiceResult(RuleServiceResult udrResults, RuleServiceResult watchlistResults) {
		if (udrResults != null) {
			this.resultList = udrResults.getResultList();
			this.executionStatistics = udrResults.getExecutionStatistics();
		} else {
			this.resultList = new LinkedList<>();
		}
		if (watchlistResults != null) {
			this.resultList.addAll(watchlistResults.getResultList());
			this.wlExecutionStatistics = watchlistResults.getExecutionStatistics();
			if (this.executionStatistics == null) {
				this.executionStatistics = this.wlExecutionStatistics;
			}
		}
	}

	@Override
	public List<RuleHitDetail> getResultList() {
		return this.resultList;
	}

	@Override
	public RuleExecutionStatistics getExecutionStatistics() {
		return this.executionStatistics;
	}

	/**
	 * @return the wlExecutionStatistics
	 */
	public RuleExecutionStatistics getWlExecutionStatistics() {
		return wlExecutionStatistics;
	}

}

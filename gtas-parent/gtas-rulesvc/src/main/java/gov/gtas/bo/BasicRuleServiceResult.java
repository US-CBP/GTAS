/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import gov.gtas.model.RuleHitDetail;

import java.io.Serializable;
import java.util.List;

/**
 * The Class BasicRuleServiceResult.
 */
public class BasicRuleServiceResult implements RuleServiceResult, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6373119898883595702L;

	private List<RuleHitDetail> resultList;
	private RuleExecutionStatistics executionStatistics;

	public BasicRuleServiceResult() {
	}
	/**
	 * Instantiates a new basic rule service result.
	 *
	 * @param resultList
	 *            the result list
	 * @param executionStatistics
	 *            the execution statistics
	 */
	public BasicRuleServiceResult(List<RuleHitDetail> resultList, RuleExecutionStatistics executionStatistics) {
		this.resultList = resultList;
		this.executionStatistics = executionStatistics;
	}

	@Override
	public List<RuleHitDetail> getResultList() {
		return this.resultList;
	}

	@Override
	public RuleExecutionStatistics getExecutionStatistics() {
		return this.executionStatistics;
	}
	public void setResultList(List<RuleHitDetail> resultList) {
		this.resultList = resultList;
	}
	public void setExecutionStatistics(RuleExecutionStatistics executionStatistics) {
		this.executionStatistics = executionStatistics;
	}
	

}

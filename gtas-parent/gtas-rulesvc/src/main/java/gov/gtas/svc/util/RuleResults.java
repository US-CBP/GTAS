package gov.gtas.svc.util;

import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.bo.TargetSummaryVo;

import java.util.Collection;

public class RuleResults {
	
	public RuleResults() {}

	private BasicRuleServiceResult udrResult;

	private BasicRuleServiceResult watchListResult;

	private Collection<TargetSummaryVo> targetingResult;

	public BasicRuleServiceResult getUdrResult() {
		return udrResult;
	}

	public void setUdrResult(BasicRuleServiceResult udrResult) {
		this.udrResult = udrResult;
	}

	public BasicRuleServiceResult getWatchListResult() {
		return watchListResult;
	}

	public void setWatchListResult(BasicRuleServiceResult watchListResult) {
		this.watchListResult = watchListResult;
	}

	public boolean hasResults() {
		return (this.watchListResult != null || this.udrResult != null);
	}

	public Collection<TargetSummaryVo> getTargetingResult() {
		return targetingResult;
	}

	public void setTargetingResult(Collection<TargetSummaryVo> targetingResult) {
		this.targetingResult = targetingResult;
	}
}

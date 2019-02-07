package gov.gtas.svc.util;

import gov.gtas.bo.RuleServiceResult;
import gov.gtas.bo.TargetSummaryVo;

import java.util.Collection;

public class RuleResults {

    private RuleServiceResult udrResult;

    private RuleServiceResult watchListResult;


    private Collection<TargetSummaryVo> targetingResult;


    public RuleServiceResult getUdrResult() {
        return udrResult;
    }

    public void setUdrResult(RuleServiceResult udrResult) {
        this.udrResult = udrResult;
    }

    public RuleServiceResult getWatchListResult() {
        return watchListResult;
    }

    public void setWatchListResult(RuleServiceResult watchListResult) {
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

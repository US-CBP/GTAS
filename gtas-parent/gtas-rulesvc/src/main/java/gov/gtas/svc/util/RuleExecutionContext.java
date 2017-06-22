/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.svc.request.builder.PassengerFlightTuple;

import java.util.Collection;
import java.util.Set;
/**
 * This class is for objects that carry state information during 
 * Targeting operations.
 */
public class RuleExecutionContext {
    private Set<PassengerFlightTuple> paxFlightTuples;
    private RuleServiceRequest ruleServiceRequest;
    private Collection<TargetSummaryVo> targetingResult;
    private RuleExecutionStatistics ruleExecutionStatistics;
    /**
     * @return the paxFlightTuples
     */
    public  Set<PassengerFlightTuple> getPaxFlightTuples() {
        return paxFlightTuples;
    }
    /**
     * @param paxFlightTuples the paxFlightTuples to set
     */
    public  void setPaxFlightTuples(Set<PassengerFlightTuple> paxFlightTuples) {
        this.paxFlightTuples = paxFlightTuples;
    }
    /**
     * @return the ruleServiceRequest
     */
    public RuleServiceRequest getRuleServiceRequest() {
        return ruleServiceRequest;
    }
    /**
     * @param ruleServiceRequest the ruleServiceRequest to set
     */
    public void setRuleServiceRequest(RuleServiceRequest ruleServiceRequest) {
        this.ruleServiceRequest = ruleServiceRequest;
    }
    /**
     * @return the targetingResult
     */
    public Collection<TargetSummaryVo> getTargetingResult() {
        return targetingResult;
    }
    /**
     * @param targetingResult the targetingResult to set
     */
    public void setTargetingResult(Collection<TargetSummaryVo> targetingResult) {
        this.targetingResult = targetingResult;
    }
    /**
     * @return the ruleExecutionStatistics
     */
    public RuleExecutionStatistics getRuleExecutionStatistics() {
        return ruleExecutionStatistics;
    }
    /**
     * @param ruleExecutionStatistics the ruleExecutionStatistics to set
     */
    public void setRuleExecutionStatistics(
            RuleExecutionStatistics ruleExecutionStatistics) {
        this.ruleExecutionStatistics = ruleExecutionStatistics;
    }
    
}

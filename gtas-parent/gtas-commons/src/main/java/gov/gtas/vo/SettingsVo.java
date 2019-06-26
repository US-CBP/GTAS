/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

public class SettingsVo {
    private double matchingThreshold;
    private int maxPassengerQueryResult;
    private int maxFlightQueryResult;
    private double flightRange;
    private String apisOnlyFlag;
    private String apisVersion;
    private Integer maxRuleHit;

    public double getMatchingThreshold() {
            return matchingThreshold;
    }
    public void setMatchingThreshold(double matchingThreshold) {
            this.matchingThreshold = matchingThreshold;
    }
    public double getFlightRange() {
            return flightRange;
    }
    public void setFlightRange(double flightRange) {
            this.flightRange = flightRange;
    }

    public String getApisOnlyFlag() {
        return apisOnlyFlag;
    }

    public void setApisOnlyFlag(String apisOnlyFlag) {
        this.apisOnlyFlag = apisOnlyFlag;
    }

    public String getApisVersion() {
        return apisVersion;
    }

    public void setApisVersion(String apisVersion) {
        this.apisVersion = apisVersion;
    }


    public int getMaxPassengerQueryResult() {
        return maxPassengerQueryResult;
    }

    public void setMaxPassengerQueryResult(int maxPassengerQueryResult) {
        this.maxPassengerQueryResult = maxPassengerQueryResult;
    }

    public int getMaxFlightQueryResult() {
        return maxFlightQueryResult;
    }

    public void setMaxFlightQueryResult(int maxFlightQueryResult) {
        this.maxFlightQueryResult = maxFlightQueryResult;
    }

    public Integer getMaxRuleHit() {
        return maxRuleHit;
    }

    public void setMaxRuleHit(Integer maxRuleHit) {
        this.maxRuleHit = maxRuleHit;
    }
}
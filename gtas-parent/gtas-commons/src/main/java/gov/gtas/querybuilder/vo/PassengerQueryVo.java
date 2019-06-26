/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.vo;

import java.util.List;

public class PassengerQueryVo {
    List<Object[]> result;
    long totalPassengers;
    private boolean queryLimitReached;

    public List<Object[]> getResult() {
        return result;
    }
    public void setResult(List<Object[]> result) {
        this.result = result;
    }
    public long getTotalPassengers() {
        return totalPassengers;
    }
    public void setTotalPassengers(long totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public boolean isQueryLimitReached() {
        return queryLimitReached;
    }

    public void setQueryLimitReached(boolean queryLimitReached) {
        this.queryLimitReached = queryLimitReached;
    }
}

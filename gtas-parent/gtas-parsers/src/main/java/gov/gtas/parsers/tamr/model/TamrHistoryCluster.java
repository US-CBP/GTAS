/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

public class TamrHistoryCluster {
    private String gtasId;
    private String tamrId;
    private int version;
    private TamrHistoryClusterAction action;
    
    public TamrHistoryCluster() {
    }
 
    public String getTamrId() {
        return tamrId;
    }

    public void setTamrId(String tamrId) {
        this.tamrId = tamrId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getGtasId() {
        return gtasId;
    }

    public void setGtasId(String gtasId) {
        this.gtasId = gtasId;
    }
    
    public TamrHistoryClusterAction getAction() {
        return action;
    }

    public void setAction(TamrHistoryClusterAction action) {
        this.action = action;
    }
}

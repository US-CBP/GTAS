/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

public class TamrTravelerResponse {
    private String gtasId;
    private List<TamrDerogHit> derogIds;
    private String tamrId;
    private int version;
    private float score;
    
    public TamrTravelerResponse() {
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

    public List<TamrDerogHit> getDerogIds() {
        return this.derogIds;
    }

    public void setDerogIds(List<TamrDerogHit> derogIds) {
        this.derogIds = derogIds;
    }
    
    public float getScore() {
        return this.score;
    }
    
    public void setScore(float score) {
        this.score = score;
    }
}

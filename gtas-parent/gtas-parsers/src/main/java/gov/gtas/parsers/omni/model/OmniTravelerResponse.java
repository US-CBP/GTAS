/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.List;

public class OmniTravelerResponse {
    private String paxId;
    private List<OmniDerogHit> derogIds;
    private float score;

    public String getPaxId() {
        return paxId;
    }

    public void setPaxId(String paxId) {
        this.paxId = paxId;
    }

    public List<OmniDerogHit> getDerogIds() {
        return this.derogIds;
    }

    public void setDerogIds(List<OmniDerogHit> derogIds) {
        this.derogIds = derogIds;
    }
    
    public float getScore() {
        return this.score;
    }
    
    public void setScore(float score) {
        this.score = score;
    }
}

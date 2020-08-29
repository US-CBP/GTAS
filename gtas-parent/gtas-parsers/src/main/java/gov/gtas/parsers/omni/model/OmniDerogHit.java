/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

public class OmniDerogHit {
    private String derogId;
    private float score = 0;
    
    public OmniDerogHit() {
    }

    public String getDerogId() {
        return this.derogId;
    }
    
    public void setDerogId(String derogId) {
        this.derogId = derogId;
    }

    public float getScore() {
        return this.score;
    }
    
    public void setScore(float score) {
        this.score = score;
    }
}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

public class TamrDerogHit {
    private String derogId;
    private float score = 0;
    
    public TamrDerogHit() {
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

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import gov.gtas.util.DateCalendarUtils;

import java.io.Serializable;
import java.util.Date;
/**
 * Class representing summary listing element for UDR.
 */
public class JsonUdrListElement implements Serializable {

    /**
     * serial version UID
     */
    private static final long serialVersionUID = -4512984413526659992L;

    private long id;
    private String modifiedBy;
    private String modifiedOn;
    private int hitCount;
    private MetaData summary;
    
    public JsonUdrListElement(long id, String modifiedBy, Date modifiedOn, MetaData meta){
        this.id = id;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = DateCalendarUtils.formatJsonDate(modifiedOn);
        this.summary = meta;
        this.hitCount = 0;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the summary
     */
    public MetaData getSummary() {
        return summary;
    }

    /**
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @return the modifiedOn
     */
    public String getModifiedOn() {
        return modifiedOn;
    }

    /**
     * @return the hitCount
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * @param hitCount the hitCount to set
     */
    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }
    
}

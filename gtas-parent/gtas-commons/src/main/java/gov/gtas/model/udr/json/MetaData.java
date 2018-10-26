/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import gov.gtas.constant.RuleConstants;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * Rule meta-data JSON object class.
 */
public class MetaData implements Serializable {
    private static final long serialVersionUID = -1376823917772400633L;
    
    private String title;
    private String description;

    private Long ruleCat;
        
    private Date startDate;
    
    private Date endDate;
    
    private String author;
    private boolean enabled;
    public MetaData(){
        
    }
    public MetaData(String title, String descr, Date startDate, String auth){
        this.title = title;
        this.description = descr;
        //this.startDate = GregorianCalendar.getInstance();
        this.startDate = startDate;
        startDate.setTime(startDate.getTime());
        this.author = auth;
    }

    public MetaData(String title, String description, Long ruleCat, Date startDate, Date endDate, String author, boolean enabled) {
            this.title = title;
            this.description = description;
            this.ruleCat = ruleCat;
            this.startDate = startDate;
            startDate.setTime(startDate.getTime());
            this.endDate = endDate;
            endDate.setTime(endDate.getTime());

    }

    public Long getRuleCat() {
        return ruleCat;
    }

    public void setRuleCat(Long ruleCat) {
        this.ruleCat = ruleCat;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
//      this.startDate = GregorianCalendar.getInstance();
//      startDate.setTime(startDate.getTime());
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
//      this.endDate = GregorianCalendar.getInstance();
//      endDate.setTime(endDate.getTime());
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
}

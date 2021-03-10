/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.POEStatusEnum;

import javax.persistence.*;

@Entity
@Table(name="poe_lane")
public class POELane extends BaseEntityAudit{
    private static final long serialVersionUID = 1L;

    public POELane(){

    }
    @Column(name = "display_name")
    private String displayName;

    @Column(name= "ord")
    private int ord;

    @Enumerated(EnumType.STRING)
    @Column(name = "poe_status" )
    private POEStatusEnum poeStatusEnum;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOrd() { return ord; }

    public void setOrd(int ord) { this.ord = ord; }

    public POEStatusEnum getPoeStatusEnum() {
        return poeStatusEnum;
    }

    public void setPoeStatusEnum(POEStatusEnum poeStatusEnum) {
        this.poeStatusEnum = poeStatusEnum;
    }
}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.JoinColumn;


import java.util.Date;
import java.util.Objects;

import gov.gtas.model.lookup.DispositionStatus;

@Entity
@Table(name = "hits_disposition")
public class HitsDisposition extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public HitsDisposition() { }

    @Column(name = "disp_id", nullable = false)
    private String disp_id;

    @Column(nullable = false)
    private String number;

    @Column(name = "created_on")
    @Temporal(TemporalType.DATE)
    private Date created_on;

    @Column(name = "updated_on")
    @Temporal(TemporalType.DATE)
    private Date updated_on;

    @Column(name = "description")
    private String description;

  /*  @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Cases case;*/

    public String getDisp_id() {
        return disp_id;
    }

    public void setDisp_id(String disp_id) {
        this.disp_id = disp_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public Date getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(Date updated_on) {
        this.updated_on = updated_on;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

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

import gov.gtas.model.lookup.DispositionStatus;

@Entity
@Table(name = "cases")
public class Cases extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public Cases() { }

    @Column(name = "case_id", nullable = false)
    private String case_id;

    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }


}

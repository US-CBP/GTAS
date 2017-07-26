/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;
import java.util.HashSet;

import java.util.Set;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "hits_disposition_comments")
public class HitsDispositionComments extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public HitsDispositionComments() { }

    @Column(name = "hit_id", nullable = false)
    private String hit_id;

    @Column(name = "disp_id", nullable = false)
    private String disp_id;

    @Column(name = "description")
    private String description;


    @Column(name="hit_disp_id")
    private long hit_disp_id;

    public String getHit_id() {
        return hit_id;
    }

    public void setHit_id(String hit_id) {
        this.hit_id = hit_id;
    }

    public String getDisp_id() {
        return disp_id;
    }

    public void setDisp_id(String disp_id) {
        this.disp_id = disp_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHit_disp_id() {
        return hit_disp_id;
    }

    public void setHit_disp_id(long hit_disp_id) {
        this.hit_disp_id = hit_disp_id;
    }
}

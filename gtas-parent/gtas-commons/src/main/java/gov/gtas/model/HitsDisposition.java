/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;


import java.util.Set;

@Entity
@Table(name = "hits_disposition")
public class HitsDisposition extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public HitsDisposition() { }

    public HitsDisposition(Long hit){
        this.hit_id = hit;
    }

//    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name="hit_id")
    private long hit_id;

    @Column(name = "disp_id", nullable = false)
    private String disp_id;

    @Column(name = "description")
    private String description;

    @OneToMany
    @JoinColumn(name = "hit_disp_id", referencedColumnName = "id")
    private Set<HitsDispositionComments> dispComments;
    /*@ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Case case;*/

//    public String getDisp_id() {
//        return disp_id;
//    }
//
//    public void setDisp_id(String disp_id) {
//        this.disp_id = disp_id;
//    }

    public Set<HitsDispositionComments> getDispComments() {
        return dispComments;
    }

    public void setDispComments(Set<HitsDispositionComments> dispComments) {
        this.dispComments = dispComments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHit_id() {
        return hit_id;
    }

    public void setHit_id(long hit_id) {
        this.hit_id = hit_id;
    }

    public String getDisp_id() {
        return disp_id;
    }

    public void setDisp_id(String disp_id) {
        this.disp_id = disp_id;
    }
}

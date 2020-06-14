package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "intra_organization_group")
public class IntraOrganizationGroup extends BaseEntityAudit {

    @OneToMany(mappedBy = "intraOrganizationGroups")
    @JsonIgnore
    private Set<HitMaker> hitMakers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "intra_organization_group_join", joinColumns = @JoinColumn(name = "intra_group_id"), inverseJoinColumns = @JoinColumn(name = "intra_id"))
    private Set<IntraOrganization> associatedIntraOrganizations;

    @Column(name = "intra_label")
    private String intraOrganizationLabel;

    public IntraOrganizationGroup(){}

    public Set<IntraOrganization> getAssociatedIntraOrganizations() {
        return associatedIntraOrganizations;
    }

    public void setAssociatedIntraOrganizations(Set<IntraOrganization> associatedIntraOrganizations) {
        this.associatedIntraOrganizations = associatedIntraOrganizations;
    }

    public String getIntraOrganizationLabel() {
        return intraOrganizationLabel;
    }

    public void setIntraOrganizationLabel(String intraOrganizationLabel) {
        this.intraOrganizationLabel = intraOrganizationLabel;
    }

    public Set<HitMaker> getHitMakers() {
        return hitMakers;
    }

    public void setHitMakers(Set<HitMaker> hitMakers) {
        this.hitMakers = hitMakers;
    }
}

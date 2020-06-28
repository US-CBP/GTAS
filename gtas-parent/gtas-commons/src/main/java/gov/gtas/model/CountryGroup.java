package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "country_group")
public class CountryGroup extends BaseEntityAudit {

    @OneToMany(mappedBy = "countryGroup")
    @JsonIgnore
    private Set<HitMaker> hitMakers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "country_group_join", joinColumns = @JoinColumn(name = "country_group_id"), inverseJoinColumns = @JoinColumn(name = "country_id"))
    private Set<CountryAndOrganization> associatedCountries = new HashSet<>();

    @Column(name = "cg_label")
    private String countryGroupLabel;

    public CountryGroup(){}

    public Set<CountryAndOrganization> getAssociatedCountries() {
        return associatedCountries;
    }

    public void setAssociatedCountries(Set<CountryAndOrganization> associatedCountries) {
        this.associatedCountries = associatedCountries;
    }

    public String getCountryGroupLabel() {
        return countryGroupLabel;
    }

    public void setCountryGroupLabel(String countryGroupLabel) {
        this.countryGroupLabel = countryGroupLabel;
    }

    public Set<HitMaker> getHitMakers() {
        return hitMakers;
    }

    public void setHitMakers(Set<HitMaker> hitMakers) {
        this.hitMakers = hitMakers;
    }
}

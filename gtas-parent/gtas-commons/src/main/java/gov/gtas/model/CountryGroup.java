package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.model.lookup.Country;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "country_group")
public class CountryGroup extends BaseEntityAudit {

    @OneToMany(mappedBy = "hitCategory")
    @JsonIgnore
    private Set<HitMaker> hitMakers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "country_group_join", joinColumns = @JoinColumn(name = "country_group_id"), inverseJoinColumns = @JoinColumn(name = "country_id"))
    Set<Country> associatedCountries;

    @Column(name = "cg_label")
    String countryGroupLabel;

    public CountryGroup(){}

    public Set<Country> getAssociatedCountries() {
        return associatedCountries;
    }

    public void setAssociatedCountries(Set<Country> associatedCountries) {
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

package gov.gtas.model;

import gov.gtas.model.lookup.Country;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "country_and_organization")
public class CountryAndOrganization extends BaseEntityAudit {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cao_country_id", referencedColumnName = "id")
    private Country country;

    @Column(name = "cao_organization")
    private String organization;

    @ManyToMany(mappedBy = "associatedCountries", fetch = FetchType.LAZY)
    private Set<CountryGroup> countryGroupSet;

    public Set<CountryGroup> getCountryGroupSet() {
        return countryGroupSet;
    }

    public void setCountryGroupSet(Set<CountryGroup> countryGroupSet) {
        this.countryGroupSet = countryGroupSet;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}

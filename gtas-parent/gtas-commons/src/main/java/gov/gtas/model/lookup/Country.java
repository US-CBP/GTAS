/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import gov.gtas.model.BaseEntity;

import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import gov.gtas.model.CountryAndOrganization;
import gov.gtas.model.CountryGroup;
import org.springframework.cache.annotation.Cacheable;

@Cacheable
@Entity
@Table(name = "country", indexes = { @Index(columnList = "iso3", name = "country_iso3_index") })
public class Country extends BaseEntity {

	private Long originId;
	@Column(length = 2)
	private String iso2;
	@Column(length = 3)
	private String iso3;

	private String name;

	@Column(name = "iso_numeric", length = 3)
	private String isoNumeric;

	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	private Set<CountryAndOrganization> countryGroupSet;

	public Country() {
	}

	public Country(Long id, Long originId, String iso2, String iso3, String name, String isoNumeric) {
		this.id = id;
		this.originId = originId;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.name = name;
		this.isoNumeric = isoNumeric;
	}


	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long data) {
		this.originId = data;
	}

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String data) {
		this.iso2 = data;
	}

	public String getIso3() {
		return iso3;
	}

	public void setIso3(String data) {
		this.iso3 = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getIsoNumeric() {
		return isoNumeric;
	}

	public void setIsoNumeric(String data) {
		this.isoNumeric = data;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.iso2, this.iso3);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Country other = (Country) obj;
		return Objects.equals(this.iso2, other.iso2) && Objects.equals(this.iso3, other.iso3);
	}

	@Override
	public String toString() {
		return this.iso2;
	}

	public Set<CountryAndOrganization> getCountryGroupSet() {
		return countryGroupSet;
	}

	public void setCountryGroupSet(Set<CountryAndOrganization> countryGroupSet) {
		this.countryGroupSet = countryGroupSet;
	}
}

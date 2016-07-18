/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "reporting_party", uniqueConstraints = @UniqueConstraint(columnNames = {
		"party_name", "telephone" }))
public class ReportingParty extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public ReportingParty() {
	}

	@Column(name = "party_name")
	private String partyName;

	private String telephone;

	private String fax;

	@ManyToMany(mappedBy = "reportingParties", targetEntity = ApisMessage.class)
	private Set<ApisMessage> apisMessages = new HashSet<>();

	public Set<ApisMessage> getApisMessages() {
		return apisMessages;
	}

	public void setApisMessages(Set<ApisMessage> apisMessages) {
		this.apisMessages = apisMessages;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.partyName, this.telephone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ReportingParty))
			return false;
		final ReportingParty other = (ReportingParty) obj;
		return Objects.equals(this.partyName, other.partyName)
				&& Objects.equals(this.telephone, other.telephone);
	}
}

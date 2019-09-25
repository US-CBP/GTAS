/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

@Entity
@Table(name = "flight_direction")
public class FlightDirection extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "code", length = 1, nullable = false)
	private String code;

	@Column(name = "description")
	private String description;

	public String getcode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.code, this.description);
	}

	@Override
	public boolean equals(Object target) {

		if (this == target) {
			return true;
		}

		if (!(target instanceof FlightDirection)) {
			return false;
		}

		FlightDirection dataTarget = ((FlightDirection) target);

		return new EqualsBuilder().append(this.id, dataTarget.getId()).append(this.code, dataTarget.getcode())
				.append(this.description, dataTarget.getDescription()).isEquals();
	}

	@Override
	public String toString() {
		return "Direction [id=" + id + ", code=" + code + ", description=" + description + "]";
	}

}

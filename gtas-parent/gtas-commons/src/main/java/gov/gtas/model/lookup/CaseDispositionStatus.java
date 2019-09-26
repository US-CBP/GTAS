package gov.gtas.model.lookup;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.cache.annotation.Cacheable;

import gov.gtas.model.BaseEntity;

@Cacheable
@Entity
@Table(name = "case_disposition_status")

public class CaseDispositionStatus extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public CaseDispositionStatus() {
	}

	private String name;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CaseDispositionStatus other = (CaseDispositionStatus) obj;
		return Objects.equals(this.name, other.name);

	}

}

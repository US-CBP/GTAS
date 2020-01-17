package gov.gtas.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "airport_physical_location")
public class PhysicalLocation extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private Boolean active;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

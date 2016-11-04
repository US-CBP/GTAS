package gov.gtas.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "privilege")
@SuppressWarnings("serial")
public class Privilege extends BaseEntityAudit {
	public Privilege() {
		super();
	}

	public Privilege(final String name) {
		super();
		this.name = name;
	}

	private String name;

	@ManyToMany(mappedBy = "privileges")
	private Set<Role> roles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Role> getRoles() {
		return roles;
	}

}

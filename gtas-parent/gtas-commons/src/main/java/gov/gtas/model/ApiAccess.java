/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "api_access")
public class ApiAccess extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;
    public ApiAccess() { }
    public ApiAccess(String username,  String password, String email, String organization) {
    	this.username = username;
    	this.email = email;
    	this.password = password;
    	this.organization = organization;
    }
    @Column(unique=true)
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String organization;

	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}

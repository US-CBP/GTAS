/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;

import gov.gtas.constant.DomainModelConstants;

@Entity
@Table(name = "user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public User() {
    }

    public User(String userId, String password, String firstName, String lastName, int active, Set<Role> roles,
            Filter filter) {

        this.userId = userId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.roles = roles;
        this.filter=filter;
    }
    @OneToOne(mappedBy = "user", targetEntity = Filter.class,cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    private Filter filter;


    @Id
    @Column(name = "user_id", length = DomainModelConstants.GTAS_USERID_COLUMN_SIZE)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "active")
    private int active;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    @ManyToMany(targetEntity = Role.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)

    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id") , inverseJoinColumns = @JoinColumn(name = "role_id") )
    private Set<Role> roles = new HashSet<Role>();

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.password, this.firstName, this.lastName, this.active);
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (!(target instanceof User)) {
            return false;
        }

        User dataTarget = ((User) target);

        return new EqualsBuilder().append(this.userId, dataTarget.getUserId())
                .append(this.firstName, dataTarget.getFirstName()).append(this.lastName, dataTarget.getLastName())
                .append(this.password, dataTarget.getPassword()).append(this.active, dataTarget.getActive())
                .append(this.roles, dataTarget.getRoles())
                .append(this.filter, dataTarget.getFilter()).isEquals();
    }
    
    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
                + lastName + ", active=" + active + ", roles=" + roles + ", filter=" + filter+"]";
    }

}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.gtas.services.Filter.FilterData;

public class UserData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2939774474055002106L;
    private final String userId;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final int active;
    private Set<RoleData> roles = new HashSet<RoleData>();
    private FilterData filter;
    
    public UserData(@JsonProperty("userId") String userId, @JsonProperty("password") String password,
            @JsonProperty("firstName") String firstName, @JsonProperty("lasatName") String lastName,
            @JsonProperty("active") int active, @JsonProperty("roles") Set<RoleData> roles,
            @JsonProperty("filter") FilterData filterData) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.password = password;
        this.roles = roles;
        this.filter=filterData;
    }

    @JsonProperty("userId")
    public final String getUserId() {
        return userId;
    }

    @JsonProperty("password")
    public final String getPassword() {
        return password;
    }

    @JsonProperty("firstName")
    public final String getFirstName() {
        return firstName;
    }

    @JsonProperty("lastName")
    public final String getLastName() {
        return lastName;
    }

    @JsonProperty("active")
    public final int getActive() {
        return active;
    }

    public final Set<RoleData> getRoles() {
        return roles;
    }
    
    public final FilterData getFilter() {
        return this.filter;
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

        if (!(target instanceof UserData)) {
            return false;
        }

        UserData dataTarget = ((UserData) target);

        return new EqualsBuilder().append(this.userId, dataTarget.getUserId())
                .append(this.firstName, dataTarget.getFirstName()).append(this.lastName, dataTarget.getLastName())
                .append(this.password, dataTarget.getPassword()).append(this.active, dataTarget.getActive())
                .append(this.roles, dataTarget.getRoles())
                .append(this.filter, dataTarget.getFilter()).isEquals();
    }

    @Override
    public String toString() {
        return "UserData [userId=" + userId + ", password=" + password + ", firstName=" + firstName + ", lastName="
                + lastName + ", active=" + active + ", roles=" + roles +", Filter=" + filter +"]";
    }

}

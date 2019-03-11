/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1912655527084973025L;
    private final int roleId;
    private final String roleDescription;

    public RoleData(@JsonProperty("roleId") int roleId, @JsonProperty("roleDescription") String roleDescription) {
        this.roleId = roleId;
        this.roleDescription = roleDescription;
    }

    @JsonProperty("roleId")
    public final int getRoleId() {
        return roleId;
    }

    @JsonProperty("roleDescription")
    public final String getRoleDescription() {
        return roleDescription;
    }

    @Override
    public String toString() {
        return "RoleData [roleId=" + roleId + ", roleDescription=" + roleDescription + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.roleId, this.roleDescription);
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (!(target instanceof RoleData)) {
            return false;
        }

        RoleData dataTarget = ((RoleData) target);

        return new EqualsBuilder().append(this.roleId, dataTarget.getRoleId())
                .append(this.roleDescription, dataTarget.getRoleDescription()).isEquals();
    }

}

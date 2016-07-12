/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import gov.gtas.model.Role;

@Component
public class RoleServiceUtil {

    public Set<RoleData> getRoleDataSetFromEntityCollection(Iterable<Role> roleEntities) {

        Set<RoleData> roles = StreamSupport.stream(roleEntities.spliterator(), false)
                .map(new Function<Role, RoleData>() {
                    @Override
                    public RoleData apply(Role role) {
                        return new RoleData(role.getRoleId(), role.getRoleDescription());
                    }
                }).collect(Collectors.toSet());

        return roles;
    }

}

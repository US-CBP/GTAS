/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import gov.gtas.model.Role;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

/**
 * The Class RoleServiceUtil.
 */
@Component
public class RoleServiceUtil {

	/**
	 * Gets the role data set from entity collection.
	 *
	 * @param roleEntities
	 *            the role entities
	 * @return the role data set from entity collection
	 */
	public Set<RoleData> getRoleDataSetFromEntityCollection(Iterable<Role> roleEntities) {

		Stream<Role> aStream = StreamSupport.stream(roleEntities.spliterator(), false);

		Set<RoleData> rRoleDatas = (Set<RoleData>) aStream.map(new Function<Role, RoleData>() {
			@Override
			public RoleData apply(Role role) {
				return new RoleData(role.getRoleId(), role.getRoleDescription());
			}
		}).collect(Collectors.toSet());
		aStream.close();
		return rRoleDatas;

	}

	/**
	 * Map entity collection from role data set.
	 *
	 * @param roleDataSet
	 *            the role data set
	 * @return the sets the
	 */
	public Set<Role> mapEntityCollectionFromRoleDataSet(Set<RoleData> roleDataSet) {
		Stream<RoleData> aStream = StreamSupport.stream(roleDataSet.spliterator(), false);

		Set<Role> rRoles = (Set<Role>) aStream.map(new Function<RoleData, Role>() {
			@Override
			public Role apply(RoleData roleData) {
				return new Role(roleData.getRoleId(), roleData.getRoleDescription());
			}
		}).collect(Collectors.toSet());

		aStream.close();
		return rRoles;
	}

}

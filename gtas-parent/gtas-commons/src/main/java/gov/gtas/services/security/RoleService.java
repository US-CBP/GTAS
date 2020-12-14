/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import java.util.Set;

import gov.gtas.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RoleService {

	@PreAuthorize(PRIVILEGE_ADMIN)
	public Set<RoleData> findAll();

	public Set<Role> getValidRoles(Set<RoleData> roleDataSet);

}

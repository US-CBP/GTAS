/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

public interface RoleService {

	@PreAuthorize("hasAuthority('Admin')")
	public Set<RoleData> findAll();

}

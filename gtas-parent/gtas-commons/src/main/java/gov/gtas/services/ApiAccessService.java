/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import gov.gtas.model.ApiAccess;

public interface ApiAccessService {
	@PreAuthorize(PRIVILEGE_ADMIN)
    public List<ApiAccess> findAll();
	
	@PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccess create(ApiAccess externalUser);
	
	@PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccess update(ApiAccess externalUser);
	
	@PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccess delete(Long id);
	
	@PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccess findById(Long id);
}

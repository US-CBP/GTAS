/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.vo.ApiAccessVo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface ApiAccessService {
    @PreAuthorize(PRIVILEGE_ADMIN)
    public List<ApiAccessVo> findAll();

    @PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccessVo create(ApiAccessVo externalUser);

    @PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccessVo update(ApiAccessVo externalUser);

    @PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccessVo delete(Long id);

    @PreAuthorize(PRIVILEGE_ADMIN)
    public ApiAccessVo findById(Long id);
}

/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.services.dto.ApplicationStatisticsDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

public interface AdminService {

	@PreAuthorize(PRIVILEGE_ADMIN)
	ApplicationStatisticsDTO createApplicationStatisticsDto();
}

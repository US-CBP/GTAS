/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.constant.GtasSecurityConstants;
import gov.gtas.model.dto.ViewUpdateDTo;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PriorityVettingListService {

	@PreAuthorize(GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	PriorityVettingListDTO generateDtoFromRequest(PriorityVettingListRequest request, String userId);

	@PreAuthorize(GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	void update(ViewUpdateDTo viewUpdateDTo);

}

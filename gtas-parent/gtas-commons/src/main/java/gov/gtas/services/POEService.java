/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */
package gov.gtas.services;

import gov.gtas.constant.GtasSecurityConstants;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.LookoutLane;
import gov.gtas.services.dto.LookoutLaneDTO;
import gov.gtas.services.dto.LookoutStatusDTO;
import gov.gtas.services.dto.POETileServiceRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

public interface POEService {
    @PreAuthorize(GtasSecurityConstants.PRIVILEGE_ADMIN)
    Set<LookoutStatusDTO> getAllTiles(String userId, POETileServiceRequest req); //Evolve parameters to send

    @PreAuthorize(GtasSecurityConstants.PRIVILEGE_ADMIN)
    List<LookoutLane> getAllLanes();

    @PreAuthorize(GtasSecurityConstants.PRIVILEGE_ADMIN)
    JsonServiceResponse updateStatus(LookoutStatusDTO tileDTO);

    @PreAuthorize(GtasSecurityConstants.PRIVILEGE_ADMIN)
    JsonServiceResponse createNewLane(LookoutLaneDTO laneDTO);

    @PreAuthorize((GtasSecurityConstants.PRIVILEGE_ADMIN))
    JsonServiceResponse updateLane(LookoutLaneDTO laneDTO);

    @PreAuthorize((GtasSecurityConstants.PRIVILEGE_ADMIN))
    JsonServiceResponse deleteLane(String laneId);

    boolean lookoutIsMissedOrInvalidAndUpdate(HitViewStatus hvs);
}

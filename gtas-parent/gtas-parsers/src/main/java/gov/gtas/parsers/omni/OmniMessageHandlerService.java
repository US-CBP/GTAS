/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni;

import org.springframework.stereotype.Service;

@Service
public interface OmniMessageHandlerService {

    /**
     * Handle passenger risk assessment responses from Omni kaizen Server.
     * This will include the derog scores for a list of passengers
     */
    public void handlePassengersRiskAssessmentResponse(String jsonStream);
    /**
     * Handle retrieval of the time the passenger derog updates were performed
     * The last run time initialization will be needed if the system goes down and comes back up
     */
    public void handleRetrieveLastRunResponse(String jsonStream);
}

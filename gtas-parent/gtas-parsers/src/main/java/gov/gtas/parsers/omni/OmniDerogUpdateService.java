/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */

package gov.gtas.parsers.omni;
import gov.gtas.model.HitDetail;
import java.util.Set;

public interface OmniDerogUpdateService {
    // void updateOmniDerogPassengers(Set<HitDetail> firstTimeHits);
    void updateOmniDerogPassengers(Set<Long> flightIds);
}

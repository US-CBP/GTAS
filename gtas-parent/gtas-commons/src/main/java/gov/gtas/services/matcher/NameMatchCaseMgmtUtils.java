package gov.gtas.services.matcher;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.PassengerService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class NameMatchCaseMgmtUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(NameMatchCaseMgmtUtils.class);

    @Autowired
    private PassengerRepository paxRepoInstance;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CaseDispositionService caseDispositionService;

    /**
     *
     * @param ruleDescription
     * @param pax_id
     * @param wl_id
     * @param flightId
     * @param dispositionService
     */
    private boolean processPassengerFlight(String ruleDescription, Long pax_id, Long wl_id,
                                               Long flightId,
                                               CaseDispositionService dispositionService) {

        // Feed into Case Mgmt., Flight_ID, Pax_ID, Rule_ID to build a case
        Long _tempPaxId = pax_id;
        Passenger _tempPax = null;
        String watchlistItemFlag = "wl_item";
        String description = watchlistItemFlag + ruleDescription;

        try {
            _tempPax = dispositionService.findPaxByID(_tempPaxId);

            if (_tempPax != null) {
                String document = null;
                for (Document documentItem : _tempPax.getDocuments()) {
                    document = documentItem.getDocumentNumber();
                }

                dispositionService.registerCasesFromRuleService(flightId, pax_id, _tempPax.getFirstName()+_tempPax.getLastName(),
                        _tempPax.getPassengerType(), _tempPax.getCitizenshipCountry(), _tempPax.getDob(),
                        document, description, wl_id);
            }
        } catch (Exception ex) {
            logger.error("Could not initiate a case for Flight:" + flightId + "  Pax:" + _tempPaxId + "  WatchList:" + wl_id + " set", ex);
          }
        return true;
    }


    /**
     * Util method to help process Jaro Winkler(JW), Double Metaphone(DM) name matches and open cases
     * @param pax_id
     * @param wl_id
     * @param ruleDescriptionText
     * @return
     */

    public boolean nameMatchCaseProcessing(Long pax_id, Long wl_id, String ruleDescriptionText, Long flight_id,
                                           CaseDispositionService dispositionService){

        return processPassengerFlight(ruleDescriptionText, pax_id, wl_id, flight_id, dispositionService);

    }

    public PassengerRepository getPaxRepoInstance() {
        return paxRepoInstance;
    }

    public void setPaxRepoInstance(PassengerRepository paxRepoInstance) {
        this.paxRepoInstance = paxRepoInstance;
    }



}

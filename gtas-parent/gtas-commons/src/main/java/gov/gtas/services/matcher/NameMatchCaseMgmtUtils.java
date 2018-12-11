package gov.gtas.services.matcher;

import gov.gtas.model.Case;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.PassengerService;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class NameMatchCaseMgmtUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(NameMatchCaseMgmtUtils.class);

    /**
     *
     * @param ruleDescription
     * @param pax_id
     * @param wl_id
     * @param flightId
     * @param dispositionService
     */
    private boolean processPassengerFlight(String ruleDescription, Passenger passenger, Long wl_id,
                                               Flight flight, Case existingCase,Map<Long, RuleCat> ruleCatMap,
                                               CaseDispositionService dispositionService) {

        // Feed into Case Mgmt., Flight_ID, Pax_ID, Rule_ID to build a case
        Long _tempPaxId = passenger.getId(); //pax_id;
        Long flightId = flight.getId();
        Passenger _tempPax = null;
        String watchlistItemFlag = "wl_item";
        String description = watchlistItemFlag + ruleDescription;

        try {
            _tempPax = passenger; //dispositionService.findPaxByID(_tempPaxId);

            //if (_tempPax != null) {
                String document = null;
                for (Document documentItem : _tempPax.getDocuments()) {
                    document = documentItem.getDocumentNumber();
                }
                
                Map<Long, Passenger> passengerMap = new HashMap<Long, Passenger>();
                Map<Long, Flight> flightMap = new HashMap<Long, Flight>();
                Map<Long, Case> passengerFlightCaseMap = new HashMap<Long, Case>(); 
                passengerMap.put(passenger.getId(), passenger);
                flightMap.put(flightId, flight);
                if(existingCase != null)
                	passengerFlightCaseMap.put(passenger.getId(), existingCase);
                
                dispositionService.registerCasesFromRuleService(flightId, passenger.getId(), _tempPax.getFirstName()+" "+_tempPax.getLastName(),
                        _tempPax.getPassengerType(), _tempPax.getCitizenshipCountry(), _tempPax.getDob(),
                        document, description, wl_id,passengerFlightCaseMap, flightMap, passengerMap, ruleCatMap);
          //  }
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

    public boolean nameMatchCaseProcessing(Passenger passenger, Long wl_id, String ruleDescriptionText, Flight flight,Case existingCase, Map<Long, RuleCat> ruleCatMap,
                                           CaseDispositionService dispositionService){

        return processPassengerFlight(ruleDescriptionText, passenger, wl_id, flight, existingCase,ruleCatMap, dispositionService);

    }

}

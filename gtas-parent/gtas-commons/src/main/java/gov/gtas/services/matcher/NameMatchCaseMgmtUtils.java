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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NameMatchCaseMgmtUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(NameMatchCaseMgmtUtils.class);

    @Autowired
    CaseDispositionService caseDispositionService;

    /**
     *
     * @param ruleDescription
     * @param pax_id
     * @param wl_id
     * @param flightId
     * @param dispositionService
     */
    public boolean processPassengerFlight(String ruleDescription, Passenger passenger, Long wl_id,
                                               Flight flight, Case existingCase,Map<Long, RuleCat> ruleCatMap) {

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

           caseDispositionService
                   .registerAndSaveNewCaseFromFuzzyMatching(flightId,
                           passenger.getId(),
                           _tempPax.getPassengerDetails().getFirstName()+" "+_tempPax.getPassengerDetails().getLastName(),
                           _tempPax.getPassengerType(),
                           _tempPax.getPassengerDetails().getCitizenshipCountry(),
                           _tempPax.getPassengerDetails().getDob(),
                           document,
                           description,
                           wl_id,
                           passengerFlightCaseMap,
                           flightMap,
                           passengerMap,
                           ruleCatMap);
          //  }
        } catch (Exception ex) {
            logger.error("Could not initiate a case for Flight:" + flightId + "  Pax:" + _tempPaxId + "  WatchList:" + wl_id + " set", ex);
          }
        return true;
    }
}

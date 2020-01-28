/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.parsers.tamr.model.TamrDerogListEntry;
import gov.gtas.parsers.tamr.model.TamrDocument;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.services.FlightService;

public class TamrAdapterImpl implements TamrAdapter {

    @Autowired
    FlightService flightService;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    private Logger logger = LoggerFactory.getLogger(TamrAdapterImpl.class);

	@Override
	public List<TamrPassenger> convertPassengers(
	        Flight flight, Set<Passenger> passengers) {
	    return passengers.stream()
	            .map(passenger ->
	                    convertPassengerToTamrPassenger(flight, passenger))
	            .collect(Collectors.toList());
	}

	private TamrPassenger convertPassengerToTamrPassenger(Flight flight, Passenger passenger) {
		TamrPassenger tamrPassenger = new TamrPassenger();

		// Basic passenger information
		PassengerDetails passengerDetails = passenger.getPassengerDetails();
		tamrPassenger.setGtasId(passenger.getId().toString());
		tamrPassenger.setFirstName(passengerDetails.getFirstName());
		tamrPassenger.setMiddleName(passengerDetails.getMiddleName());
		tamrPassenger.setLastName(passengerDetails.getLastName());
		tamrPassenger.setGender(passengerDetails.getGender());
		tamrPassenger.setDob(passengerDetails.getDob());
		
		// Convert documents
		tamrPassenger.setDocuments(passenger.getDocuments().stream()
		        .map(document -> convertDocumentToTamrDocument(document))
		        .collect(Collectors.toList()));
		
		// Nationalities (only one in GTAS)
		tamrPassenger.setCitizenshipCountry(Collections
		        .singletonList(passengerDetails.getNationality()));

		return tamrPassenger;
	}

	private TamrDocument convertDocumentToTamrDocument(Document document) {
		TamrDocument tamrDocument = new TamrDocument();

		tamrDocument.setDocumentId(document.getDocumentNumber());
		tamrDocument.setDocumentType(document.getDocumentType());
		tamrDocument.setDocumentIssuingCountry(document.getIssuanceCountry());

		return tamrDocument;
	}
	
    @Override
    public List<TamrDerogListEntry> convertWatchlist(Collection<WatchlistItem> watchlistItems) {
        return watchlistItems.stream()
                .map(watchlistItem ->
                        convertWatchlistItemToTamrDerogListEntry(watchlistItem))
                .filter(derogListEntry -> derogListEntry != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a GTAS watchlist item to a derog entry that can be sent to
     * Tamr. This may return null for some watchlist items that should not
     * be sent to Tamr.
     */
    private TamrDerogListEntry convertWatchlistItemToTamrDerogListEntry(
            WatchlistItem watchlistItem) {        
        TamrDerogListEntry derogListEntry = new TamrDerogListEntry();

        WatchlistItemSpec itemSpec;
        try {
            itemSpec = mapper.readValue(watchlistItem.getItemData(),
                    WatchlistItemSpec.class);
        } catch (IOException e) {
            logger.warn("Error parsing watchlist item data {}. Ignoring...",
                    watchlistItem.getItemData());
            return null;
        }
        
        for (WatchlistTerm term: itemSpec.getTerms()) {
            if(term.getField().equals("firstName")) {
                derogListEntry.setFirstName(term.getValue());
            } else if (term.getField().equals("lastName")) {
                derogListEntry.setLastName(term.getValue());
            } else if (term.getField().equals("dob")) {
                try {
                    SimpleDateFormat parser =
                            new SimpleDateFormat("yyyy-MM-dd");
                    derogListEntry.setDob(parser.parse(term.getValue()));
                } catch (ParseException e) {
                    logger.warn("Unable to parse watchlist DOB {}. " +
                            "Ignoring...", term.getValue());
                }
            } else if (term.getField().equals("documentType") ||
                    term.getField().equals("documentNumber")) {
                TamrDocument document;
                if (derogListEntry.getDocuments() == null) {
                    document = new TamrDocument();
                    derogListEntry.setDocuments(
                            Collections.singletonList(document));
                } else {
                    document = derogListEntry.getDocuments().get(0);
                }
                if (term.getField().equals("documentType")) {
                    document.setDocumentType(term.getValue());
                } else if (term.getField().equals("documentNumber")) {
                    document.setDocumentId(term.getValue());
                }
            } else {
                logger.warn("Unknown watchlist field \"{}\". Ignoring...",
                        term.getField());
            }
        }
        
        if (derogListEntry.getLastName() == null) {
            // We only add to the derog list entries where the last name
            // at least is populated.
            return null;
        }
        
        // We set both gtasId and derogId to the watchlist item ID from
        // GTAS. Tamr can merge multiple derog list entries with the same
        // derogId, but we don't use that functionality yet.
        String itemIdStr = Long.toString(watchlistItem.getId());
        derogListEntry.setGtasId(itemIdStr);
        derogListEntry.setDerogId(itemIdStr);
        
        return derogListEntry;
        
    }
}

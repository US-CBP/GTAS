/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gov.gtas.bo.BasicRuleServiceRequest;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceRequestType;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;

/**
 * Watch list Request Builder constructs Rule Engine execution request for watch lists from
 * APIS and PNR messages. The constructed request contains all watch list related objects (e.g.,
 * passenger, document) associated with the APIS and PNR messages supplied.
 * Duplicates are also removed in the construction process.
 * @deprecated
 *
 */
public class WatchlistRequestBuilder {
    private final List<Object> requestObjectList;
    private final Set<Long> passengerIdSet;

    private RuleServiceRequestType requestType;

    public WatchlistRequestBuilder() {
        this.requestObjectList = new LinkedList<Object>();
        this.passengerIdSet = new HashSet<Long>();

        this.requestType = null;
    }

    public RuleServiceRequest build() {
        return new BasicRuleServiceRequest(requestObjectList, this.requestType);
    }

    /**
     * Adds an Apis Message.
     * 
     * @param apisMessage
     *            the message to add.
     */
    public void addApisMessage(ApisMessage apisMessage) {
        // add passengers and documents.
        if (apisMessage.getFlights() != null) {
            for (Flight flight : apisMessage.getFlights()) {
                    addPassengerObjects(flight.getPassengers());
            }
        }
        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.APIS_MESSAGE) {
            this.requestType = RuleServiceRequestType.APIS_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }

    /**
     * Adds a PNR message and its associated components.
     * 
     * @param pnr
     *            the pnr to add.
     */
    public void addPnr(Pnr pnr) {
        // add PNR objects
        if (pnr != null) {
            // add all the PNR related objects
            requestObjectList.add(pnr);
            addPassengerObjects(pnr.getPassengers());
        }
        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.PNR_MESSAGE) {
            this.requestType = RuleServiceRequestType.PNR_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }


    /**
     * Adds passenger and documents for PNR and APIS messages. In case of PNR a
     * link object is also created.
     * 
     * @param pnr
     *            the PNR object. If not null then a link object is also
     *            created.
     * @param passengers
     *            the collection of passengers.
     */
    private void addPassengerObjects(final Collection<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return;
        }
        for (Passenger passenger : passengers) {
            Long id = passenger.getId();
            if (!this.passengerIdSet.contains(id)) {
                requestObjectList.add(passenger);
                if (passenger.getDocuments() != null) {
                    for (Document doc : passenger.getDocuments()) {
                        this.requestObjectList.add(doc);
                    }
                }
                this.passengerIdSet.add(id);
            }
        }
    }
}

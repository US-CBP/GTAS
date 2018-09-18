/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import gov.gtas.bo.BasicRuleServiceRequest;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceRequestType;
import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrBookingLink;
import gov.gtas.bo.match.PnrCreditCardLink;
import gov.gtas.bo.match.PnrDwellTimeLink;
import gov.gtas.bo.match.PnrEmailLink;
import gov.gtas.bo.match.PnrFrequentFlyerLink;
import gov.gtas.bo.match.PnrPassengerLink;
import gov.gtas.bo.match.PnrPhoneLink;
import gov.gtas.bo.match.PnrTravelAgencyLink;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.DwellTime;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPax;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.Seat;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Rule Engine Request Builder constructs Rule Engine execution requests from
 * APIS and PNR messages. The constructed request contains all entities (e.g.,
 * passenger, flight) associated with the APIS and PNR messages supplied.
 * Duplicate entities are removed in the construction process.
 */
public class RuleEngineRequestBuilder {

    private static final Logger logger = LoggerFactory
            .getLogger(RuleEngineRequestBuilder.class);

    private final List<Object> requestObjectList;
    private final Set<Long> passengerIdSet;
    private final Set<PnrPassengerLink> passengerLinkSet;
    private final Set<Long> flightIdSet;
    private final Set<Long> addressIdSet;
    private final Set<Long> phoneIdSet;
    private final Set<Long> emailIdSet;
    private final Set<Long> creditCardIdSet;
    private final Set<Long> frequentFlyerIdSet;
    private final Set<Long> travelAgencyIdSet;
    private final Set<Long> dwellTimeIdSet;
    private final Set<Long> bookingDetailIdSet;
    
    private final Set<PassengerFlightTuple> passengerFlightSet;

    private RuleServiceRequestType requestType;

    public RuleEngineRequestBuilder() {
        this.requestObjectList = new LinkedList<Object>();
        this.addressIdSet = new HashSet<Long>();
        this.creditCardIdSet = new HashSet<Long>();
        this.bookingDetailIdSet = new HashSet<Long>();
        this.emailIdSet = new HashSet<Long>();
        this.flightIdSet = new HashSet<Long>();
        this.frequentFlyerIdSet = new HashSet<Long>();
        this.passengerIdSet = new HashSet<Long>();
        this.passengerLinkSet = new HashSet<PnrPassengerLink>();
        this.phoneIdSet = new HashSet<Long>();
        this.travelAgencyIdSet = new HashSet<Long>();
        this.passengerFlightSet = new HashSet<PassengerFlightTuple>();
        this.dwellTimeIdSet=new HashSet<Long>();
        this.requestType = null;
    }

    /**
     * Builds and returns the request object.
     * 
     * @return the request object.
     */
    public RuleServiceRequest build() {
        return new BasicRuleServiceRequest(requestObjectList, this.requestType);
    }

    /**
     * @return the passengerFlightSet
     */
    public Set<PassengerFlightTuple> getPassengerFlightSet() {
        return passengerFlightSet;
    }

    /**
     * Adds an Apis Message and its associated entities.
     * 
     * @param apisMessage
     *            the message to add.
     */
    public void addApisMessage(ApisMessage apisMessage) {
        // add flights, passengers and documents.
        // true for the second parameter means add passengers and documents
        addFlights(apisMessage.getFlights(), apisMessage.getPassengers(), true);
        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.APIS_MESSAGE) {
            this.requestType = RuleServiceRequestType.APIS_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }

    /**
     * Adds a PNR message and its associated entities.
     * 
     * @param pnr
     *            the pnr to add.
     */
    public void addPnr(Pnr pnr) {
        // add PNR objects
        if (pnr != null) {
            if (logger.isDebugEnabled()) {

            }
            // add all the PNR related objects
            requestObjectList.add(pnr);
            addFlights(pnr.getFlights(), pnr.getPassengers(), false);// false
                                                                        // means
                                                                        // do
                                                                        // not
                                                                        // add
            // passengers and documents.
            addAddressObjects(pnr, pnr.getAddresses());
            addPhoneObjects(pnr, pnr.getPhones());
            addEmailObjects(pnr, pnr.getEmails());
            addCreditCardObjects(pnr, pnr.getCreditCards());
            addBookingDetailObjects(pnr, pnr.getBookingDetails());
            addFrequentFlyerObjects(pnr, pnr.getFrequentFlyers());
            addPassengerObjects(pnr, pnr.getPassengers());
            addTravelAgencyObjects(pnr, pnr.getAgencies());
            addDwellTimeObjects(pnr, pnr.getDwellTimes());
            // add the passenger flight tuples
            if (pnr.getFlights() != null && pnr.getPassengers() != null) {
                for (Flight flight : pnr.getFlights()) {
                    for (Passenger passenger : pnr.getPassengers()) {
                        passengerFlightSet.add(new PassengerFlightTuple(
                                passenger, flight));
                    }
                }
            }
        }
        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.PNR_MESSAGE) {
            this.requestType = RuleServiceRequestType.PNR_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }

    /**
     * Adds flight objects to the builders list.
     * 
     * @param flights
     *            the flights to add
     * @param addAssociatedPassengers
     *            if true adds the associated passengers and documents.
     * @param addAssociatedPassengers
     */
    private void addFlights(Collection<Flight> flights,
            Collection<Passenger> passengers, boolean addAssociatedPassengers) {
        if (flights != null) {
            for (Flight flight : flights) {
                Long id = flight.getId();
                if (!this.flightIdSet.contains(id)) {
                    this.requestObjectList.add(flight);
                    this.flightIdSet.add(id);
                }
                if (addAssociatedPassengers) {
                    addPassengerObjects(null, passengers);
                    // addPassengerObjects(null, flight.getPassengers());
                    // add the passenger flight tuples
                    if (passengers != null) {
                        for (Passenger passenger : passengers) {
                            passengerFlightSet.add(new PassengerFlightTuple(
                                    passenger, flight));
                        }
                    }
                }
            }
        }

    }

    private void addAddressObjects(final Pnr pnr,
            final Collection<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return;
        }
        for (Address addr : addresses) {
            Long id = addr.getId();
            if (!this.addressIdSet.contains(id)) {
                requestObjectList.add(addr);
                requestObjectList.add(new PnrAddressLink(pnr.getId(), addr
                        .getId()));
                this.addressIdSet.add(id);
            }
        }
    }

    private void addPhoneObjects(final Pnr pnr, final Collection<Phone> phones) {
        if (phones == null || phones.isEmpty()) {
            logger.info("No phones info.");
            return;
        }
        for (Phone phone : phones) {
            Long id = phone.getId();
            if (!this.phoneIdSet.contains(id)) {
                requestObjectList.add(phone);
                requestObjectList.add(new PnrPhoneLink(pnr.getId(), phone
                        .getId()));
                this.phoneIdSet.add(id);
            }
        }
    }

    private void addEmailObjects(final Pnr pnr, final Collection<Email> emails) {
        if (emails == null || emails.isEmpty()) {
            return;
        }
        for (Email email : emails) {
            long id = email.getId();
            if (!this.emailIdSet.contains(id)) {
                requestObjectList.add(email);
                requestObjectList.add(new PnrEmailLink(pnr.getId(), email
                        .getId()));
                this.emailIdSet.add(id);
            }
        }
    }

    private void addFrequentFlyerObjects(final Pnr pnr,
            final Collection<FrequentFlyer> frequentFlyers) {
        if (frequentFlyers == null || frequentFlyers.isEmpty()) {
            return;
        }
        for (FrequentFlyer ff : frequentFlyers) {
            Long id = ff.getId();
            if (!this.frequentFlyerIdSet.contains(id)) {
                requestObjectList.add(ff);
                requestObjectList.add(new PnrFrequentFlyerLink(pnr.getId(), ff
                        .getId()));
                this.frequentFlyerIdSet.add(id);
            }
        }
    }

    private void addCreditCardObjects(final Pnr pnr,
            final Collection<CreditCard> creditCards) {
        if (creditCards == null || creditCards.isEmpty()) {
            return;
        }
        for (CreditCard cc : creditCards) {
            Long id = cc.getId();
            if (!this.creditCardIdSet.contains(id)) {
                requestObjectList.add(cc);
                requestObjectList.add(new PnrCreditCardLink(pnr.getId(), cc
                        .getId()));
                this.creditCardIdSet.add(id);
            }
        }
    }
    private void addBookingDetailObjects(final Pnr pnr,
            final Collection<BookingDetail> bookingDetails) {
        if (bookingDetails == null || bookingDetails.isEmpty()) {
            return;
        }
        for (BookingDetail bl : bookingDetails) {
            Long id = bl.getId();
            if (!this.bookingDetailIdSet.contains(id)) {
                requestObjectList.add(bl);
                requestObjectList.add(new PnrBookingLink(pnr.getId(), bl
                        .getId()));
                this.bookingDetailIdSet.add(id);
            }
        }
    }
    private void addDwellTimeObjects(final Pnr pnr,
            final Collection<DwellTime> dwellTimes) {
        if (CollectionUtils.isEmpty(dwellTimes)) {
            return;
        }
        for (DwellTime a : dwellTimes) {
            Long id = a.getId();
            if (!this.dwellTimeIdSet.contains(id)) {
                requestObjectList.add(a);
                requestObjectList.add(new PnrDwellTimeLink(pnr.getId(), a
                        .getId()));
                this.dwellTimeIdSet.add(id);
            }
        }
    }
    
    private void addTravelAgencyObjects(final Pnr pnr,
            final Collection<Agency> agencies) {
        if (CollectionUtils.isEmpty(agencies)) {
            return;
        }
        for (Agency a : agencies) {
            Long id = a.getId();
            if (!this.travelAgencyIdSet.contains(id)) {
                requestObjectList.add(a);
                requestObjectList.add(new PnrTravelAgencyLink(pnr.getId(), a
                        .getId()));
                this.travelAgencyIdSet.add(id);
            }
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
    private void addPassengerObjects(final Pnr pnr,
            final Collection<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return;
        }
        for (Passenger passenger : passengers) {
            Long id = passenger.getId();
            if (!this.passengerIdSet.contains(id)) {
                addPassengerAndDependdencies(passenger);
                this.passengerIdSet.add(id);
            }
            if (pnr != null) {
                addPnrPassengerLink(pnr, passenger);
            }
        }
    }

    private void addPassengerAndDependdencies(Passenger passenger) {
        requestObjectList.add(passenger);
        if (passenger.getDocuments() != null) {
            for (Document doc : passenger.getDocuments()) {
                this.requestObjectList.add(doc);
            }
        }
        if (passenger.getSeatAssignments() != null) {
            for (Seat seat : passenger.getSeatAssignments()) {
                this.requestObjectList.add(seat);
            }
        }
        if(passenger.getBags() != null){
        	for (Bag bag : passenger.getBags()) {
                this.requestObjectList.add(bag);
            }
        }
        if(passenger.getFlightPaxList() != null){
        	for (FlightPax flightPax : passenger.getFlightPaxList()) {
                this.requestObjectList.add(flightPax);
            }
        }
    }

    private void addPnrPassengerLink(final Pnr pnr, final Passenger passenger) {
        PnrPassengerLink link = new PnrPassengerLink(pnr.getId(),
                passenger.getId());
        if (!this.passengerLinkSet.contains(link)) {
            requestObjectList.add(link);
            this.passengerLinkSet.add(link);
        }

    }
}

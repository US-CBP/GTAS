/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import gov.gtas.bo.BasicRuleServiceRequest;
import gov.gtas.bo.FlightPassengerLink;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceRequestType;
import gov.gtas.bo.match.*;
import gov.gtas.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gov.gtas.repository.*;
import gov.gtas.services.PnrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Rule Engine Request Builder constructs Rule Engine execution requests from
 * APIS and PNR messages. The constructed request contains all entities (e.g.,
 * passenger, flight) associated with the APIS and PNR messages supplied.
 * Duplicate entities are removed in the construction process.
 */

@Component
@Scope("prototype")
public class RuleEngineRequestBuilder {
	private static final Logger logger = LoggerFactory.getLogger(RuleEngineRequestBuilder.class);
	private List<Object> requestObjectList;
	private Set<PnrPassengerLink> passengerLinkSet;
	private Set<Long> flightIdSet;
	private Set<Long> addressIdSet;
	private Set<Long> phoneIdSet;
	private Set<Long> emailIdSet;
	private Set<Long> creditCardIdSet;
	private Set<Long> frequentFlyerIdSet;
	private Set<Long> travelAgencyIdSet;
	private Set<Long> dwellTimeIdSet;
	private Set<Long> paymentFormIdSet;
	private Set<Long> bookingDetailIdSet;
	private Set<PnrAddressLink> addressLinks;
	private Set<PnrPhoneLink> phoneLinks;
	private Set<PnrEmailLink> emailLinks;
	private Set<PnrCreditCardLink> creditCardLinks;
	private Set<PnrFrequentFlyerLink> frequentFlyerLinks;
	private Set<PnrTravelAgencyLink> travelAgencyLinks;
	private Set<PnrDwellTimeLink> dwellTimeLinks;
	private Set<PnrFormOfPaymentLink> pnrFormOfPaymentLinks;
	private Set<PnrBookingLink> pnrBookingLinks;
	private Set<PassengerFlightTuple> passengerFlightSet;
	private Set<FlightPassengerLink> flightPassengerLinks;

	private RuleServiceRequestType requestType;

	private final PnrRepository pnrRepository;

	private final ApisMessageRepository apisMessageRepository;

	private final PassengerRepository passengerRepository;

	private final SeatRepository seatRepository;

	private final BagRepository bagRepository;

	private final FlightPaxRepository flightPaxRepository;

	private final DocumentRepository documentRepository;

	@Value("${ruleRunner.makeEmptyApisBagsOnNullBag}")
	private Boolean makeEmptyApisBagsOnNullBag;

	@Value("${ruleRunner.makeEmptyPnrBagsOnNullBag}")
	private Boolean makeEmptyPnrBagsOnNullBag;

	private final PnrService pnrService;

	@Autowired
	public RuleEngineRequestBuilder(PnrRepository pnrRepository, PnrService pnrService, SeatRepository seatRepository,
									BagRepository bagRepository, FlightPaxRepository flightPaxRepository, DocumentRepository documentRepository,
									ApisMessageRepository apisMessageRepository, PassengerRepository passengerRepository) {
		this.passengerRepository = passengerRepository;
		this.pnrService = pnrService;
		this.requestObjectList = new ArrayList<>(300000);
		this.addressIdSet = new HashSet<>();
		this.creditCardIdSet = new HashSet<>();
		this.bookingDetailIdSet = new HashSet<>();
		this.emailIdSet = new HashSet<>();
		this.flightIdSet = new HashSet<>();
		this.frequentFlyerIdSet = new HashSet<>();
		this.passengerLinkSet = new HashSet<>();
		this.phoneIdSet = new HashSet<>();
		this.travelAgencyIdSet = new HashSet<>();
		this.passengerFlightSet = new HashSet<>();
		this.flightPassengerLinks = new HashSet<>();
		this.dwellTimeIdSet = new HashSet<>();
		this.paymentFormIdSet = new HashSet<>();
		this.addressLinks = new HashSet<>();
		this.phoneLinks = new HashSet<>();
		this.emailLinks = new HashSet<>();
		this.creditCardLinks = new HashSet<>();
		this.frequentFlyerLinks = new HashSet<>();
		this.travelAgencyLinks = new HashSet<>();
		this.dwellTimeLinks = new HashSet<>();
		this.pnrFormOfPaymentLinks = new HashSet<>();
		this.pnrBookingLinks = new HashSet<>();
		this.requestType = null;
		this.pnrRepository = pnrRepository;
		this.seatRepository = seatRepository;
		this.bagRepository = bagRepository;
		this.flightPaxRepository = flightPaxRepository;
		this.documentRepository = documentRepository;
		this.apisMessageRepository = apisMessageRepository;
	}

	/**
	 * Builds and returns the request object.
	 *
	 * @return the request object.
	 */
	public RuleServiceRequest build() {
		addToRequestObjectSet(flightPassengerLinks);
		BasicRuleServiceRequest basicRuleServiceRequest = new BasicRuleServiceRequest(requestObjectList,
				this.requestType);
		this.requestObjectList = new ArrayList<>(300000);
		this.addressIdSet = new HashSet<>();
		this.creditCardIdSet = new HashSet<>();
		this.bookingDetailIdSet = new HashSet<>();
		this.emailIdSet = new HashSet<>();
		this.flightIdSet = new HashSet<>();
		this.frequentFlyerIdSet = new HashSet<>();
		this.passengerLinkSet = new HashSet<>();
		this.phoneIdSet = new HashSet<>();
		this.travelAgencyIdSet = new HashSet<>();
		this.passengerFlightSet = new HashSet<>();
		this.flightPassengerLinks = new HashSet<>();
		this.dwellTimeIdSet = new HashSet<>();
		this.addressLinks = new HashSet<>();
		this.phoneLinks = new HashSet<>();
		this.emailLinks = new HashSet<>();
		this.creditCardLinks = new HashSet<>();
		this.frequentFlyerLinks = new HashSet<>();
		this.travelAgencyLinks = new HashSet<>();
		this.dwellTimeLinks = new HashSet<>();
		this.pnrBookingLinks = new HashSet<>();
		this.requestType = null;
		return basicRuleServiceRequest;
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
	public void addApisMessage(List<ApisMessage> apisMessage) {
		logger.debug("Entering APIS messages");
		Set<Long> apisIds = apisMessage.stream().map(ApisMessage::getId).collect(Collectors.toSet());
		Set<Long> paxId = apisMessageRepository.getPassengerWithFlightInfo(apisIds);
		logger.debug("FlightGot");
		if (paxId.isEmpty()) {
			return;
		}
		Set<Passenger> passengerSet = passengerRepository.getPassengersWithFlightDetails(paxId);
		addFlights(passengerSet);
		Set<Flight> flightSet = addFlights(passengerSet);
		addFlights(flightSet);
		logger.debug("APIS bags");
		Set<Long> paxIds = passengerSet.stream().map(Passenger::getId).collect(Collectors.toSet());
		Set<Long> flightIds = passengerSet.stream().map(p -> p.getFlight().getId()).collect(Collectors.toSet());
		Map<Long, Passenger> passengerMap = passengerSet.stream()
				.collect(Collectors.toMap(Passenger::getId, Function.identity()));
		Set<Bag> bags = bagRepository.getAllByPaxId(paxIds, flightIds);
		if (makeEmptyApisBagsOnNullBag) {
			Set<Long> passengerWhoHaveBagsId = bags.stream().map(Bag::getPassengerId).collect(Collectors.toSet());
			Set<Long> passengersWithoutBags = new HashSet<>(paxIds);
			passengersWithoutBags.removeAll(passengerWhoHaveBagsId);
			for (Long baglessPaxId : passengersWithoutBags) {
				bags.add(makeEmptyBag(passengerMap.get(baglessPaxId), "APIS", true));
			}
		}

		addToRequestObjectSet(bags);
		addPassengerInfo(passengerSet);
		logger.debug("APIS done loading.");
		if (this.requestType == null || this.requestType == RuleServiceRequestType.APIS_MESSAGE) {
			this.requestType = RuleServiceRequestType.APIS_MESSAGE;
		} else {
			this.requestType = RuleServiceRequestType.ANY_MESSAGE;
		}
	}

	private Set<Flight> addFlights(Set<Passenger> passengerSet) {
		Set<Flight> flightSet = new HashSet<>();
		for (Passenger p : passengerSet) {
			passengerFlightSet.add(new PassengerFlightTuple(p, p.getFlight()));
			flightPassengerLinks.add(new FlightPassengerLink(p.getFlight().getId(), p.getId()));
			flightSet.add(p.getFlight());
		}
		return flightSet;
	}

	/**
	 * Adds a PNR message and its associated entities.
	 * <p>
	 * the pnr to add.
	 */
	public void addPnr(List<Pnr> pnrList) {
		// add PNR objects
		logger.debug("Calling DB");
		Set<Long> pnrIds = pnrList.stream().map(Pnr::getId).collect(Collectors.toSet());
		Map<Long, Set<Address>> addressObjects = pnrService.createAddressMap(pnrIds);
		Map<Long, Set<Phone>> phoneObjects = pnrService.createPhoneMap(pnrIds);
		Map<Long, Set<Email>> emailsObjects = pnrService.createEmailMap(pnrIds);
		Map<Long, Set<CreditCard>> creditCardObjects = pnrService.createCreditCardMap(pnrIds);
		Map<Long, Set<BookingDetail>> bookingDetailObjects = pnrService.createBookingDetailMap(pnrIds);
		Map<Long, Set<FrequentFlyer>> frequentFlyerObjects = pnrService.createFrequentFlyersMap(pnrIds);
		Map<Long, Set<Agency>> travelAgency = pnrService.createTravelAgencyMap(pnrIds);
		Map<Long, Set<DwellTime>> dwellMap = pnrService.createDwellTime(pnrIds);
		Map<Long, Set<Passenger>> paxMap = pnrService.createPaxMap(pnrIds);
		Map<Long, Set<PaymentForm>> pnrPaymentForm = pnrService.createPaymentFormMap(pnrIds);

		Set<Passenger> passengersFromPnr = pnrRepository.getPassengersWithFlight(pnrIds);
		if (passengersFromPnr.isEmpty()) {
			return;
		}
		logger.debug("pnr bags");
		Set<Long> flightIds = passengersFromPnr.stream().map(p -> p.getFlight().getId()).collect(Collectors.toSet());
		Set<Long> paxIds = passengersFromPnr.stream().map(Passenger::getId).collect(Collectors.toSet());
		Set<Bag> bags = bagRepository.getAllByPaxId(paxIds, flightIds);
		if (makeEmptyPnrBagsOnNullBag) {
			Map<Long, Passenger> passengerMap = passengersFromPnr.stream()
					.collect(Collectors.toMap(Passenger::getId, Function.identity()));
			// generating same thing as p.getBags().
			// The database joins can be expensive
			// and performance is critical here
			// So map bags are mapped in memory with data already pulled back instead.
			Map<Long, Set<Bag>> passengerBags = passengerBagMap(bags);

			// Create empty bags by booking detail (which are mapped to pnr).
			// Search the booking details for bags, if any passengers do not have a
			// record of a bag but are on a booking detail create an empty bag to run rules
			// against (measurement = 0, weight =0).
			for (long pnrId : pnrIds) {
				Set<BookingDetail> bookingDetailSet = bookingDetailObjects.get(pnrId);
				Set<Passenger> passengerSet = paxMap.get(pnrId);
				if (passengerSet != null) {
					Set<Long> paxIdSetFromPassengerSet = passengerSet.stream().map(Passenger::getId)
							.collect(Collectors.toSet());

					if (bookingDetailSet != null) {
						for (BookingDetail bd : bookingDetailSet) {
							Set<Bag> bagBdSet = bd.getBags();
							// For all bags that *are* with a bd already make a new bag representing a non
							// prime flight bag.
							// Bags that are prime flights are not distinguished from bd bags.
							// TODO: Update data model to allow for multiple bags to be created on a
							// per-flight baises.
							Set<Bag> primeFlightBdBagsForBDFlights = bagBdSet.stream().filter(Bag::isPrimeFlight)
									.map(this::createBookingDetailBag).collect(Collectors.toSet());
							bags.addAll(primeFlightBdBagsForBDFlights);

							// Populate a bag with 0 on bag measurement on any booking details with no bag
							// info. Only perform this check on booking details that are BEFORE the prime
							// flight.
							if (bd.getEtd() != null
									&& bd.getEtd().before(bd.getFlight().getMutableFlightDetails().getEtd())) {
								Set<Long> paxIdsWithBags = bagBdSet.stream().map(Bag::getPassengerId)
										.collect(Collectors.toSet());
								Set<Long> paxWithNoBags = new HashSet<>(paxIdSetFromPassengerSet);
								paxWithNoBags.removeAll(paxIdsWithBags);
								for (Long paxId : paxWithNoBags) {
									bags.add(makeEmptyBag(passengerMap.get(paxId), "PNR", false));
								}
							}
						}
					}
					// Populate a bag with 0 on bag measurement on any prime flight details with no
					// bag info.
					for (long paxId : paxIds) {
						Set<Bag> pBagsSet = passengerBags.get(paxId);
						if (pBagsSet == null) {
							bags.add(makeEmptyBag(passengerMap.get(paxId), "PNR", true));
						} else {
							boolean primeFlightBagPresent = pBagsSet.stream().anyMatch(Bag::isPrimeFlight);
							if (!primeFlightBagPresent) {
								bags.add(makeEmptyBag(passengerMap.get(paxId), "PNR", true));
							}
						}
					}
				}
			}
		}
		addToRequestObjectSet(bags);
		Set<Flight> flightSet = addFlights(passengersFromPnr);
		addFlights(flightSet);
		addPassengerInfo(passengersFromPnr);

		requestObjectList.addAll(pnrList);
		for (Long pnrId : pnrIds) {
			logger.debug("in the pnr ");
			addAddressObjects(pnrId, addressObjects.get(pnrId));
			addFrequentFlyerObjects(pnrId, frequentFlyerObjects.get(pnrId));
			addBookingDetailObjects(pnrId, bookingDetailObjects.get(pnrId));
			addCreditCardObjects(pnrId, creditCardObjects.get(pnrId));
			addEmailObjects(pnrId, emailsObjects.get(pnrId));
			addPhoneObjects(pnrId, phoneObjects.get(pnrId));
			addTravelAgencyObjects(pnrId, travelAgency.get(pnrId));
			addDwellTimeObjects(pnrId, dwellMap.get(pnrId));
			addPassengerObjects(pnrId, paxMap.get(pnrId));
			addPaymentFormObjects(pnrId, pnrPaymentForm.get(pnrId));
		}
		logger.debug("all done we go");

		if (this.requestType == null || this.requestType == RuleServiceRequestType.PNR_MESSAGE) {
			this.requestType = RuleServiceRequestType.PNR_MESSAGE;
		} else {
			this.requestType = RuleServiceRequestType.ANY_MESSAGE;
		}
	}

	private Bag createBookingDetailBag(Bag bdBagThatIsAlsoPrimeFlight) {
		Bag bookingDetailBag = Bag.fromBag(bdBagThatIsAlsoPrimeFlight);
		bookingDetailBag.setBagId(UUID.randomUUID().toString() + bdBagThatIsAlsoPrimeFlight.getBagId());
		bookingDetailBag.setPrimeFlight(false);
		return bookingDetailBag;
	}
	// generating same thing as p.getBags().
	// The database joins can be expensive/time consuming
	// and performance is critical here
	// So map bags are mapped in memory instead.
	protected Map<Long, Set<Bag>> passengerBagMap(Set<Bag> bags) {
		Map<Long, Set<Bag>> passengerBags = new HashMap<>();

		for (Bag bag : bags) {
			long passengerId = bag.getPassengerId();
			if (passengerBags.get(passengerId) != null) {
				passengerBags.get(passengerId).add(bag);
			} else {
				Set<Bag> bags1 = new HashSet<>();
				bags1.add(bag);
				passengerBags.put(passengerId, bags1);
			}
		}
		return passengerBags;
	}

	protected Bag makeEmptyBag(Passenger passenger, String source, boolean primeFlight) {
		Bag bag = new Bag();
		bag.setPassengerId(passenger.getId());
		bag.setPassenger(passenger);
		bag.setPrimeFlight(primeFlight);
		bag.setFlight(passenger.getFlight());
		bag.setFlightId(passenger.getFlight().getId());
		bag.setData_source(source);
		bag.setBagId(UUID.randomUUID().toString());
		BagMeasurements bMeasurement = new BagMeasurements();
		bMeasurement.setBagCount(0);
		bMeasurement.setWeight(0D);
		bMeasurement.setRawWeight(0D);
		bag.setBagMeasurements(bMeasurement);
		return bag;
	}

	private void addPassengerInfo(Set<Passenger> passengerSet) {
		addToRequestObjectSet(passengerSet);
		addPassengesInformationFacts(passengerSet);
	}

	private void addPassengesInformationFacts(Set<Passenger> passengerSet) {
		logger.debug("pax info");
		logger.debug("paxMap");
		Set<PassengerDetails> passengerDetails = passengerSet.stream().map(Passenger::getPassengerDetails)
				.collect(Collectors.toSet());
		logger.debug("trip detail map");
		Set<PassengerTripDetails> passengerTripDetails = passengerSet.stream().map(Passenger::getPassengerTripDetails)
				.collect(Collectors.toSet());

		Set<Long> paxIds = passengerSet.stream().map(Passenger::getId).collect(Collectors.toSet());
		logger.debug("seatMap");
		Set<Seat> paxSeats = seatRepository.getByPaxId(paxIds);
		logger.debug("flightpax");
		Set<FlightPax> flightPaxSet = flightPaxRepository.findFlightFromPassIdList(paxIds);
		logger.debug("document");
		Set<Document> documentSet = documentRepository.getAllByPaxId(paxIds);
		addToRequestObjectSet(passengerTripDetails);
		addToRequestObjectSet(passengerDetails);
		addToRequestObjectSet(paxSeats);
		addToRequestObjectSet(flightPaxSet);
		addToRequestObjectSet(documentSet);
		logger.debug("pax info done");
	}

	private <T extends Collection> void addToRequestObjectSet(T set) {
		if (!set.isEmpty()) {
			requestObjectList.addAll(set);
		}
	}

	/**
	 * Adds flight objects to the builders list.
	 *
	 * @param flights
	 *            the flights to add
	 */
	private void addFlights(Collection<Flight> flights) {
		if (flights != null) {
			for (Flight flight : flights) {
				Long id = flight.getId();
				if (!this.flightIdSet.contains(id)) {
					this.requestObjectList.add(flight);
					this.flightIdSet.add(id);
				}
			}
		}

	}

	private void addAddressObjects(final Long pnrId, final Collection<Address> addresses) {
		if (addresses == null || addresses.isEmpty()) {
			return;
		}
		for (Address addr : addresses) {
			Long id = addr.getId();
			if (!this.addressIdSet.contains(id)) {
				requestObjectList.add(addr);
				this.addressIdSet.add(id);
			}
			PnrAddressLink pnrAddressLink = new PnrAddressLink(pnrId, addr.getId());
			if (!this.addressLinks.contains(pnrAddressLink)) {
				requestObjectList.add(pnrAddressLink);
				this.addressLinks.add(pnrAddressLink);
			}
		}
	}

	private void addPhoneObjects(final Long pnrId, final Collection<Phone> phones) {
		if (phones == null || phones.isEmpty()) {
			logger.debug("No phones info.");
			return;
		}
		for (Phone phone : phones) {
			Long id = phone.getId();
			if (!this.phoneIdSet.contains(id)) {
				requestObjectList.add(phone);
				this.phoneIdSet.add(id);
			}

			PnrPhoneLink pnrPhoneLink = new PnrPhoneLink(pnrId, phone.getId());
			if (!phoneLinks.contains(pnrPhoneLink)) {
				requestObjectList.add(pnrPhoneLink);
				phoneLinks.add(pnrPhoneLink);
			}
		}
	}

	private void addEmailObjects(final Long pnrId, final Collection<Email> emails) {
		if (emails == null || emails.isEmpty()) {
			return;
		}
		for (Email email : emails) {
			long id = email.getId();
			if (!this.emailIdSet.contains(id)) {
				requestObjectList.add(email);
				this.emailIdSet.add(id);
			}
			PnrEmailLink pnrEmailLink = new PnrEmailLink(pnrId, email.getId());
			if (!emailLinks.contains(pnrEmailLink)) {
				requestObjectList.add(pnrEmailLink);
				emailLinks.add(pnrEmailLink);
			}
		}
	}

	private void addFrequentFlyerObjects(final Long pnrId, final Collection<FrequentFlyer> frequentFlyers) {
		if (frequentFlyers == null || frequentFlyers.isEmpty()) {
			return;
		}
		for (FrequentFlyer ff : frequentFlyers) {
			Long id = ff.getId();
			if (!this.frequentFlyerIdSet.contains(id)) {
				this.requestObjectList.add(ff);
				this.frequentFlyerIdSet.add(id);
			}
			PnrFrequentFlyerLink pnrFrequentFlyerLink = new PnrFrequentFlyerLink(pnrId, ff.getId());
			if (!this.frequentFlyerLinks.contains(pnrFrequentFlyerLink)) {
				this.requestObjectList.add(pnrFrequentFlyerLink);
				this.frequentFlyerLinks.add(pnrFrequentFlyerLink);
			}
		}
	}

	private void addCreditCardObjects(final Long pnrId, final Collection<CreditCard> creditCards) {
		if (creditCards == null || creditCards.isEmpty()) {
			return;
		}
		for (CreditCard cc : creditCards) {
			Long id = cc.getId();
			if (!this.creditCardIdSet.contains(id)) {
				this.requestObjectList.add(cc);
				this.creditCardIdSet.add(id);
			}
			PnrCreditCardLink pnrCreditCardLink = new PnrCreditCardLink(pnrId, cc.getId());
			if (!this.creditCardLinks.contains(pnrCreditCardLink)) {
				this.requestObjectList.add(pnrCreditCardLink);
				this.creditCardLinks.add(pnrCreditCardLink);
			}
		}
	}

	private void addBookingDetailObjects(final Long pnrId, final Collection<BookingDetail> bookingDetails) {
		if (bookingDetails == null || bookingDetails.isEmpty()) {
			return;
		}
		for (BookingDetail bl : bookingDetails) {
			Long id = bl.getId();
			if (!this.bookingDetailIdSet.contains(id)) {
				this.requestObjectList.add(bl);
				this.bookingDetailIdSet.add(id);
			}
			PnrBookingLink pnrBookingLink = new PnrBookingLink(pnrId, bl.getId());
			if (!this.pnrBookingLinks.contains(pnrBookingLink)) {
				this.requestObjectList.add(pnrBookingLink);
				this.pnrBookingLinks.add(pnrBookingLink);
			}
		}
	}

	private void addDwellTimeObjects(final Long pnrId, final Collection<DwellTime> dwellTimes) {
		if (CollectionUtils.isEmpty(dwellTimes)) {
			return;
		}
		for (DwellTime a : dwellTimes) {
			Long id = a.getId();
			if (!this.dwellTimeIdSet.contains(id)) {
				this.requestObjectList.add(a);
				this.dwellTimeIdSet.add(id);
			}

			PnrDwellTimeLink pnrDwellTimeLink = new PnrDwellTimeLink(pnrId, a.getId());
			if (!this.dwellTimeLinks.contains(pnrDwellTimeLink)) {
				this.requestObjectList.add(pnrDwellTimeLink);
				this.dwellTimeLinks.add(pnrDwellTimeLink);
			}
		}
	}

	private void addPaymentFormObjects(final Long pnrId, final Collection<PaymentForm> paymentForms) {

		if (CollectionUtils.isEmpty(paymentForms)) {
			return;
		}
		for (PaymentForm a : paymentForms) {
			Long id = a.getId();
			if (!this.paymentFormIdSet.contains(id)) {
				this.requestObjectList.add(a);
				this.paymentFormIdSet.add(id);
			}

			PnrFormOfPaymentLink pnrFormOfPaymentLink = new PnrFormOfPaymentLink(pnrId, a.getId());
			if (!this.pnrFormOfPaymentLinks.contains(pnrFormOfPaymentLink)) {
				this.requestObjectList.add(pnrFormOfPaymentLink);
				this.pnrFormOfPaymentLinks.add(pnrFormOfPaymentLink);
			}
		}
	}

	private void addTravelAgencyObjects(final Long pnrId, final Collection<Agency> agencies) {
		if (CollectionUtils.isEmpty(agencies)) {
			return;
		}
		for (Agency a : agencies) {
			Long id = a.getId();
			if (!this.travelAgencyIdSet.contains(id)) {
				this.requestObjectList.add(a);
				this.travelAgencyIdSet.add(id);
			}
			PnrTravelAgencyLink pnrTravelAgencyLink = new PnrTravelAgencyLink(pnrId, a.getId());
			if (!this.travelAgencyLinks.contains(pnrTravelAgencyLink)) {
				requestObjectList.add(pnrTravelAgencyLink);
				this.travelAgencyLinks.add(pnrTravelAgencyLink);
			}
		}
	}

	/**
	 * Adds passenger and documents for PNR and APIS messages. In case of PNR a link
	 * object is also created.
	 * <p>
	 * the PNR object. If not null then a link object is also created.
	 *
	 * @param passengers
	 *            the collection of passengers.
	 */
	private void addPassengerObjects(final Long pnrId, final Collection<Passenger> passengers) {
		if (passengers == null || passengers.isEmpty()) {
			return;
		}
		for (Passenger passenger : passengers) {
			if (pnrId != null) {
				addPnrPassengerLink(pnrId, passenger);
			}
		}
	}

	private void addPnrPassengerLink(final Long pnrId, final Passenger passenger) {
		PnrPassengerLink link = new PnrPassengerLink(pnrId, passenger.getId());
		if (!this.passengerLinkSet.contains(link)) {
			requestObjectList.add(link);
			this.passengerLinkSet.add(link);
		}
	}

}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.*;

import gov.gtas.model.*;

public interface PnrService {

	public Pnr create(Pnr pnr);

	public Pnr delete(Long id);

	public Pnr update(Pnr pnr);

	public Pnr findById(Long id);

	public List<Pnr> findAll();

	public List<Pnr> findByPassengerId(Long passengerId);

	/*
	 * A duplicate method to avoid 'LazyInitializationException' in the Controller
	 * -- Can be removed after a fix
	 */
	public List<Pnr> findPnrByPassengerIdAndFlightId(Long passengerId, Long flightId);

	public List<Pnr> getPNRsByDates(Date startDate, Date endDate);
	Map<Long, Set<FrequentFlyer>> createFrequentFlyersMap(Set<Long> pnrIds);
	Map<Long, Set<BookingDetail>> createBookingDetailMap(Set<Long> pnrIds);
	Map<Long, Set<CreditCard>> createCreditCardMap(Set<Long> pnrIds);
	Map<Long, Set<Email>> createEmailMap(Set<Long> pnrIds);
	Map<Long, Set<Phone>> createPhoneMap(Set<Long> pnrIds);
	Map<Long, Set<Address>> createAddressMap(Set<Long> pnrIds);
	Map<Long, Set<Passenger>> createPaxMap(Set<Long> pnrIds);
	Map<Long, Set<PaymentForm>> createPaymentFormMap(Set<Long> pnrIds);
	Map<Long, Set<DwellTime>> createDwellTime(Set<Long> pnrIds);
	Map<Long, Set<Agency>> createTravelAgencyMap(Set<Long> pnrIds);
	Map<Long, Set<Passenger>> getPassengersOnPnr(Set<Long> pids, Set<Long> hitApisIds);
	Set<Pnr> pnrMessageWithFlightInfo(Set<Long> pids,Set<Long> messageIds, Long flightId);


	}

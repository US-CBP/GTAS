/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.PnrRepository;

import java.util.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PnrServiceImpl implements PnrService {

	@Resource
	private PnrRepository pnrRepository;

	@Override
	@Transactional
	public Pnr create(Pnr pnr) {
		return pnrRepository.save(pnr);
	}

	@Override
	@Transactional
	public Pnr delete(Long id) {
		Pnr pnr = this.findById(id);
		if (pnr != null) {
			pnrRepository.delete(pnr);
		}
		return pnr;
	}

	@Override
	@Transactional
	public Pnr update(Pnr pnr) {
		Pnr rv = this.findById(pnr.getId());
		if (rv != null) {
			mapPnr(pnr, rv);
		}
		return rv;
	}

	@Override
	@Transactional
	public Pnr findById(Long id) {
		return pnrRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public List<Pnr> findAll() {
		return (List<Pnr>) pnrRepository.findAll();
	}

	@Override
	@Transactional
	public List<Pnr> findByPassengerId(Long passengerId) {
		return pnrRepository.getPnrsByPassengerId(passengerId);
	}

	@Override
	public List<Pnr> getPNRsByDates(Date startDate, Date endDate) {
		return pnrRepository.getPNRsByDates();
	}

	@Override
	@Transactional
	/*
	 * A duplicate method to avoid 'LazyInitializationException' in the Controller
	 * -- Can be removed after a fix
	 */
	public List<Pnr> findPnrByPassengerIdAndFlightId(Long passengerId, Long flightId) {

		Pnr rv = new Pnr();
		List<Pnr> _retList = new ArrayList<Pnr>();
		Set<Pnr> _tempPnrList = pnrRepository.getPnrsByPassengerIdAndFlightId(passengerId, flightId);

		for (Pnr _tempPnr : _tempPnrList) {
			rv = new Pnr();
			rv.setRecordLocator(_tempPnr.getRecordLocator());
			if (checkPassengerAndFlightOnPNR(_tempPnr, passengerId, flightId)) {
				mapPnr(_tempPnr, rv);
				_retList.add(rv);
			}
		}
		return _retList;
	}

	private void mapPnr(Pnr source, Pnr target) {
		target.setId(source.getId());
		target.setBagCount(source.getBagCount());
		target.setTotal_bag_count(source.getTotal_bag_count());
		target.setBaggageUnit(source.getBaggageUnit());
		target.setBaggageWeight(source.getBaggageWeight());
		target.setDateBooked(source.getDateBooked());
		target.setCarrier(source.getCarrier());
		target.setDaysBookedBeforeTravel(source.getDaysBookedBeforeTravel());
		target.setDepartureDate(source.getDepartureDate());
		target.setFormOfPayment(source.getFormOfPayment());
		target.setOrigin(source.getOrigin());
		target.setOriginCountry(source.getOriginCountry());
		target.setPassengerCount(source.getPassengerCount());
		target.setDateReceived(source.getDateReceived());
		target.setRaw(source.getRaw());
		target.setEdifactMessage(source.getEdifactMessage());
		target.setTripType(source.getTripType());

		if (source.getAddresses() != null && source.getAddresses().size() > 0) {
			Iterator it = source.getAddresses().iterator();
			while (it.hasNext()) {
				Address a = (Address) it.next();

				// TODO equals contract is not working for address.work
				// around/compare manually
				Address chkAddress = getExistingAddress(a, target.getAddresses());
				if (chkAddress == null) {
					target.addAddress(a);
				}
			}
		}
		if (!CollectionUtils.isEmpty(source.getAgencies())) {
			target.setAgencies(source.getAgencies());
		}

		if (source.getCreditCards() != null && source.getCreditCards().size() > 0) {
			Iterator it1 = source.getCreditCards().iterator();
			while (it1.hasNext()) {
				CreditCard cc = (CreditCard) it1.next();
				if (!target.getCreditCards().contains(cc)) {
					target.addCreditCard(cc);
				}
			}
		}
		if (source.getFrequentFlyers() != null && source.getFrequentFlyers().size() > 0) {
			Iterator it2 = source.getFrequentFlyers().iterator();
			while (it2.hasNext()) {
				FrequentFlyer ff = (FrequentFlyer) it2.next();
				if (!target.getFrequentFlyers().contains(ff)) {
					target.addFrequentFlyer(ff);
				}
			}
		}
		if (source.getEmails() != null && source.getEmails().size() > 0) {
			Iterator it3 = source.getEmails().iterator();
			while (it3.hasNext()) {
				Email e = (Email) it3.next();
				if (!target.getEmails().contains(e)) {
					target.addEmail(e);
				}
			}
		}
		if (source.getPhones() != null && source.getPhones().size() > 0) {
			Iterator it4 = source.getPhones().iterator();
			while (it4.hasNext()) {
				Phone p = (Phone) it4.next();
				if (!target.getPhones().contains(p)) {
					target.addPhone(p);
				}
			}
		}
		if (source.getFlightLegs() != null && source.getFlightLegs().size() > 0) {
			List<FlightLeg> _tempFL = source.getFlightLegs();
			for (FlightLeg fl : _tempFL) {
				if (!checkFlightLegExistence(target.getFlightLegs(), fl)) {
					target.getFlightLegs().add(fl);
				}
			}
		}

		if (source.getPaymentForms() != null && source.getPaymentForms().size() > 0) {
			List<PaymentForm> tempForms = source.getPaymentForms();
			for (PaymentForm form : tempForms) {
				if (!checkFormOfPaymentExist(target.getPaymentForms(), form)) {
					target.getPaymentForms().add(form);
				}
			}
		}

		if (source.getPassengers() != null && source.getPassengers().size() > 0) {
			Iterator it6 = source.getPassengers().iterator();
			while (it6.hasNext()) {
				Passenger passenger = (Passenger) it6.next();
				if (!target.getPassengers().contains(passenger)) {
					target.addPassenger(passenger);
				}

			}
		}

	}

	private boolean checkFormOfPaymentExist(List<PaymentForm> forms, PaymentForm form) {
		boolean flag = false;
		for (PaymentForm pf : forms) {
			if (pf.getPnr().equals(form.getPnr())) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private boolean checkFlightLegExistence(List<FlightLeg> flightLegs, final FlightLeg fl) {

		if (fl.getFlight() == null)
			return false;

		for (FlightLeg leg : flightLegs) {
			if (leg.getFlight() != null) {
				if ((leg.getFlight()).equals(fl.getFlight()) && (leg.getMessage()).equals(fl.getMessage())) {
					return true;
				}
			} else { // Check instead for booking detail as a flight leg
				if (leg.getBookingDetail() != null && fl.getBookingDetail() != null
						&& ((leg.getBookingDetail()).equals(fl.getBookingDetail())
								&& (leg.getMessage()).equals(fl.getMessage()))) {
					return true;
				}
			}
		}
		return false;
	}

	private Address getExistingAddress(Address a, Set<Address> addresses) {
		Address chk = null;
		if (addresses != null && addresses.size() > 0) {
			Iterator it = addresses.iterator();
			while (it.hasNext()) {
				chk = (Address) it.next();
				if (StringUtils.equals(a.getCity(), chk.getCity())
						&& StringUtils.equals(a.getCountry(), chk.getCountry())
						&& StringUtils.equals(a.getLine1(), chk.getLine1())
						&& StringUtils.equals(a.getState(), chk.getState())
						&& StringUtils.equals(a.getPostalCode(), chk.getPostalCode())) {
					return chk;
				}
				chk = null;
			}
		}
		return chk;

	}

	private boolean checkPassengerAndFlightOnPNR(Pnr source, Long passengerId, Long flightId) {

		boolean flightCheck = false, passengerCheck = false;

		if (source.getFlightLegs() != null && source.getFlightLegs().size() > 0) {
			List<FlightLeg> _tempFL = source.getFlightLegs();
			for (FlightLeg fl : _tempFL) {

				if (fl.getFlight() != null) { // FlightLegs contain both flights and booking details, we only care to
												// compare against flights here
					if (fl.getFlight().getId().equals(flightId)) {
						flightCheck = true;
						break;
					}
				}
			}
		}

		if (source.getPassengers() != null && source.getPassengers().size() > 0) {
			Iterator it6 = source.getPassengers().iterator();
			while (it6.hasNext()) {
				Passenger passenger = (Passenger) it6.next();
				if (passenger.getId().equals(passengerId)) {
					passengerCheck = true;
					break;
				}

			}
		}

		if (flightCheck && passengerCheck)
			return true;
		else
			return false;

	}

	public Map<Long, Set<Passenger>> createPaxMap(Set<Long> pnrIds) {
		Map<Long, Set<Passenger>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getPax(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			Passenger object = (Passenger) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<PaymentForm>> createPaymentFormMap(Set<Long> pnrIds) {
		Map<Long, Set<PaymentForm>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getPaymentFormsByPnrIds(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			PaymentForm object = (PaymentForm) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<DwellTime>> createDwellTime(Set<Long> pnrIds) {
		Map<Long, Set<DwellTime>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getDwellTimeByPnr(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			DwellTime object = (DwellTime) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<Agency>> createTravelAgencyMap(Set<Long> pnrIds) {
		Map<Long, Set<Agency>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getTravelAgencyByPnr(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			Agency object = (Agency) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<FrequentFlyer>> createFrequentFlyersMap(Set<Long> pnrIds) {
		Map<Long, Set<FrequentFlyer>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getFrequentFlyerByPnrId(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			FrequentFlyer object = (FrequentFlyer) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<BookingDetail>> createBookingDetailMap(Set<Long> pnrIds) {
		Map<Long, Set<BookingDetail>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getBookingDetailsByPnrId(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			BookingDetail object = (BookingDetail) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<CreditCard>> createCreditCardMap(Set<Long> pnrIds) {
		Map<Long, Set<CreditCard>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.getCreditCardByIds(pnrIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			CreditCard object = (CreditCard) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Map<Long, Set<Email>> createEmailMap(Set<Long> pnrIds) {
		Map<Long, Set<Email>> emailMap = new HashMap<>();
		List<Object[]> emailList = pnrRepository.getEmailByPnrIds(pnrIds);
		for (Object[] answerKey : emailList) {
			Long pnrId = (Long) answerKey[0];
			Email email = (Email) answerKey[1];
			processObject(email, emailMap, pnrId);
		}
		return emailMap;
	}

	public Map<Long, Set<Phone>> createPhoneMap(Set<Long> pnrIds) {
		Map<Long, Set<Phone>> phoneMap = new HashMap<>();
		List<Object[]> phoneList = pnrRepository.getPhonesByPnr(pnrIds);
		for (Object[] answerKey : phoneList) {
			Long pnrId = (Long) answerKey[0];
			Phone phone = (Phone) answerKey[1];
			processObject(phone, phoneMap, pnrId);
		}
		return phoneMap;
	}

	public Map<Long, Set<Address>> createAddressMap(Set<Long> pnrIds) {
		Map<Long, Set<Address>> addressMap = new HashMap<>();
		List<Object[]> addressList = pnrRepository.getAddressesByPnr(pnrIds);
		for (Object[] answerKey : addressList) {
			Long pnrId = (Long) answerKey[0];
			Address address = (Address) answerKey[1];
			processObject(address, addressMap, pnrId);
		}
		return addressMap;
	}

	private static <T> void processObject(T type, Map<Long, Set<T>> map, Long pnrId) {
		if (map.containsKey(pnrId)) {
			map.get(pnrId).add(type);
		} else {
			Set<T> objectHashSet = new HashSet<>(map.values().size() * 50);
			objectHashSet.add(type);
			map.put(pnrId, objectHashSet);
		}
	}
	public Map<Long, Set<Passenger>> getPassengersOnPnr(Set<Long> pids, Set<Long> hitApisIds) {
		Map<Long, Set<Passenger>> objectMap = new HashMap<>();
		List<Object[]> oList = pnrRepository.pnrAndObject(pids, hitApisIds);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			Passenger object = (Passenger) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}

	public Set<Pnr> pnrMessageWithFlightInfo(Set<Long> pids,Set<Long> messageIds, Long flightId) {
		return pnrRepository.pnrMessageWithFlightInfo(pids, messageIds, flightId);
	}

}

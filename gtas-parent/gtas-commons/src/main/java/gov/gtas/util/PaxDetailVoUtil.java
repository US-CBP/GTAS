package gov.gtas.util;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.stream.Collectors;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.PassengerDetailFromMessage;
import gov.gtas.model.PassengerDetails;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.AgencyVo;
import gov.gtas.vo.passenger.CreditCardVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.EmailVo;
import gov.gtas.vo.passenger.FlightLegVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FlightVoForFlightHistory;
import gov.gtas.vo.passenger.FrequentFlyerVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PhoneVo;
import gov.gtas.vo.passenger.PnrVo;

public class PaxDetailVoUtil {

	private static final Logger logger = LoggerFactory.getLogger(PaxDetailVoUtil.class);

	public static List<FlightVoForFlightHistory> copyBookingDetailFlightModelToVo(
			List<Passenger> allPassengersRelatingToSingleIdTag) {
		List<FlightVoForFlightHistory> flightsAndBookingDetailsRelatingToSamePaxIdTag = new ArrayList<>();
		try {
			Set<Passenger> flightPassengersList = new HashSet<>(allPassengersRelatingToSingleIdTag);
			Set<Pair<Passenger, Flight>> associatedPaxFlights = flightPassengersList.stream()
					.map(p -> new ImmutablePair<>(p, p.getFlight()))
					.collect(Collectors.toSet());

			List<FlightVoForFlightHistory> flightHistory = associatedPaxFlights.stream().map(passengerFlightPair -> {
				FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
				populateFlightVoWithFlightDetail(passengerFlightPair.getRight(), flightVo);
				Long pId = passengerFlightPair.getLeft().getId();
				flightVo.setPassId(pId.toString());
				flightVo.setBookingDetail(false);
				return flightVo;
			}).collect(toList());

			List<BookingDetail> passengerBookingDetails = allPassengersRelatingToSingleIdTag.stream()
					.map(Passenger::getBookingDetails).flatMap(Collection::stream).distinct().collect(toList());

			List<FlightVoForFlightHistory> bookingDetailsHistory = passengerBookingDetails.stream()
					.map(bookingDetail -> {
						FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
						populateFlightVoWithBookingDetail(bookingDetail, flightVo);
						flightVo.setBookingDetail(true);
						return flightVo;
					}).collect(toList());

			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(flightHistory);
			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(bookingDetailsHistory);

		} catch (Exception e) {
			logger.error("error copying model to vo.", e);
		}

		return flightsAndBookingDetailsRelatingToSamePaxIdTag;
	}

	public static void populateFlightVoWithBookingDetail(BookingDetail source, FlightVo target) {
		try {

			target.setFlightNumber(((BookingDetail) source).getFlightNumber());
			target.setFullFlightNumber(((BookingDetail) source).getFullFlightNumber());
			target.setEtaDate(((BookingDetail) source).getEtaDate());
			target.setEtdDate(((BookingDetail) source).getEtdDate());
			target.setOriginCountry(((BookingDetail) source).getOriginCountry());
			target.setOrigin(((BookingDetail) source).getOrigin());
			target.setDestinationCountry(((BookingDetail) source).getDestinationCountry());
			target.setDestination(((BookingDetail) source).getDestination());
			target.setEtd(((BookingDetail) source).getEtd());
			target.setEta(((BookingDetail) source).getEta());
			// target.setFullFlightNumber(((BookingDetail)source).getFullFlightNumber());
			target.setFlightId(source.getId().toString());

		} catch (Exception e) {
			logger.error("error populating flight with booking details", e);
		}
	}

	public static PassengerDetails filterOutMaskedAPISOrPnr(Passenger t) {
		PassengerDetails passengerDetails = t.getPassengerDetails();
		if (t.getDataRetentionStatus().isMaskedAPIS() || t.getDataRetentionStatus().isMaskedPNR() || t.getDataRetentionStatus().isDeletedAPIS() || t.getDataRetentionStatus().isDeletedPNR()) {
			if (!t.getDataRetentionStatus().isMaskedPNR() && !t.getDataRetentionStatus().isDeletedPNR() && t.getDataRetentionStatus().isHasPnrMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.PNR);
			} else if (!t.getDataRetentionStatus().isMaskedAPIS() && !t.getDataRetentionStatus().isDeletedAPIS() && t.getDataRetentionStatus().isHasApisMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.APIS);
			} else if ((t.getDataRetentionStatus().isHasApisMessage() && !t.getDataRetentionStatus().isDeletedAPIS())
					|| (t.getDataRetentionStatus().isHasPnrMessage() && !t.getDataRetentionStatus().isDeletedPNR())){
				passengerDetails.maskPII();
			} else {
				passengerDetails.deletePII();
			}
		} return passengerDetails;
	}

	private static PassengerDetails getPassengerDetails(Passenger t, MessageType messageType) {
		return t
				.getPassengerDetailFromMessages()
				.stream()
				.filter(fs -> fs.getMessageType() == messageType)
				.sorted(Comparator.comparing(PassengerDetailFromMessage::getCreatedAt).reversed())
				.map(PassengerDetails::from)
				.findFirst()
				.orElse(new PassengerDetails());
	}

	public static void populatePassengerVoWithPassengerDetails(PassengerVo vo, PassengerDetails passengerDetails, Passenger passenger) {
		if(vo == null) {
			logger.error("error populating passengerVo with passenger details");
			return;
		}

		if(passengerDetails != null) {
			vo.setPassengerType(passengerDetails.getPassengerType());
			vo.setLastName(passengerDetails.getLastName());
			vo.setFirstName(passengerDetails.getFirstName());
			vo.setMiddleName(passengerDetails.getMiddleName());
			vo.setNationality(passengerDetails.getNationality());
			vo.setDob(passengerDetails.getDob());
			vo.setAge(passengerDetails.getAge());
			vo.setEmbarkation(passenger.getPassengerTripDetails().getEmbarkation());
			vo.setEmbarkCountry(passenger.getPassengerTripDetails().getEmbarkCountry());
			vo.setGender(passengerDetails.getGender() != null ? passengerDetails.getGender() : "");
			vo.setResidencyCountry(passengerDetails.getResidencyCountry());
			vo.setSuffix(passengerDetails.getSuffix());
			vo.setTitle(passengerDetails.getTitle());
		}

		if(passenger != null && passenger.getPassengerTripDetails() != null) {
			vo.setDebarkation(passenger.getPassengerTripDetails().getDebarkation());
			vo.setDebarkCountry(passenger.getPassengerTripDetails().getDebarkCountry());
		}
	}

	public static void populateFlightVoWithFlightDetail(Flight source, FlightVo target) {
		try {

			target.setFlightNumber(source.getFlightNumber());
			target.setCarrier(source.getCarrier());
			target.setEtaDate(source.getMutableFlightDetails().getEtaDate());
			target.setEtdDate(source.getEtdDate());
			target.setOriginCountry(source.getOriginCountry());
			target.setOrigin(source.getOrigin());
			target.setDestinationCountry(source.getDestinationCountry());
			target.setDestination(source.getDestination());
			target.setEtd(source.getMutableFlightDetails().getEtd());
			target.setEta(source.getMutableFlightDetails().getEta());
			target.setFullFlightNumber(source.getFullFlightNumber());
			target.setFlightId(source.getId().toString());
			target.setIdTag(source.getIdTag());
		} catch (Exception e) {
			logger.error("error populating flight vo", e);
		}
	}

	/**
	 * Util method to map PNR model object to VO
	 * 
	 * @param source
	 * @return
	 */
	public static PnrVo mapPnrToPnrVo(Pnr source) {
		PnrVo target = new PnrVo();

		if (source.getRecordLocator() == null || source.getRecordLocator().isEmpty()) {
			target.setPnrRecordExists(false);
			return target;
		}

		if (!source.getPassengers().isEmpty()) {
			for (Passenger p : source.getPassengers()) {
				PassengerVo pVo = new PassengerVo();
				pVo.setLastName(p.getPassengerDetails().getLastName());
				pVo.setFirstName(p.getPassengerDetails().getFirstName());
				pVo.setMiddleName(p.getPassengerDetails().getMiddleName());
				pVo.setAge(p.getPassengerDetails().getAge());
				pVo.setGender(p.getPassengerDetails().getGender());
				pVo.setPaxId(Long.toString(p.getId()));
				target.getPassengers().add(pVo);
				Set<Document> documents = p.getDocuments();
				for (Document d : documents) {
					if (d.getMessageType() == MessageType.PNR) {
						DocumentVo documentVo = new DocumentVo();
						documentVo.setFirstName(d.getPassenger().getPassengerDetails().getFirstName());
						documentVo.setLastName(d.getPassenger().getPassengerDetails().getLastName());
						documentVo.setDocumentType(d.getDocumentType());
						documentVo.setIssuanceCountry(d.getIssuanceCountry());
						documentVo.setDocumentNumber(d.getDocumentNumber());
						documentVo.setIssuanceDate(d.getIssuanceDate());
						documentVo.setExpirationDate(d.getExpirationDate());
						target.getDocuments().add(documentVo);
					}
				}
				if (p.getDataRetentionStatus().isMaskedPNR()) {
					pVo.maskPII();
				}
			}
		}

		target.setPnrRecordExists(true);
		target.setRecordLocator(source.getRecordLocator());
		target.setBagCount(source.getBagCount());
		target.setDateBooked(source.getDateBooked());
		target.setCarrier(source.getCarrier());
		target.setDaysBookedBeforeTravel(source.getDaysBookedBeforeTravel());
		target.setDepartureDate(source.getDepartureDate());
		target.setFormOfPayment(source.getFormOfPayment());
		target.setOrigin(source.getOrigin());
		target.setOriginCountry(source.getOriginCountry());
		target.setPassengerCount(source.getPassengerCount());
		target.setDateReceived(source.getDateReceived());
		target.setRaw(LobUtils.convertClobToString(source.getRaw()));
		target.setTransmissionDate(source.getEdifactMessage().getTransmissionDate());
		target.setTotal_bag_count(source.getTotal_bag_count());
		if (source.getBaggageWeight() != null)
			target.setBaggageWeight(source.getBaggageWeight());

		target.setTripType(source.getTripType());

		if (!source.getAddresses().isEmpty()) {
			Iterator it = source.getAddresses().iterator();
			while (it.hasNext()) {
				Address a = (Address) it.next();
				AddressVo aVo = new AddressVo();

				try {

					BeanUtils.copyProperties(aVo, a);

				} catch (Exception e) {
					logger.error("Unable to copy properties, catching and moving to next address", e);
				}

				target.getAddresses().add(aVo);

			} // End of While Loop

		}

		if (CollectionUtils.isNotEmpty(source.getAgencies())) {
			AgencyVo aVo = new AgencyVo();
			for (Agency agency : source.getAgencies()) {
				copyModelToVo(agency, aVo);
				target.getAgencies().add(aVo);
			}
		}

		if (!source.getCreditCards().isEmpty()) {
			Iterator<CreditCard> it1 = source.getCreditCards().iterator();
			while (it1.hasNext()) {
				CreditCard cc = it1.next();
				CreditCardVo cVo = new CreditCardVo();
				copyModelToVo(cc, cVo);
				target.getCreditCards().add(cVo);
			}
		}
		if (!source.getFrequentFlyers().isEmpty()) {
			Iterator<FrequentFlyer> it2 = source.getFrequentFlyers().iterator();
			while (it2.hasNext()) {
				FrequentFlyer ff = it2.next();
				FrequentFlyerVo fVo = new FrequentFlyerVo();
				copyModelToVo(ff, fVo);
				target.getFrequentFlyerDetails().add(fVo);
			}
		}

		if (!source.getEmails().isEmpty()) {
			Iterator<Email> it3 = source.getEmails().iterator();
			while (it3.hasNext()) {
				Email e = it3.next();
				EmailVo eVo = new EmailVo();
				copyModelToVo(e, eVo);
				target.getEmails().add(eVo);
			}
		}

		if (!source.getPhones().isEmpty()) {
			Iterator<Phone> it4 = source.getPhones().iterator();
			while (it4.hasNext()) {
				Phone p = it4.next();
				PhoneVo pVo = new PhoneVo();
				copyModelToVo(p, pVo);
				target.getPhoneNumbers().add(pVo);
			}
		}

		if (!source.getFlightLegs().isEmpty()) {
			List<FlightLeg> _tempFL = source.getFlightLegs();
			for (FlightLeg fl : _tempFL) {
				FlightLegVo flVo = new FlightLegVo();
				flVo.setLegNumber(fl.getLegNumber().toString());
				if (fl.getFlight() != null) {
					flVo.setFlightNumber(fl.getFlight().getFullFlightNumber());
					flVo.setOriginAirport(fl.getFlight().getOrigin());
					flVo.setDestinationAirport(fl.getFlight().getDestination());
					flVo.setEtd(
							fl.getFlight().getMutableFlightDetails().getEtd());
					flVo.setEta(
							fl.getFlight().getMutableFlightDetails().getEta());
					flVo.setFlightId(Long.toString(fl.getFlight().getId()));
					flVo.setDirection(fl.getFlight().getDirection());
				} else {
					flVo.setFlightNumber(fl.getBookingDetail().getFullFlightNumber());
					flVo.setOriginAirport(fl.getBookingDetail().getOrigin());
					flVo.setDestinationAirport(fl.getBookingDetail().getDestination());
					flVo.setEtd(fl.getBookingDetail().getEtd());
					flVo.setEta(fl.getBookingDetail().getEta());
					flVo.setBookingDetailId(Long.toString(fl.getBookingDetail().getId()));
				}
				target.getFlightLegs().add(flVo);
			}
		}

		boolean pnrHasUnmaskedPassenger = source.getPassengers().stream().anyMatch(p -> !p.getDataRetentionStatus().isMaskedPNR());
		boolean pnrHasUndeletedPassenger = source.getPassengers().stream().anyMatch(p -> !p.getDataRetentionStatus().isDeletedPNR());
		if (!pnrHasUndeletedPassenger) {
			target.deletePII();
		} else if (!pnrHasUnmaskedPassenger) {
			target.maskPII();
		}

		return target;
	}

	/**
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyModelToVo(Object source, Object target) {

		try {
			BeanUtils.copyProperties(source, target);
		} catch (Exception e) {
			logger.error("error copying model to vo", e);
		}
	}

	public static Pnr getLatestPnrFromList(List<Pnr> pnrList) {
		Pnr latest = pnrList.get(0);
		for (Pnr p : pnrList) {
			if (p.getId() > latest.getId()) {
				latest = p;
			}
		}
		return latest;
	}

}

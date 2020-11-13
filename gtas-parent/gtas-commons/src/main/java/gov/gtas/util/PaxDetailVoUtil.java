package gov.gtas.util;

import static java.util.stream.Collectors.toList;

import java.util.*;
import java.util.stream.Collectors;

import gov.gtas.enumtype.MessageType;
import gov.gtas.json.KeyValue;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.PassengerDetailFromMessage;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.UserGroup;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.passenger.*;
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
		if (t.getDataRetentionStatus().requiresMaskedAPIS() || t.getDataRetentionStatus().requiresMaskedPNR() || t.getDataRetentionStatus().requiresDeletedAPIS() || t.getDataRetentionStatus().requiresDeletedPNR()) {
			if (!t.getDataRetentionStatus().requiresMaskedPNR() && !t.getDataRetentionStatus().requiresDeletedPNR() && t.getDataRetentionStatus().isHasPnrMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.PNR);
			} else if (!t.getDataRetentionStatus().requiresMaskedAPIS() && !t.getDataRetentionStatus().requiresDeletedAPIS() && t.getDataRetentionStatus().isHasApisMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.APIS);
			} else if ((t.getDataRetentionStatus().isHasApisMessage() && !t.getDataRetentionStatus().requiresDeletedAPIS())
					|| (t.getDataRetentionStatus().isHasPnrMessage() && !t.getDataRetentionStatus().requiresDeletedPNR())){
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
			vo.setGender(passengerDetails.getGender() != null ? passengerDetails.getGender() : "");
			vo.setResidencyCountry(passengerDetails.getResidencyCountry());
			vo.setSuffix(passengerDetails.getSuffix());
			vo.setTitle(passengerDetails.getTitle());
		}

		if(passenger != null && passenger.getPassengerTripDetails() != null) {
			vo.setEmbarkation(passenger.getPassengerTripDetails().getEmbarkation());
			vo.setEmbarkCountry(passenger.getPassengerTripDetails().getEmbarkCountry());
			vo.setDebarkation(passenger.getPassengerTripDetails().getDebarkation());
			vo.setDebarkCountry(passenger.getPassengerTripDetails().getDebarkCountry());
		}
	}

	public static HitDetailVo populateHitDetailVo(HitDetailVo hitDetailVo, HitDetail htd, User user) {
		hitDetailVo.setRuleId(htd.getRuleId());
		hitDetailVo.setRuleTitle(htd.getTitle());
		hitDetailVo.setRuleDesc(htd.getDescription());
		hitDetailVo.setSeverity(htd.getHitMaker().getHitCategory().getSeverity().toString());
		HitMaker lookout = htd.getHitMaker();
		HitCategory hitCategory = lookout.getHitCategory();
		hitDetailVo.setCategory(hitCategory.getName() + "(" + htd.getHitEnum().getDisplayName() + ")");
		hitDetailVo.setRuleAuthor(htd.getHitMaker().getAuthor().getUserId());
		hitDetailVo.setRuleConditions(htd.getRuleConditions());
		hitDetailVo.setRuleTitle(htd.getTitle());
		StringJoiner stringJoiner = new StringJoiner(", ");

		Set<UserGroup> userGroups = user.getUserGroups();
		for (HitViewStatus hitViewStatus : htd.getHitViewStatus()) {
			if (userGroups.contains(hitViewStatus.getUserGroup())) {
				stringJoiner.add(hitViewStatus.getHitViewStatusEnum().toString());
			}
		}
		hitDetailVo.setFlightDate(htd.getFlight().getMutableFlightDetails().getEtd());
		hitDetailVo.setStatus(stringJoiner.toString());
		PaxDetailVoUtil.deleteAndMaskPIIFromHitDetailVo(hitDetailVo, htd.getPassenger());

		return hitDetailVo;
	}

	public static void deleteAndMaskPIIFromHitDetailVo(HitDetailVo hitDetailVo, Passenger hdPassenger) {
		if (hdPassenger.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
			hitDetailVo.deletePII();
		} else if (hdPassenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
			hitDetailVo.maskPII();
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
						documentVo.setMessageType(d.getMessageType() == null ? "" : d.getMessageType().toString());
						documentVo.setExpirationDate(d.getExpirationDate());
						target.getDocuments().add(documentVo);
					}
				}
				if (p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
					pVo.deletePII();
				} else if (p.getDataRetentionStatus().requiresMaskedPNR()) {
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

		if (CollectionUtils.isNotEmpty(source.getAddresses())) {
			for (Address a : source.getAddresses()) {
				AddressVo aVo = new AddressVo();
				copyModelToVo(a, aVo);
				target.getAddresses().add(aVo);
			}
		}

		if (CollectionUtils.isNotEmpty(source.getAgencies())) {
			AgencyVo aVo = new AgencyVo();
			for (Agency agency : source.getAgencies()) {
				copyModelToVo(agency, aVo);
				target.getAgencies().add(aVo);
			}
		}

		if (!source.getCreditCards().isEmpty()) {
			for (CreditCard cc : source.getCreditCards()) {
				CreditCardVo cVo = new CreditCardVo();
				copyModelToVo(cc, cVo);
				target.getCreditCards().add(cVo);
			}
		}
		if (!source.getFrequentFlyers().isEmpty()) {
			for (FrequentFlyer ff : source.getFrequentFlyers()) {
				FrequentFlyerVo fVo = new FrequentFlyerVo();
				copyModelToVo(ff, fVo);
				target.getFrequentFlyerDetails().add(fVo);
			}
		}

		if (!source.getEmails().isEmpty()) {
			for (Email e : source.getEmails()) {
				EmailVo eVo = new EmailVo();
				copyModelToVo(e, eVo);
				target.getEmails().add(eVo);
			}
		}

		if (!source.getPhones().isEmpty()) {
			for (Phone p : source.getPhones()) {
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

		boolean pnrHasUnmaskedPassenger = source.getPassengers().stream().anyMatch(p -> !p.getDataRetentionStatus().requiresMaskedPNR());
		boolean pnrHasUndeletedPassenger = source.getPassengers().stream().anyMatch(p -> !p.getDataRetentionStatus().requiresDeletedPNR());
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


	/**
	 * Segments PnrRaw String Required for Frontend to highlight segment
	 * corresponding to pnr section
	 *
	 * @param targetVo
	 */
	public static void parseRawMessageToSegmentList(PnrVo targetVo) {
		if (targetVo != null && targetVo.getRaw() != null) {

			StringTokenizer _tempStr = new StringTokenizer(targetVo.getRaw(), "\n");
			List<KeyValue> segmentList = new ArrayList<>();

			final String ITIN = "TVL";
			final String NAME = "SSR";
			final String DOC = "DOCS";
			final String ADD = "ADD";
			final String CC = "FOP";
			final String FF = "FTI";
			final String BAG = "TBD";
			final String TIF = "TIF";

			String tifSegment = "";
			Integer indexInteger = 0;

			while (_tempStr.hasMoreTokens()) {
				String currString = _tempStr.nextToken();
				StringBuilder segment = new StringBuilder();

				// Itinerary
				if (currString.contains(ITIN)) {
					for (FlightLegVo f : targetVo.getFlightLegs()) {
						if (currString.contains(f.getOriginAirport())) {
							segment.append(ITIN);
							segment.append(f.getOriginAirport());
							segment.append(" ");
						}
					}
				}
				// PNR names
				if (currString.contains(NAME)) {
					for (PassengerVo p : targetVo.getPassengers()) {
						if (currString.contains(p.getFirstName())) {
							segment.append(NAME);
							segment.append(p.getFirstName());
							segment.append(" ");
						}
					}
				}
				// Doc Numbers
				if (currString.contains(DOC)) {
					for (DocumentVo d : targetVo.getDocuments()) {
						if (d.getDocumentNumber() != null && currString.contains(d.getDocumentNumber())) {
							segment.append(DOC);
							segment.append(d.getDocumentNumber());
							segment.append(" ");
						}
					}
				}
				// Addresses
				if (currString.contains(ADD)) {
					for (AddressVo a : targetVo.getAddresses()) {
						if (a.getCity() != null && currString.contains(a.getCity())) {
							segment.append(ADD);
							segment.append(a.getCity());
							segment.append(" ");
						}
					}
				}
				// FOP
				if (currString.contains(CC)) {
					for (CreditCardVo c : targetVo.getCreditCards()) {
						if (currString.contains(c.getNumber().substring(c.getNumber().length() - 4))) {
							segment.append(CC);
							segment.append(c.getNumber());
							segment.append(" ");
						}
					}
				}
				// Frequent Flyer
				if (currString.contains(FF)) {
					for (FrequentFlyerVo f : targetVo.getFrequentFlyerDetails()) {
						if (currString.contains(f.getNumber())) {
							segment.append(FF);
							segment.append(f.getNumber());
							segment.append(" ");
						}
					}
				}

				/*
				 * GR.7 TIF - the checked-in name. Used to link bags to passengers.
				 */
				if (currString.contains(TIF)) {
					tifSegment = currString;
				}

//				// Bag
//				if (currString.contains(BAG)) {
//					for (BagVo b : targetVo.getBags()) {
//						if (isRelatedToTifPassenger(tifSegment, b)) {
//							segment.append(BAG);
//							segment.append(b.getPassFirstName());
//							segment.append(b.getPassLastName());
//							segment.append(" ");
//						}
//					}
//				}
				// Phone
				for (PhoneVo p : targetVo.getPhoneNumbers()) {
					if (currString.contains(p.getNumber().substring(p.getNumber().length() - 4))) {
						segment.append("PHONE");
						segment.append(p.getNumber());
						segment.append(" ");
					}
				}

				// Email
				for (EmailVo e : targetVo.getEmails()) {
					boolean isMatch = true;
					String[] words = e.getAddress().split("[^a-zA-Z0-9']+");

					for (String word : words) {
						if (!currString.contains(word)) {
							isMatch = false;
							break;
						}
					}
					if (words.length > 0 && isMatch) {
						segment.append("EMAIL");
						segment.append(e.getAddress());
						segment.append(" ");
					}
				}

				// Seat
				for (SeatVo s : targetVo.getSeatAssignments()) {
					if (currString.contains(s.getNumber())) {
						segment.append("SEAT");
						segment.append(s.getNumber());
						segment.append(" ");
					}
				}

				// Agency
				for (AgencyVo a : targetVo.getAgencies()) {
					if (a.getIdentifier() != null && currString.contains(a.getIdentifier())) {
						segment.append("AGEN");
						segment.append(a.getIdentifier());
						segment.append(" ");
					}
				}

				if (segment.toString().isEmpty()) {
					KeyValue kv = new KeyValue(indexInteger.toString(), currString);
					segmentList.add(kv);
				} else {
					KeyValue kv2 = new KeyValue(segment.toString(), currString);
					segmentList.add(kv2);
					;
				}

				indexInteger++;
			}
			targetVo.setSegmentList(segmentList);
		}
	}
}

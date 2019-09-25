package gov.gtas.services;

import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;
import gov.gtas.parsers.vo.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

class BagVoToBagAdapter {

	private Set<Bag> existingBags;
	private Set<BagVo> newBags;
	private Map<UUID, Set<BagVo>> paxParserUUIDToBagVoSetMap;
	private Set<BagMeasurementsVo> bagMeasurementsVos;
	private Set<BookingDetail> messageBookingDetails;
	private MessageVo messageVo;

	BagVoToBagAdapter(MessageVo messageVo, Set<Bag> passengerBags, Set<BookingDetail> messageBookingDetails) {

		this.messageBookingDetails = messageBookingDetails;
		this.messageVo = messageVo;
		handleDuplicateBags(messageVo.getBagVos(), passengerBags);
		bagMeasurementsVos = newBags.stream().map(BagVo::getBagMeasurementsVo).filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

	private Map<UUID, UUID> getOrphanMap(MessageVo messageVo, Set<BookingDetail> messageBookingDetails) {

		Map<UUID, UUID> orphanToBD = new HashMap<>();
		if (messageBookingDetails.size() < messageVo.getFlights().size() - 1) {
			Set<FlightVo> orphanedFlightVo = new HashSet<>();
			Set<UUID> bookingDetails = messageBookingDetails.stream().map(BookingDetail::getParserUUID)
					.collect(Collectors.toSet());
			for (FlightVo flightVo : messageVo.getFlights()) {
				if (!bookingDetails.contains(flightVo.getUuid())) {
					orphanedFlightVo.add(flightVo);
				}
			}

			for (FlightVo orphan : orphanedFlightVo) {
				for (BookingDetail bookingDetail : messageBookingDetails) {
					if (orphan.equalsThisBD(bookingDetail)) {
						if (!orphanToBD.containsKey(orphan.getUuid())) {
							orphanToBD.put(orphan.getUuid(), bookingDetail.getParserUUID());
						}
					}
				}
			}
		}
		return orphanToBD;
	}

	private void handleDuplicateBags(List<BagVo> bagVoList, Set<Bag> existingBags) {
		// Prime flight bags take priority and get merged into.
		Set<BagVo> newBagVos = new HashSet<>();
		Set<BagVo> badVos = new HashSet<>();
		// Handle duplicate bag vos first.
		for (BagVo bagvo : bagVoList) {
			if (!badVos.contains(bagvo)) {
				for (BagVo secondBag : new ArrayList<>(bagVoList)) {
					if (!bagvo.equals(secondBag) && hasSameBagInfo(bagvo, secondBag)) {
						if (secondBag.isPrimeFlight()) {
							bagvo.setPrimeFlight(true);
						}
						bagvo.getFlightVoId().addAll(secondBag.getFlightVoId());
						badVos.add(secondBag);
					}
				}
				newBagVos.add(bagvo);
			}
		}

		Set<Bag> bagsToUpdate = new HashSet<>();
		// Handle duplicate bag -> bagVos second.
		for (Bag bag : existingBags) {
			for (BagVo bagVo : new ArrayList<>(newBagVos)) {
				if (bagVo.hasSameBagInfo(bag)) {
					bag.getFlightVoUUID().addAll(bagVo.getFlightVoId());
					bag.setHeadPool(bagVo.isHeadPool());
					bag.setMemberPool(bagVo.isMemberPool());
					bag.setPrimeFlight(bagVo.isPrimeFlight());
					newBagVos.remove(bagVo);
					bagsToUpdate.add(bag);
				}
			}
		}
		// Map each passengers new bags (as BagVos) to their parser UUID.
		Map<UUID, Set<BagVo>> paxBagSet = new HashMap<>();
		for (BagVo bagVo : newBagVos) {
			if (paxBagSet.containsKey(bagVo.getPassengerId())) {
				paxBagSet.get(bagVo.getPassengerId()).add(bagVo);
			} else {
				Set<BagVo> paxBagVo = new HashSet<>();
				paxBagVo.add(bagVo);
				paxBagSet.put(bagVo.getPassengerId(), paxBagVo);
			}
		}

		this.existingBags = bagsToUpdate;
		this.newBags = newBagVos;
		this.paxParserUUIDToBagVoSetMap = paxBagSet;
	}

	private boolean hasSameBagInfo(BagVo bagvo, BagVo secondBag) {

		boolean sameWeight = false;
		boolean sameQuantity = false;
		if (bagvo.getBagMeasurementsVo() != null && secondBag.getBagMeasurementsVo() != null) {
			Double bagVoWeight = bagvo.getBagMeasurementsVo().getWeightInKilos();
			Integer bagVoQuanitity = bagvo.getBagMeasurementsVo().getQuantity();
			Double secondBagWeight = secondBag.getBagMeasurementsVo().getWeightInKilos();
			Integer secondBagQuanity = secondBag.getBagMeasurementsVo().getQuantity();

			sameWeight = (((bagVoWeight != null && secondBagWeight != null) && bagVoWeight.equals(secondBagWeight))
					|| (bagVoWeight == null && secondBagWeight == null));

			sameQuantity = (((bagVoQuanitity != null && secondBagQuanity != null)
					&& bagVoQuanitity.equals(secondBagQuanity))
					|| (bagVoQuanitity == null && secondBagQuanity == null));
		}
		return ((StringUtils.isBlank(bagvo.getConsecutiveTagNumber())
				&& StringUtils.isBlank(secondBag.getConsecutiveTagNumber()))
				|| (bagvo.getConsecutiveTagNumber() != null
						&& bagvo.getConsecutiveTagNumber().equals(secondBag.getConsecutiveTagNumber())))
				&& (StringUtils.isNotBlank(bagvo.getBagId()) && bagvo.getBagId().equals(secondBag.getBagId()))
				&& ((bagvo.getBagMeasurementsVo() == null && secondBag.getBagMeasurementsVo() == null)
						|| (sameQuantity && sameWeight))
				&& bagvo.getPassengerId() == secondBag.getPassengerId();
	}

	Set<Bag> getExistingBags() {
		return existingBags;
	}

	Map<UUID, UUID> getOrphanToBD() {
		// de duplicating bags will leave a reference to a uuid that is no longer valid.
		// we map these orphaned guid to their actual guid.
		return getOrphanMap(messageVo, messageBookingDetails);
	}

	Map<UUID, BookingDetail> getUuidBookingDetailMap() {
		return messageBookingDetails.stream().collect(Collectors.toMap(BookingDetail::getParserUUID, bd -> bd));
	}

	Set<BagMeasurementsVo> getBagMeasurementsVos() {
		return bagMeasurementsVos;
	}

	Map<UUID, Set<BagVo>> getPaxMapBagVo() {
		return paxParserUUIDToBagVoSetMap;
	}

}

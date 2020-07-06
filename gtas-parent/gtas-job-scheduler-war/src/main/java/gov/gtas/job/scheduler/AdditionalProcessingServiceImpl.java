package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.AdditionalProcessingService;
import gov.gtas.model.*;
import gov.gtas.services.*;
import gov.gtas.services.dto.MappedGroups;
import gov.gtas.summary.*;
import gov.gtas.services.jms.AdditionalProcessingMessageSender;
import gov.gtas.util.LobUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Scope(scopeName = "prototype")
@Component
public class AdditionalProcessingServiceImpl implements AdditionalProcessingService {

    private final ApisService apisService;

    private final PnrService pnrService;

    private final HitDetailService hitDetailService;

    final
    AdditionalProcessingMessageSender additionalProcessingMessageSender;

    final FlightService flightService;

    @Value("${additional.processing.queue}")
    private String addProcessQueue;

    public AdditionalProcessingServiceImpl(ApisService apisService, PnrService pnrService, HitDetailService hitDetailService, AdditionalProcessingMessageSender additionalProcessingMessageSender, FlightService flightService) {
        this.apisService = apisService;
        this.pnrService = pnrService;
        this.hitDetailService = hitDetailService;
        this.additionalProcessingMessageSender = additionalProcessingMessageSender;
        this.flightService = flightService;
    }

    @Override
    @Transactional(readOnly = true)
    public void passengersAdditionalHits(Set<HitDetail> hitDetailList, Set<Long> messageIds) {
        if (hitDetailList.isEmpty() || messageIds.isEmpty()) {
            return;
        }
        MappedGroups mappedGroups = hitDetailService.getHitDetailsWithGroups(hitDetailList);
        //For each label or group type send a separate group of messages.
        Map<String, Set<HitDetail>> hitDetailsWithCountryGroups = mappedGroups.getCountryMap();
        for (String labelKey : hitDetailsWithCountryGroups.keySet()) {
            HitDetail hd = hitDetailsWithCountryGroups.get(labelKey).iterator().next();
            CountryGroup sendingTo = hd.getHitMaker().getCountryGroup();
            List<String> countryOrgNames = new ArrayList<>();
            for (CountryAndOrganization countryAndOrganization : sendingTo.getAssociatedCountries()) {
                String iso3 = countryAndOrganization.getCountry().getIso3();
                String orgCode = countryAndOrganization.getOrganization();
                countryOrgNames.add(iso3 + "-" + orgCode);
            }
            SummaryMetaData smd = new SummaryMetaData();
            smd.setCountryList(countryOrgNames);
            smd.setSummary("1-MANY-MESSAGES");
            smd.setCountryGroupName(sendingTo.getCountryGroupLabel());
            sendMessages(hitDetailList, messageIds, smd);
        }
    }

    private void sendMessages(Set<HitDetail> hitDetailList, Set<Long> messageIds, SummaryMetaData smd) {

        List<MessageSummaryList> msList = new ArrayList<>();
        Set<Long> pids = hitDetailList.stream().map(HitDetail::getPassengerId).collect(Collectors.toSet());
        Set<Flight> bcEvent = flightService.getFlightByPaxIds(pids);
        for (Flight flight : bcEvent) {
            Set<MessageSummary> apisMessageSummarySet = getApisSummaries(messageIds, pids, flight);
            if (!apisMessageSummarySet.isEmpty()) {
                EventIdentifier ei = SummaryFactory.from("APIS_RULE", flight);
                MessageSummaryList msl = new MessageSummaryList();
                msl.setMessageAction(MessageAction.HIT);
                msl.setEventIdentifier(ei);
                msl.getMessageSummaryList().addAll(apisMessageSummarySet);
                msList.add(msl);
            }
            Set<MessageSummary> pnrMessageSummarySet = getPnrSummaries(messageIds, pids, flight);
            if (!pnrMessageSummarySet.isEmpty()) {
                EventIdentifier ei = SummaryFactory.from("PNR_RULE", flight);
                MessageSummaryList msl = new MessageSummaryList();
                msl.setMessageAction(MessageAction.HIT);
                msl.setEventIdentifier(ei);
                msl.getMessageSummaryList().addAll(pnrMessageSummarySet);
                msList.add(msl);
            }
        }
        for (MessageSummaryList msl : msList) {
            List<MessageSummaryList> messageSummaryLists = batchMessageSummary(msl);
            for (MessageSummaryList list : messageSummaryLists) {
                additionalProcessingMessageSender.sendProcessedMessage(addProcessQueue, list, smd);
            }
        }
    }

    private List<MessageSummaryList> batchMessageSummary(MessageSummaryList messageSummaryList) {
        List<MessageSummaryList> returnList = new ArrayList<>();
        int BATCH_SIZE = 50;
        EventIdentifier ei = messageSummaryList.getEventIdentifier();
        MessageAction ma = messageSummaryList.getMessageAction();
        SummaryMetaData smd = messageSummaryList.getSummaryMetaData();
        List<MessageSummary> msLists = messageSummaryList.getMessageSummaryList();
        List<MessageSummary> filler = new ArrayList<>();
        for (int i = 0; i < msLists.size(); i++ ) {
            if (i != 0 && !(i % BATCH_SIZE == 0)) {
                filler.add(msLists.get(i));
            } else {
                filler.add(msLists.get(i));
                MessageSummaryList msl = new MessageSummaryList();
                msl.setEventIdentifier(ei);
                msl.setMessageAction(ma);
                msl.setMessageSummaryList(filler);
                msl.setSummaryMetaData(smd);
                returnList.add(msl);
                filler = new ArrayList<>();
            }
        }
        if (!filler.isEmpty()) {
            MessageSummaryList msl = new MessageSummaryList();
            msl.setEventIdentifier(ei);
            msl.setMessageAction(ma);
            msl.setMessageSummaryList(filler);
            msl.setSummaryMetaData(smd);
            returnList.add(msl);
        }
        return returnList;
    }

    private Set<MessageSummary> getPnrSummaries(Set<Long> messageIds, Set<Long> pids, Flight flight) {
        Set<Pnr> message = pnrService.pnrMessageWithFlightInfo(pids, messageIds, flight.getId());
        if (message.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> hitPnrIds = message.stream().map(Pnr::getId).collect(Collectors.toSet());

        //Populate Data Maps
        Map<Long, Set<Passenger>> paxPnrMap = pnrService.getPassengersOnPnr(pids, hitPnrIds);
        Map<Long, Set<FrequentFlyer>> ffMap = pnrService.createFrequentFlyersMap(hitPnrIds);
        Map<Long, Set<BookingDetail>> bdMap = pnrService.createBookingDetailMap(hitPnrIds);
        Map<Long, Set<CreditCard>> ccMap = pnrService.createCreditCardMap(hitPnrIds);
        Map<Long, Set<Email>> emailMap = pnrService.createEmailMap(hitPnrIds);
        Map<Long, Set<Phone>> phoneMap = pnrService.createPhoneMap(hitPnrIds);
        Map<Long, Set<Address>> addressMap = pnrService.createAddressMap(hitPnrIds);
        //TODO: Add payment form dwell time and agency to MessageSummary
//      Map<Long, Set<PaymentForm>> paymentFormMap = pnrService.createPaymentFormMap(hitPnrIds);
//      Map<Long, Set<DwellTime>> dwellTimeMap = pnrService.createDwellTime(hitPnrIds);
//      Map<Long, Set<Agency>> agencyMap = pnrService.createTravelAgencyMap(hitPnrIds);

        Set<MessageSummary> messageSummarySet = new HashSet<>();
        for (Pnr pnr : message) {
            Flight messageFlight = pnr.getFlights().iterator().next();
            MessageSummary messageSummary = makeHitMessageSummary(pnr, messageFlight);
            // Set summary to random UUID hash to prevent screening the message out.
            // These messages will always contain hit information and should always be processed.
            messageSummary.setHashCode(UUID.randomUUID().toString());
            Long pnrId = pnr.getId();

            if (ffMap.get(pnrId) != null && !ffMap.get(pnrId).isEmpty()) {
                for (FrequentFlyer ff : ffMap.get(pnrId)) {
                    SummaryFactory.addFrequentFlyer(ff, messageSummary);
                }
            }

            if (bdMap.get(pnrId) != null && !bdMap.get(pnrId).isEmpty()) {
                for (BookingDetail bd : bdMap.get(pnrId)) {
                    SummaryFactory.addBookingDetail(bd, messageSummary);
                }
            }

            if (ccMap.get(pnrId) != null && !ccMap.get(pnrId).isEmpty()) {
                for (CreditCard cc : ccMap.get(pnrId)) {
                    SummaryFactory.addCreditCard(cc, messageSummary);
                }
            }

            if (emailMap.get(pnrId) != null && !emailMap.get(pnrId).isEmpty()) {
                for (Email email : emailMap.get(pnrId)) {
                    SummaryFactory.addEmail(email, messageSummary);
                }
            }

            if (phoneMap.get(pnrId) != null && !phoneMap.get(pnrId).isEmpty()) {
                for (Phone phone : phoneMap.get(pnrId)) {
                    SummaryFactory.addPhone(phone, messageSummary);
                }
            }

            if (addressMap.get(pnrId) != null && !addressMap.get(pnrId).isEmpty()) {
                for (Address address : addressMap.get(pnrId)) {
                    SummaryFactory.addAddress(address, messageSummary);
                }
            }

            for (Passenger p : paxPnrMap.get(pnr.getId())) {
                PassengerSummary passengerSummary = getPassengerSummary(messageFlight.getIdTag(), p);
                messageSummary.getPassengerSummaries().add(passengerSummary);
            }
            messageSummarySet.add(messageSummary);
        }
        return messageSummarySet;
    }

    private Set<MessageSummary> getApisSummaries(Set<Long> messageIds, Set<Long> pids, Flight flight) {
        Set<ApisMessage> message = apisService.apisMessageWithFlightInfo(pids, messageIds, flight.getId());
        if (message.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> hitApisIds = message.stream().map(ApisMessage::getId).collect(Collectors.toSet());
        Map<Long, Set<Passenger>> paxApisMap = apisService.getPassengersOnApis(pids, hitApisIds, flight.getId());
        Set<MessageSummary> messageSummarySet = new HashSet<>();
        for (ApisMessage am : message) {
            Flight messageFlight = am.getFlights().iterator().next();
            MessageSummary messageSummary = makeHitMessageSummary(am, messageFlight);

            // Set summary to random UUID hash to prevent screening the message out.
            // These messages will always contain hit information and should always be processed.            messageSummary.setHashCode(UUID.randomUUID().toString());
            messageSummary.setHashCode(UUID.randomUUID().toString());

            for (Passenger p : paxApisMap.get(am.getId())) {
                PassengerSummary passengerSummary = getPassengerSummary(messageFlight.getIdTag(), p);
                messageSummary.getPassengerSummaries().add(passengerSummary);
            }
            messageSummarySet.add(messageSummary);
        }
        return messageSummarySet;
    }

    private MessageSummary makeHitMessageSummary(Message message, Flight messageFlight) {
        MessageSummary messageSummary = new MessageSummary(message.getHashCode(), messageFlight.getIdTag());
        EventIdentifier ei = SummaryFactory.from(messageFlight, message);
        EdifactMessage em = message.getEdifactMessage();
        if ("PAXLST".equalsIgnoreCase(em.getMessageType()) || "APIS".equalsIgnoreCase(em.getMessageType())) {
            ApisMessage apis = (ApisMessage) message;
            messageSummary.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());
            messageSummary.setSourceMessageVersion("APIS");
            messageSummary.setMessageType("APIS");
            messageSummary.setSourceMessageVersion(apis.getEdifactMessage().getVersion());
            messageSummary.setTransmissionSource(apis.getEdifactMessage().getTransmissionSource());
        } else if ("PNRGOV".equalsIgnoreCase(em.getMessageType()) || "PNR".equalsIgnoreCase(em.getMessageType())) {
            Pnr pnr = (Pnr) message;
            messageSummary.setTransmissionDate(pnr.getEdifactMessage().getTransmissionDate());
            messageSummary.setSourceMessageVersion("PNR");
            messageSummary.setMessageType("PNR");
            messageSummary.setPnrRefNumber(pnr.getRecordLocator());
            messageSummary.setSourceMessageVersion(pnr.getEdifactMessage().getVersion());
            messageSummary.setTransmissionSource(pnr.getEdifactMessage().getTransmissionSource());
        }

        messageSummary.setEventIdentifier(ei);
        messageSummary.setFlightIdTag(messageFlight.getIdTag());
        MessageTravelInformation mti = SummaryFactory.from(messageFlight, messageFlight.getIdTag());
        messageSummary.getMessageTravelInformation().add(mti);
        messageSummary.setRawMessage(LobUtils.convertClobToString(message.getRaw()));
        messageSummary.setRelatedToHit(true);
        messageSummary.setAction(MessageAction.HIT);
        return messageSummary;
    }

    private PassengerSummary getPassengerSummary(String flightIdTag, Passenger p) {
        PassengerSummary passengerSummary = new PassengerSummary();
        PassengerIds idTags = SummaryFactory.from(p.getPassengerIDTag());
        for (Document d : p.getDocuments()) {
            PassengerDocument pdoc = SummaryFactory.from(d, flightIdTag);
            passengerSummary.getPassengerDocumentsList().add(pdoc);
        }
        for (HitDetail hd : p.getHitDetails()) {
            PassengerHit pHit = SummaryFactory.from(hd);
            passengerSummary.getPassengerHits().add(pHit);
        }
        PassengerTrip pt = SummaryFactory.from(p.getPassengerTripDetails());
        PassengerBiographic pb = SummaryFactory.from(p.getPassengerDetails());
        passengerSummary.setGtasId(p.getId());
        passengerSummary.setPassengerTrip(pt);
        passengerSummary.setPassengerBiographic(pb);
        passengerSummary.setPassengerIds(idTags);
        return passengerSummary;
    }
}

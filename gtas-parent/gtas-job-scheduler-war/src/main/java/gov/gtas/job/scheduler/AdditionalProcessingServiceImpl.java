package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.AdditionalProcessingService;
import gov.gtas.model.*;
import gov.gtas.services.ApisService;
import gov.gtas.services.PnrService;
import gov.gtas.services.SummaryFactory;
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

    final
    AdditionalProcessingMessageSender additionalProcessingMessageSender;

    @Value("${additional.processing.queue}")
    private String addProcessQueue;

    public AdditionalProcessingServiceImpl(ApisService apisService, PnrService pnrService, AdditionalProcessingMessageSender additionalProcessingMessageSender) {
        this.apisService = apisService;
        this.pnrService = pnrService;
        this.additionalProcessingMessageSender = additionalProcessingMessageSender;
    }

    @Override
    @Transactional(readOnly = true)
    public void passengersAdditionalHits(Set<Passenger> passengerList, Set<Long> messageIds) {
        if (passengerList.isEmpty() || messageIds.isEmpty()) {
            return;
        }
        EventIdentifier ident = new EventIdentifier();
        ident.setIdentifier("1-MANY MESSAGES");
        ident.setIdentifierArrayList(new ArrayList<>());
        ident.setCountryDestination("1-MANY");
        ident.setCountryOrigin("1-MANY");
        ident.setEventType("RULE_HIT");
        MessageSummaryList messageSummaryList = new MessageSummaryList();
        messageSummaryList.setEventIdentifier(ident);
        messageSummaryList.setMessageAction(MessageAction.HIT);
        Set<Long> pids = passengerList.stream().map(Passenger::getId).collect(Collectors.toSet());
        Set<MessageSummary> apisMessageSummarySet = getApisSummaries(messageIds, pids);
        Set<MessageSummary> pnrMessageSummarySet = getPnrSummaries(messageIds, pids);
        messageSummaryList.getMessageSummaryList().addAll(apisMessageSummarySet);
        messageSummaryList.getMessageSummaryList().addAll(pnrMessageSummarySet);

        List<MessageSummaryList> messageSummaryLists = batchMessageSummary(messageSummaryList);
        for (MessageSummaryList msl : messageSummaryLists) {
            additionalProcessingMessageSender.sendFileContent(addProcessQueue, msl);
        }
    }

    private List<MessageSummaryList> batchMessageSummary(MessageSummaryList messageSummaryList) {
        List<MessageSummaryList> returnList = new ArrayList<>();
        int BATCH_SIZE = 50;
        EventIdentifier ei = messageSummaryList.getEventIdentifier();
        MessageAction ma = messageSummaryList.getMessageAction();
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
                returnList.add(msl);
                filler = new ArrayList<>();
            }
        }
        if (!filler.isEmpty()) {
            MessageSummaryList msl = new MessageSummaryList();
            msl.setEventIdentifier(ei);
            msl.setMessageAction(ma);
            msl.setMessageSummaryList(filler);
            returnList.add(msl);
        }
        return returnList;
    }

    private Set<MessageSummary> getPnrSummaries(Set<Long> messageIds, Set<Long> pids) {
        Set<Pnr> message = pnrService.pnrMessageWithFlightInfo(pids, messageIds);
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

    private Set<MessageSummary> getApisSummaries(Set<Long> messageIds, Set<Long> pids) {
        Set<ApisMessage> message = apisService.apisMessageWithFlightInfo(pids, messageIds);
        if (message.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> hitApisIds = message.stream().map(ApisMessage::getId).collect(Collectors.toSet());
        Map<Long, Set<Passenger>> paxApisMap = apisService.getPassengersOnApis(pids, hitApisIds);
        Set<MessageSummary> messageSummarySet = new HashSet<>();
        for (ApisMessage am : message) {
            Flight messageFlight = am.getFlights().iterator().next();
            MessageSummary messageSummary = makeHitMessageSummary(am, messageFlight);
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
        EventIdentifier ei = SummaryFactory.from(messageFlight);
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
            passengerSummary.getPassengerDerogs().add(pHit);
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

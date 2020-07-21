package gov.gtas.job.scheduler.service;

import com.google.common.collect.Sets;
import gov.gtas.enumtype.RetentionPolicyAction;
import gov.gtas.job.scheduler.*;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.util.LobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
public class DataRetentionServiceImpl implements DataRetentionService {

    private static Logger logger = LoggerFactory.getLogger(DataRetentionServiceImpl.class);

    private final DataRetentionStatusRepository dataRetentionStatusRepository;

    private final DocumentRepository documentRepository;

    private final DocumentRetentionPolicyAuditRepository documentRetentionPolicyAuditRepository;

    private final PassengerDetailRepository passengerDetailRepository;

    private final PassengerDetailFromMessageRepository passengerDetailFromMessageRepository;

    private final PassengerDetailRetentionPolicyAuditRepository passengerDetailRetentionPolicyAuditRepository;

    private final MessageStatusRepository messageStatusRepository;

    private final MessageRepository<Message> messageRepository;

    private final AddressRepository addressRepository;

    private final AddressDataRetentionPolicyAuditRepository addressDataRetentionPolicyAuditRepository;

    private final CreditCardRepository creditCardRepository;

    private final CreditCardDataRetentionPolicyAuditRepository creditCardDataRetentionPolicyAuditRepository;

    private final FrequentFlyerRepository frequentFlyerRepository;

    private final FrequentFlyerDataRetentionPolicyAuditRepository frequentFlyerDataRetentionPolicyAuditRepository;

    private final PhoneRepository phoneRepository;

    private final PhoneDataRetentionPolicyAuditRepository phoneDataRetentionPolicyAuditRepository;

    private final EmailRepository emailRepository;

    private final EmailDataRetentionPolicyAuditRepository emailDataRetentionPolicyAuditRepository;

    private final PnrRepository pnrRepository;

    private final PassengerNoteRepository passengerNoteRepository;

    private final NoteDataRetentionPolicyRepository noteDataRetentionPolicyRepository;


    public DataRetentionServiceImpl(DataRetentionStatusRepository dataRetentionStatusRepository, DocumentRepository documentRepository,
                                    DocumentRetentionPolicyAuditRepository documentRetentionPolicyAuditRepository,
                                    PassengerDetailRepository passengerDetailRepository,
                                    PassengerDetailFromMessageRepository passengerDetailFromMessageRepository, PassengerDetailRetentionPolicyAuditRepository passengerDetailRetentionPolicyAuditRepository,
                                    MessageStatusRepository messageStatusRepository, MessageRepository<Message> messageRepository, AddressRepository addressRepository,
                                    AddressDataRetentionPolicyAuditRepository addressDataRetentionPolicyAuditRepository1,
                                    CreditCardRepository creditCardRepository,
                                    CreditCardDataRetentionPolicyAuditRepository creditCardDataRetentionPolicyAuditRepository,
                                    FrequentFlyerRepository frequentFlyerRepository, FrequentFlyerDataRetentionPolicyAuditRepository frequentFlyerDataRetentionPolicyAuditRepository, PhoneRepository phoneRepository, EmailRepository emailRepository, AddressDataRetentionPolicyAuditRepository addressDataRetentionPolicyAuditRepository, PhoneDataRetentionPolicyAuditRepository phoneDataRetentionPolicyAuditRepository, EmailDataRetentionPolicyAuditRepository emailDataRetentionPolicyAuditRepository, PnrRepository pnrRepository, PassengerNoteRepository passengerNoteRepository, NoteDataRetentionPolicyRepository noteDataRetentionPolicyRepository) {
        this.dataRetentionStatusRepository = dataRetentionStatusRepository;
        this.documentRepository = documentRepository;
        this.documentRetentionPolicyAuditRepository = documentRetentionPolicyAuditRepository;
        this.passengerDetailRepository = passengerDetailRepository;
        this.passengerDetailFromMessageRepository = passengerDetailFromMessageRepository;
        this.passengerDetailRetentionPolicyAuditRepository = passengerDetailRetentionPolicyAuditRepository;
        this.messageStatusRepository = messageStatusRepository;
        this.messageRepository = messageRepository;
        this.addressRepository = addressRepository;
        this.addressDataRetentionPolicyAuditRepository = addressDataRetentionPolicyAuditRepository1;
        this.creditCardRepository = creditCardRepository;
        this.creditCardDataRetentionPolicyAuditRepository = creditCardDataRetentionPolicyAuditRepository;
        this.frequentFlyerRepository = frequentFlyerRepository;
        this.frequentFlyerDataRetentionPolicyAuditRepository = frequentFlyerDataRetentionPolicyAuditRepository;
        this.phoneRepository = phoneRepository;
        this.emailRepository = emailRepository;
        this.phoneDataRetentionPolicyAuditRepository = phoneDataRetentionPolicyAuditRepository;
        this.emailDataRetentionPolicyAuditRepository = emailDataRetentionPolicyAuditRepository;
        this.pnrRepository = pnrRepository;
        this.passengerNoteRepository = passengerNoteRepository;
        this.noteDataRetentionPolicyRepository = noteDataRetentionPolicyRepository;
    }

    @Transactional
    public void saveDataRetentionStatus(Set<DataRetentionStatus> drsSet) {
        if (!drsSet.isEmpty()) {
           dataRetentionStatusRepository.saveAll(drsSet);
        }
    }

    @Transactional
    public void saveMessageStatus(List<MessageStatus> messageStatuses) {
        if (!messageStatuses.isEmpty()) {
            messageStatusRepository.saveAll(messageStatuses);
        }
    }

    @Transactional
    public void deletePnrMessage(NoteDeletionResult noteDeletionResult, PnrFieldsToScrub pnrFieldsToScrub, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatusList) {
        saveNoteUpdates(noteDeletionResult);
        saveApisFields(noteDeletionResult, documentDeletionResult, passengerDeletionResult);
        savePnrFields(documentDeletionResult, passengerDeletionResult, pnrFieldsToScrub);
        processRawPnrMessage(messageStatusList);
        messageStatusList.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.PNR_DATA_DELETED));
        saveMessageStatus(messageStatusList);
    }

    @Transactional
    public void saveNoteUpdates(NoteDeletionResult noteDeletionResult) {
        if (!noteDeletionResult.getPassengerNotes().isEmpty()) {
            passengerNoteRepository.saveAll(noteDeletionResult.getPassengerNotes());
        }
        if (!noteDeletionResult.getAudits().isEmpty()) {
            noteDataRetentionPolicyRepository.saveAll(noteDeletionResult.getAudits());
        }
    }

    @Transactional
    public void deleteApisMessage(NoteDeletionResult noteDeletionResult, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatusList) {
        saveNoteUpdates(noteDeletionResult);
        saveApisFields(noteDeletionResult, documentDeletionResult, passengerDeletionResult);
        messageStatusList.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.APIS_DATA_DELETED));
        processRawApisMessage(messageStatusList);
        saveMessageStatus(messageStatusList);
    }

    @Transactional
    public void processRawPnrMessage(List<MessageStatus> messageStatuses) {
        Set<Long> mIds = messageStatuses.stream().map(MessageStatus::getMessageId).collect(toSet());
        Set<Message> messages = messageRepository.messagesWithNoPnrHits(mIds);
        for (Message m : messages) {
            m.setRaw(LobUtils.createClob("DELETED"));
        }
        messageRepository.saveAll(messages);
    }

    @Transactional
    public void processRawApisMessage(List<MessageStatus> messageStatuses) {
        Set<Long> mIds = messageStatuses.stream().map(MessageStatus::getMessageId).collect(toSet());
        Set<Message> messages = messageRepository.messagesWithNoApisHits(mIds);
        for (Message m : messages) {
            m.setRaw(LobUtils.createClob("DELETED"));
        }
        messageRepository.saveAll(messages);
    }



    public PnrFieldsToScrub scrubPnrs(Set<Long> flightIds, Set<Long> messageIds, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint) {

        PnrFieldsToScrub pnrFieldsToScrub = new PnrFieldsToScrub();
        if (messageIds.isEmpty()) {
            return pnrFieldsToScrub;
        }

//        logger.info("getting pnrs");
        Set<Pnr> pnrs = pnrRepository.getPnrsToScrub(flightIds, messageIds);
 //       logger.info("got pnrs");
        Set<Long> pnrIds = pnrs.stream().map(Pnr::getId).collect(toSet());
        Set<Passenger> retainedPassengers = gtasShareConstraint.getWhitelistedPassengers();

  //      logger.info("doing address");
        //Address
        Set<Address> addressesWithPnr = addressRepository.findAddressesToDelete(flightIds, pnrIds);
        if (!addressesWithPnr.isEmpty()) {
            Set<Address> addressToSave = new HashSet<>();
            Set<AddressDataRetentionPolicyAudit> addressDataRetentionPolicyAudits = new HashSet<>();
            for (Address address : addressesWithPnr) {
                boolean referenceBeforeCutOffDate = address.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
                Set<Passenger> retainedPassengerLinked = Sets.intersection(retainedPassengers, address.getPnrs().stream().flatMap(p -> p.getPassengers().stream()).collect(toSet()));
                boolean noActionMarkedForRetention = !retainedPassengerLinked.isEmpty();
                AddressDataRetentionPolicyAudit addressDataRetentionPolicyAudit = new AddressDataRetentionPolicyAudit();
                addressDataRetentionPolicyAudit.setAddress(address);
                if (noActionMarkedForRetention) {
                    populateNoActionRetainedRecord(addressDataRetentionPolicyAudit);
                } else if (referenceBeforeCutOffDate) {
                    populateNoActionRecord(addressDataRetentionPolicyAudit);
                } else {
                    address.deletePII();
                    addressToSave.add(address);
                    populateActionTaken(addressDataRetentionPolicyAudit);
                }
                addressDataRetentionPolicyAudits.add(addressDataRetentionPolicyAudit);
            }
            pnrFieldsToScrub.setAddresses(addressToSave);
      //      logger.info("deleted addr number: " + addressToSave.size());

            pnrFieldsToScrub.setAddressAudits(addressDataRetentionPolicyAudits);
        }
 //       logger.info("addr done, getting cc now");

        //Credit Cards
          Set<CreditCard> creditCardsFromPnr = creditCardRepository.findCreditCardToDelete(flightIds, pnrIds);
          if (!creditCardsFromPnr.isEmpty()) {
            Set<CreditCard> creditCardsToSave = new HashSet<>();
            Set<CreditCardDataRetentionPolicyAudit> cardDataRetentionPolicyAudits = new HashSet<>();

            for (CreditCard cc : creditCardsFromPnr) {
                Set<Passenger> retainedPassengerLinked = Sets.intersection(retainedPassengers, cc.getPnrs().stream().flatMap(p -> p.getPassengers().stream()).collect(toSet()));
                boolean noActionMarkedForRetention = !retainedPassengerLinked.isEmpty();
                boolean referenceBeforeCutOffDate = cc.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
                CreditCardDataRetentionPolicyAudit creditCardDataRetentionPolicyAudit = new CreditCardDataRetentionPolicyAudit();
            creditCardDataRetentionPolicyAudit.setCreditCard(cc);
                if (noActionMarkedForRetention) {
                    populateNoActionRetainedRecord(creditCardDataRetentionPolicyAudit);
                } else if (referenceBeforeCutOffDate) {
                    populateNoActionRecord(creditCardDataRetentionPolicyAudit);
                } else {
                    cc.deletePII();
                    creditCardsToSave.add(cc);
                    populateActionTaken(creditCardDataRetentionPolicyAudit);
                }
                cardDataRetentionPolicyAudits.add(creditCardDataRetentionPolicyAudit);
            }
            pnrFieldsToScrub.setCreditCard(creditCardsToSave);
//              logger.info("deleted cc number: " + creditCardsToSave.size());

              pnrFieldsToScrub.setCreditCardAudits(cardDataRetentionPolicyAudits);
        }
//            logger.info("cc done getting phones now");
        //Phones
        Set<Phone> phonesFromPnr = phoneRepository.findPhonesFromPnr( flightIds, pnrIds);
        if (!phonesFromPnr.isEmpty()) {
            Set<Phone> phoneToSave = new HashSet<>();
            Set<PhoneDataRetentionPolicyAudit> phoneRetentionPolicyAudits = new HashSet<>();
            for (Phone phone : phonesFromPnr) {
                Set<Passenger> retainedPassengerLinked = Sets.intersection(retainedPassengers, phone.getPnrs().stream().flatMap(p -> p.getPassengers().stream()).collect(toSet()));
                boolean noActionMarkedForRetention = !retainedPassengerLinked.isEmpty();
                boolean referenceBeforeCutOffDate = phone.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
                PhoneDataRetentionPolicyAudit phoneDataRetentionPolicyAudit = new PhoneDataRetentionPolicyAudit();
                phoneDataRetentionPolicyAudit.setPhone(phone);
                if (noActionMarkedForRetention) {
                    populateNoActionRetainedRecord(phoneDataRetentionPolicyAudit);
                } else if (referenceBeforeCutOffDate) {
                    populateNoActionRecord(phoneDataRetentionPolicyAudit);
                } else {
                    phone.deletePII();
                    phoneToSave.add(phone);
                    populateActionTaken(phoneDataRetentionPolicyAudit);
                }
                phoneRetentionPolicyAudits.add(phoneDataRetentionPolicyAudit);
            }
            pnrFieldsToScrub.setPhones(phoneToSave);
//            logger.info("deleted phone number: " + phoneToSave.size());

            pnrFieldsToScrub.setPhoneDataRetentionPolicy(phoneRetentionPolicyAudits);
        }

//        logger.info("phone done doing emails now");

        //Emails
        Set<Email> emailsFromPnr = emailRepository.findEmails( flightIds, pnrIds);
        if (!emailsFromPnr.isEmpty()) {
            Set<Email> emailsToSave = new HashSet<>();
            Set<EmailDataRetentionPolicyAudit> emailRetentionPolciyAudit = new HashSet<>();
            for (Email email : emailsFromPnr) {
                Set<Passenger> retainedPassengerLinked = Sets.intersection(new HashSet<>(retainedPassengers), email.getPnrs().stream().flatMap(p -> p.getPassengers().stream()).collect(toSet()));
                boolean noActionMarkedForRetention = !retainedPassengerLinked.isEmpty();
                boolean referenceBeforeCutOffDate = email.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
                EmailDataRetentionPolicyAudit emailDataRetentionPolicyAudit = new EmailDataRetentionPolicyAudit();
            emailDataRetentionPolicyAudit.setEmail(email);
                if (noActionMarkedForRetention) {
                    populateNoActionRetainedRecord(emailDataRetentionPolicyAudit);
                } else if (referenceBeforeCutOffDate) {
                    populateNoActionRecord(emailDataRetentionPolicyAudit);
                } else {
                    email.deletePII();
                    emailsToSave.add(email);
                    populateActionTaken(emailDataRetentionPolicyAudit);
                }
                emailRetentionPolciyAudit.add(emailDataRetentionPolicyAudit);
            }
            pnrFieldsToScrub.setEmails(emailsToSave);
//            logger.info("deleted email number: " + emailsToSave.size());
            pnrFieldsToScrub.setEmailsDataRetentionPolicy(emailRetentionPolciyAudit);
        }
 //       logger.info("email done about to do ff");

        //Frequent Flyers
       // Set<Long> ffIds = pnrs.stream().flatMap(pnr -> pnr.getFrequentFlyers().stream()).map(FrequentFlyer::getId).collect(toSet());
        Set<FrequentFlyer> ffFromPnr = frequentFlyerRepository.findFrequentFlyers(flightIds, pnrIds);
        if (!ffFromPnr.isEmpty()) {
            Set<FrequentFlyer> frequentFlyers = new HashSet<>();
            Set<FrequentFlyerDataRetentionPolicyAudit> frequentFlyerDataRetentionPolicyAudits = new HashSet<>();
            for (FrequentFlyer ff : ffFromPnr) {
                Set<Passenger> retainedPassengerLinked = Sets.intersection(retainedPassengers, ff.getPnrs().stream().flatMap(p -> p.getPassengers().stream()).collect(toSet()));
                boolean noActionMarkedForRetention = !retainedPassengerLinked.isEmpty();
                boolean referenceBeforeCutOffDate = ff.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
                FrequentFlyerDataRetentionPolicyAudit frequentFlyerDataRetentionPolicyAudit = new FrequentFlyerDataRetentionPolicyAudit();
            frequentFlyerDataRetentionPolicyAudit.setFrequentFlyer(ff);
                if (noActionMarkedForRetention) {
                    populateNoActionRetainedRecord(frequentFlyerDataRetentionPolicyAudit);
                } else if (referenceBeforeCutOffDate) {
                    populateNoActionRecord(frequentFlyerDataRetentionPolicyAudit);
                } else {
                    ff.deletePII();
                    frequentFlyers.add(ff);
                    populateActionTaken(frequentFlyerDataRetentionPolicyAudit);
                }
                frequentFlyerDataRetentionPolicyAudits.add(frequentFlyerDataRetentionPolicyAudit);
            }
        pnrFieldsToScrub.setFrequentFlyers(frequentFlyers);
//            logger.info("deleted ff number: " + frequentFlyers.size());

            pnrFieldsToScrub.setFrequentFlyersDataRetentionPolicy(frequentFlyerDataRetentionPolicyAudits);
        }
 //       logger.info("done ff done it all");

        return pnrFieldsToScrub;

    }

    private void populateActionTaken(BaseEntityRetention ber) {
        ber.setDescription("PII had no references before the cut off date - deleting record.");
        ber.setRetentionPolicyAction(RetentionPolicyAction.PNR_DATA_MARKED_TO_DELETE);
        ber.setCreatedBy("PNR_DELETE");
    }

    private void populateNoActionRecord(BaseEntityRetention ber) {
        ber.setDescription("PII has reference to another message before the cut off date. No action needed.");
        ber.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_PNR);
        ber.setCreatedBy("PNR_DELETE");
    }

    private void populateNoActionRetainedRecord(BaseEntityRetention ber) {
        ber.setDescription("PII is retained due to data settings.");
        ber.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_MARKED_FOR_RETENTION);
        ber.setCreatedBy("PNR_DELETE");
    }

    @Transactional
    public void saveApisFields(NoteDeletionResult noteDeletionResult, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult) {

        if (!passengerDeletionResult.getPassengerDetailFromMessageSet().isEmpty()) {
            passengerDetailFromMessageRepository.saveAll(passengerDeletionResult.getPassengerDetailFromMessageSet());
    }

        if (!passengerDeletionResult.getPassengerDetails().isEmpty()) {
            passengerDetailRepository.saveAll(passengerDeletionResult.getPassengerDetails());
        }

        if (!passengerDeletionResult.getPassengerDetailRetentionPolicyAudits().isEmpty()) {
            passengerDetailRetentionPolicyAuditRepository.saveAll(passengerDeletionResult.getPassengerDetailRetentionPolicyAudits());
        }

        if (!passengerDeletionResult.getDataRetentionStatuses().isEmpty()) {
            dataRetentionStatusRepository.saveAll(passengerDeletionResult.getDataRetentionStatuses());
        }

        if (!documentDeletionResult.getDocuments().isEmpty()) {
            documentRepository.saveAll(documentDeletionResult.getDocuments());
        }

        if (!documentDeletionResult.getDocumentRetentionPolicyAudits().isEmpty()) {
            documentRetentionPolicyAuditRepository.saveAll(documentDeletionResult.getDocumentRetentionPolicyAudits());
        }

    }

    @Override
    @Transactional
    public void savePnrFields(DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, PnrFieldsToScrub pnrFieldsToScrub) {

        if (!pnrFieldsToScrub.getAddresses().isEmpty()) {
            addressRepository.saveAll(pnrFieldsToScrub.getAddresses());
        }

        if (!pnrFieldsToScrub.getAddressAudits().isEmpty()) {
            addressDataRetentionPolicyAuditRepository.saveAll(pnrFieldsToScrub.getAddressAudits());
        }

        if (!pnrFieldsToScrub.getCreditCard().isEmpty()) {
            creditCardRepository.saveAll(pnrFieldsToScrub.getCreditCard());
        }

        if (!pnrFieldsToScrub.getCreditCardAudits().isEmpty()) {
            creditCardDataRetentionPolicyAuditRepository.saveAll(pnrFieldsToScrub.getCreditCardAudits());
        }

        if (!pnrFieldsToScrub.getEmails().isEmpty()) {
            emailRepository.saveAll(pnrFieldsToScrub.getEmails());
        }

        if (!pnrFieldsToScrub.getEmailsDataRetentionPolicy().isEmpty()) {
            emailDataRetentionPolicyAuditRepository.saveAll(pnrFieldsToScrub.getEmailsDataRetentionPolicy());
        }

        if (!pnrFieldsToScrub.getPhones().isEmpty()) {
            phoneRepository.saveAll(pnrFieldsToScrub.getPhones());
        }

        if (!pnrFieldsToScrub.getPhoneDataRetentionPolicy().isEmpty()) {
            phoneDataRetentionPolicyAuditRepository.saveAll(pnrFieldsToScrub.getPhoneDataRetentionPolicy());
        }

        if (!pnrFieldsToScrub.getFrequentFlyers().isEmpty()) {
            frequentFlyerRepository.saveAll(pnrFieldsToScrub.getFrequentFlyers());
        }

        if (!pnrFieldsToScrub.getFrequentFlyersDataRetentionPolicy().isEmpty()) {
            frequentFlyerDataRetentionPolicyAuditRepository.saveAll(pnrFieldsToScrub.getFrequentFlyersDataRetentionPolicy());
        }
    }

    @Override
    public List<MessageStatus> maskApisMessage(List<MessageStatus> statuses) {
        // Separate out APIS from PNR - only run deletion check on APIS messages.
        List<ApisMessage> apisMessages = new ArrayList<>();
        List<Pnr> pnrMessages = new ArrayList<>();
        List<Message> allMessages = statuses.stream().map(MessageStatus::getMessage).collect(Collectors.toList());
        for (Message message : allMessages) {
            if (message instanceof ApisMessage) {
                apisMessages.add((ApisMessage) message);
            } else {
                pnrMessages.add((Pnr) message);
            }
        }
        return null;
    }

    @Override
    public List<MessageStatus> deleteApisMessage(List<MessageStatus> messageStatuses) {
        return null;
    }
}

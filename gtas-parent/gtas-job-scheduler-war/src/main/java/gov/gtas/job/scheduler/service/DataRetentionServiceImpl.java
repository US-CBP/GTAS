package gov.gtas.job.scheduler.service;

import gov.gtas.enumtype.RetentionPolicyAction;
import gov.gtas.job.scheduler.DocumentDeletionResult;
import gov.gtas.job.scheduler.PassengerDeletionResult;
import gov.gtas.job.scheduler.PnrFieldsToScrub;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataRetentionServiceImpl implements DataRetentionService {


    private final DataRetentionStatusRepository dataRetentionStatusRepository;

    private final DocumentRepository documentRepository;

    private final DocumentRetentionPolicyAuditRepository documentRetentionPolicyAuditRepository;

    private final PassengerDetailRepository passengerDetailRepository;

    private final PassengerDetailFromMessageRepository passengerDetailFromMessageRepository;

    private final PassengerDetailRetentionPolicyAuditRepository passengerDetailRetentionPolicyAuditRepository;

    private final MessageStatusRepository messageStatusRepository;

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


    public DataRetentionServiceImpl(DataRetentionStatusRepository dataRetentionStatusRepository, DocumentRepository documentRepository,
                                    DocumentRetentionPolicyAuditRepository documentRetentionPolicyAuditRepository,
                                    PassengerDetailRepository passengerDetailRepository,
                                    PassengerDetailFromMessageRepository passengerDetailFromMessageRepository, PassengerDetailRetentionPolicyAuditRepository passengerDetailRetentionPolicyAuditRepository,
                                    MessageStatusRepository messageStatusRepository, AddressRepository addressRepository,
                                    AddressDataRetentionPolicyAuditRepository addressDataRetentionPolicyAuditRepository1,
                                    CreditCardRepository creditCardRepository,
                                    CreditCardDataRetentionPolicyAuditRepository creditCardDataRetentionPolicyAuditRepository,
                                    FrequentFlyerRepository frequentFlyerRepository, FrequentFlyerDataRetentionPolicyAuditRepository frequentFlyerDataRetentionPolicyAuditRepository, PhoneRepository phoneRepository, EmailRepository emailRepository, AddressDataRetentionPolicyAuditRepository addressDataRetentionPolicyAuditRepository, PhoneDataRetentionPolicyAuditRepository phoneDataRetentionPolicyAuditRepository, EmailDataRetentionPolicyAuditRepository emailDataRetentionPolicyAuditRepository, PnrRepository pnrRepository) {
        this.dataRetentionStatusRepository = dataRetentionStatusRepository;
        this.documentRepository = documentRepository;
        this.documentRetentionPolicyAuditRepository = documentRetentionPolicyAuditRepository;
        this.passengerDetailRepository = passengerDetailRepository;
        this.passengerDetailFromMessageRepository = passengerDetailFromMessageRepository;
        this.passengerDetailRetentionPolicyAuditRepository = passengerDetailRetentionPolicyAuditRepository;
        this.messageStatusRepository = messageStatusRepository;
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
    }

    public void saveDataRetentionStatus(Set<DataRetentionStatus> drsSet) {
        if (!drsSet.isEmpty()) {
            dataRetentionStatusRepository.saveAll(drsSet);
        }
    }

    public void saveMessageStatus(List<MessageStatus> messageStatuses) {
        if (!messageStatuses.isEmpty()) {
            messageStatusRepository.saveAll(messageStatuses);
        }
    }

    @Transactional
    public void deletePnrMessage(PnrFieldsToScrub pnrFieldsToScrub, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatusList) {
        savePnrFields(documentDeletionResult, passengerDeletionResult, pnrFieldsToScrub);
        messageStatusList.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.PNR_DATA_DELETED));
        saveMessageStatus(messageStatusList);
    }

    @Transactional
    public void deleteApisMessage(DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatusList) {
        saveApisFields(documentDeletionResult, passengerDeletionResult);
        messageStatusList.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.APIS_DATA_DELETED));
        saveMessageStatus(messageStatusList);
    }

    @Transactional(readOnly = true)
    public PnrFieldsToScrub scrubPnrs(Set<Long> flightIds, Set<Long> messageIds, Date pnrCutOffDate) {

        PnrFieldsToScrub pnrFieldsToScrub = new PnrFieldsToScrub();
        if (messageIds.isEmpty()) {
            return pnrFieldsToScrub;
        }
        Set<Pnr> pnrs = pnrRepository.getPnrsToScrub(flightIds, messageIds);

        Set<Long> pnrIds = pnrs.stream().map(Pnr::getId).collect(Collectors.toSet());

        //Address
        Set<Long> addressIds = pnrs.stream().flatMap(pnr -> pnr.getAddresses().stream()).map(Address::getId).collect(Collectors.toSet());
        Set<Address> addressesWithPnr = addressRepository.findAddressesToDelete(addressIds, flightIds, pnrIds);
        Set<Address> addressToSave = new HashSet<>();
        Set<AddressDataRetentionPolicyAudit> addressDataRetentionPolicyAudits = new HashSet<>();
        for (Address address : addressesWithPnr) {
            boolean referenceBeforeCutOffDate = address.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
            AddressDataRetentionPolicyAudit addressDataRetentionPolicyAudit = new AddressDataRetentionPolicyAudit();
            addressDataRetentionPolicyAudit.setAddress(address);
            if (referenceBeforeCutOffDate) {
                populateNoActionRecord(addressDataRetentionPolicyAudit);
            } else {
                address.deletePII();
                addressToSave.add(address);
                populateActionTaken(addressDataRetentionPolicyAudit);
            }
            addressDataRetentionPolicyAudits.add(addressDataRetentionPolicyAudit);
        }
        pnrFieldsToScrub.setAddresses(addressToSave);
        pnrFieldsToScrub.setAddressAudits(addressDataRetentionPolicyAudits);


        //Credit Cards
        Set<Long> creditCardIds = pnrs.stream().flatMap(pnr -> pnr.getCreditCards().stream()).map(CreditCard::getId).collect(Collectors.toSet());
        Set<CreditCard> creditCardsFromPnr = creditCardRepository.findCreditCardToDelete(creditCardIds, flightIds, pnrIds);
        Set<CreditCard> creditCardsToSave = new HashSet<>();
        Set<CreditCardDataRetentionPolicyAudit> cardDataRetentionPolicyAudits = new HashSet<>();
        for (CreditCard cc : creditCardsFromPnr) {
            boolean referenceBeforeCutOffDate = cc.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
            CreditCardDataRetentionPolicyAudit creditCardDataRetentionPolicyAudit = new CreditCardDataRetentionPolicyAudit();
            creditCardDataRetentionPolicyAudit.setCreditCard(cc);
            if (referenceBeforeCutOffDate) {
                populateNoActionRecord(creditCardDataRetentionPolicyAudit);
            } else {
                cc.deletePII();
                creditCardsToSave.add(cc);
                populateActionTaken(creditCardDataRetentionPolicyAudit);
            }
            cardDataRetentionPolicyAudits.add(creditCardDataRetentionPolicyAudit);
        }
        pnrFieldsToScrub.setCreditCard(creditCardsToSave);
        pnrFieldsToScrub.setCreditCardAudits(cardDataRetentionPolicyAudits);


        //Phones
        Set<Long> phoneIds = pnrs.stream().flatMap(pnr -> pnr.getPhones().stream()).map(Phone::getId).collect(Collectors.toSet());
        Set<Phone> phonesFromPnr = phoneRepository.findPhonesFromPnr(phoneIds, flightIds, pnrIds);
        Set<Phone> phoneToSave = new HashSet<>();
        Set<PhoneDataRetentionPolicyAudit> phoneRetentionPolicyAudits = new HashSet<>();
        for (Phone phone : phonesFromPnr) {
            boolean referenceBeforeCutOffDate = phone.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
            PhoneDataRetentionPolicyAudit phoneDataRetentionPolicyAudit = new PhoneDataRetentionPolicyAudit();
            phoneDataRetentionPolicyAudit.setPhone(phone);
            if (referenceBeforeCutOffDate) {
                populateNoActionRecord(phoneDataRetentionPolicyAudit);
            } else {
                phone.deletePII();
                phoneToSave.add(phone);
                populateActionTaken(phoneDataRetentionPolicyAudit);
            }
            phoneRetentionPolicyAudits.add(phoneDataRetentionPolicyAudit);
        }
        pnrFieldsToScrub.setPhones(phoneToSave);
        pnrFieldsToScrub.setPhoneDataRetentionPolicy(phoneRetentionPolicyAudits);


        //Emails
        Set<Long> emailIds = pnrs.stream().flatMap(pnr -> pnr.getEmails().stream()).map(Email::getId).collect(Collectors.toSet());
        Set<Email> emailsFromPnr = emailRepository.findEmails(emailIds, flightIds, pnrIds);
        Set<Email> emailsToSave = new HashSet<>();
        Set<EmailDataRetentionPolicyAudit> emailRetentionPolciyAudit = new HashSet<>();
        for (Email email : emailsFromPnr) {
            boolean referenceBeforeCutOffDate = email.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
            EmailDataRetentionPolicyAudit emailDataRetentionPolicyAudit = new EmailDataRetentionPolicyAudit();
            emailDataRetentionPolicyAudit.setEmail(email);
            if (referenceBeforeCutOffDate) {
                populateNoActionRecord(emailDataRetentionPolicyAudit);
            } else {
                email.deletePII();
                emailsToSave.add(email);
                populateActionTaken(emailDataRetentionPolicyAudit);
            }
            emailRetentionPolciyAudit.add(emailDataRetentionPolicyAudit);
        }
        pnrFieldsToScrub.setEmails(emailsToSave);
        pnrFieldsToScrub.setEmailsDataRetentionPolicy(emailRetentionPolciyAudit);


        //Frequent Flyers
        Set<Long> ffIds = pnrs.stream().flatMap(pnr -> pnr.getFrequentFlyers().stream()).map(FrequentFlyer::getId).collect(Collectors.toSet());
        Set<FrequentFlyer> ffFromPnr = frequentFlyerRepository.findFrequentFlyers(ffIds, flightIds, pnrIds);
        Set<FrequentFlyer> frequentFlyers = new HashSet<>();
        Set<FrequentFlyerDataRetentionPolicyAudit> frequentFlyerDataRetentionPolicyAudits = new HashSet<>();
        for (FrequentFlyer ff : ffFromPnr) {
            boolean referenceBeforeCutOffDate = ff.getPnrs().stream().anyMatch(p -> p.getCreateDate().after(pnrCutOffDate));
            FrequentFlyerDataRetentionPolicyAudit frequentFlyerDataRetentionPolicyAudit = new FrequentFlyerDataRetentionPolicyAudit();
            frequentFlyerDataRetentionPolicyAudit.setFrequentFlyer(ff);
            if (referenceBeforeCutOffDate) {
                populateNoActionRecord(frequentFlyerDataRetentionPolicyAudit);
            } else {
                ff.deletePII();
                frequentFlyers.add(ff);
                populateActionTaken(frequentFlyerDataRetentionPolicyAudit);
            }
            frequentFlyerDataRetentionPolicyAudits.add(frequentFlyerDataRetentionPolicyAudit);
        }

        pnrFieldsToScrub.setFrequentFlyers(frequentFlyers);
        pnrFieldsToScrub.setFrequentFlyersDataRetentionPolicy(frequentFlyerDataRetentionPolicyAudits);

        return pnrFieldsToScrub;

    }

    private void populateActionTaken(BaseEntityRetention ber) {
        ber.setDescription("PII had no references before the cut off date - deleting record.");
        ber.setRetentionPolicyAction(RetentionPolicyAction.PNR_DATA_MARKED_TO_DELETE);
        ber.setCreatedBy("PNR_DELETE");
    }

    private void  populateNoActionRecord(BaseEntityRetention ber) {
        ber.setDescription("PII has reference to another message before the cut off date. No action needed.");
        ber.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_PNR);
        ber.setCreatedBy("PNR_DELETE");
    }

    public void saveApisFields(DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult) {

        if (!passengerDeletionResult.getPassengerDetailFromMessageSet().isEmpty()) {
            passengerDetailFromMessageRepository.saveAll(passengerDeletionResult.getPassengerDetailFromMessageSet());
        }

        if (!passengerDeletionResult.getPassengerDetails().isEmpty()) {
            passengerDetailRepository.saveAll(passengerDeletionResult.getPassengerDetails());
        }

        if (!passengerDeletionResult.getPassengerDetailRetentionPolicyAudits().isEmpty()) {
            passengerDetailRetentionPolicyAuditRepository.saveAll(passengerDeletionResult.getPassengerDetailRetentionPolicyAudits());
        }

        if (!documentDeletionResult.getDocuments().isEmpty()) {
            documentRepository.saveAll(documentDeletionResult.getDocuments());
        }
        if (!documentDeletionResult.getDataRetentionStatuses().isEmpty()) {
            saveDataRetentionStatus(documentDeletionResult.getDataRetentionStatuses());
        }
        if (!documentDeletionResult.getDocumentRetentionPolicyAudits().isEmpty()) {
            documentRetentionPolicyAuditRepository.saveAll(documentDeletionResult.getDocumentRetentionPolicyAudits());
        }
    }

    @Override
    @Transactional
    public void savePnrFields(DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, PnrFieldsToScrub pnrFieldsToScrub) {
            saveApisFields(documentDeletionResult, passengerDeletionResult);

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

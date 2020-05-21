package gov.gtas.job.scheduler;

import gov.gtas.enumtype.MessageType;
import gov.gtas.enumtype.RetentionPolicyAction;
import gov.gtas.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PassengerDeletionResult {

    private static Logger logger = LoggerFactory.getLogger(PassengerDeletionResult.class);
    private Set<DataRetentionStatus> dataRetentionStatuses = new HashSet<>();
    private Set<PassengerDetails> passengerDetails = new HashSet<>();
    private Set<PassengerDetailRetentionPolicyAudit> passengerDetailRetentionPolicyAudits = new HashSet<>();
    private Set<PassengerDetailFromMessage> passengerDetailFromMessageSet = new HashSet<>();

    public static PassengerDeletionResult processApisPassengers(Set<Passenger> passengers, Date apisCutOffDate, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint) {
        PassengerDeletionResult passengerDeletionResult = new PassengerDeletionResult();
        Set<Long> ignoredPassengers = gtasShareConstraint.getWhiteListedPassenerIds();
        for (Passenger p : passengers) {
            RelevantMessageChecker relevantMessageChecker = new RelevantMessageChecker(apisCutOffDate, pnrCutOffDate, p).invoke();
            boolean relevantAPIS = relevantMessageChecker.isRelevantAPIS();
            boolean relevantPnr = relevantMessageChecker.isRelevantPnr();
            PassengerDetailRetentionPolicyAudit pdrpa = new PassengerDetailRetentionPolicyAudit();
            pdrpa.setPassenger(p);
            pdrpa.setCreatedAt(new Date());
            pdrpa.setCreatedBy("APIS_DELETE");
            if (ignoredPassengers.contains(p.getId())) {
                logger.debug("Passenger marked for retention");
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_MARKED_FOR_RETENTION);
                pdrpa.setDescription("Passenger marked for retention. No action taken!");
            } else if (relevantAPIS) {
                logger.debug("Not performing passenger data deletion, another APIS message under the cut off date references this passenger.");
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_APIS);
                pdrpa.setDescription("Another APIS message under the cut off date references this passenger. No Deletion.");
                passengerDeletionResult.getPassengerDetailFromMessageSet().addAll(getInvalidApisMessageDetails(p, apisCutOffDate));
            } else {
                p.getDataRetentionStatus().setDeletedAPIS(true);
                scrubApisPassengerDetail(p, pnrCutOffDate);
                passengerDeletionResult.getPassengerDetails().add(p.getPassengerDetails());
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.APIS_DATA_MARKED_TO_DELETE);
                if (relevantPnr) {
                    logger.debug("Orphan Passenger Detail - performing data deletion or swapping to pnr only details!");
                    pdrpa.setDescription("Passenger Details Relating to APIS replaced by most recent PNR");
                } else {
                    logger.debug("Orphan Passenger Detail - performing data deletion!");
                    pdrpa.setDescription("Passenger Details Relating to APIS deleted.");
                }
                passengerDeletionResult.getPassengerDetailFromMessageSet().addAll(getInvalidApisMessageDetails(p, apisCutOffDate));
            }
            passengerDeletionResult.getPassengerDetailRetentionPolicyAudits().add(pdrpa);
        }
        return passengerDeletionResult;
    }

    public static PassengerDeletionResult processPnrPassengers(Set<Passenger> passengers, Date apisCutOffDate, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint) {
        PassengerDeletionResult passengerDeletionResult = new PassengerDeletionResult();
        Set<Long> ignoredPassengers = gtasShareConstraint.getWhiteListedPassenerIds();
        for (Passenger p : passengers) {
            RelevantMessageChecker relevantMessageChecker = new RelevantMessageChecker(apisCutOffDate, pnrCutOffDate, p).invoke();
            boolean relevantAPIS = relevantMessageChecker.isRelevantAPIS();
            boolean relevantPnr = relevantMessageChecker.isRelevantPnr();
            PassengerDetailRetentionPolicyAudit pdrpa = new PassengerDetailRetentionPolicyAudit();
            pdrpa.setPassenger(p);
            pdrpa.setCreatedAt(new Date());
            pdrpa.setCreatedBy("PNR_DELETE");
            if (ignoredPassengers.contains(p.getId())) {
                logger.debug("Passenger marked for retention");
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_MARKED_FOR_RETENTION);
                pdrpa.setDescription("Passenger marked for retention. No action taken!");
            } else if (relevantPnr) {
                logger.debug("Not performing passenger data deletion, another PNR message under the cut off date references this passenger.");
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_PNR);
                pdrpa.setDescription("Another PNR message under the cut off date references this passenger. No Deletion.");
                passengerDeletionResult.getPassengerDetailFromMessageSet().addAll(getInvalidPnrMessageDetails(p, pnrCutOffDate));
            } else {
                p.getDataRetentionStatus().setDeletedPNR(true);
                scrubPnrPassengerDetail(p, apisCutOffDate);
                passengerDeletionResult.getPassengerDetails().add(p.getPassengerDetails());
                pdrpa.setRetentionPolicyAction(RetentionPolicyAction.PNR_DATA_MARKED_TO_DELETE);
                if (relevantAPIS) {
                    logger.debug("Orphan Passenger Detail - performing data deletion or swapping to pnr only details!");
                    pdrpa.setDescription("Passenger Details Relating to PNR replaced by most recent APIS");
                } else {
                    logger.debug("Orphan Passenger Detail - performing data deletion!");
                    pdrpa.setDescription("Passenger Details Relating to PNR deleted.");
                }
                passengerDeletionResult.getPassengerDetailFromMessageSet().addAll(getInvalidPnrMessageDetails(p, pnrCutOffDate));
            }
            passengerDeletionResult.getPassengerDetailRetentionPolicyAudits().add(pdrpa);
        }
        return passengerDeletionResult;
    }

    private static Set<PassengerDetailFromMessage> getInvalidPnrMessageDetails(Passenger p, Date pnrCutOffDate) {
        Set<PassengerDetailFromMessage> invalidPdfmSet = p.getPassengerDetailFromMessages().stream()
                .filter(m -> m.getMessageType() == MessageType.PNR)
                .filter(m -> m.getCreatedAt().before(pnrCutOffDate))
                .filter(m -> !m.getDeleted())
                .collect(Collectors.toSet());
        for (PassengerDetailFromMessage oldDetails : invalidPdfmSet) {
            scrubPdfm(oldDetails);
        }
        return invalidPdfmSet;
    }

    private static Set<PassengerDetailFromMessage> getInvalidApisMessageDetails(Passenger p, Date apisCutOffDate) {
        Set<PassengerDetailFromMessage> invalidPdfmSet = p.getPassengerDetailFromMessages().stream()
                .filter(m -> m.getMessageType() == MessageType.APIS)
                .filter(m -> m.getCreatedAt().before(apisCutOffDate))
                .filter(m -> !m.getDeleted())
                .collect(Collectors.toSet());
        for (PassengerDetailFromMessage oldDetails : invalidPdfmSet) {
            scrubPdfm(oldDetails);
        }
        return invalidPdfmSet;
    }

    private static void scrubPdfm(PassengerDetailFromMessage oldDetails) {
        oldDetails.setNationality(null);
        oldDetails.setMiddleName(null);
        oldDetails.setFirstName("DELETED");
        oldDetails.setLastName("DELETED");
        oldDetails.setResidencyCountry(null);
        oldDetails.setDob(null);
        oldDetails.setAge(null);
        oldDetails.setGender(null);
        oldDetails.setSuffix(null);
        oldDetails.setTitle(null);
    }

    private static void scrubPnrPassengerDetail(Passenger p, Date apisCutOffDate) {
        PassengerDetailFromMessage pdfm = p.getPassengerDetailFromMessages().stream()
                .filter(m -> m.getMessageType() == MessageType.APIS)
                .filter(m -> m.getCreatedAt().after(apisCutOffDate))
                .max(Comparator.comparing(BaseEntityAudit::getCreatedAt))
                .orElseGet(PassengerDeletionResult::getDefault);
        replacePassengerDetailInformation(p.getPassengerDetails(), pdfm);
    }

    private static void scrubApisPassengerDetail(Passenger p, Date pnrCutOffDate) {
        PassengerDetailFromMessage pdfm = p.getPassengerDetailFromMessages().stream()
                .filter(m -> m.getMessageType() == MessageType.PNR)
                .filter(m -> m.getCreatedAt().after(pnrCutOffDate))
                .max(Comparator.comparing(BaseEntityAudit::getCreatedAt))
                .orElseGet(PassengerDeletionResult::getDefault);
        replacePassengerDetailInformation(p.getPassengerDetails(), pdfm);
    }

    private static void replacePassengerDetailInformation(PassengerDetails pd, PassengerDetailFromMessage pdfmReplacement) {
        pd.setAge(pdfmReplacement.getAge());
        pd.setDob(pdfmReplacement.getDob());
        pd.setDeleted(pd.getDeleted());
        pd.setFirstName(pdfmReplacement.getFirstName());
        pd.setLastName(pdfmReplacement.getLastName());
        pd.setGender(pdfmReplacement.getGender());
        pd.setMiddleName(pdfmReplacement.getMiddleName());
        pd.setResidencyCountry(pdfmReplacement.getResidencyCountry());
        pd.setNationality(pdfmReplacement.getNationality());
        pd.setSuffix(pdfmReplacement.getSuffix());
        pd.setTitle(pdfmReplacement.getTitle());
    }

    private static PassengerDetailFromMessage getDefault() {
        PassengerDetailFromMessage pdfm = new PassengerDetailFromMessage();
        pdfm.setDeleted(true);
        pdfm.setFirstName("DELETED");
        pdfm.setLastName("DELETED");
        pdfm.setMiddleName(null);
        return pdfm;
    }

    public Set<DataRetentionStatus> getDataRetentionStatuses() {
        return dataRetentionStatuses;
    }

    public void setDataRetentionStatuses(Set<DataRetentionStatus> dataRetentionStatuses) {
        this.dataRetentionStatuses = dataRetentionStatuses;
    }

    public Set<PassengerDetails> getPassengerDetails() {
        return passengerDetails;
    }

    public void setPassengerDetails(Set<PassengerDetails> passengerDetails) {
        this.passengerDetails = passengerDetails;
    }

    public Set<PassengerDetailFromMessage> getPassengerDetailFromMessageSet() {
        return passengerDetailFromMessageSet;
    }

    public Set<PassengerDetailRetentionPolicyAudit> getPassengerDetailRetentionPolicyAudits() {
        return passengerDetailRetentionPolicyAudits;
    }

    public void setPassengerDetailRetentionPolicyAudits(Set<PassengerDetailRetentionPolicyAudit> passengerDetailRetentionPolicyAudits) {
        this.passengerDetailRetentionPolicyAudits = passengerDetailRetentionPolicyAudits;
    }


    private static class RelevantMessageChecker {
        private Date apisCutOffDate;
        private Date pnrCutOffDate;
        private Passenger p;
        private boolean relevantAPIS;
        private boolean relevantPnr;

        public RelevantMessageChecker(Date apisCutOffDate, Date pnrCutOffDate, Passenger p) {
            this.apisCutOffDate = apisCutOffDate;
            this.pnrCutOffDate = pnrCutOffDate;
            this.p = p;
        }

        public boolean isRelevantAPIS() {
            return relevantAPIS;
        }

        public boolean isRelevantPnr() {
            return relevantPnr;
        }

        public RelevantMessageChecker invoke() {
            relevantAPIS = false;
            relevantPnr = false;
            for (ApisMessage apis : p.getApisMessage()) {
                if (apis.getCreateDate().after(apisCutOffDate)) {
                    relevantAPIS = true;
                    break;
                }
            }
            for (Pnr pnr : p.getPnrs()) {
                if (pnr.getCreateDate().after(pnrCutOffDate)) {
                    relevantPnr = true;
                    break;
                }
            }
            return this;
        }
    }
}

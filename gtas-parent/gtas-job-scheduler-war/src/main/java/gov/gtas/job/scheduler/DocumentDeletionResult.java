package gov.gtas.job.scheduler;

import gov.gtas.enumtype.RetentionPolicyAction;
import gov.gtas.model.*;
import gov.gtas.rule.listener.GtasAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DocumentDeletionResult {

    private static Logger logger = LoggerFactory.getLogger(DocumentDeletionResult.class);
    private Set<DataRetentionStatus> dataRetentionStatuses = new HashSet<>();
    private Set<Document> documents = new HashSet<>();
    private Set<DocumentRetentionPolicyAudit> documentRetentionPolicyAudits = new HashSet<>();


    public static DocumentDeletionResult processApisPassengers(Set<Document> documents, Date apisCutOffDate, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint) {
        DocumentDeletionResult documentDeletionResult = new DocumentDeletionResult();
        for (Document pd : documents) {
            RelevantDocumentChecker relevantDocumentChecker = new RelevantDocumentChecker(apisCutOffDate, pnrCutOffDate, pd).invoke();
            boolean apisCutOffDateReached = relevantDocumentChecker.isApisCutOffDateReached();
            boolean relevantMessage = relevantDocumentChecker.isRelevantMessage();
            Set<Long> ignoredPassengers = gtasShareConstraint.getWhiteListedPassenerIds();
            DocumentRetentionPolicyAudit drpa = new DocumentRetentionPolicyAudit();
            drpa.setCreatedAt(new Date());
            drpa.setCreatedBy("APIS_DELETE");
            drpa.setDocument(pd);
            if (ignoredPassengers.contains(pd.getPassengerId())) {
                logger.debug("Passenger marked for retention");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_MARKED_FOR_RETENTION);
                drpa.setDescription("Passenger marked for retention");
            } else if (relevantMessage) {
                logger.debug("Not performing data deletion, another message under the cut off date references this document.");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_MESSAGE);
                drpa.setDescription("Another message under the cut off date references this document. No Deletion.");
            } else if (apisCutOffDateReached) {
                logger.debug("Orphan document - performing data deletion!");
                pd.setDocumentNumber("DELETED_" + pd.getDocumentType() + UUID.randomUUID().toString());
                documentDeletionResult.getDocuments().add(pd);
                drpa.setRetentionPolicyAction(RetentionPolicyAction.APIS_DATA_MARKED_TO_DELETE);
                drpa.setDescription("Document number has been removed.");
            } else {
                logger.debug("This document was not created by an APIS message. No actions will be performed.");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_NO_APIS);
                drpa.setDescription("Document number had no association to APIS message and was ignored.");
            }
            documentDeletionResult.getDocumentRetentionPolicyAudits().add(drpa);
        }
        return documentDeletionResult;
    }

    public static DocumentDeletionResult processPnrPassengers(Set<Document> documents, Date apisCutOffDate, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint) {
        DocumentDeletionResult documentDeletionResult = new DocumentDeletionResult();
        Set<Long> ignoredPassengers = gtasShareConstraint.getWhiteListedPassenerIds();
        for (Document pd : documents) {
            RelevantDocumentChecker relevantDocumentChecker = new RelevantDocumentChecker(apisCutOffDate, pnrCutOffDate, pd).invoke();
            boolean pnrCutOffDateReached = relevantDocumentChecker.isPnrCutOffDateReached();
            boolean relevantMessage = relevantDocumentChecker.isRelevantMessage();
            DocumentRetentionPolicyAudit drpa = new DocumentRetentionPolicyAudit();
            drpa.setCreatedAt(new Date());
            drpa.setCreatedBy("PNR_DELETE");
            drpa.setDocument(pd);
            if (ignoredPassengers.contains(pd.getPassengerId())) {
                logger.debug("Passenger marked for retention");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_MARKED_FOR_RETENTION);
                drpa.setDescription("Passenger marked for retention");
            } else if (relevantMessage) {
                logger.debug("Not performing data deletion, another message under the cut off date references this document.");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_RELEVANT_MESSAGE);
                drpa.setDescription("Another message under the cut off date references this document. No Deletion.");
            } else if (pnrCutOffDateReached) {
                pd.setDocumentNumber("DELETED_" + pd.getDocumentType()+ UUID.randomUUID().toString());
                documentDeletionResult.getDocuments().add(pd);
                logger.debug("Orphan document - performing data deletion!");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.PNR_DATA_MARKED_TO_DELETE);
                drpa.setDescription("Document number has been removed.");
            } else {
                logger.debug("This document was not created by an PNR message. No actions will be performed.");
                drpa.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION_NO_PNR);
                drpa.setDescription("Document number had no association to PNR message and was ignored.");
            }
            documentDeletionResult.getDocumentRetentionPolicyAudits().add(drpa);
        }

        return documentDeletionResult;
    }


    public Set<DataRetentionStatus> getDataRetentionStatuses() {
        return dataRetentionStatuses;
    }

    public void setDataRetentionStatuses(Set<DataRetentionStatus> dataRetentionStatuses) {
        this.dataRetentionStatuses = dataRetentionStatuses;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public Set<DocumentRetentionPolicyAudit> getDocumentRetentionPolicyAudits() {
        return documentRetentionPolicyAudits;
    }

    public void setDocumentRetentionPolicyAudits(Set<DocumentRetentionPolicyAudit> documentRetentionPolicyAudits) {
        this.documentRetentionPolicyAudits = documentRetentionPolicyAudits;
    }

    private static class RelevantDocumentChecker {
        private Date apisCutOffDate;
        private Date pnrCutOffDate;
        private Document pd;
        private boolean apisCutOffDateReached;
        private boolean pnrCutOffDateReached;
        private boolean relevantMessage;

        public RelevantDocumentChecker(Date apisCutOffDate, Date pnrCutOffDate, Document pd) {
            this.apisCutOffDate = apisCutOffDate;
            this.pnrCutOffDate = pnrCutOffDate;
            this.pd = pd;
        }

        public boolean isApisCutOffDateReached() {
            return apisCutOffDateReached;
        }

        public boolean isRelevantMessage() {
            return relevantMessage;
        }


        public boolean isPnrCutOffDateReached() {
            return pnrCutOffDateReached;
        }

        public RelevantDocumentChecker invoke() {
            apisCutOffDateReached = false;
            relevantMessage = false;
            pnrCutOffDateReached = false;
            for (Message m : pd.getMessages()) {
                if (m instanceof ApisMessage && m.getCreateDate().before(apisCutOffDate)) {
                    apisCutOffDateReached = true;
                } else if (m instanceof Pnr && m.getCreateDate().before(pnrCutOffDate)) {
                    pnrCutOffDateReached = true;
                } else if (m.getCreateDate().after(apisCutOffDate)) {
                    relevantMessage = true;
                    break;
                }
            }
            return this;
        }


    }
}

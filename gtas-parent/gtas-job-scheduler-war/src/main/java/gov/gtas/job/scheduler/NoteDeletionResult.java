package gov.gtas.job.scheduler;

import gov.gtas.enumtype.RetentionPolicyAction;
import gov.gtas.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NoteDeletionResult {
    private static Logger logger = LoggerFactory.getLogger(NoteDeletionResult.class);
    private Set<PassengerNote> passengerNotes = new HashSet<>();
    private Set<NoteDataRetentionPolicyAudit> audits  = new HashSet<>();

    public static NoteDeletionResult processPassengers(Set<Passenger> passengers, Date apisCutOffDate, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint, NoteType deletedNoteType) {

        NoteDeletionResult noteDeletionResult = new NoteDeletionResult();
        for (Passenger p : passengers) {
            RelevantMessageChecker relevantMessageChecker = new RelevantMessageChecker(apisCutOffDate, pnrCutOffDate, p).invoke();
            boolean relevantAPIS = relevantMessageChecker.isRelevantAPIS();
            boolean relevantPnr = relevantMessageChecker.isRelevantPnr();
            for (PassengerNote note: p.getNotes()) {
                NoteDataRetentionPolicyAudit noteDataRetentionPolicyAudit = new NoteDataRetentionPolicyAudit();
                noteDataRetentionPolicyAudit.setNote(note);
                if (!relevantAPIS && !relevantPnr && !gtasShareConstraint.getWhitelistedPassengers().contains(p)) {
                    note.setPlainTextComment("DELETED");
                    note.setRtfComment("DELETED");
                    noteDataRetentionPolicyAudit.setDescription("No relevant messages - Note deleted");
                    noteDataRetentionPolicyAudit.setRetentionPolicyAction(RetentionPolicyAction.DELETED);
                    noteDeletionResult.getPassengerNotes().add(note);
                    note.getNoteType().clear();
                    note.getNoteType().add(deletedNoteType);
                } else {
                    noteDataRetentionPolicyAudit.setDescription("Relevant PNR or APIS or Retention Action - No Action Taken");
                    noteDataRetentionPolicyAudit.setRetentionPolicyAction(RetentionPolicyAction.NO_ACTION);
                }
                noteDeletionResult.getAudits().add(noteDataRetentionPolicyAudit);
            }
        }
        return noteDeletionResult;

    }

    public Set<PassengerNote> getPassengerNotes() {
        return passengerNotes;
    }

    public void setPassengerNotes(Set<PassengerNote> passengerNotes) {
        this.passengerNotes = passengerNotes;
    }

    public Set<NoteDataRetentionPolicyAudit> getAudits() {
        return audits;
    }

    public void setAudits(Set<NoteDataRetentionPolicyAudit> audits) {
        this.audits = audits;
    }
}

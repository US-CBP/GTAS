package gov.gtas.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notes_data_retention_policy_audit")
public class NoteDataRetentionPolicyAudit extends BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "note_data_retention_id", referencedColumnName = "id")
    private Note note;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

}

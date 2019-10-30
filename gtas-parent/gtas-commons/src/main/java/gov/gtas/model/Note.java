/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "notes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Note extends BaseEntityAudit {

    @Column(name = "note_plain_text", length = 10000, nullable = false)
    private String plainTextNote;

    @Column(name = "note_rtf_text", length = 10000, nullable = false)
    private String rtfNote;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "note_type_join", joinColumns = @JoinColumn(name = "nt_id"), inverseJoinColumns = @JoinColumn(name = "n_id"))
    private Set<NoteType> noteType = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "note_attachment",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    private Set<Attachment> attachments = new HashSet<>();

    public String getPlainTextNote() {
        return plainTextNote;
    }

    public void setPlainTextComment(String plainTextNote) {
        this.plainTextNote = plainTextNote;
    }

    public String getRtfNote() {
        return rtfNote;
    }

    public void setRtfComment(String rtfNote) {
        this.rtfNote = rtfNote;
    }

	public Set<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

    public Set<NoteType> getNoteType() {
        return noteType;
    }

    public void setNoteType(Set<NoteType> noteType) {
        this.noteType = noteType;
    }
}

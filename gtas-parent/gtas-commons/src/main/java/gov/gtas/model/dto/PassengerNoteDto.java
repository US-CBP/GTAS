/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model.dto;

import gov.gtas.model.NoteType;

import java.util.HashSet;
import java.util.Set;

public class PassengerNoteDto {

    private String plainTextNote;

    private String rtfNote;

    private String noteType;

    private Long passengerId;

    private String user;
    
    private String createdBy;
    
    private String createdAt;

    transient private Set<NoteType> noteTypeSet = new HashSet<>();

    public PassengerNoteDto() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public String getPlainTextNote() {
        return plainTextNote;
    }

    public void setPlainTextNote(String plainTextNote) {
        this.plainTextNote = plainTextNote;
    }

    public String getRtfNote() {
        return rtfNote;
    }

    public void setRtfNote(String rtfNote) {
        this.rtfNote = rtfNote;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

    public Set<NoteType> getNoteTypeSet() {
        return noteTypeSet;
    }

    public void setNoteTypeSet(Set<NoteType> noteTypeSet) {
        this.noteTypeSet = noteTypeSet;
    }
}

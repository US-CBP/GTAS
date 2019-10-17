/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model.dto;

import gov.gtas.enumtype.NoteType;

public class PassengerNoteDto {

    private String plainTextNote;

    private String rtfNote;

    private NoteType noteType;

    private Long passengerId;

    private String user;

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

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
        this.noteType = noteType;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
}

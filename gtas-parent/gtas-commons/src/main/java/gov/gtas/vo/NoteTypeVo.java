/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo;

import gov.gtas.model.NoteType;

public class NoteTypeVo {

    private Long id;

    private String noteType;

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public static NoteTypeVo from(NoteType noteType) {
        NoteTypeVo noteTypeVo = new NoteTypeVo();
        noteTypeVo.setId(noteType.getId());
        noteTypeVo.setNoteType(noteType.getType());
        return noteTypeVo;
    }

}

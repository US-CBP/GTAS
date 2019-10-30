/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import org.hibernate.annotations.NaturalId;

import gov.gtas.vo.NoteTypeVo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "note_type")
public class NoteType extends BaseEntityAudit {

    public NoteType(){}

    @ManyToMany(mappedBy = "noteType")
    private Set<Note> noteSet = new HashSet<>();

    @NaturalId(mutable = true)
    @Column(name = "nt_type", unique = true)
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Note> getNoteSet() {
        return noteSet;
    }

    public void setNoteSet(Set<Note> noteSet) {
        this.noteSet = noteSet;
    }
    
    public static NoteType from(NoteTypeVo noteTypeVo) {
        NoteType noteType = new NoteType();
        noteType.setType(noteTypeVo.getNoteType());
        return noteType;
    }
}

/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.NoteType;

import java.io.IOException;
import java.util.Objects;

public class NoteTypeVo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("noteType")
    private String noteType;

    @JsonProperty("archived")
    private boolean archived;

    public NoteTypeVo(){}

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

    public boolean isArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }


    public static NoteTypeVo from(NoteType noteType) {
        NoteTypeVo noteTypeVo = new NoteTypeVo();
        noteTypeVo.setId(noteType.getId());
        noteTypeVo.setNoteType(noteType.getType());
        noteTypeVo.setArchived(noteType.isArchived());
        return noteTypeVo;
    }

    @JsonCreator
    public static NoteTypeVo create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NoteTypeVo noteTypeVo = null;
        noteTypeVo = mapper.readValue(jsonString, NoteTypeVo.class);
        return noteTypeVo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteTypeVo that = (NoteTypeVo) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getNoteType(), that.getNoteType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNoteType());
    }
}

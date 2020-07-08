package gov.gtas.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.gtas.model.NoteType;
import gov.gtas.model.PIIObject;
import gov.gtas.model.PassengerNote;

public class NoteVo implements PIIObject {
	private Long id;
	private Long passengerId;
	private String plainTextNote;
	private String rtfNote;
	@JsonProperty("noteType")
	private Set<NoteTypeVo> noteTypeVoSet = new HashSet<>();
	private String createdBy;
	private Date createdAt;
	private transient Set<NoteType> noteTypeSet = new HashSet<>();

	public static NoteVo from(PassengerNote pNote) {
		NoteVo noteVo = new NoteVo();
		noteVo.setId(pNote.getId());
		for (NoteType noteType : pNote.getNoteType()) {
			NoteTypeVo noteTypeVo = NoteTypeVo.from(noteType);
			noteVo.getNoteTypeVoSet().add(noteTypeVo);
		}
		noteVo.setPlainTextNote(pNote.getPlainTextNote());
		noteVo.setRtfNote(pNote.getRtfNote());
		noteVo.setCreatedBy(pNote.getCreatedBy());
		noteVo.setCreatedAt(pNote.getCreatedAt());
		return noteVo;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Set<NoteTypeVo> getNoteTypeVoSet() {
		return noteTypeVoSet;
	}

	public void setNoteTypeVoSet(Set<NoteTypeVo> noteTypeVoSet) {
		this.noteTypeVoSet = noteTypeVoSet;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public Set<NoteType> getNoteTypeSet() {
		return noteTypeSet;
	}

	public void setNoteTypeSet(Set<NoteType> noteTypeSet) {
		this.noteTypeSet = noteTypeSet;
	}

	@Override
	public PIIObject deletePII() {
		setRtfNote("DELETED");
		setPlainTextNote("DELETED");
		return this;
	}

	@Override
	public PIIObject maskPII() {
		setRtfNote("MASKED");
		setPlainTextNote("MASKED");
		return this;	}
}

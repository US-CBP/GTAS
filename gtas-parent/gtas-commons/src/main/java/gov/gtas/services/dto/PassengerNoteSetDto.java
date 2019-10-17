package gov.gtas.services.dto;

import java.util.LinkedHashSet;

import gov.gtas.vo.NoteVo;

public class PassengerNoteSetDto {
	private LinkedHashSet<NoteVo> paxNotes;
	private long totalPaxNotes;

	public PassengerNoteSetDto(LinkedHashSet<NoteVo> paxNotes, long totalPaxNotes) {
		this.paxNotes = paxNotes;
		this.totalPaxNotes = totalPaxNotes;
	}

	public LinkedHashSet<NoteVo> getPaxNotes() {
		return paxNotes;
	}

	public long getTotalPaxNotes() {
		return totalPaxNotes;
	}
}

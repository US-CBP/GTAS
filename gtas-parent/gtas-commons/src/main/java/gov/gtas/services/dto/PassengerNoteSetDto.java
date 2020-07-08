package gov.gtas.services.dto;

import java.util.LinkedHashSet;
import java.util.List;

import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerNote;
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

	public static PassengerNoteSetDto fromNotes(List<PassengerNote> passengerNotes) {
		LinkedHashSet<NoteVo> noteVos = new LinkedHashSet<>();
		for (PassengerNote pNote : passengerNotes) {
			Passenger p = pNote.getPassenger();
			NoteVo noteVo = NoteVo.from(pNote);
			if (p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
				noteVo.deletePII();
			} else if (p.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()){
				noteVo.maskPII();
			}
			noteVos.add(noteVo);
		}

		return new PassengerNoteSetDto(noteVos, noteVos.size());
	}
}

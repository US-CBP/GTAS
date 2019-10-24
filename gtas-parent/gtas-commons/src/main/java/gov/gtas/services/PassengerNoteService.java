package gov.gtas.services;

import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.vo.NoteVo;
import org.springframework.security.access.prepost.PreAuthorize;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER;

public interface PassengerNoteService {


	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	PassengerNoteSetDto getAllEventNotes(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	PassengerNoteSetDto getAllHistoricalNotes(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	void saveNote(NoteVo note, String userId);

}

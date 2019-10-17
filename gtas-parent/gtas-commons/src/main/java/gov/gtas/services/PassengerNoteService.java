package gov.gtas.services;

import gov.gtas.model.dto.PassengerNoteDto;
import gov.gtas.services.dto.PassengerNoteSetDto;
import org.springframework.security.access.prepost.PreAuthorize;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER;

public interface PassengerNoteService {


	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	PassengerNoteSetDto getAllEventNotes(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	PassengerNoteSetDto getAllHistoricalNotes(Long paxId);

	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	void saveNote(PassengerNoteDto note, String userId);

}

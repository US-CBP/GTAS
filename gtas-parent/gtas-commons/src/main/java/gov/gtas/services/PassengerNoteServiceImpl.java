package gov.gtas.services;

import org.springframework.beans.factory.annotation.Autowired;

import gov.gtas.model.PassengerNote;
import gov.gtas.repository.PassengerNoteRepository;
import gov.gtas.services.dto.PassengerNoteSetDto;

public class PassengerNoteServiceImpl implements PassengerNoteService {
	
	@Autowired
	PassengerNoteRepository noteRepo;

	@Override
	public PassengerNoteSetDto getAllEventNotes(String paxId) {
		return null;
	}

	@Override
	public PassengerNoteSetDto getAllHistoricalNotes(String paxId) {
		return null;
	}

	@Override
	public void saveNote(PassengerNote note) {
		noteRepo.save(note);
	}

	@Override
	public void deleteNote(String noteId) {
	}

}

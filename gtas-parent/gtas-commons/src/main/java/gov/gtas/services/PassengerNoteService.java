package gov.gtas.services;

import java.util.LinkedHashSet;

import gov.gtas.model.PassengerNote;
import gov.gtas.services.dto.PassengerNoteSetDto;

public interface PassengerNoteService {
	
	 PassengerNoteSetDto getAllEventNotes(String paxId);
	 PassengerNoteSetDto getAllHistoricalNotes(String paxId);
	 void saveNote(PassengerNote note);
	 
	 //This particular action might not be used for now
	 void deleteNote(String noteId); 
	
}

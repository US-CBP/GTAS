package gov.gtas.repository;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Note;
import gov.gtas.model.PassengerNote;

public interface PassengerNoteRepository extends CrudRepository<PassengerNote, Long>{
	
	default PassengerNote findOne(Long noteId) {
		return findById(noteId).orElse(null);
	}
	
	LinkedHashSet<Note> getFullNoteHistory(String paxId);
	
	LinkedHashSet<Note> getEventNoteHistory(String paxId);
	
}

package gov.gtas.services;

import gov.gtas.model.NoteType;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerNote;
import gov.gtas.model.dto.PassengerNoteDto;
import gov.gtas.repository.NoteTypeRepository;
import gov.gtas.repository.PassengerNoteRepository;
import gov.gtas.services.dto.PassengerNoteSetDto;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PassengerNoteServiceImpl implements PassengerNoteService {

	private PassengerNoteRepository passengerNoteRepository;

	private NoteTypeRepository noteTypeRepository;

	private PassengerService passengerService;

	public PassengerNoteServiceImpl(PassengerNoteRepository passengerNoteRepository, NoteTypeRepository noteTypeRepository, PassengerService passengerService) {
		this.passengerNoteRepository = passengerNoteRepository;
		this.noteTypeRepository = noteTypeRepository;
		this.passengerService = passengerService;
	}

	@Override
	@Transactional
	public PassengerNoteSetDto getAllEventNotes(Long paxId) {
		List<PassengerNote> notes = passengerNoteRepository.findAllByPassengerIdOrderByCreatedAt(paxId);
		return PassengerNoteSetDto.fromNotes(notes);
	}

	@Override
	@Transactional
	public PassengerNoteSetDto getAllHistoricalNotes(Long paxId) {
		List<Passenger> passengersWithSamePassengerIdTag = passengerService.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = passengerService.findById(paxId);
		passengerSet.remove(p);
		List<PassengerNote> historicalNotes = passengerNoteRepository.findFirst10ByPassengerInOrderByCreatedAtDesc(passengerSet);
		return PassengerNoteSetDto.fromNotes(historicalNotes);
	}

	@Override
	@Transactional
	public void saveNote(PassengerNoteDto note, String userId) {
		String noteType = note.getNoteType();
		NoteType type = noteTypeRepository.findByType(noteType).orElseThrow(RuntimeException::new);
		note.getNoteTypeSet().add(type);
		PassengerNote paxNote = PassengerNote.from(note, userId);
		passengerNoteRepository.save(paxNote);
	}

}

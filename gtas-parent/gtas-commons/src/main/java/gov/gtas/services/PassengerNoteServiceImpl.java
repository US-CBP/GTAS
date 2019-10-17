package gov.gtas.services;

import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerNote;
import gov.gtas.model.dto.PassengerNoteDto;
import gov.gtas.repository.PassengerNoteRepository;
import gov.gtas.services.dto.PassengerNoteSetDto;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PassengerNoteServiceImpl implements PassengerNoteService {

	private PassengerNoteRepository passengerNoteRepository;

	private PassengerService passengerService;

	public PassengerNoteServiceImpl(PassengerNoteRepository passengerNoteRepository, PassengerService passengerService) {
		this.passengerNoteRepository = passengerNoteRepository;
		this.passengerService = passengerService;
	}

	@Override
	public PassengerNoteSetDto getAllEventNotes(Long paxId) {
		List<PassengerNote> notes = passengerNoteRepository.findAllByPassengerIdOrderByCreatedAt(paxId);
		return PassengerNoteSetDto.fromNotes(notes);
	}

	@Override
	public PassengerNoteSetDto getAllHistoricalNotes(Long paxId) {
		List<Passenger> passengersWithSamePassengerIdTag = passengerService.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = passengerService.findById(paxId);
		passengerSet.remove(p);
		List<PassengerNote> historicalNotes = passengerNoteRepository.findFirst10ByPassengerInOrderByCreatedAtDesc(passengerSet);
		return PassengerNoteSetDto.fromNotes(historicalNotes);
	}

	@Override
	public void saveNote(PassengerNoteDto note, String userId) {
		PassengerNote paxNote = PassengerNote.from(note, userId);
		passengerNoteRepository.save(paxNote);
	}

}

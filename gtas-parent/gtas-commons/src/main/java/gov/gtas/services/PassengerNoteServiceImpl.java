package gov.gtas.services;

import gov.gtas.model.NoteType;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerNote;
import gov.gtas.model.dto.PassengerNoteDto;
import gov.gtas.repository.NoteTypeRepository;
import gov.gtas.repository.PassengerNoteRepository;
import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.vo.NoteTypeVo;
import gov.gtas.vo.NoteVo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		List<PassengerNote> notes = passengerNoteRepository.findAllByPassengerIdOrderByCreatedAtDesc(paxId);
		Passenger p = passengerService.findById(paxId);
		PassengerNoteSetDto passengerNoteSetDto = PassengerNoteSetDto.fromNotes(notes);
		if (p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
			passengerNoteSetDto.getPaxNotes().forEach(NoteVo::deletePII);
		} else if (p.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
			passengerNoteSetDto.getPaxNotes().forEach(NoteVo::maskPII);
		}
		return passengerNoteSetDto;
	}

	@Override
	@Transactional
	public PassengerNoteSetDto getPrevious10PassengerNotes(Long paxId) {
		List<Passenger> passengersWithSamePassengerIdTag = passengerService.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = passengerService.findById(paxId);
		passengerSet.remove(p);
		List<PassengerNote> historicalNotes = passengerNoteRepository.findFirst10ByPassengerInOrderByCreatedAtDesc(passengerSet);
		PassengerNoteSetDto passengerNoteSetDto = PassengerNoteSetDto.fromNotes(historicalNotes);

		//First mask the note IF the passenger is requires it.
		if (p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
			passengerNoteSetDto.getPaxNotes().forEach(NoteVo::deletePII);
		} else if (p.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
			passengerNoteSetDto.getPaxNotes().forEach(NoteVo::maskPII);
		}
		return passengerNoteSetDto;
	}

	@Override
	@Transactional
	public void saveNote(NoteVo note, String userId) {
		Set<NoteTypeVo> noteTypeVos = note.getNoteTypeVoSet();
		Set<Long> noteTypesIds = noteTypeVos.stream().map(NoteTypeVo::getId).collect(Collectors.toSet());
		Set<NoteType> types = noteTypeRepository.findAllById(noteTypesIds);
		if (types.isEmpty()) {
			throw new RuntimeException("Notes must have a type!");
		}
		note.setNoteTypeSet(types);
		PassengerNote paxNote = PassengerNote.from(note, userId);
		passengerNoteRepository.save(paxNote);
	}

}

package gov.gtas.repository;

import java.util.List;
import java.util.Set;

import gov.gtas.model.Passenger;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.PassengerNote;

public interface PassengerNoteRepository extends CrudRepository<PassengerNote, Long>{
	
	default PassengerNote findOne(Long noteId) {
		return findById(noteId).orElse(null);
	}
	
	List<PassengerNote> findAllByPassengerIdOrderByCreatedAt(Long passengerId);

	List<PassengerNote> findFirst10ByPassengerInOrderByCreatedAtDesc(Set<Passenger> passenger);

}

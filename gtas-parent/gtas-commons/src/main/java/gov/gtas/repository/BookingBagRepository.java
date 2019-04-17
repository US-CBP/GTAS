package gov.gtas.repository;

import gov.gtas.model.BookingBag;
import gov.gtas.model.BookingBagId;
import org.springframework.data.repository.CrudRepository;

public interface BookingBagRepository extends CrudRepository<BookingBag, BookingBagId> {

}

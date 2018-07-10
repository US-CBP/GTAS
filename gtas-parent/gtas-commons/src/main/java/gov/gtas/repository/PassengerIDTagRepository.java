package gov.gtas.repository;

import gov.gtas.model.PassengerIDTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface PassengerIDTagRepository extends CrudRepository<PassengerIDTag, Long> {

}

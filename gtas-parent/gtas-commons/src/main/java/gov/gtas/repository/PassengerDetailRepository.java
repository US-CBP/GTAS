package gov.gtas.repository;

import gov.gtas.model.PassengerDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Set;

public interface PassengerDetailRepository extends CrudRepository <PassengerDetails, Long> {

    @Transactional
    @Query("Select pd from PassengerDetails pd where pd.passengerId in :paxIds " )
    Set<PassengerDetails> getDetailsofPaxId(@Param("paxIds") Set<Long> paxIds);
}

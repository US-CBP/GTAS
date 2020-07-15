package gov.gtas.repository;

import gov.gtas.model.Email;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
public interface EmailRepository extends CrudRepository<Email, Long> {

    @Query("select e from Email e" +
            " left join fetch e.pnrs pnr" +
            " left join fetch pnr.passengers" +
            " where pnr.id in :pnrIds" +
            " and e.flightId in :flightIds")
    Set<Email> findEmails( @Param("flightIds")Set<Long> flightIds, @Param("pnrIds") Set<Long> pnrIds);

    List<Email> findByAddressAndFlightId(String address, Long flightId);
}

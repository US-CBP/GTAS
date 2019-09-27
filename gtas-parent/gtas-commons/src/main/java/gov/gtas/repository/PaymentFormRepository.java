package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Bag;
import gov.gtas.model.PaymentForm;

public interface PaymentFormRepository extends CrudRepository<PaymentForm, Long> {

	@Query("SELECT p FROM PaymentForm p WHERE p.pnr.id = :pnrId")
	List<Bag> findByPnrId(@Param("pnrId") Long pnrId);
}

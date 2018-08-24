package gov.gtas.repository;

import gov.gtas.model.PassengerIDTag;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public interface PassengerIDTagRepository extends CrudRepository<PassengerIDTag, Long> {
	@Query("select pax_id from PassengerIDTag pIdTag where pIdTag.idTag = :idTag")
	public List<Long> findPaxIdsByTagId(@Param("idTag") String idTag);
}

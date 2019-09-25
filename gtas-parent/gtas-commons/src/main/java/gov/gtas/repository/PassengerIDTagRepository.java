package gov.gtas.repository;

import gov.gtas.model.PassengerIDTag;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PassengerIDTagRepository extends CrudRepository<PassengerIDTag, Long> {

	@Query("select pax_id from PassengerIDTag pIdTag where pIdTag.idTag = :idTag")
	public List<Long> findPaxIdsByTagId(@Param("idTag") String idTag);

	@Query("select pax_id from PassengerIDTag pIdTag where pIdTag.tamrId = :tamrId")
	public List<Long> findPaxIdsByTamrId(@Param("tamrId") String tamrId);

	@Query("select pIdTag from PassengerIDTag pIdTag where pIdTag.pax_id = :paxId")
	public PassengerIDTag findByPaxId(@Param("paxId") Long paxId);

	@Transactional
	@Modifying
	@Query("update PassengerIDTag pIdTag set pIdTag.tamrId = :tamrId where pIdTag.pax_id = :paxId")
	public void updateTamrId(@Param("paxId") Long paxId, @Param("tamrId") String tamrId);
}

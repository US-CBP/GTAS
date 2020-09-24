/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.User;
import gov.gtas.model.lookup.Country;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AuditRecordRepository extends CrudRepository<AuditRecord, Long> {
	public List<AuditRecord> findByActionType(AuditActionType actionType);

	public List<AuditRecord> findByUser(User user);

	public List<AuditRecord> findByUserAndActionType(User user, AuditActionType actionType);

	public List<AuditRecord> findByUserAndTarget(User user, String target);

	public List<AuditRecord> findByTarget(String target);

	@Query("SELECT ar FROM AuditRecord ar WHERE ar.timestamp >= :fromDate and  ar.timestamp <= :toDate ORDER BY ar.timestamp DESC")
	public List<AuditRecord> findByTimestampRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("SELECT ar FROM AuditRecord ar WHERE ar.user = :user and ar.timestamp >= :fromDate and  ar.timestamp <= :toDate ORDER BY ar.timestamp DESC")
	public List<AuditRecord> findByUserTimestampRange(@Param("user") User user, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);

	@Query("SELECT ar FROM AuditRecord ar WHERE ar.actionType = :action and ar.timestamp >= :fromDate and  ar.timestamp <= :toDate ORDER BY ar.timestamp DESC")
	public List<AuditRecord> findByActionTimestampRange(@Param("action") AuditActionType action,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("SELECT ar FROM AuditRecord ar WHERE ar.user = :user and ar.actionType = :action and ar.timestamp >= :fromDate and  ar.timestamp <= :toDate ORDER BY ar.timestamp DESC")
	public List<AuditRecord> findByUserActionTimestampRange(@Param("user") User user,
			@Param("action") AuditActionType action, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("SELECT ar FROM AuditRecord ar WHERE ar.timestamp >= :fromDate ORDER BY ar.timestamp DESC")
	public List<AuditRecord> findByTimestampFrom(@Param("fromDate") Date fromDate);

	default AuditRecord findOne(Long id) {
		return findById(id).orElse(null);
	}
}

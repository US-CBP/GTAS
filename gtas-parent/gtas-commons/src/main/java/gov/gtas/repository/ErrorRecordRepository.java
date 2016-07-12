/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.ErrorRecord;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ErrorRecordRepository extends CrudRepository<ErrorRecord, Long>{
    public List<ErrorRecord> findByCode(String code);
    
    @Query("SELECT er FROM ErrorRecord er WHERE er.timestamp >= :fromDate and  er.timestamp <= :toDate")
    public List<ErrorRecord> findByTimestampRange(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);  
    
    @Query("SELECT er FROM ErrorRecord er WHERE er.timestamp >= :fromDate")
    public List<ErrorRecord> findByTimestampFrom(@Param("fromDate") Date fromDate);   
}

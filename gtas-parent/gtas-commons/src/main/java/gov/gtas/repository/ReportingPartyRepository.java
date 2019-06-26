/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.ReportingParty;

import java.util.List;

public interface ReportingPartyRepository extends CrudRepository<ReportingParty, Long>{
    @Query("select rp from ReportingParty rp where upper(rp.partyName) = upper(:partyName) and rp.telephone = :telephone")
    public List<ReportingParty> getReportingParty(@Param("partyName") String partyName, @Param("telephone") String telephone);

}

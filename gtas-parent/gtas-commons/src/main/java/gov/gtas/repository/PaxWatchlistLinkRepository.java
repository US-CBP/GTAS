/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.model.PaxWatchlistLink;

public interface PaxWatchlistLinkRepository extends JpaRepository<PaxWatchlistLink, Long> {
	public List<PaxWatchlistLink> findByPassengerId(Long id);
    public void setPaxWatchlistLink(Date lastDate, float percentMatch, int verifiedStatus,Long passengerId, Long watchlistItemId);
}
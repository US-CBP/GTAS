/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.model.PaxWatchlistLink;

public interface PaxWatchlistLinkRepository extends JpaRepository<PaxWatchlistLink, Long> {
	
	@Query("SELECT p FROM PaxWatchlistLink p WHERE p.passenger.id =:paxId")
	public List<PaxWatchlistLink> findByPassengerId(@Param("paxId") Long paxId);
	
	@Query("SELECT p.watchlistItem.id FROM PaxWatchlistLink p WHERE p.passenger.id =:paxId")
	public List<Long> findWatchlistItemByPassengerId(@Param("paxId") Long paxId);
	
	@Modifying
	@Transactional
	@Query(value="insert into pax_watchlist_link (last_run_timestamp, percent_match, verified_status, passenger_id, watchlist_item_id) VALUES (:lastDate, :percentMatch, :verifiedStatus, :passengerId, :watchlistItemId)", nativeQuery=true)
	public void savePaxWatchlistLink(@Param("lastDate") Date lastDate, @Param("percentMatch") float percentMatch, @Param("verifiedStatus") int verifiedStatus, @Param("passengerId") Long passengerId, @Param("watchlistItemId") Long watchlistItemId);
	
	@Query(value = "select new map(p.id as passenger_id, i.id as watchlist_item_id) from PaxWatchlistLink pl left join pl.passenger p left join  pl.watchlistItem i where p.id in :paxIds")
	public List<Map<Long, Long>> findPaxWatchlistMap(@Param("paxIds") Set<Long> paxIds);
}
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository.watchlist;

import gov.gtas.model.lookup.Airport;
import gov.gtas.model.watchlist.WatchlistItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Watch list Repository with custom queries.
 */
public interface WatchlistItemRepository
		extends CrudRepository<WatchlistItem, Long>, JpaSpecificationExecutor<WatchlistItem> {
	@Query("SELECT wli FROM WatchlistItem wli WHERE wli.watchlist.watchlistName = :watchlistName")
	public List<WatchlistItem> getItemsByWatchlistName(@Param("watchlistName") String watchlistName);

	@Query("DELETE FROM WatchlistItem wli WHERE wli.watchlist.watchlistName = :watchlistName")
	public void deleteItemsByWatchlistName(@Param("watchlistName") String watchlistName);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM WatchlistItem wli WHERE wli.id IN :watchlistItemIds")
	public void deleteItemsByWatchlistItemId(@Param("watchlistItemIds")List<Long> watchlistItemIds);
	
	default WatchlistItem findOne(Long id) {
		return findById(id).orElse(null);
	}
}

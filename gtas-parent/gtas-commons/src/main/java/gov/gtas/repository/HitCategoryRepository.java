/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.Watchlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HitCategoryRepository extends CrudRepository<HitCategory, Long> {

	default HitCategory findOne(Long hitCatId) {
		return findById(hitCatId).orElse(null);
	}

	@Query("SELECT wl FROM Watchlist wl WHERE wl.watchlistName = :name")
	public Watchlist getWatchlistByName(@Param("name") String name);

	@Query("SELECT watchlistName, watchlistEntity FROM Watchlist")
	public List<Object[]> fetchWatchlistSummary();

	@Query("SELECT wl FROM Watchlist wl WHERE wl.watchlistName in :names")
	public List<Watchlist> getWatchlistByNames(@Param("names") List<String> names);

	@Query("select hc from HitCategory hc where hc.archived = false  OR hc.archived IS NULL")
    List<HitCategory> getAllNonArchivedCategories();
}

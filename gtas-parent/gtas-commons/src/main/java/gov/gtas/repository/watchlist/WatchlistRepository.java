/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository.watchlist;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.model.watchlist.Watchlist;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Watch list Repository with custom queries.
 */
public interface WatchlistRepository extends CrudRepository<Watchlist, Long>, JpaSpecificationExecutor<Watchlist> {
	public List<Watchlist> findByWatchlistEntity(EntityEnum entity);

	@Query("SELECT wl FROM Watchlist wl WHERE wl.watchlistName = :name")
	public Watchlist getWatchlistByName(@Param("name") String name);

	@Query("SELECT watchlistName, watchlistEntity FROM Watchlist")
	public List<Object[]> fetchWatchlistSummary();

	@Query("SELECT wl FROM Watchlist wl WHERE wl.watchlistName in :names")
	public List<Watchlist> getWatchlistByNames(@Param("names") List<String> names);

	@Query("select categories from WatchlistCategory categories")
	public List<WatchlistCategory> getWatchlistCategories();

	@Query("select c from WatchlistCategory c where c.id = :category_id")
	public WatchlistCategory getWatchlistCategoryById(@Param("category_id") Long category_id);
}

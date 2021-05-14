/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Country;
import gov.gtas.model.lookup.CountryRestore;
import gov.gtas.vo.lookup.CountryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Date;

@Repository
public class CountryRepositoryCustomImpl implements CountryRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private CountryRepository repo;

	@Override
	public Country restore(Country origCountry) {
		if (origCountry.getOriginId() == null) return null;

		String sqlString = " SELECT c FROM CountryRestore c WHERE c.id = :originid";
		TypedQuery<CountryRestore> query = em.createQuery(sqlString, CountryRestore.class);
		query.setParameter("originid", origCountry.getOriginId());
		CountryRestore cr = query.getSingleResult();

		if (cr != null) {
			Country restored = setFields(cr, origCountry);

			return repo.save(restored);
		}
		return null;
	}

	/* Restore the original countries to their initial unedited values. Does not affect user-created countries */
	@Override
	public int restoreAll() {
		String sqlString = " SELECT c FROM Country c WHERE c.originId > 0";
		TypedQuery<Country> query = em.createQuery(sqlString, Country.class);
		List<Country> originalCountries = query.getResultList();

		int numRestored = 0;

		for (Country orig : originalCountries) {
			if (restore(orig) != null) numRestored++;
		}
		return numRestored;
	}

	private Country setFields(CountryRestore cr, Country restored) {
		restored.setIso2(cr.getIso2());
		restored.setIso3(cr.getIso3());
		restored.setIsoNumeric(cr.getIsoNumeric());
		restored.setName(cr.getName());
		restored.setUpdatedAt(new Date());
		restored.setArchived(false);

		return restored;
	}

}

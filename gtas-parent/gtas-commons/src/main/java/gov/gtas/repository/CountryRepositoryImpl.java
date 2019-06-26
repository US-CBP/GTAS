/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Country;
import gov.gtas.model.lookup.CountryRestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CountryRepositoryImpl implements CountryRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private CountryRepository repo;

    @Override
    public Country restore(Country origCountry) {
      if (origCountry.getOriginId() == null) return origCountry;

      String sqlString = " SELECT c FROM CountryRestore c WHERE c.id = :originid";
      TypedQuery<CountryRestore> query = em.createQuery(sqlString, CountryRestore.class);
      query.setParameter("originid", origCountry.getOriginId());
      CountryRestore cr = query.getSingleResult();

      if (cr != null) {
        origCountry.setIso2(cr.getIso2());
        origCountry.setIso3(cr.getIso3());
        origCountry.setIsoNumeric(cr.getIsoNumeric());
        origCountry.setName(cr.getName());

        return repo.save(origCountry);
      }
      /// else throw warning there was no matching data found.
      /// record is either user-created or there's an issue with the CountryRestore data.
      return origCountry;
    }

  @Override
  public int restoreAll() {
    String sqlString = " SELECT c FROM CountryRestore c";
    TypedQuery<CountryRestore> query = em.createQuery(sqlString, CountryRestore.class);
    List<CountryRestore> crs = query.getResultList();

    Query deleteQuery = em.createNativeQuery(" DELETE FROM Country ");
    int numDeleted = deleteQuery.executeUpdate();

    int numRestored = 0;

    for (CountryRestore cr : crs) {
      try {
        Country country = new Country();
        country.setOriginId(cr.getId());
        country.setIso2(cr.getIso2());
        country.setIso3(cr.getIso3());
        country.setIsoNumeric(cr.getIsoNumeric());
        country.setName(cr.getName());

        numRestored++;
        repo.save(country);
      }
      catch (Exception ex) {
        // Log.
        // return recs not updated?
      }
    }
    return numRestored;
  }

}

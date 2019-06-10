/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.AirportRestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

@Repository
public class AirportRepositoryImpl implements AirportRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AirportRepository repo;

    @Override
    public Airport restore(Airport origAirport) {
      if (origAirport.getOriginId() == null) return origAirport;

      String sqlString = " SELECT c FROM AirportRestore c WHERE c.id = :originid";
      TypedQuery<AirportRestore> query = em.createQuery(sqlString, AirportRestore.class);
      query.setParameter("originid", origAirport.getOriginId());
      AirportRestore cr = query.getSingleResult();

      if (cr != null) {
        Airport restored = setFields(cr, origAirport);

        return repo.save(restored);
      }
      /// else throw warning there was no matching data found.
      /// record is either user-created or there's an issue with the AirportRestore data.
      return origAirport;
    }

  @Override
  public int restoreAll() {
    String sqlString = " SELECT c FROM AirportRestore c";
    TypedQuery<AirportRestore> query = em.createQuery(sqlString, AirportRestore.class);
    List<AirportRestore> crs = query.getResultList();

    Query deleteQuery = em.createNativeQuery(" DELETE FROM Airport ");
    int numDeleted = deleteQuery.executeUpdate();

    int numRestored = 0;

    for (AirportRestore cr : crs) {
      try {
        Airport restored = setFields(cr, new Airport());
        restored.setOriginId(cr.getId());
        numRestored++;

        repo.save(restored);
      }
      catch (Exception ex) {
        // Log.
        // return recs not updated?
      }
    }
    return numRestored;
  }

  private Airport setFields(AirportRestore cr, Airport restored) {
    restored.setCity(cr.getCity());
    restored.setCountry(cr.getCountry());
    restored.setIcao(cr.getIcao());
    restored.setIata(cr.getIata());
    restored.setName(cr.getName());
    restored.setLatitude(cr.getLatitude());
    restored.setLongitude(cr.getLongitude());
    restored.setUtcOffset(cr.getUtcOffset());
    restored.setTimezone(cr.getTimezone());

    return restored;
  }

}

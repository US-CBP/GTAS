/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import gov.gtas.model.lookup.Carrier;
import gov.gtas.model.lookup.CarrierRestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;

@Repository
public class CarrierRepositoryImpl implements CarrierRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private CarrierRepository repo;

    @Override
    public Carrier restore(Carrier currentEdited) {
      if (currentEdited.getOriginId() == null) return currentEdited;

      String sqlString = " SELECT c FROM CarrierRestore c WHERE c.id = :originid";
      TypedQuery<CarrierRestore> query = em.createQuery(sqlString, CarrierRestore.class);
      query.setParameter("originid", currentEdited.getOriginId());
      CarrierRestore unedited = query.getSingleResult();

      if (unedited != null) {
        Carrier restored = currentEdited;
        restored.setIcao(unedited.getIcao());
        restored.setIata(unedited.getIata());
        restored.setName(unedited.getName());

        return repo.save(restored);
      }
      /// else throw warning there was no matching data found.
      /// record is either user-created or there's an issue with the CarrierRestore data.
      return currentEdited;
    }

    @Override
    public int restoreAll() {
      String sqlString = " SELECT c FROM CarrierRestore c";
      TypedQuery<CarrierRestore> query = em.createQuery(sqlString, CarrierRestore.class);
      List<CarrierRestore> crs = query.getResultList();

      Query deleteQuery = em.createNativeQuery(" DELETE FROM Carrier ");
      int numDeleted = deleteQuery.executeUpdate();

      int numRestored = 0;

      for (CarrierRestore unedited : crs) {
        try {
          Carrier carrier = new Carrier();
          carrier.setOriginId(unedited.getId());
          carrier.setIcao(unedited.getIcao());
          carrier.setIata(unedited.getIata());
          carrier.setName(unedited.getName());

          repo.save(carrier);
          numRestored++;
        }
        catch (Exception ex) {
          // Log.
          // return recs not updated?
        }
      }
      return numRestored;
    }

}

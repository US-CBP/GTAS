/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Carrier;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

@Repository
public class CarrierRepositoryImpl implements CarrierRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Carrier restore(Long id) {
      String sqlString = " SELECT c FROM CarrierRestore WHERE id = :id";
      Query query = em.createNativeQuery(sqlString);
      query.setParameter("id", id.toString());
      CarrierRestore cr = query.executeUpdate();

      if (cr != null) {

      Carrier carrier = new Carrier();
      carrier.setId(id);
      carrier.setIata(cr.getIata());
      carrier.setName(cr.getName());
      carrier.setIcao(cr.getIcao());

      return update(carrier);
      }
      return cr;
    }

}

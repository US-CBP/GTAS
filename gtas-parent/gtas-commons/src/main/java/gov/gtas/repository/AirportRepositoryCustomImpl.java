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
import java.util.Date;
import java.util.List;

@Repository
public class AirportRepositoryCustomImpl implements AirportRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private AirportRepository repo;

	@Override
	public Airport restore(Airport origAirport) {
		if (origAirport.getOriginId() == null) return null;

		String sqlString = " SELECT c FROM AirportRestore c WHERE c.id = :originid";
		TypedQuery<AirportRestore> query = em.createQuery(sqlString, AirportRestore.class);
		query.setParameter("originid", origAirport.getOriginId());
		AirportRestore cr = query.getSingleResult();

		if (cr != null) {
			Airport restored = setFields(cr, origAirport);

			return repo.save(restored);
		}
		return null;
	}

	/* Restore the original airports to their initial unedited values. Does not affect user-created airports */
	@Override
	public int restoreAll() {
		String sqlString = " SELECT a FROM Airport a where a.originId > 0";
		TypedQuery<Airport> query = em.createQuery(sqlString, Airport.class);
		List<Airport> origAirports = query.getResultList();

		int numRestored = 0;

		for (Airport orig: origAirports) {
			if (restore(orig) != null) numRestored++;
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
		restored.setUpdatedAt(new Date());
		restored.setArchived(false);
		return restored;
	}

}

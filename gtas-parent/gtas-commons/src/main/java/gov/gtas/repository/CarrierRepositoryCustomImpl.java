/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
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
public class CarrierRepositoryCustomImpl implements CarrierRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private CarrierRepository repo;

	@Override
	public Carrier restore(Carrier currentEdited) {
		if (currentEdited.getOriginId() == null) return null;

		String sqlString = " SELECT c FROM CarrierRestore c WHERE c.id = :originid";
		TypedQuery<CarrierRestore> query = em.createQuery(sqlString, CarrierRestore.class);
		query.setParameter("originid", currentEdited.getOriginId());
		CarrierRestore unedited = query.getSingleResult();

		if (unedited != null) {
			Carrier restored = setFields(unedited, currentEdited);

			return repo.save(restored);
		}
		return null;
	}

	/* Restore the original carriers to their initial unedited values. Does not affect user-created carriers */
	@Override
	public int restoreAll() {
		String sqlString = " SELECT c FROM Carrier c where c.originId > 0";
		TypedQuery<Carrier> query = em.createQuery(sqlString, Carrier.class);
		List<Carrier> originalCarriers = query.getResultList();

		int numRestored = 0;

		for (Carrier orig : originalCarriers) {
			if (restore(orig) != null) numRestored++;
		}
		return numRestored;
	}

	private Carrier setFields(CarrierRestore cr, Carrier restored) {
		restored.setIcao(cr.getIcao());
		restored.setIata(cr.getIata());
		restored.setName(cr.getName());
		restored.setUpdatedAt(new Date());
		restored.setArchived(false);

		return restored;
	}

}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.CreditCardType;
import gov.gtas.model.lookup.CreditCardTypeRestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class CreditCardTypeRepositoryCustomImpl implements CreditCardTypeRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private CreditCardTypeRepository repo;

	@Override
	public CreditCardType restore(CreditCardType currentEdited) {
		if (currentEdited.getOriginId() == null) return null;

		String sqlString = " SELECT c FROM CreditCardTypeRestore c WHERE c.id = :originid";
		TypedQuery<CreditCardTypeRestore> query = em.createQuery(sqlString, CreditCardTypeRestore.class);
		query.setParameter("originid", currentEdited.getOriginId());
		CreditCardTypeRestore unedited = query.getSingleResult();

		if (unedited != null) {
			CreditCardType restored = currentEdited;
			restored.setCode(unedited.getCode());
			restored.setDescription(unedited.getDescription());
			restored.setUpdatedAt(new Date());
			restored.setArchived(false);

			return repo.save(restored);
		}
		return null;
	}

	/* Restore the original cctypes to their initial unedited values. Does not affect user-created cctypes */
	@Override
	public int restoreAll() {
		String sqlString = " SELECT c FROM CreditCardType c WHERE c.originId > 0";
		TypedQuery<CreditCardType> query = em.createQuery(sqlString, CreditCardType.class);
		List<CreditCardType> originalCctypes = query.getResultList();

		int numRestored = 0;

		for (CreditCardType orig : originalCctypes) {
			if (restore(orig) != null) numRestored++;
		}
		return numRestored;
	}

}

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
import java.util.List;

@Repository
public class CreditCardTypeRepositoryImpl implements CreditCardTypeRepositoryCustom {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private CreditCardTypeRepository repo;

	@Override
	public CreditCardType restore(CreditCardType currentEdited) {
		if (currentEdited.getOriginId() == null)
			return currentEdited;

		String sqlString = " SELECT c FROM CreditCardTypeRestore c WHERE c.id = :originid";
		TypedQuery<CreditCardTypeRestore> query = em.createQuery(sqlString, CreditCardTypeRestore.class);
		query.setParameter("originid", currentEdited.getOriginId());
		CreditCardTypeRestore unedited = query.getSingleResult();

		if (unedited != null) {
			CreditCardType restored = currentEdited;
			restored.setCode(unedited.getCode());
			restored.setDescription(unedited.getDescription());

			return repo.save(restored);
		}
		/// else throw warning there was no matching data found.
		/// record is either user-created or there's an issue with the CreditCardTypeRestore
		/// data.
		return currentEdited;
	}

	@Override
	public int restoreAll() {
		String sqlString = " SELECT c FROM CreditCardTypeRestore c";
		TypedQuery<CreditCardTypeRestore> query = em.createQuery(sqlString, CreditCardTypeRestore.class);
		List<CreditCardTypeRestore> cctr = query.getResultList();

		Query deleteQuery = em.createNativeQuery(" DELETE FROM credit_card_type ");
		int numDeleted = deleteQuery.executeUpdate();

		int numRestored = 0;

		for (CreditCardTypeRestore unedited : cctr) {
			try {
				CreditCardType cctype = new CreditCardType();
				cctype.setOriginId(unedited.getId());
				cctype.setCode(unedited.getCode());
				cctype.setDescription(unedited.getDescription());

				repo.save(cctype);
				numRestored++;
			} catch (Exception ex) {
				// Log.
				// return recs not updated?
			}
		}
		return numRestored;
	}

}

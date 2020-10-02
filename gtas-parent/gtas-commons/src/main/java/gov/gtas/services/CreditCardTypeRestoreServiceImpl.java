/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.CreditCardTypeRestore;
import gov.gtas.repository.CreditCardTypeRestoreRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class CreditCardTypeRestoreServiceImpl implements CreditCardTypeRestoreService {

	@Resource
	private CreditCardTypeRestoreRepository repo;

	@Override
	@Transactional
	public List<CreditCardTypeRestore> findAll() {

		return (List<CreditCardTypeRestore>) repo.findAll();
	}

	@Override
	@Transactional
	public CreditCardTypeRestore findById(Long id) {

		return repo.findById(id).orElse(null);
	}

}

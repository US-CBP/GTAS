/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.HitCategoryRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class RuleCatServiceImpl implements HitCategoryService {

	private static final Logger logger = LoggerFactory.getLogger(RuleCatServiceImpl.class);

	@Resource
	private HitCategoryRepository hitCategoryRepository;

	@Resource
	private UdrRuleRepository udrRuleRepository;

	@Override
	public Iterable<HitCategory> findAll() {
		return hitCategoryRepository.findAll();
	}

	@Override
	public HitCategory findById(Long id) {
		return hitCategoryRepository.findOne(id);
	}

	@Override
	public void create(HitCategory hitCategory) {
		hitCategory.setCreatedAt(new Date());
		hitCategoryRepository.save(hitCategory);
	}

}

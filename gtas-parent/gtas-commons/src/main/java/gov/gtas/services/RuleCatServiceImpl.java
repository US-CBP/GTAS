/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.UserGroup;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.HitCategoryRepository;
import gov.gtas.repository.UserGroupRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class RuleCatServiceImpl implements HitCategoryService {

	private static final Logger logger = LoggerFactory.getLogger(RuleCatServiceImpl.class);

	@Resource
	private HitCategoryRepository hitCategoryRepository;

	@Resource
	private UdrRuleRepository udrRuleRepository;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Value("${user.group.default}")
	private Long defaultUserGroupId;

	@Override
	public Iterable<HitCategory> findAll() {
		return hitCategoryRepository.findAll();
	}

	@Override
	public HitCategory findById(Long id) {
		return hitCategoryRepository.findOne(id);
	}

	@Override
	@Transactional
	public void create(HitCategory hitCategory) {
		UserGroup defaultUserGroup = userGroupRepository.findById(defaultUserGroupId).orElseThrow(RuntimeException::new);
		hitCategory.setCreatedAt(new Date());
		hitCategory = hitCategoryRepository.save(hitCategory);
		defaultUserGroup.getHitCategories().add(hitCategory);
		userGroupRepository.save(defaultUserGroup);
	}

}

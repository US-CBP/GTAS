/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonLookupData;
import gov.gtas.json.JsonServiceResponse;
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
import java.util.List;
import java.util.stream.Collectors;

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
		UserGroup defaultUserGroup = userGroupRepository.findById(defaultUserGroupId)
				.orElseThrow(RuntimeException::new);
		hitCategory.setCreatedAt(new Date());
		hitCategory = hitCategoryRepository.save(hitCategory);
		defaultUserGroup.getHitCategories().add(hitCategory);
		userGroupRepository.save(defaultUserGroup);
	}

	@Override
	public JsonServiceResponse updateHitCategory(HitCategory hitCategory) {
		hitCategoryRepository.save(hitCategory);
		return new JsonServiceResponse(Status.SUCCESS, "Updated hit category", hitCategory);
	}

	@Override
	public List<JsonLookupData> getAllNonArchivedCategories() {
		List<JsonLookupData> result = hitCategoryRepository.getAllNonArchivedCategories().stream().map(w ->
				new JsonLookupData(w.getId(), w.getName(), w.getDescription(), w.getSeverity().toString(), w.isArchived()))
				.collect(Collectors.toList());
		return result;
	}


}

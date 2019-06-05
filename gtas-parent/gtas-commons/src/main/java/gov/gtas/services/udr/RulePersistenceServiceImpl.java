/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.udr;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.BaseEntity;
import gov.gtas.model.User;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.services.security.UserService;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * The back-end service for persisting rules.
 */
@Service
public class RulePersistenceServiceImpl implements RulePersistenceService {
	/*
	 * The logger for the RulePersistenceService.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(RulePersistenceServiceImpl.class);

	private static final int UPDATE_BATCH_SIZE = 100;

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private UdrRuleRepository udrRuleRepository;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public UdrRule create(UdrRule r, String userId) {
		final User user = userService.fetchUser(userId);
		// remove meta for now, since its ID is the same as the parent UdrRule
		// ID.
		// we will add it after saving the UDR rule and the ID has been
		// generated.
		RuleMeta savedMeta = r.getMetaData();
		r.setMetaData(null);

		if (savedMeta == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"UDR metatdata", "RulePersistenceServiceImpl.create()");
		}

		// set the audit fields
		r.setEditDt(new Date());
		r.setAuthor(user);
		r.setEditedBy(user);

		// save the rule with the meta data stripped.
		// Once the rule id is generated we will add back the meta data
		// and set its key to the rule ID.
		UdrRule rule = udrRuleRepository.save(r);

		// now add back the meta and conditions and update the rule.
		long ruleid = rule.getId();
		savedMeta.setId(ruleid);
		rule.setMetaData(savedMeta);
		savedMeta.setParent(rule);
		rule = udrRuleRepository.save(rule);

		return rule;
	}

	@Override
	@Transactional
	public UdrRule delete(Long id, String userId) {
		final User user = userService.fetchUser(userId);

		UdrRule ruleToDelete = udrRuleRepository.findById(id).orElse(null);
		if (ruleToDelete != null && ruleToDelete.getDeleted() == YesNoEnum.N) {
			ruleToDelete.setDeleted(YesNoEnum.Y);
			ruleToDelete.setDeleteId(ruleToDelete.getId());
			RuleMeta meta = ruleToDelete.getMetaData();
			meta.setEnabled(YesNoEnum.N);
			ruleToDelete.setEditedBy(user);
			ruleToDelete.setEditDt(new Date());

			// remove references to the Knowledge Base
			if (ruleToDelete.getEngineRules() != null) {
				for (Rule rl : ruleToDelete.getEngineRules()) {
					rl.setKnowledgeBase(null);
				}
			}
			udrRuleRepository.save(ruleToDelete);
		} else {
			ruleToDelete = null; // in case delete flag was Y
			logger.warn("RulePersistenceServiceImpl.delete() - object does not exist or has already been deleted:"
					+ id);
		}
		return ruleToDelete;
	}

	@Override
	@Transactional(value = TxType.SUPPORTS)
	public List<UdrRule> findAll() {
		return udrRuleRepository.findByDeletedAndEnabled(YesNoEnum.N,
				YesNoEnum.Y);
	}

	@Override
	public List<UdrRule> findAllUdrSummary(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return udrRuleRepository.findAllUdrRuleSummary();
		} else {
			return udrRuleRepository.findAllUdrRuleSummaryByAuthor(userId);
		}
	}

	@Override
	public Collection<? extends BaseEntity> batchUpdate(
			Collection<? extends BaseEntity> entities) {
		/*
		 * Note: this method is only used for Knowledge base maintenance. Hence
		 * there is no need for logging the updates in this method.
		 */
		List<BaseEntity> ret = new LinkedList<>();
		int count = 0;
		for (BaseEntity ent : entities) {
			BaseEntity upd = entityManager.merge(ent);
			ret.add(upd);
			++count;
			if (count > UPDATE_BATCH_SIZE) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		return ret;
	}

	@Override
	@Transactional
	public UdrRule update(UdrRule rule, String userId) {
		final User user = userService.fetchUser(userId);

		if (rule.getId() == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "id",
					"Update UDR");
		}

		rule.setEditDt(new Date());
		rule.setEditedBy(user);
		return udrRuleRepository.save(rule);
	}

	@Override
	@Transactional
	public UdrRule update(UdrRule rule, User user) {

		if (rule.getId() == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "id",
					"Update UDR");
		}

		rule.setEditDt(new Date());
		rule.setEditedBy(user);
		return udrRuleRepository.save(rule);
	}

	@Override
	@Transactional(TxType.SUPPORTS)
	public UdrRule findById(Long id) {
		return udrRuleRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(TxType.SUPPORTS)
	public UdrRule findByTitleAndAuthor(String title, String authorUserId) {
		return udrRuleRepository
				.getUdrRuleByTitleAndAuthor(title, authorUserId);
	}

	@Override
	public List<UdrRule> findByAuthor(String authorUserId) {
		return udrRuleRepository.findUdrRuleByAuthor(authorUserId);
	}

	@Override
	public List<UdrRule> findValidUdrOnDate(Date targetDate) {
		List<UdrRule> ret = null;
		try {
			// remove the time portion of the date
			Date tDate = DateCalendarUtils.parseJsonDate(DateCalendarUtils
					.formatJsonDate(targetDate));
			ret = udrRuleRepository.findValidUdrRuleByDate(tDate);
		} catch (ParseException ex) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, ex,
					"targetDate",
					"RulePersistenceServiceImpl.findValidUdrOnDate");
		}
		return ret;
	}

	@Override
	public KnowledgeBase findUdrKnowledgeBase() {
		return this.findUdrKnowledgeBase(RuleConstants.UDR_KNOWLEDGE_BASE_NAME);
	}

	@Override
	public KnowledgeBase findUdrKnowledgeBase(String kbName) {
		return udrRuleRepository.getKnowledgeBaseByName(kbName);
	}

	@Override
	public KnowledgeBase saveKnowledgeBase(KnowledgeBase kb) {
		kb.setCreationDt(new Date());
		if (kb.getId() == null) {
			entityManager.persist(kb);
		} else {
			entityManager.merge(kb);
		}
		return kb;
	}

	@Override
	public KnowledgeBase deleteKnowledgeBase(String kbName) {
		KnowledgeBase kb = findUdrKnowledgeBase(kbName);
		if (kb != null) {
			entityManager.remove(kb);
		}
		return kb;
	}

	@Override
	public List<Rule> findRulesByKnowledgeBaseId(Long id) {
		return udrRuleRepository.getRuleByKbId(id);
	}

	/**
	 * @return the entityManager
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
}

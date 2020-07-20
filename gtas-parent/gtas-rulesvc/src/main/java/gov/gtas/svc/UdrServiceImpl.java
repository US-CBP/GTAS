/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.constant.AuditLogConstants.UDR_LOG_CREATE_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.UDR_LOG_DELETE_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.UDR_LOG_UPDATE_MESSAGE;
import static gov.gtas.constant.AuditLogConstants.UDR_LOG_UPDATE_META_MESSAGE;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.JsonToDomainObjectConverter;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.security.UserService;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.UdrServiceHelper;
import gov.gtas.svc.util.UdrServiceJsonResponseHelper;
import gov.gtas.util.DateCalendarUtils;

import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the UDR Service API.
 */
@Service
public class UdrServiceImpl implements UdrService {
	private static Logger logger = LoggerFactory.getLogger(UdrServiceImpl.class);

	@Autowired
	private AppConfigurationService appConfigurationService;

	@Autowired
	private RulePersistenceService rulePersistenceService;

	@Resource
	private UdrRuleRepository udrRuleRepository;

	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;

	@Autowired
	private UserService userService;

	@Autowired
	private RuleManagementService ruleManagementService;

	// @RuleCategory changes
	@Autowired
	private HitCategoryService hitCategoryService;

	@Value("${hit.general.category}")
	private Long defaultCategory;

	@Override
	@Transactional
	public UdrSpecification fetchUdr(String userId, String title) {
		UdrRule fetchedRule = rulePersistenceService.findByTitleAndAuthor(title, userId);
		if (fetchedRule == null) {
			throw ErrorHandlerFactory.getErrorHandler()
					.createException(CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE, "UDR", "title=" + title);
		}
		UdrSpecification jsonObject = null;
		try {
			jsonObject = JsonToDomainObjectConverter.getJsonFromUdrRule(fetchedRule);
		} catch (Exception ex) {
			logger.error("error fetching udr!", ex);
			throw new RuntimeException(ex.getMessage());
		}
		return jsonObject;
	}

	@Override
	@Transactional
	public UdrSpecification fetchUdr(Long id) {
		UdrRule fetchedRule = rulePersistenceService.findById(id);
		if (fetchedRule == null) {
			throw ErrorHandlerFactory.getErrorHandler()
					.createException(CommonErrorConstants.QUERY_RESULT_EMPTY_ERROR_CODE, "UDR", "id=" + id);
		}
		UdrSpecification jsonObject = null;
		try {
			jsonObject = JsonToDomainObjectConverter.getJsonFromUdrRule(fetchedRule);
		} catch (Exception ex) {
			logger.error("error fetching udr.", ex);
			throw new RuntimeException(ex.getMessage());
		}
		return jsonObject;
	}

	@Override
	public List<JsonUdrListElement> fetchUdrSummaryList(String userId) {
		return convertSummaryList(rulePersistenceService.findAllUdrSummary(userId));
	}

	/**
	 * Converts the UDR query data into a summary list.
	 * 
	 * @param fetchedRuleList
	 *            the query data.
	 * @return summary list.
	 */
	private List<JsonUdrListElement> convertSummaryList(List<UdrRule> fetchedRuleList) {
		Map<Long, Long> udrHitCountMap = createUdrHitCountMap();
		List<JsonUdrListElement> ret = new LinkedList<>();
		if (fetchedRuleList != null && !fetchedRuleList.isEmpty()) {
			for (UdrRule udrRule : fetchedRuleList) {
				String editedBy = udrRule.getEditedBy().getUserId();
				Date editedOn = udrRule.getEditDt();
				String authorUserId = udrRule.getAuthor().getUserId();
				final MetaData meta = new MetaData(udrRule.getMetaData().getTitle(),
						udrRule.getMetaData().getDescription(), udrRule.getMetaData().getStartDt(), authorUserId);
				meta.setEnabled(udrRule.getMetaData().getEnabled() == YesNoEnum.Y);
				meta.setEndDate(udrRule.getMetaData().getEndDt());
				meta.setOverMaxHits(udrRule.getMetaData().getOverMaxHits());
				Long udrId = udrRule.getId();
				JsonUdrListElement item = new JsonUdrListElement(udrId, editedBy, editedOn, meta);
				Long hitCount = udrHitCountMap.get(udrId);
				if (hitCount != null) {
					item.setHitCount(hitCount.intValue());
				}
				ret.add(item);

			}

		}
		return ret;
	}

	private Map<Long, Long> createUdrHitCountMap() {
		List<Object[]> udrCounts = udrRuleRepository.getCounts();
		Map<Long, Long> hitCountMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(udrCounts)) {
			for (Object[] data : udrCounts) {
				Long udrId = (Long) data[0];
				Long udrHitCount = (Long) data[1];
				hitCountMap.put(udrId, udrHitCount);
			}
		}
		return hitCountMap;
	}

	@Override
	public List<JsonUdrListElement> fetchUdrSummaryList() {
		return convertSummaryList(rulePersistenceService.findAllUdrSummary(null));
	}

	@Override
	@Transactional
	public JsonServiceResponse copyUdr(String userId, Long udrId) {
		// fetch the UDR
		UdrSpecification udrToCopy = fetchUdr(udrId);

		// create new UDR
		udrToCopy.setId(null);
		udrToCopy.getSummary().setTitle(makeTitleForCopy(userId, udrToCopy));
		udrToCopy.getSummary().setAuthor(userId);
		udrToCopy.getSummary().setStartDate(new Date());
		udrToCopy.getSummary().setEndDate(null);
		// save
		return createUdr(userId, udrToCopy);
	}

	private String makeTitleForCopy(String userId, UdrSpecification udrToCopy) {
		String ret = null;
		// fetch UDR with the same title
		String oldTitle = udrToCopy.getSummary().getTitle();
		/*
		 * if the title contains a generated suffix then remove it.
		 */
		int indx = oldTitle.indexOf("##");
		if (indx >= 0) {
			oldTitle = oldTitle.substring(0, indx);
		}

		List<String> titleList = udrRuleRepository.getUdrTitleByTitlePrefixAndAuthor(oldTitle + "%", userId);
		if (CollectionUtils.isEmpty(titleList)) {
			ret = oldTitle;
		} else {
			/*
			 * This user has already authored UDR with the same title. Create a new title by
			 * append a numbered suffix.
			 */
			Set<String> titleSet = new HashSet<>(titleList);
			int titleSuffix = 1;
			while (titleSuffix < RuleConstants.UDR_MAX_NUMBER_COPIES) {
				String newTitle = oldTitle + "##" + titleSuffix;
				titleSuffix++;
				int tl = newTitle.length();
				if (tl > RuleConstants.UDR_TITLE_LENGTH) {
					newTitle = newTitle.substring(tl - RuleConstants.UDR_TITLE_LENGTH);
				}
				if (!titleSet.contains(newTitle)) {
					ret = newTitle;
					break;
				}
			}
			if (ret == null) {
				throw new IllegalArgumentException("Too many copies requested for:" + oldTitle);
			}
		}

		return ret;
	}

	@Override
	@Transactional
	public JsonServiceResponse createUdr(String userId, UdrSpecification udrToCreate) {
		if (udrToCreate == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"udrToCreate", "Create UDR");
		}
		MetaData meta = udrToCreate.getSummary();
		if (meta == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"summary", "Create UDR");
		}
		// get the author object
		String authorUserId = meta.getAuthor();
		User author = fetchRuleAuthor(userId, authorUserId);

		UdrRule ruleToSave = null;
		try {
			ruleToSave = JsonToDomainObjectConverter.createUdrRuleFromJson(udrToCreate, author);
			setRuleCatOnRuleMeta(ruleToSave.getMetaData(), ruleToSave);
			UdrServiceHelper.addEngineRulesToUdrRule(ruleToSave, udrToCreate);
		} catch (IOException ioe) {
			logger.error("error creating udr!", ioe);
			throw new RuntimeException(ioe.getMessage());
		} catch (Exception ex) {
			logger.error("Error creating udr rule!", ex);
		}

		UdrRule savedRule = rulePersistenceService.create(ruleToSave, userId);
		appConfigurationService.setRecompileFlag();

		writeAuditLog(AuditActionType.CREATE_UDR, savedRule, udrToCreate, userId, author);
		return UdrServiceJsonResponseHelper.createResponse(true, RuleConstants.UDR_CREATE_OP_NAME, savedRule);
	}

	/**
	 * Fetches all the active rules and recompiles the Knowledge Base. If no rules
	 * are found then the knowledge Base is deleted.
	 *
	 * @param kbName
	 * @param userId
	 */
	public void recompileRules(final String kbName, String userId) {
		List<UdrRule> ruleList = rulePersistenceService.findAll();
		if (!CollectionUtils.isEmpty(ruleList)) {
			ruleManagementService.createKnowledgeBaseFromUdrRules(kbName, ruleList, userId);
		} else {
			KnowledgeBase kb = rulePersistenceService.findUdrKnowledgeBase(kbName);
			if (kb != null) {
				List<Rule> rules = rulePersistenceService.findRulesByKnowledgeBaseId(kb.getId());
				if (CollectionUtils.isEmpty(rules)) {
					ruleManagementService.deleteKnowledgeBase(kbName);
					logger.warn("UdrService - no active rules -> deleting Knowledge Base!");
				}
			}
		}
	}

	private User fetchRuleAuthor(final String userId, final String authorUserId) {
		String authorId = authorUserId;
		if (StringUtils.isEmpty(authorId)) {
			authorId = userId;
		}
		return userService.fetchUser(authorId);
	}

	@Override
	@Transactional
	public JsonServiceResponse updateUdr(String userId, UdrSpecification udrToUpdate) {
		if (udrToUpdate == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"udrToUpdate", "Update UDR");
		}
		Long id = udrToUpdate.getId();
		if (id == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"id", "Update UDR");
		}
		MetaData meta = udrToUpdate.getSummary();
		if (meta == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"udrToUpdate.summary", "Update UDR");
		}
		// get the author object
		String authorUserId = meta.getAuthor();
		// fetch the UdrRule
		UdrRule ruleToUpdate = rulePersistenceService.findById(id);
		if (ruleToUpdate == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.UPDATE_RECORD_MISSING_ERROR_CODE, udrToUpdate.getSummary().getTitle(), userId,
					id);
		}
		ruleToUpdate.getMetaData().setOverMaxHits(false); // reset over max hits flag.
		/*
		 * check if the user has permission to update the UDR
		 */
		User author = ruleToUpdate.getAuthor();
		if (!author.getUserId().equals(userId)) {
			// TODO check if the user is admin
			// else throw exception
			logger.error(String.format("UdrServiceImpl.updateUdr() - %s trying to update rule by different author %s!",
					authorUserId, ruleToUpdate.getAuthor().getUserId()));
		}
		updateRuleMetaData(udrToUpdate, ruleToUpdate);
		UdrRule updatedRule = null;

		QueryObject queryObject = udrToUpdate.getDetails();
		if (queryObject != null) {
			try {
				final byte[] ruleBlob = JsonToDomainObjectConverter.convertQueryObjectToBlob(queryObject);
				ruleToUpdate.setUdrConditionObject(ruleBlob);
			} catch (IOException ex) {
				logger.error("error updating udr!", ex);
				throw new RuntimeException(ex.getMessage());
			}

			// update the engine rules
			List<Rule> newEngineRules = UdrServiceHelper.listEngineRules(ruleToUpdate, udrToUpdate);

			ruleToUpdate.clearEngineRules();
			for (Rule r : newEngineRules) {
				ruleToUpdate.addEngineRule(r);
			}
			if (ruleToUpdate.getAuthor().getUserId().equals(userId)) {
				updatedRule = rulePersistenceService.update(ruleToUpdate, ruleToUpdate.getAuthor());
			} else {
				updatedRule = rulePersistenceService.update(ruleToUpdate, userId);
			}
			appConfigurationService.setRecompileFlag();

			writeAuditLog(AuditActionType.UPDATE_UDR, updatedRule, udrToUpdate, userId, author);
		} else {
			// simple update - meta data only
			// no need to re-generate the Knowledge Base.
			if (ruleToUpdate.getAuthor().getUserId().equals(userId)) {
				updatedRule = rulePersistenceService.update(ruleToUpdate, ruleToUpdate.getAuthor());
			} else {
				updatedRule = rulePersistenceService.update(ruleToUpdate, userId);
			}
			writeAuditLog(AuditActionType.UPDATE_UDR_META, ruleToUpdate, udrToUpdate, userId, author);
		}

		return UdrServiceJsonResponseHelper.createResponse(true, RuleConstants.UDR_UPDATE_OP_NAME, updatedRule);
	}

	private void updateRuleMetaData(UdrSpecification udrToUpdate, UdrRule ruleToUpdate) {
		try {
			RuleMeta ruleMeta = JsonToDomainObjectConverter.extractRuleMetaUpdates(udrToUpdate);
			ruleToUpdate.setTitle(ruleMeta.getTitle());
			setRuleCatOnRuleMeta(ruleMeta, ruleToUpdate);
			ruleToUpdate.setMetaData(ruleMeta);
		} catch (Exception ex) {
			logger.error("Error updating rule meta data!", ex);
		}
	}

	@Override
	@Transactional
	public JsonServiceResponse deleteUdr(String userId, Long id) {
		UdrRule deletedRule = rulePersistenceService.delete(id, userId);
		if (deletedRule != null) {
			appConfigurationService.setRecompileFlag();

			writeAuditLog(AuditActionType.DELETE_UDR, deletedRule, null, userId, null);
			return UdrServiceJsonResponseHelper.createResponse(true, RuleConstants.UDR_DELETE_OP_NAME, deletedRule);
		} else {
			return UdrServiceJsonResponseHelper.createResponse(false, RuleConstants.UDR_DELETE_OP_NAME, deletedRule,
					"since it does not exist or has been deleted previously");
		}
	}

	private void writeAuditLog(AuditActionType actionType, UdrRule udr, UdrSpecification udrspec, String userId,
			User author) {
		User user = author;
		if (author == null || !author.getUserId().equals(userId)) {
			user = userService.fetchUser(userId);
		}

		String auditUserId = userId;
		if (user != null) {
			auditUserId = user.getUserId();
		}

		String message = null;
		AuditActionTarget target = new AuditActionTarget(actionType, udr.getTitle(),
				udr.getId() != null ? String.valueOf(udr.getId()) : null);
		AuditActionData actionData = new AuditActionData();
		switch (actionType) {
		case UPDATE_UDR:
			message = UDR_LOG_UPDATE_MESSAGE;
			actionData = createAuditDetailForUdr(udrspec, auditUserId);
			break;
		case UPDATE_UDR_META:
			message = UDR_LOG_UPDATE_META_MESSAGE;
			actionData = createAuditDetailForUdr(udrspec, auditUserId);
			break;
		case CREATE_UDR:
			message = UDR_LOG_CREATE_MESSAGE;
			actionData = createAuditDetailForUdr(udrspec, auditUserId);
			break;
		case DELETE_UDR:
			message = UDR_LOG_DELETE_MESSAGE;
			actionData = createAuditDetailForUdr(udr, auditUserId);
			break;
		default:
			break;

		}
		auditLogPersistenceService.create(actionType, target, actionData, message, auditUserId);
	}

	private AuditActionData createAuditDetailForUdr(UdrRule udr, String auditUserId) {
		AuditActionData actionData = new AuditActionData();
		actionData.addProperty("author", udr.getAuthor() != null ? udr.getAuthor().getUserId() : auditUserId);
		actionData.addProperty("description",
				udr.getMetaData().getDescription() != null ? udr.getMetaData().getDescription() : StringUtils.EMPTY);
		actionData.addProperty("startDate", DateCalendarUtils.formatJsonDate(udr.getMetaData().getStartDt()));
		if (udr.getMetaData().getEndDt() != null) {
			actionData.addProperty("endDate", DateCalendarUtils.formatJsonDate(udr.getMetaData().getEndDt()));
		} else {
			actionData.addProperty("endDate", StringUtils.EMPTY);
		}
		return actionData;
	}

	private AuditActionData createAuditDetailForUdr(UdrSpecification udrspec, String auditUserId) {
		AuditActionData actionData = new AuditActionData();
		MetaData meta = udrspec.getSummary();
		actionData.addProperty("author", meta.getAuthor() != null ? meta.getAuthor() : auditUserId);
		actionData.addProperty("description",
				meta.getDescription() != null ? meta.getDescription() : StringUtils.EMPTY);
		actionData.addProperty("startDate", DateCalendarUtils.formatJsonDate(meta.getStartDate()));
		if (meta.getEndDate() != null) {
			actionData.addProperty("endDate", DateCalendarUtils.formatJsonDate(meta.getEndDate()));
		} else {
			actionData.addProperty("endDate", StringUtils.EMPTY);
		}
		return actionData;
	}

	// @RuleCat changes
	// Utility method to retrieve RuleCat Set from nested RuleMeta under UDR Rule
	private void setRuleCatOnRuleMeta(RuleMeta ruleMeta, UdrRule ruleToSave) {
		HitCategory hitCategory = ruleMeta.getRuleCategories().stream().filter(rc -> rc.getId() != null).findAny()
				.orElse(hitCategoryService.findById(defaultCategory));
		hitCategory = hitCategoryService.findById(hitCategory.getId());
		Set<HitCategory> ruleCatSet = new HashSet<>();
		ruleCatSet.add(hitCategory);
		ruleMeta.setRuleCategories(ruleCatSet);
		ruleToSave.setHitCategory(hitCategory);
	}
}

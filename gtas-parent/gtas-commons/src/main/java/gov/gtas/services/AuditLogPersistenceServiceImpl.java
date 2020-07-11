/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.AuditLogConstants.AUDIT_LOG_WARNING_CANNOT_CONVERT_JSON_TO_STRING;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.GeneralAuditRecord;
import gov.gtas.model.User;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.services.security.UserService;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class AuditLogPersistenceServiceImpl.
 */
@Service
public class AuditLogPersistenceServiceImpl implements AuditLogPersistenceService {

	private static Logger logger = LoggerFactory.getLogger(AuditLogPersistenceServiceImpl.class);

	@Resource
	private AuditRecordRepository auditLogRepository;

	@Autowired
	private UserService userService;

	@Override
	public AuditRecord create(AuditRecord aRec) {
		return auditLogRepository.save(aRec);
	}

	@Override
	public AuditRecord findById(Long id) {
		return auditLogRepository.findOne(id);
	}

	@Override
	public List<AuditRecord> findByDateRange(Date dateFrom, Date dateTo) {
		return auditLogRepository.findByTimestampRange(dateFrom, dateTo);
	}

	@Override
	public List<AuditRecord> findByDateFrom(Date dateFrom) {
		return auditLogRepository.findByTimestampFrom(dateFrom);
	}

	@Override
	public List<AuditRecord> findByUser(String userId) {
		User user = userService.fetchUser(userId);
		return auditLogRepository.findByUser(user);
	}

	@Override
	public List<AuditRecord> findByActionType(AuditActionType actionType) {
		return auditLogRepository.findByActionType(actionType);
	}

	@Override
	public List<AuditRecord> findByUserAndActionType(AuditActionType actionType, String userId) {
		User user = userService.fetchUser(userId);
		return auditLogRepository.findByUserAndActionType(user, actionType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.AuditLogPersistenceService#findByUserAndTarget(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public List<AuditRecord> findByUserAndTarget(String userId, String target) {
		User user = userService.fetchUser(userId);
		return auditLogRepository.findByUserAndTarget(user, target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.AuditLogPersistenceService#findByTarget(java.lang.String )
	 */
	@Override
	public List<AuditRecord> findByTarget(String target) {
		return auditLogRepository.findByTarget(target);
	}

	@Override
	public List<AuditRecord> findAll() {
		Iterable<AuditRecord> res = auditLogRepository.findAll();
		List<AuditRecord> ret = new LinkedList<>();
		res.forEach(ret::add);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.AuditLogPersistenceService#create(gov.gtas.enumtype
	 * .AuditActionType, java.lang.String, java.lang.Object, java.lang.String,
	 * gov.gtas.model.User)
	 */
	@Override
	public AuditRecord create(AuditActionType actionType, String target, Object actionData, String message, User user) {
		ObjectMapper mapper = new ObjectMapper();
		String actionDataString = null;
		if (actionData != null) {
			try {
				if (actionData instanceof String) {
					actionDataString = (String) actionData;
				} else {
					actionDataString = mapper.writeValueAsString(actionData);
				}
			} catch (Exception ex) {
				logger.warn(
						String.format(AUDIT_LOG_WARNING_CANNOT_CONVERT_JSON_TO_STRING, actionType, target, message));
			}
		}
		return auditLogRepository
				.save(new GeneralAuditRecord(actionType, target, Status.SUCCESS, message, actionDataString, user));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.AuditLogPersistenceService#create(gov.gtas.enumtype
	 * .AuditActionType, gov.gtas.json.AuditActionTarget,
	 * gov.gtas.json.AuditActionData, java.lang.String, java.lang.String)
	 */
	@Override
	public AuditRecord create(AuditActionType actionType, AuditActionTarget target, AuditActionData actionData,
			String message, String userId) {
		try {
			String targetStr = target != null ? target.toString() : StringUtils.EMPTY;
			String actionDataStr = actionData != null ? actionData.toString() : StringUtils.EMPTY;
			return create(actionType, targetStr, actionDataStr, message, userId);
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
		return create(actionType, StringUtils.EMPTY, null, message, userId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.AuditLogPersistenceService#create(gov.gtas.enumtype
	 * .AuditActionType, java.lang.String, java.lang.Object, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public AuditRecord create(AuditActionType actionType, String target, Object actionData, String message,
			String userId) {
		User user = userService.fetchUser(userId);
		return create(actionType, target, actionData, message, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.AuditLogPersistenceService#findByUserActionDateRange
	 * (java.lang.String, gov.gtas.enumtype.AuditActionType, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<AuditRecord> findByUserActionDateRange(String userId, AuditActionType action, Date dateFrom,
			Date dateTo) {

		List<AuditRecord> ret = new LinkedList<>();
		boolean byUser = !StringUtils.isEmpty(userId);
		Date today = new Date();
		if (dateFrom != null && byUser && action != null) {
			User user = userService.fetchUser(userId);
			ret = auditLogRepository.findByUserActionTimestampRange(user, action, dateFrom,
					dateTo != null ? dateTo : today);
		} else if (dateFrom != null && byUser) {
			User user = userService.fetchUser(userId);
			ret = auditLogRepository.findByUserTimestampRange(user, dateFrom, dateTo != null ? dateTo : today);
		} else if (dateFrom != null && action != null) {
			ret = auditLogRepository.findByActionTimestampRange(action, dateFrom, dateTo != null ? dateTo : today);
		} else if (dateFrom != null) {
			ret = auditLogRepository.findByTimestampRange(dateFrom, dateTo != null ? dateTo : today);
		} else if (byUser && action != null) {
			User user = userService.fetchUser(userId);
			ret = auditLogRepository.findByUserAndActionType(user, action);
		} else if (byUser) {
			User user = userService.fetchUser(userId);
			ret = auditLogRepository.findByUser(user);
		} else if (action != null) {
			ret = auditLogRepository.findByActionType(action);
		}
		return ret;
	}

}

/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.AuditActionType;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.User;

import java.util.Date;
import java.util.List;

public interface AuditLogPersistenceService {
    public AuditRecord create(AuditRecord aRec);
    public AuditRecord create(AuditActionType actionType, String target, Object actionData, String message, User user);
    public AuditRecord create(AuditActionType actionType, String target, Object actionData, String message, String userId);
    public AuditRecord create(AuditActionType actionType, AuditActionTarget target, AuditActionData actionData, String message, String userId);
    public AuditRecord findById(Long id);

    public List<AuditRecord> findByUserActionDateRange(String userId, AuditActionType action,  Date dateFrom, Date dateTo);
    public List<AuditRecord> findByDateRange(Date dateFrom, Date dateTo);
    public List<AuditRecord> findByDateFrom(Date dateFrom);
    public List<AuditRecord> findByUser(String userId);
    public List<AuditRecord> findByActionType(AuditActionType type);
    public List<AuditRecord> findByUserAndActionType(AuditActionType type, String user);
    public List<AuditRecord> findByUserAndTarget(String user, String target);
    public List<AuditRecord> findByTarget(String target);
    public List<AuditRecord> findAll();
}

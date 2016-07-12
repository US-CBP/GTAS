/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static gov.gtas.constant.GtasSecurityConstants.GTAS_APPLICATION_USERID;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.ErrorPersistenceService;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dashboard Scheduler class. Using Spring's Scheduled annotation for 
 * scheduling tasks. The class reads configuration values from 
 * an external file.
 *
 */
@Component
public class DashboardUpdateScheduler {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(DashboardUpdateScheduler.class);

    /** The entity manager. */
    @PersistenceContext
    private EntityManager entityManager;

    /** The api dashboard update sql. */
    @Value("${dashboard.api.message.update}")
    private String apiDashboardUpdateSql;

    /** The pnr dashboard update sql. */
    @Value("${dashboard.pnr.message.update}")
    private String pnrDashboardUpdateSql;

    /** The error persistence service. */
    private ErrorPersistenceService errorPersistenceService;

    /** The audit log persistence service. */
    private AuditLogPersistenceService auditLogPersistenceService;

    /**
     * Instantiates a new dashboard update scheduler.
     *
     * @param errorPersistenceService the error persistence service
     * @param auditLogPersistenceService the audit log persistence service
     */
    @Autowired
    public DashboardUpdateScheduler(
            ErrorPersistenceService errorPersistenceService,
            AuditLogPersistenceService auditLogPersistenceService) {
        this.errorPersistenceService = errorPersistenceService;
        this.auditLogPersistenceService = auditLogPersistenceService;
    }

    /**
     * Job scheduling.
     */
    @Scheduled(fixedDelayString = "${dashboard.fixedDelay.in.milliseconds}")
    @Transactional
    public void jobScheduling() {
        logger.info("entering jobScheduling()");

        try {
            entityManager.createNativeQuery(apiDashboardUpdateSql)
                    .executeUpdate();
            logger.info("Updated dashboard api.");
            int updatedRecords = entityManager.createNativeQuery(
                    pnrDashboardUpdateSql).executeUpdate();
            logger.info("Updated dashboard pnr.");
            writeAuditLogForUpdatingDashboardRun(updatedRecords);
        } catch (Exception ex) {
            logger.error("SQLException:" + ex.getMessage(), ex);
            ErrorDetailInfo errInfo = ErrorHandlerFactory
                    .createErrorDetails(ex);
            errorPersistenceService.create(errInfo);
        }

        logger.info("exiting jobScheduling()");

    }

    /**
     * Write audit log for updating dashboard run.
     *
     * @param updatedRecords the updated records
     */
    private void writeAuditLogForUpdatingDashboardRun(Integer updatedRecords) {
        try {
            AuditActionTarget target = new AuditActionTarget(
                    AuditActionType.UPDATE_DASHBOARD_RUN,
                    "GTAS Updating Dashboard", null);
            AuditActionData actionData = new AuditActionData();
            actionData.addProperty("updatedDashboardRecord", String.valueOf(updatedRecords));
            String message = "Updating Dashboard run on " + new Date();
            auditLogPersistenceService.create(AuditActionType.UPDATE_DASHBOARD_RUN,
                    target.toString(), actionData.toString(), message,
                    GTAS_APPLICATION_USERID);
        } catch (Exception ex) {
            logger.error("Exception:" + ex.getMessage(), ex);
            ErrorDetailInfo errInfo = ErrorHandlerFactory
                    .createErrorDetails(ex);
            errorPersistenceService.create(errInfo);
        }
    }

}

package gov.gtas.aop.auditPiiAccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.model.*;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.dto.PriorityVettingListRequest;
import gov.gtas.services.security.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class WebMethodAuditor {

    protected final Logger logger = LoggerFactory.getLogger(WebMethodAuditor.class);

    final
    UserService userService;

    final
    AuditRecordRepository auditRecordRepository;

    @Autowired
    public WebMethodAuditor(AuditRecordRepository auditRecordRepository, UserService userService) {
        this.auditRecordRepository = auditRecordRepository;
        this.userService = userService;
    }

    @Before(value = "@annotation(gov.gtas.aop.annotations.PVLRequestAuditFirstArgRequest)")
    public void pvlRequestAudit(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            PriorityVettingListRequest pvlr = null;
            if (args.length > 0 && args[0] != null) {
                pvlr = (PriorityVettingListRequest) args[0];
            }
            String data = null;
            if (pvlr != null) {
                ObjectMapper om = new ObjectMapper();
                data = om.writeValueAsString(pvlr);
            }
            String methodName = joinPoint.getSignature().getName();
            String userId = GtasSecurityUtils.fetchLoggedInUserId();
            if (userId != null) {
                User u = userService.fetchUser(userId);
                Date date = new Date();
                if (pvlr != null) {
                    AuditRecord ar = new PvlAuditRecord(AuditActionType.ACCESS_INFORMATION, methodName, gov.gtas.enumtype.Status.WEB_CALL, u.getUserId() + " accessed PVL", data, u, date);
                    auditRecordRepository.save(ar);
                } else {
                    logger.warn("Null user attempting web service call!");
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("unable to make audit log for pvl!");
        }
    }

    @Before(value = "@annotation(gov.gtas.aop.annotations.PassengerAuditFirstArgPaxIdAsString)")
    public void passengerAuditFirstArgPaxId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String paxId = null;
        if (args.length > 0 && args[0] != null) {
            paxId = (String)args[0];
        }
        String methodName = joinPoint.getSignature().getName();
        String userId = GtasSecurityUtils.fetchLoggedInUserId();
        if (userId != null) {
            User u = userService.fetchUser(userId);
            Date date = new Date();
            Long paxIdLong = null;
            if (paxId != null) {
                paxIdLong = Long.parseLong(paxId);
            } else {
                logger.error("Attempt to access site with null parameters!");
            }
            AuditRecord ar = new PassengerAuditRecord(AuditActionType.ACCESS_INFORMATION, methodName,  gov.gtas.enumtype.Status.WEB_CALL, u.getUserId() + " ACCESSED PAX ID " + paxId, paxId, u, date, paxIdLong  );
            auditRecordRepository.save(ar);
        } else {
            logger.warn("Null user attempting web service call!");
        }
    }

    @Before(value = "@annotation(gov.gtas.aop.annotations.QueryRequestAudit)")
    public void queryAudit(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        QueryRequest qr = null;
        if (args.length > 0 && args[0] != null) {
            qr = (QueryRequest)args[0];
        }
        String methodName = joinPoint.getSignature().getName();
        String userId = GtasSecurityUtils.fetchLoggedInUserId();
        if (userId != null && qr != null && qr.getQuery() != null) {
            User u = userService.fetchUser(userId);
            Date date = new Date();
            String data = qr.getQuery().toString();
            AuditRecord ar = new QueryAuditRecord(AuditActionType.ACCESS_INFORMATION, methodName,  gov.gtas.enumtype.Status.WEB_CALL, u.getUserId() + " RAN A QUERY ", data, u, date);
            auditRecordRepository.save(ar);
        } else {
            logger.warn("Null user attempting web service call!");
        }
    }

    @Before(value = "@annotation(gov.gtas.aop.annotations.FlightAuditFirstArgFlightIdAsLong)")
    public void flightAuditFirstArgFlightId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long flightIdLong = null;
        if (args.length > 0 && args[0] != null) {
            flightIdLong = (Long)args[0];
        }
        String methodName = joinPoint.getSignature().getName();
        String userId = GtasSecurityUtils.fetchLoggedInUserId();
        if (userId != null) {
            User u = userService.fetchUser(userId);
            Date date = new Date();
            if (flightIdLong == null) {
                logger.error("Attempt to access site with null parameters!");
            }
            AuditRecord ar = new FlightAuditRecord(AuditActionType.ACCESS_INFORMATION, methodName,  gov.gtas.enumtype.Status.WEB_CALL, u.getUserId() + " ACCESSED FLIGHT ID " + flightIdLong, flightIdLong == null ? "" : flightIdLong.toString(), u, date, flightIdLong  );
            auditRecordRepository.save(ar);
        } else {
            logger.warn("Null user attempting web service call!");
        }
    }
}
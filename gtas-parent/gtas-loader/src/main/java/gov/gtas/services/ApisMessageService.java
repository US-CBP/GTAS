/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.EdifactMessage;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.MessageStatus;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.paxlst.PaxlstParserUNedifact;
import gov.gtas.parsers.paxlst.PaxlstParserUSedifact;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.util.LobUtils;

@Service
public class ApisMessageService extends MessageLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(ApisMessageService.class);

    @Autowired
    private ApisMessageRepository msgDao;

    private ApisMessage apisMessage;

    @Override
    public List<String> preprocess(String message) {
        return Arrays.asList(message);
    }
    
    @Override
    public MessageVo parse(String message) {
        apisMessage = new ApisMessage();
        apisMessage.setCreateDate(new Date());
        apisMessage.setStatus(MessageStatus.RECEIVED);
        apisMessage.setFilePath(filePath);
        
        MessageVo vo = null;
        try {            
            EdifactParser<ApisMessageVo> parser = null;
            if (isUSEdifactFile(message)) {
                parser = new PaxlstParserUSedifact();
            } else {
                parser = new PaxlstParserUNedifact();                
            }
    
            vo = parser.parse(message);
            loaderRepo.checkHashCode(vo.getHashCode());
            apisMessage.setRaw(LobUtils.createClob(vo.getRaw()));

            apisMessage.setStatus(MessageStatus.PARSED);
            apisMessage.setHashCode(vo.getHashCode());
            EdifactMessage em = new EdifactMessage();
            em.setTransmissionDate(vo.getTransmissionDate());
            em.setTransmissionSource(vo.getTransmissionSource());
            em.setMessageType(vo.getMessageType());
            em.setVersion(vo.getVersion());
            apisMessage.setEdifactMessage(em);

        } catch (Exception e) {
            handleException(e, MessageStatus.FAILED_PARSING);
            return null;
        } finally {
            createMessage(apisMessage);
        }
        
        return vo;
    }

    @Override
    public boolean load(MessageVo messageVo) {
        boolean success = true;
        try {
            ApisMessageVo m = (ApisMessageVo)messageVo;
            loaderRepo.processReportingParties(apisMessage, m.getReportingParties());
            loaderRepo.processFlightsAndPassengers(m.getFlights(), m.getPassengers(), 
                    apisMessage.getFlights(), apisMessage.getPassengers(), new ArrayList<FlightLeg>());
            apisMessage.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            success = false;
            handleException(e, MessageStatus.FAILED_LOADING);
        } finally {
            success &= createMessage(apisMessage);            
        }
        return success;
    }  

    private void handleException(Exception e, MessageStatus status) {
        apisMessage.setFlights(null);
        apisMessage.setStatus(status);
        String stacktrace = ErrorUtils.getStacktrace(e);
        apisMessage.setError(stacktrace);
        logger.error(stacktrace);
    }

    @Transactional
    private boolean createMessage(ApisMessage m) {
        boolean ret = true;
        try {
            msgDao.save(m);
        	indexer.indexApis(m);
        } catch (Exception e) {
            ret = false;
            handleException(e, MessageStatus.FAILED_LOADING);
            msgDao.save(m);
        }
        return ret;
    }

    private boolean isUSEdifactFile(String msg) {
        return (msg.contains("CDT") || msg.contains("PDT"));
    }
}

/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.EdifactMessage;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Pnr;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.pnrgov.PnrGovParser;
import gov.gtas.parsers.pnrgov.PnrUtils;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.repository.PnrRepository;
import gov.gtas.util.LobUtils;

@Service
public class PnrMessageService extends MessageLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(PnrMessageService.class);
   
    @Autowired
    private PnrRepository msgDao;
    
    private Pnr pnr;

    @Override
    public List<String> preprocess(String message) {
        return PnrUtils.getPnrs(message);
    }
    
    @Override
    public MessageVo parse(String message) {
        pnr = new Pnr();
        pnr.setCreateDate(new Date());
        pnr.setStatus(MessageStatus.RECEIVED);
        pnr.setFilePath(filePath);
        
        MessageVo vo = null;
        try {
            EdifactParser<PnrVo> parser = new PnrGovParser();
            vo = parser.parse(message);
            loaderRepo.checkHashCode(vo.getHashCode());
            pnr.setRaw(LobUtils.createClob(vo.getRaw()));

            pnr.setStatus(MessageStatus.PARSED);
            pnr.setHashCode(vo.getHashCode());            
            EdifactMessage em = new EdifactMessage();
            em.setTransmissionDate(vo.getTransmissionDate());
            em.setTransmissionSource(vo.getTransmissionSource());
            em.setMessageType(vo.getMessageType());
            em.setVersion(vo.getVersion());
            pnr.setEdifactMessage(em);
            
        } catch (Exception e) {
            handleException(e, MessageStatus.FAILED_PARSING);
            return null;
        } finally {
            createMessage(pnr);
        }

        return vo;
    }
    
    @Override
    public boolean load(MessageVo messageVo) {
        boolean success = true;
        try {
            PnrVo vo = (PnrVo)messageVo;
            // TODO: fix this, combine methods
            utils.convertPnrVo(pnr, vo);
            loaderRepo.processPnr(pnr, vo);
            loaderRepo.processFlightsAndPassengers(vo.getFlights(), vo.getPassengers(), 
                    pnr.getFlights(), pnr.getPassengers(), pnr.getFlightLegs());
            for (FlightLeg leg : pnr.getFlightLegs()) {
                leg.setPnr(pnr);
            }
            pnr.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            success = false;
            handleException(e, MessageStatus.FAILED_LOADING);
        } finally {
            success &= createMessage(pnr);            
        }
        return success;
    }

    private void handleException(Exception e, MessageStatus status) {
        // set all the collections to null so we can save the message itself
        pnr.setFlights(null);
        pnr.setPassengers(null);
        pnr.setFlightLegs(null);
        pnr.setCreditCards(null);
        pnr.setAddresses(null);
        pnr.setAgencies(null);
        pnr.setEmails(null);
        pnr.setFrequentFlyers(null);
        pnr.setPhones(null);

        pnr.setStatus(status);
        String stacktrace = ErrorUtils.getStacktrace(e);
        pnr.setError(stacktrace);
        logger.error(stacktrace);
    }

    @Transactional
    private boolean createMessage(Pnr m) {
        boolean ret = true;
        try {
            msgDao.save(m);
        	indexer.indexPnr(m);
        } catch (Exception e) {
            ret = false;
            handleException(e, MessageStatus.FAILED_LOADING);
            msgDao.save(m);
        }
        return ret;
    }
}
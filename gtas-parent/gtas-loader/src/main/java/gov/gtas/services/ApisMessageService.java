/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.EdifactMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FlightPax;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.paxlst.PaxlstParserUNedifact;
import gov.gtas.parsers.paxlst.PaxlstParserUSedifact;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.FlightPaxRepository;
import gov.gtas.util.LobUtils;

@Service
public class ApisMessageService extends MessageLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(ApisMessageService.class);

    @Autowired
    private ApisMessageRepository msgDao;

    @Autowired
    private FlightPaxRepository paxDao;

    @Autowired
    private LookUpRepository lookupRepo;
    
    private ApisMessage apisMessage;

    @Override
    public List<String> preprocess(String message) {
        return Arrays.asList(message);
    }
    
    @Override
    public MessageDto parse(MessageDto msgDto){
        ApisMessage apis = new ApisMessage();
        apis.setCreateDate(new Date());
        apis.setStatus(MessageStatus.RECEIVED);
        apis.setFilePath(msgDto.getFilepath());
        
        MessageVo vo = null;
        try {            
            EdifactParser<ApisMessageVo> parser = null;
            if (isUSEdifactFile(msgDto.getRawMsg())) {
                parser = new PaxlstParserUSedifact();
            } else {
                parser = new PaxlstParserUNedifact();                
            }
    
            vo = parser.parse(msgDto.getRawMsg());
            loaderRepo.checkHashCode(vo.getHashCode());
            apis.setRaw(LobUtils.createClob(vo.getRaw()));

            apis.setStatus(MessageStatus.PARSED);
            apis.setHashCode(vo.getHashCode());
            EdifactMessage em = new EdifactMessage();
            em.setTransmissionDate(vo.getTransmissionDate());
            em.setTransmissionSource(vo.getTransmissionSource());
            em.setMessageType(vo.getMessageType());
            em.setVersion(vo.getVersion());
            apis.setEdifactMessage(em);
            msgDto.setMsgVo(vo);

        } catch (Exception e) {
            handleException(e, MessageStatus.FAILED_PARSING, apis);
            return null;
        } finally {
            createMessage(apis);
        }
        msgDto.setApis(apis);
        return msgDto;
    }
    
	@Override
    public boolean load(MessageDto msgDto){
        boolean success = true;
        ApisMessage apis = msgDto.getApis();
        try {
            ApisMessageVo m = (ApisMessageVo)msgDto.getMsgVo();
            loaderRepo.processReportingParties(apis, m.getReportingParties());
            loaderRepo.processFlightsAndPassengers(m.getFlights(), m.getPassengers(), 
            		apis.getFlights(), apis.getPassengers(), new ArrayList<FlightLeg>(), msgDto.getPrimeFlightKey());
            createFlightPax(apis);
            apis.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            success = false;
            handleException(e, MessageStatus.FAILED_LOADING, msgDto.getApis());
        } finally {
            success &= createMessage(apis);            
        }
        return success;
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
            handleException(e, MessageStatus.FAILED_PARSING, apisMessage);
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
                    apisMessage.getFlights(), apisMessage.getPassengers(), new ArrayList<FlightLeg>(),"placeHolder"); //TODO: Placeholder string removed
            createFlightPax(apisMessage);
            apisMessage.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            success = false;
            handleException(e, MessageStatus.FAILED_LOADING, apisMessage);
        } finally {
            success &= createMessage(apisMessage);            
        }
        return success;
    }  

    private void handleException(Exception e, MessageStatus status, ApisMessage apisMessage) {
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
            if (useIndexer) {
            	indexer.indexApis(m);
            }
        } catch (Exception e) {
            ret = false;
            handleException(e, MessageStatus.FAILED_LOADING, m);
            msgDao.save(m);
        }
        return ret;
    }

    private boolean isUSEdifactFile(String msg) {
    	//review of Citizenship from foreign APIS Issue #387 fix
    	//Both UNS and PDT are mandatory for USEDIFACT.CDT doesn't exist in spec
    	if(((msg.contains("PDT+P")) || (msg.contains("PDT+V")) || (msg.contains("PDT+A")))
    			&& (msg.contains("UNS"))){
    		return true;
    	}
        //return (msg.contains("CDT") || msg.contains("PDT"));
    	return false;
    }
    
    private void createFlightPax(ApisMessage apisMessage){
    	Set<Flight> flights=apisMessage.getFlights();
    	String homeAirport=lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
    	for(Flight f : flights){
    		for(Passenger p:f.getPassengers()){
    			FlightPax fp=new FlightPax();
    			fp.getApisMessage().add(apisMessage);
    			fp.setDebarkation(p.getDebarkation());
    			fp.setDebarkationCountry(p.getDebarkCountry());
    			fp.setEmbarkation(p.getEmbarkation());
    			fp.setEmbarkationCountry(p.getEmbarkCountry());
    			fp.setPortOfFirstArrival(f.getDestination());
    			fp.setMessageSource("APIS");
    			fp.setFlight(f);
    			fp.setResidenceCountry(p.getResidencyCountry());
    			fp.setTravelerType(p.getPassengerType());
    			fp.setPassenger(p);
    			fp.setReservationReferenceNumber(p.getReservationReferenceNumber());
    			int bCount=0;
    			if(StringUtils.isNotBlank(p.getBagNum())){
    				try {
						bCount=Integer.parseInt(p.getBagNum());
					} catch (NumberFormatException e) {
						bCount=0;
					}
    			}
    			fp.setBagCount(bCount);
    			try {
					double weight=p.getTotalBagWeight() == null?0:Double.parseDouble(p.getTotalBagWeight());
					fp.setBagWeight(weight);
					if(weight > 0 && bCount >0){
						fp.setAverageBagWeight(Math.round(weight/bCount));
					}
				} catch (NumberFormatException e) {

				}
    			if(StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())){
    				if(homeAirport.equalsIgnoreCase(fp.getDebarkation()) || homeAirport.equalsIgnoreCase(fp.getEmbarkation())){
    					p.setTravelFrequency(p.getTravelFrequency()+1);
    				}
    			}
    			apisMessage.addToFlightPax(fp);
    		}
    	}
    }
}

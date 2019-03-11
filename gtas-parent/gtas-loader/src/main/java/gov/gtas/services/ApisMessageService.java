/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.*;


import gov.gtas.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.paxlst.PaxlstParserUNedifact;
import gov.gtas.parsers.paxlst.PaxlstParserUSedifact;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.util.LobUtils;

@Service
public class ApisMessageService extends MessageLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(ApisMessageService.class);

    @Autowired
    private ApisMessageRepository msgDao;

    @Autowired
    private LookUpRepository lookupRepo;

    @Override
    public List<String> preprocess(String message) {
        return Collections.singletonList(message);
    }
    
    @Override
    public MessageDto parse(MessageDto msgDto){
        ApisMessage apis = new ApisMessage();
        apis.setCreateDate(new Date());
        apis.setFilePath(msgDto.getFilepath());
        apis = msgDao.save(apis);
        MessageStatus messageStatus = new MessageStatus(apis.getId(), MessageStatusEnum.RECEIVED);
        msgDto.setMessageStatus(messageStatus);
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

            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.PARSED);
            apis.setHashCode(vo.getHashCode());
            EdifactMessage em = new EdifactMessage();
            em.setTransmissionDate(vo.getTransmissionDate());
            em.setTransmissionSource(vo.getTransmissionSource());
            em.setMessageType(vo.getMessageType());
            em.setVersion(vo.getVersion());
            apis.setEdifactMessage(em);
            msgDto.setMsgVo(vo);

        } catch (Exception e) {
            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
            handleException(e , apis);
            return null;
        } finally {
            if (!createMessage(apis)) {
                msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
            }
        }
        msgDto.setApis(apis);
        return msgDto;
    }
    
	@Override
    public MessageStatus load(MessageDto msgDto){
        msgDto.getMessageStatus().setSuccess(true);
        ApisMessage apis = msgDto.getApis();
        try {
            ApisMessageVo m = (ApisMessageVo)msgDto.getMsgVo();
            loaderRepo.processReportingParties(apis, m.getReportingParties());

            Flight primeFlight = loaderRepo.processFlightsAndBookingDetails(
                    m.getFlights(),
            		apis.getFlights(),
                    new ArrayList<>(),
                    msgDto.getPrimeFlightKey(),
                    new HashSet<>());

            CreatedAndOldPassengerInformation createdAndOldPassengerInformation = loaderRepo.makeNewPassengerObjects(
                    primeFlight,
                    m.getPassengers(),
                    apis.getPassengers(),
                    new HashSet<>(),
                    apis);

            int createdPassengers = loaderRepo.createPassengers(createdAndOldPassengerInformation.getNewPax(), apis.getPassengers(), primeFlight, new HashSet<>());
            loaderRepo.updateFlightPassengerCount(primeFlight, createdPassengers);
            createFlightPax(apis);
            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.LOADED);

        } catch (Exception e) {
            msgDto.getMessageStatus().setSuccess(false);
            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
            handleException(e, msgDto.getApis());
            logger.error("ERROR", e);
        } finally {
            msgDto.getMessageStatus().setSuccess(createMessage(apis));
        }
        return msgDto.getMessageStatus();
	}
    
    @Override
    public MessageVo parse(String message) {
        return null; //unused
    }

    @Override
    public boolean load(MessageVo messageVo) {
        return false;
    }  

    private void handleException(Exception e, ApisMessage apisMessage) {
        apisMessage.setFlights(null);
        String stacktrace = ErrorUtils.getStacktrace(e);
        apisMessage.setError(stacktrace);
        logger.error(stacktrace);
    }

    boolean createMessage(ApisMessage m) {
        boolean ret = true;
        try {
           m = msgDao.save(m);
       /*     if (useIndexer) {
            	indexer.indexApis(m);
            }
       */ } catch (Exception e) {
            ret = false;
            handleException(e, m);
            try {
                msgDao.save(m);
            } catch (Exception ignored) {}
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
    		for(Passenger p:apisMessage.getPassengers()){
    			FlightPax fp=new FlightPax();
    			fp.getApisMessage().add(apisMessage);
    			fp.setDebarkation(p.getPassengerTripDetails().getDebarkation());
    			fp.setDebarkationCountry(p.getPassengerTripDetails().getDebarkCountry());
    			fp.setEmbarkation(p.getPassengerTripDetails().getEmbarkation());
    			fp.setEmbarkationCountry(p.getPassengerTripDetails().getEmbarkCountry());
    			fp.setPortOfFirstArrival(f.getDestination());
    			fp.setMessageSource("APIS");
    			fp.setFlight(f);
    			fp.setFlightId(f.getId());
    			fp.setResidenceCountry(p.getPassengerDetails().getResidencyCountry());
    			fp.setTravelerType(p.getPassengerDetails().getPassengerType());
    			fp.setPassenger(p);
    			fp.setPassengerId(p.getId());
    			fp.setReservationReferenceNumber(p.getPassengerTripDetails().getReservationReferenceNumber());
    			int bCount=0;
    			if(StringUtils.isNotBlank(p.getPassengerTripDetails().getBagNum())){
    				try {
						bCount=Integer.parseInt(p.getPassengerTripDetails().getBagNum());
					} catch (NumberFormatException e) {
						bCount=0;
					}
    			}
    			fp.setBagCount(bCount);
    			try {
					double weight=p.getPassengerTripDetails().getTotalBagWeight() == null?0:Double.parseDouble(p.getPassengerTripDetails().getTotalBagWeight());
					fp.setBagWeight(weight);
					if(weight > 0 && bCount >0){
						fp.setAverageBagWeight(Math.round(weight/bCount));
					}
				} catch (NumberFormatException e) {

				}
    			if(StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())){
    				if(homeAirport.equalsIgnoreCase(fp.getDebarkation()) || homeAirport.equalsIgnoreCase(fp.getEmbarkation())){
    					p.getPassengerTripDetails().setTravelFrequency(p.getPassengerTripDetails().getTravelFrequency()+1);
    				}
    			}
    			apisMessage.addToFlightPax(fp);
    		}
    	}
    }
}

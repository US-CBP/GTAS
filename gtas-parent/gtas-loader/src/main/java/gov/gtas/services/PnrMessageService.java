/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Bag;
import gov.gtas.model.CodeShareFlight;
import gov.gtas.model.DwellTime;
import gov.gtas.model.EdifactMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FlightPax;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.PnrGovParser;
import gov.gtas.parsers.pnrgov.PnrUtils;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.util.LobUtils;

@Service
public class PnrMessageService extends MessageLoaderService {
    private static final Logger logger = LoggerFactory.getLogger(PnrMessageService.class);
   
    @Autowired
    private PnrRepository msgDao;
    
    @Autowired
    private LoaderUtils utils;
    
    @Autowired
    private LookUpRepository lookupRepo;

    //private Pnr pnr;

    @Override
    public List<String> preprocess(String message) {
        return PnrUtils.getPnrs(message);
    }
    
    @Override
    public MessageDto parse(MessageDto msgDto) {
    	logger.debug("@ parse");
    	long startTime = System.nanoTime();
    	Pnr pnr = new Pnr();
        pnr.setCreateDate(new Date());
        pnr.setStatus(MessageStatus.RECEIVED);
        pnr.setFilePath(msgDto.getFilepath());
        
        MessageVo vo = null;
        try {
            EdifactParser<PnrVo> parser = new PnrGovParser();
            vo = parser.parse(msgDto.getRawMsg());
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
            msgDto.setMsgVo(vo);
        } catch (Exception e) {
            handleException(e, MessageStatus.FAILED_PARSING, pnr);
            return null;
        } finally {
            createMessage(pnr);
        }
        msgDto.setPnr(pnr);
        logger.debug("load time = "+(System.nanoTime()-startTime)/1000000);
        return msgDto;
    }
    
    @Override
    public boolean load(MessageDto msgDto) {
    	logger.debug("@ load");
    	long startTime = System.nanoTime();
        boolean success = true;
        Pnr pnr = msgDto.getPnr();
        try {
            PnrVo vo = (PnrVo)msgDto.getMsgVo();
            // TODO: fix this, combine methods
            utils.convertPnrVo(pnr, vo);
            loaderRepo.processPnr(pnr, vo);
            loaderRepo.processFlightsAndPassengers(vo.getFlights(), vo.getPassengers(), 
                    pnr.getFlights(), pnr.getPassengers(), pnr.getFlightLegs(), msgDto.getPrimeFlightKey());
            
            // update flight legs
           	for (FlightLeg leg : pnr.getFlightLegs()) {
               	leg.setPnr(pnr);
            }
            calculateDwellTimes(pnr);
            updatePaxEmbarkDebark(pnr);
            loaderRepo.createBagsFromPnrVo(vo,pnr);
            loaderRepo.createFormPfPayments(vo,pnr);
            setCodeShareFlights(pnr);
            //if(vo.getBags() != null && vo.getBags().size() >0 ){
            createFlightPax(pnr);
            //}
            pnr.setStatus(MessageStatus.LOADED);

        } catch (Exception e) {
            success = false;
            handleException(e, MessageStatus.FAILED_LOADING, pnr);
        } finally {
            success &= createMessage(pnr);            
        }
        logger.debug("load time = "+(System.nanoTime()-startTime)/1000000);
        return success;
    }
    

    private void updatePaxEmbarkDebark(Pnr pnr) throws ParseException {
    	logger.debug("@ updatePaxEmbarkDebark");
    	long startTime = System.nanoTime();
    	List<FlightLeg> legs = pnr.getFlightLegs();
    	if (CollectionUtils.isEmpty(legs)) {
    		return;
    	}
    	String embark = legs.get(0).getFlight().getOrigin();
    	Date firstDeparture=legs.get(0).getFlight().getEtd();
    	String debark = legs.get(legs.size() - 1).getFlight().getDestination();
    	Date finalArrival=legs.get(legs.size() - 1).getFlight().getEta();
    	//Origin / Destination Country Issue #356 code fix.
    	if(legs.size() <=2 && (embark.equals(debark))){
    		debark=legs.get(0).getFlight().getDestination();
    		finalArrival=legs.get(0).getFlight().getEta();
    	}
    	else if(legs.size() >2 && (embark.equals(debark))){
    		DwellTime d=getMaxDwelltime(pnr);
    		if(d != null && d.getFlyingTo() != null){
    			debark=d.getFlyingTo();
    			finalArrival=d.getArrivalTime();
    		}
    	}
    	setTripDurationTimeForPnr(pnr,firstDeparture,finalArrival);
    	for (Passenger p : pnr.getPassengers()) {
    		p.setEmbarkation(embark);
    		Airport airport = utils.getAirport(embark);
    		if (airport != null) {
    			p.setEmbarkCountry(airport.getCountry());
    		}
    		
    		p.setDebarkation(debark);
    		airport = utils.getAirport(debark);
    		if (airport != null) {
    			p.setDebarkCountry(airport.getCountry());
    		}
    	}
    	logger.debug("updatePaxEmbarkDebark time = "+(System.nanoTime()-startTime)/1000000);
    }
    
    private void calculateDwellTimes(Pnr pnr){
    	logger.debug("@ calculateDwellTimes");
    	long startTime = System.nanoTime();
    	List<FlightLeg> legs=pnr.getFlightLegs();
        if (CollectionUtils.isEmpty(legs)) {
        	return;
        }
        
    	Flight firstFlight=null;
    	Flight secondFlight=null;
    	Flight thirdFlight=null;
    	Flight fourthFlight=null;
    	Flight fifthFlight=null;
    	Flight sixthFlight=null;
    	Flight seventhFlight=null;
    	Flight eighthFlight=null;
    	Flight ninethFlight=null;
    	Flight tenthFlight=null;
    	for(int i=0;i<legs.size();i++){
            switch (i) {
            case 0:
            	firstFlight=legs.get(0).getFlight();
                break;
            case 1:
            	secondFlight=legs.get(1).getFlight();
                break;
            case 2:
            	thirdFlight=legs.get(2).getFlight();
                break;
            case 3:
            	fourthFlight=legs.get(3).getFlight();
                break;
            case 4:
            	fifthFlight=legs.get(4).getFlight();
            	break;
            case 5:
            	sixthFlight=legs.get(5).getFlight();
            	break;
            case 6:
            	seventhFlight=legs.get(6).getFlight();
            	break;
            case 7:
            	eighthFlight=legs.get(7).getFlight();
            	break;
            case 8:
            	ninethFlight=legs.get(8).getFlight();
            	break;
            case 9:
            	tenthFlight=legs.get(9).getFlight();
            	break;
            } 
 
    	}
    	setDwelTime(firstFlight,secondFlight,pnr);
    	setDwelTime(secondFlight,thirdFlight,pnr);
    	setDwelTime(thirdFlight,fourthFlight,pnr);
    	setDwelTime(fourthFlight,fifthFlight,pnr);
    	setDwelTime(fifthFlight,sixthFlight,pnr);
    	setDwelTime(sixthFlight,seventhFlight,pnr);
    	setDwelTime(seventhFlight,eighthFlight,pnr);
    	setDwelTime(eighthFlight,ninethFlight,pnr);
    	setDwelTime(ninethFlight,tenthFlight,pnr);
    	logger.debug("calculateDwellTime time = "+(System.nanoTime()-startTime)/1000000);
    }
    private void setDwelTime(Flight firstFlight,Flight secondFlight,Pnr pnr){
 
    	if(firstFlight != null && secondFlight != null 
    			&& firstFlight.getDestination().equalsIgnoreCase(secondFlight.getOrigin())
    			&& !(secondFlight.getDestination().equals( firstFlight.getOrigin()))
    			&& (firstFlight.getEta()!=null && secondFlight.getEtd() != null)){
    		
    	   	DwellTime d =new DwellTime(firstFlight.getEta(),secondFlight.getEtd(),secondFlight.getOrigin(),pnr);
    		d.setFlyingFrom(firstFlight.getOrigin());
    		d.setFlyingTo(secondFlight.getDestination());
    		pnr.addDwellTime(d);
    	}
    }

    private void setCodeShareFlights(Pnr pnr){
    	Set<Flight> flights=pnr.getFlights();
    	Set<CodeShareFlight> codeshares=pnr.getCodeshares();
    	for(Flight f : flights){
    		for(CodeShareFlight cs : codeshares){
    			if(cs.getOperatingFlightNumber().equals(f.getFullFlightNumber())){
    				cs.setOperatingFlightId(f.getId());
    			}
    		}
    	}
    }
    
    private DwellTime getMaxDwelltime(Pnr pnr) {
        Double highest = 0.0;
        DwellTime dt = new DwellTime();
        for (DwellTime d : pnr.getDwellTimes()) {
            highest = d.getDwellTime();
            if (highest == null) {
                continue;
            }
            dt = d;
            for (DwellTime dChk : pnr.getDwellTimes()) {
                if (dChk.getDwellTime() != null) {
                    if (dChk.getDwellTime().equals(highest) || dChk.getDwellTime() < highest) {
                        continue;
                    } else if ((dChk.getDwellTime() > highest) && (highest > 12)) {
                        return dChk;
                    }
                }

            }
        }
        return dt;
    }
    
    private void setTripDurationTimeForPnr(Pnr pnr,Date firstDeparture,Date finalArrival){
    	if(firstDeparture != null && finalArrival != null){
	    	long diff = finalArrival.getTime() - firstDeparture.getTime(); 
	    	if(diff > 0){
		    	int minutes=(int)TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
		    	DecimalFormat df = new DecimalFormat("#.##");      
		    	pnr.setTripDuration(Double.valueOf(df.format((double) minutes / 60)));
	    	}
    	}

    }
    private void handleException(Exception e, MessageStatus status, Pnr pnr) {
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
        pnr.setDwellTimes(null);
        pnr.setPaymentForms(null);
        pnr.setStatus(status);
        String stacktrace = ErrorUtils.getStacktrace(e);
        pnr.setError(stacktrace);
        logger.error(stacktrace);
    }
    
    @Transactional
    private boolean createMessage(Pnr m) {
        boolean ret = true;
        logger.debug("@createMessage");
        long startTime = System.nanoTime();
        try {
        	msgDao.save(m);
            if (useIndexer) {
            	indexer.indexPnr(m);
            }
        } catch (Exception e) {
            ret = false;
            handleException(e, MessageStatus.FAILED_LOADING, m);
            	msgDao.save(m);
        }
        logger.debug("createMessage time = "+(System.nanoTime()-startTime)/1000000);
        return ret;
    }
    
    private void createFlightPax(Pnr pnr){
    	logger.debug("@ createFlightPax");
    	long startTime = System.nanoTime();
    	Set<Flight> flights=pnr.getFlights();
    	String homeAirport=lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
    	for(Flight f : flights){
    		for(Passenger p : f.getPassengers()){
    			FlightPax fp=new FlightPax();
    			fp.setDebarkation(p.getDebarkation());
    			fp.setDebarkationCountry(p.getDebarkCountry());
    			fp.setEmbarkation(p.getEmbarkation());
    			fp.setEmbarkationCountry(p.getEmbarkCountry());
    			fp.setPortOfFirstArrival(f.getDestination());
    			fp.setMessageSource("PNR");
    			fp.setFlight(f);
    			fp.setResidenceCountry(p.getResidencyCountry());
    			fp.setTravelerType(p.getPassengerType());
    			fp.setPassenger(p);
    			fp.setReservationReferenceNumber(p.getReservationReferenceNumber());
    			int passengerBags=0;
    			if(StringUtils.isNotBlank(p.getBagNum())){
    				try {
    					passengerBags=Integer.parseInt(p.getBagNum());
					} catch (NumberFormatException e) {
						passengerBags=0;
					}
    			}
    			fp.setBagCount(passengerBags);
    			try {
					if(StringUtils.isNotBlank(p.getTotalBagWeight()) && (passengerBags >0)){
						Double weight=Double.parseDouble(p.getTotalBagWeight());
						fp.setAverageBagWeight(Math.round(weight/passengerBags));
						fp.setBagWeight(weight);
					}
				} catch (NumberFormatException e) {
					// Do nothing
				}
    			if(StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())){
    				if(homeAirport.equalsIgnoreCase(fp.getDebarkation()) || homeAirport.equalsIgnoreCase(fp.getEmbarkation())){
    					p.setTravelFrequency(p.getTravelFrequency()+1);
    				}
    			}
    			p.getFlightPaxList().add(fp);
    		}
    	}
    	logger.debug("createFlightPax time = "+(System.nanoTime()-startTime)/1000000);
    }

	@Override
	public MessageVo parse(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean load(MessageVo messageVo) {
		// TODO Auto-generated method stub
		return false;
	}
}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import gov.gtas.model.BookingDetail;
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
                    pnr.getFlights(), pnr.getPassengers(), pnr.getFlightLegs(), msgDto.getPrimeFlightKey(), pnr.getBookingDetails(), pnr.getId());
            
            // update flight legs
           	for (FlightLeg leg : pnr.getFlightLegs()) {
               	leg.setPnr(pnr);
            }
           	
           	for (BookingDetail bD : pnr.getBookingDetails()){
           		bD.getPnrs().add(pnr);
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
    	String embark,debark = "";
    	Date firstDeparture, finalArrival = null;
    	
    	//If flight is null in either of these checks, then the particular leg must be comprised of a booking detail...
    	if(legs.get(0).getFlight() != null){
    		embark = legs.get(0).getFlight().getOrigin();
    		firstDeparture=legs.get(0).getFlight().getEtd();
    	}else{ //use BD instead
    		embark = legs.get(0).getBookingDetail().getOrigin();
    		firstDeparture=legs.get(0).getBookingDetail().getEtd();
    	}
    	
    	if(legs.get(legs.size()-1).getFlight() != null){
    		debark = legs.get(legs.size() - 1).getFlight().getDestination();
    		finalArrival=legs.get(legs.size() - 1).getFlight().getEta();
    	} else{ //use BD instead
    		debark = legs.get(legs.size() - 1).getBookingDetail().getDestination();
    		finalArrival=legs.get(legs.size() - 1).getBookingDetail().getEta();
    	}
    	
    	//Origin / Destination Country Issue #356 code fix.
    	if(legs.size() <=2 && (embark.equals(debark))){
    		if(legs.get(0).getFlight() != null){
    			debark=legs.get(0).getFlight().getDestination();
    			finalArrival=legs.get(0).getFlight().getEta();
    		}else{ //use BD instead
    			debark=legs.get(0).getBookingDetail().getDestination();
    			finalArrival=legs.get(0).getBookingDetail().getEta();
    		}
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
        
    	for(int i=0;i<legs.size();i++){
        	if(i+1 < legs.size()){ //If the 'next' leg actually exists
        		//4 different combinations of flights and booking details n^2, where n = 2. FxF, FxB, BxF, BxB. Order matters due to time calc
	    		if(legs.get(i).getFlight() != null){
	    			if(legs.get(i+1).getFlight() != null){ //FxF
	    				utils.setDwellTime(legs.get(i).getFlight(), legs.get(i+1).getFlight(),pnr);
	    			} else{ //next leg is a booking detail //FxB
	    				utils.setDwellTime(legs.get(i).getFlight(),legs.get(i+1).getBookingDetail(), pnr);
	    			}
	    		} else if(legs.get(i+1).getFlight() != null){ //first leg is booking detail BxF
	    			utils.setDwellTime(legs.get(i).getBookingDetail(),legs.get(i+1).getFlight(),pnr);
	    		} else{ //both legs are booking details BxB
	    			utils.setDwellTime(legs.get(i).getBookingDetail(),legs.get(i+1).getBookingDetail(),pnr);
	    		}
        	}
    	}
    	logger.debug("calculateDwellTime time = "+(System.nanoTime()-startTime)/1000000);
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
    	boolean oneFlight=false;
    	List<FlightPax> paxRecords=new ArrayList<>();
    	long startTime = System.nanoTime();
    	Set<Flight> flights=pnr.getFlights();
    	String homeAirport=lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
    	int pnrBagCount=0;
    	double pnrBagWeight=0.0;
    	for(Flight f : flights){
    		for(Passenger p : pnr.getPassengers()){
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
    			pnrBagCount=pnrBagCount+passengerBags;
    			try {
					if(StringUtils.isNotBlank(p.getTotalBagWeight()) && (passengerBags >0)){
						Double weight=Double.parseDouble(p.getTotalBagWeight());
						fp.setAverageBagWeight(Math.round(weight/passengerBags));
						fp.setBagWeight(weight);
						pnrBagWeight=pnrBagWeight+weight;
					}
				} catch (NumberFormatException e) {
					// Do nothing
				}
    			if(StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())){
    				if(homeAirport.equalsIgnoreCase(fp.getDebarkation()) || homeAirport.equalsIgnoreCase(fp.getEmbarkation())){
    					p.setTravelFrequency(p.getTravelFrequency()+1);
    				}
    			}
    			setHeadPool( fp,p,f);
    			p.getFlightPaxList().add(fp);
    			paxRecords.add(fp);
    		}
    		if(!oneFlight) {
    			setBagDetails(paxRecords,pnr);
    		}
    		oneFlight=true;
    	}
    	logger.debug("createFlightPax time = "+(System.nanoTime()-startTime)/1000000);
    }

    private void setBagDetails(List<FlightPax> paxes,Pnr pnr) {
    	int pnrBagCount=0;
    	double pnrBagWeight=0.0;
    	for(FlightPax fp:paxes) {
    		pnrBagCount=pnrBagCount+fp.getBagCount();
    		pnrBagWeight=pnrBagWeight+fp.getBagWeight();
    	}
   		pnr.setBagCount(pnrBagCount);
		pnr.setBaggageWeight(pnrBagWeight);
		pnr.setTotal_bag_count(pnrBagCount);
		pnr.setTotal_bag_weight((float)pnrBagWeight);
    }
    private void setHeadPool(FlightPax fp,Passenger p,Flight f){
    	try {
			if(p.getBags() != null && p.getBags().size() >0){
				for(Bag b:p.getBags()){
					if(b.isHeadPool() && b.getFlight().getId().equals(f.getId()) 
							&& b.getPassenger().getId().equals(p.getId())){
						fp.setHeadOfPool(true);
						break;
					}
				}
			}
		} catch (Exception e) {
			//Skip..Do nothing..
		}
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

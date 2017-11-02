package gov.gtas.services.matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.Passenger;
import gov.gtas.model.PaxWatchlistLink;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PaxWatchlistLinkRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.services.FlightService;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

@Service
 public class MatchingServiceImpl implements MatchingService {
	private DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
	private JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
	@Autowired
	private PaxWatchlistLinkRepository paxWatchlistLinkRepository;
	@Autowired
	private WatchlistItemRepository watchlistItemRepository;
	@Autowired
	private PassengerRepository passengerRepository;
	private ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = LoggerFactory
	            .getLogger(MatchingService.class);
	
	private float stringMatcher(String str1, String str2) {
		if(doubleMetaphone.isDoubleMetaphoneEqual(str1, str2)) {
			return 1.0f;
		}
		return jaroWinklerDistance.getDistance(str1, str2);
	}
	private PaxWatchlistLinkVo convertToVo(PaxWatchlistLink pwLink) {
		return convertToVo(pwLink.getPercentMatch(), pwLink.getLastRunTimestamp(), pwLink.getVerifiedStatus(), pwLink.getId(), pwLink.getWatchlistItem());
	}
	private PaxWatchlistLinkVo convertToVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus, long paxId, WatchlistItem item) {
		 try{
             WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(),
                 WatchlistItemSpec.class);
             String firstName = new String();
             String lastName = new String();
             String dob = new String();
             
             for(WatchlistTerm term: itemSpec.getTerms()) {
             	if(term.getField().equals("firstName")) {
             		firstName = term.getValue();
             	}
             	if(term.getField().equals("lastName")) {
             		lastName = term.getValue();
             	}
             	if(term.getField().equals("dob")) {
             		dob = term.getValue();
             	}
             }  
             return new PaxWatchlistLinkVo(percentMatch, lastRunTimestamp, verifiedStatus,paxId, item.getId(), firstName, lastName, dob);
         } catch(IOException ioe){
             logger.error("ConvertToVo"
                     + ioe.getMessage());
             throw ErrorHandlerFactory
                     .getErrorHandler()
                     .createException(
                             CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                             item.getId(), "getWatchListMatchByPaxId");

         }
	}
	
	public List<PaxWatchlistLinkVo> findByPassengerId(Long id) {
		List<PaxWatchlistLinkVo> pwlList = new ArrayList<PaxWatchlistLinkVo>();
		for(PaxWatchlistLink pwLink: paxWatchlistLinkRepository.findByPassengerId(id)) {
			pwlList.add(convertToVo(pwLink));
		}
		return pwlList;
	}
	
	public void saveWatchListMatchByPaxId(Long id){
		final float threshold = 70.0f;
		Passenger passenger = passengerRepository.getPassengerById(id);
		List<WatchlistItem> passengerWatchlist = watchlistItemRepository.getItemsByWatchlistName("Passenger");
		for (WatchlistItem item : passengerWatchlist) {
            try{
                WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(),
                    WatchlistItemSpec.class);
                float percentMatch = 0;
                for(WatchlistTerm term: itemSpec.getTerms()) {
                	if(term.getField().equals("firstName")) {
                		percentMatch+=stringMatcher(passenger.getFirstName(), term.getValue());
                	}
                	if(term.getField().equals("lastName")) {
                		percentMatch+=stringMatcher(passenger.getLastName(), term.getValue());
                	}
                	//TODO Incorporate Date into matchPercentage
//                	if(!term.getField().equals("dob")) {
//                		percentMatch=0;
//                	}
                }
                percentMatch*=100;
            	percentMatch = percentMatch==0?percentMatch:percentMatch/2.0f;
                if(percentMatch>=threshold) {
                	Date lastRunTimestamp = new Date();
                	paxWatchlistLinkRepository.savePaxWatchlistLink(lastRunTimestamp,percentMatch, 0, id, item.getId());
                }
                
            } catch(IOException ioe){
                logger.error("Matching Service"
                        + ioe.getMessage());
                throw ErrorHandlerFactory
                        .getErrorHandler()
                        .createException(
                                CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                                item.getId(), "getWatchListMatchByPaxId");

            }
		}
	}
}

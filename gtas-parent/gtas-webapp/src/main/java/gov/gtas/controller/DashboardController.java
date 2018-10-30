/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.DashboardMessageStats;
import gov.gtas.model.Flight;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.YTDAirportStatistics;
import gov.gtas.model.YTDRules;
import gov.gtas.services.FlightService;
import gov.gtas.services.AirportService;
import gov.gtas.services.HitsSummaryService;
import gov.gtas.services.MessageService;
import gov.gtas.services.MessageStatisticsService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.PnrService;
import gov.gtas.services.YTDStatisticsService;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerService paxService;

    @Autowired
    private AirportService airportService;

    @Autowired
    private HitsSummaryService hitsSummaryService;

    @Autowired
    private MessageService apisMessageService;

    @Autowired
    private MessageStatisticsService messageStatsService;


    @Autowired
    private YTDStatisticsService ytdStatsService;

    @Autowired
    private PnrService pnrService;

    private static final String commaStringToAppend = ", ";
    private static final String EMPTY_STRING="";

	/**
	 * Gets the flights, passengers and hits count.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the flights, passengers and hits count
	 * @throws ParseException
	 *             the parse exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getFlightsAndPassengersAndHitsCountInbound")
	public Map<String, Object> getFlightsAndPassengersAndHitsCountInbound(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) {
		// passed in arguments not used currently.

		List<Flight> flightList = flightService.getFlightsThreeDaysForwardInbound();

		Integer paxCount = flightList.stream().mapToInt(Flight::getPassengerCount).sum();

        HitAndAirportExtractor hitAndAirportExtractor = new HitAndAirportExtractor(flightList).invoke();
        int ruleHits = hitAndAirportExtractor.getRuleHits();
        long watchListHits = hitAndAirportExtractor.getWatchListHits();

        HashMap<String, Object> flightsAndPassengersAndHitsCount = new HashMap<>();
        flightsAndPassengersAndHitsCount.put("flightsCount", new AtomicInteger(
				flightList.size()));
		flightsAndPassengersAndHitsCount.put("ruleHitsCount",
                new AtomicInteger(ruleHits));
		flightsAndPassengersAndHitsCount.put("watchListCount",
                new AtomicLong(watchListHits));
		flightsAndPassengersAndHitsCount.put("passengersCount",
                new AtomicInteger(paxCount));
        flightsAndPassengersAndHitsCount.put("flightsList",
                hitAndAirportExtractor.getAirportList());

		return flightsAndPassengersAndHitsCount;
	}

    /**
     * Gets the flights, passengers and hits count.
     *
     * @param startDate
     *            the start date
     * @param endDate
     *            the end date
     * @return the flights, passengers and hits count
     * @throws ParseException
     *             the parse exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getFlightsAndPassengersAndHitsCountOutbound")
    public Map<String, Object> getFlightsAndPassengersAndHitsCountOutbound(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        // passed in arguments not used currently.

        List<Flight> flightList = flightService.getFlightsThreeDaysForwardOutbound();

        int paxCount = flightList.stream().mapToInt(Flight::getPassengerCount).sum();
        HitAndAirportExtractor hitAndAirportExtractor = new HitAndAirportExtractor(flightList).invoke();
        int ruleHits = hitAndAirportExtractor.getRuleHits();
        long watchListHits = hitAndAirportExtractor.getWatchListHits();

        HashMap<String, Object> flightsAndPassengersAndHitsCount = new HashMap<>();
        flightsAndPassengersAndHitsCount.put("flightsCount", new AtomicInteger(
                flightList.size()));
        flightsAndPassengersAndHitsCount.put("ruleHitsCount",
                new AtomicInteger(ruleHits));
        flightsAndPassengersAndHitsCount.put("watchListCount",
                new AtomicLong(watchListHits));
        flightsAndPassengersAndHitsCount.put("passengersCount",
                new AtomicInteger(paxCount));
        flightsAndPassengersAndHitsCount.put("flightsList",
                hitAndAirportExtractor.getAirportList());

        return flightsAndPassengersAndHitsCount;
    }

    class MessageCount implements Serializable {
        String STATE = EMPTY_STRING;
        String API = EMPTY_STRING;
        String PNR = EMPTY_STRING;

        public MessageCount(String STATE, String API, String PNR) {
            this.STATE = STATE;
            this.API = API;
            this.PNR = PNR;
        }

        public String getSTATE() {
            return STATE;
        }

        public void setSTATE(String STATE) {
            this.STATE = STATE;
        }

        public String getAPI() {
            return API;
        }

        public void setAPI(String API) {
            this.API = API;
        }

        public String getPNR() {
            return PNR;
        }

        public void setPNR(String PNR) {
            this.PNR = PNR;
        }
    }


    class AirportVO implements Serializable {
	    Double longitude = 0.0;
	    Double latitude = 0.0;
	    String airportCodeStr = EMPTY_STRING;
	    String airportName = EMPTY_STRING;
	    boolean hits = false;

        public AirportVO(Double longitude, Double latitude, String airportCodeStr, String airportName, boolean hits) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.airportCodeStr = airportCodeStr;
            this.airportName = airportName;
            this.hits = hits;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getAirportCodeStr() {
            return airportCodeStr;
        }

        public void setAirportCodeStr(String airportCodeStr) {
            this.airportCodeStr = airportCodeStr;
        }

        public String getAirportName() {
            return airportName;
        }

        public void setAirportName(String airportName) {
            this.airportName = airportName;
        }

        public boolean isHits() {
            return hits;
        }

        public void setHits(boolean hits) {
            this.hits = hits;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getMessagesCount")
    public ArrayList<MessageCount> getMessagesCount(
            @RequestParam(value="startDate", required=false) String startDate,
            @RequestParam(value="endDate", required=false) String endDate) throws ParseException{

        ArrayList<MessageCount> _apisAndPnrCount = new ArrayList<MessageCount>();
        String stateLabel = "State";
        String ApiMessages = "API 148";
        String PnrMessages = "PNR 252";
        String[] displayTokens = new String[]   {"12 - 1 AM","1 - 2 AM","2 - 3 AM","3 - 4 AM","4 - 5 AM","5 - 6 AM","6 - 7 AM",
                                                    "7 - 8 AM","8 - 9 AM","9 - 10 AM","10 - 11 AM","11 - 12 PM","12 - 1 PM",
                                                    "1 - 2 PM","2 - 3 PM","3 - 4 PM","4 - 5 PM","5 - 6 PM","6 - 7 PM",
                                                    "7 - 8 PM","8 - 9 PM","9 - 10 PM","10 - 11 PM","11 - 12 AM"};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int apisMessageCount=0, pnrMessageCount=0;
        int clockTicks = 24;

//        List<Message> _tempApisList = apisMessageService.getAPIsByDates(sdf.parse(startDate), sdf.parse(endDate));
//        List<Pnr> _tempPnrList = pnrService.getPNRsByDates(sdf.parse(startDate), sdf.parse(endDate));

        DashboardMessageStats apisStatistics = messageStatsService.getDashboardAPIMessageStats();
        DashboardMessageStats pnrStatistics = messageStatsService.getDashboardPNRMessageStats();

//        apisMessageCount = _tempApisList.size();
//        pnrMessageCount = _tempPnrList.size();

        MessageCount mc = new MessageCount(stateLabel,EMPTY_STRING,EMPTY_STRING);
        for(int i=0; i<clockTicks; i++){

            mc = new MessageCount(displayTokens[i]+EMPTY_STRING, Arrays.asList(getApiPnrCountPerRow(apisStatistics, pnrStatistics, i).split(",")).get(0),
                    Arrays.asList(getApiPnrCountPerRow(apisStatistics, pnrStatistics, i).split(",")).get(1));
            _apisAndPnrCount.add(mc);
        }

        return _apisAndPnrCount;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getYtdRulesCount")
    public List<YTDRules> getYtdRulesCount(){
      List<YTDRules> ruleList = new ArrayList<YTDRules>();

        ruleList = ytdStatsService.getYTDRules();

        return ruleList;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/getYtdAirportStats")
    public List<YTDAirportStatistics> getYtdAirportStats(){
        List<YTDAirportStatistics> ruleList = new ArrayList<YTDAirportStatistics>();

        ruleList = ytdStatsService.getYTDAirportStats();

        return ruleList;
    }


    private String getApiPnrCountPerRow(DashboardMessageStats apisStatistics,DashboardMessageStats pnrStatistics, int position ){

        String returnString = "";

        switch (position){

            case 0: returnString = apisStatistics.getZero()+ commaStringToAppend + pnrStatistics.getZero(); break;
            case 1: returnString = apisStatistics.getOne()+ commaStringToAppend + pnrStatistics.getOne(); break;
            case 2: returnString = apisStatistics.getTwo()+ commaStringToAppend + pnrStatistics.getTwo(); break;
            case 3: returnString = apisStatistics.getThree()+ commaStringToAppend + pnrStatistics.getThree(); break;
            case 4: returnString = apisStatistics.getFour()+ commaStringToAppend + pnrStatistics.getFour(); break;
            case 5: returnString = apisStatistics.getFive()+ commaStringToAppend + pnrStatistics.getFive(); break;
            case 6: returnString = apisStatistics.getSix()+ commaStringToAppend + pnrStatistics.getSix(); break;
            case 7: returnString = apisStatistics.getSeven()+ commaStringToAppend + pnrStatistics.getSeven(); break;
            case 8: returnString = apisStatistics.getEight()+ commaStringToAppend + pnrStatistics.getEight(); break;
            case 9: returnString = apisStatistics.getNine()+ commaStringToAppend + pnrStatistics.getNine(); break;
            case 10: returnString = apisStatistics.getTen()+ commaStringToAppend + pnrStatistics.getTen(); break;
            case 11: returnString = apisStatistics.getEleven()+ commaStringToAppend + pnrStatistics.getEleven(); break;
            case 12: returnString = apisStatistics.getTwelve()+ commaStringToAppend + pnrStatistics.getTwelve(); break;
            case 13: returnString = apisStatistics.getThirteen()+ commaStringToAppend + pnrStatistics.getThirteen(); break;
            case 14: returnString = apisStatistics.getFourteen()+ commaStringToAppend + pnrStatistics.getFourteen(); break;
            case 15: returnString = apisStatistics.getFifteen()+ commaStringToAppend + pnrStatistics.getFifteen(); break;
            case 16: returnString = apisStatistics.getSixteen()+ commaStringToAppend + pnrStatistics.getSixteen(); break;
            case 17: returnString = apisStatistics.getSeventeen()+ commaStringToAppend + pnrStatistics.getSeventeen(); break;
            case 18: returnString = apisStatistics.getEighteen()+ commaStringToAppend + pnrStatistics.getEighteen(); break;
            case 19: returnString = apisStatistics.getNineteen()+ commaStringToAppend + pnrStatistics.getNineteen(); break;
            case 20: returnString = apisStatistics.getTwenty()+ commaStringToAppend + pnrStatistics.getTwenty(); break;
            case 21: returnString = apisStatistics.getTwentyOne()+ commaStringToAppend + pnrStatistics.getTwentyOne(); break;
            case 22: returnString = apisStatistics.getTwentyTwo()+ commaStringToAppend + pnrStatistics.getTwentyTwo(); break;
            case 23: returnString = apisStatistics.getTwentyThree()+ commaStringToAppend + pnrStatistics.getTwentyThree(); break;

        }

        return returnString;
    }

    private class HitAndAirportExtractor {
        private List<Flight> flightList;
        private int ruleHits = 0;
        private long watchListHits = 0L;
        private List<AirportVO> tempAirportList;

        public HitAndAirportExtractor(List<Flight> flightList) {
            this.flightList = flightList;
            tempAirportList = new ArrayList<>();
        }

        public int getRuleHits() {
            return ruleHits;
        }

        public List<AirportVO> getAirportList() {
            return tempAirportList;
        }

        public long getWatchListHits() {
            return watchListHits;
        }

        public HitAndAirportExtractor invoke() {
            Airport _tempAirport;
            AirportVO _tempAirportVO;
            for (Flight flight : flightList) {
                List<HitsSummary> hitsSummaryList = hitsSummaryService
                        .findHitsByFlightId(flight.getId());
                _tempAirport = airportService.getAirportByThreeLetterCode(flight.getOrigin());
                _tempAirportVO = new AirportVO(0.0,0.0,EMPTY_STRING,EMPTY_STRING,false);
                _tempAirportVO.setAirportCodeStr(_tempAirport.getIata());
                _tempAirportVO.setAirportName(_tempAirport.getCity());
                _tempAirportVO.setLatitude(_tempAirport.getLatitude().doubleValue());
                _tempAirportVO.setLongitude(_tempAirport.getLongitude().doubleValue());
                if(flight.getRuleHitCount()>0 || flight.getListHitCount()>0){
                    _tempAirportVO.setHits(true);
                }
                tempAirportList.add(_tempAirportVO);
                for (HitsSummary summ : hitsSummaryList) {
                    ruleHits = summ.getRuleHitCount() + ruleHits;
                }
                watchListHits = watchListHits + flight.getListHitCount() + flightService.getFlightFuzzyMatchesOnly(flight.getId());
            }

            return this;
        }
    }
}

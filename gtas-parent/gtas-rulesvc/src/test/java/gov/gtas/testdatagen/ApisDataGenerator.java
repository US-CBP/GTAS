/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.testdatagen;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.Seat;
import gov.gtas.model.lookup.DocumentTypeCode;
import gov.gtas.model.lookup.FlightDirectionCode;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class ApisDataGenerator {
    public static final String DOCUMENT_NUMBER="12345";
    public static final long FLIGHT_ID1=12345L;
    public static final long FLIGHT_ID2=2931L;
        
    /**
     * Creates a ApisMessage with 2flights with 3 passengers each.
     * @return
     */
    public static ApisMessage createSimpleTestApisMesssage(){
        ApisMessage msg = new ApisMessage();
        msg.setId(9999L);
        msg.setFlights(createFlights());
        msg.setCreateDate(new Date());
        msg.setStatus(MessageStatus.LOADED);
        fixPassengerReferences(msg);
        fixDocReferences(msg);
        return msg;
    }
    
    private static void fixPassengerReferences(ApisMessage msg){
    	Set<Passenger> passengers = new HashSet<>();
        for(Flight fl:msg.getFlights()){
        	passengers.addAll(fl.getPassengers());
        }
        msg.setPassengers(passengers);
    }
    
    
    private static void fixDocReferences(ApisMessage msg){
        for(Flight fl:msg.getFlights()){
            for(Passenger tr:fl.getPassengers()){
                for(Document doc:tr.getDocuments()){
                    if(doc.getPassenger() == null){
                        doc.setPassenger(tr);
                    }
                }
            }
        }
    }
    private static Set<Passenger> createPassengerAndDocument(Flight flight, long[] ids, String[][]param){
        Set<Passenger> passengers = new HashSet<Passenger>();
        int passengerCount = 0;
        for(String[] args:param){
            Passenger passenger = new Passenger();
            passenger.setId(ids[passengerCount]);
            passenger.setPassengerType(PassengerTypeCode.P.name());
            passenger.setId(new Long(args[6]));
            passenger.setDocuments(createDocuments(new long[]{ids[passengerCount]},
                    new String[]{args[0]}, new String[]{args[1]}));
            passenger.setFirstName(args[2]);
            passenger.setLastName(args[3]);
            passenger.setCitizenshipCountry(args[4]);
            passenger.setEmbarkation(args[5]);
            if(args.length > 7){
                passenger.getSeatAssignments().add(createSeat(passenger, flight, args[7]));
            }
            passengers.add(passenger);
            passengerCount++;
        }
        return passengers;
    }
    private static Seat createSeat(Passenger p, Flight f, String seatNo){
        Seat seat = new Seat();
        seat.setApis(true);
        seat.setPassenger(p);
        seat.setFlight(f);
        seat.setNumber(seatNo);
        return seat;
    }
    private static Set<Flight> createFlights(){
        Set<Flight> flights = new HashSet<Flight>();
        
        Flight flight = new Flight();
        flight.setId(FLIGHT_ID1);
        Set<Passenger> passengers = createPassengerAndDocument(flight, new long[]{29391L,29392L,29393L},
                new String[][]{
                {/*document*/"GB","2012-01-15", /*passenger(name, citzenship, embarkation*/"Ragner", "Yilmaz", "GB", "YHZ","11","39G"},//added seat 39G to this passenger only
                {"US", "2010-01-15", "Gitstash", "Garbled", "US", "BOB","22"},
                {"CA", "2011-12-31", "Kalimar", "Rultan", "CA", "YHZ","33"}
               }
                );
        flight.setPassengers(passengers);
        for(Passenger pas:passengers){
            pas.getFlights().add(flight);
        }
        flight.setCarrier("V7");//Continental
        flight.setDestination("BOB");
        flight.setFlightDate(new Date());
        flight.setFlightNumber("0012");
        flight.setOrigin("YHZ");
        flight.setOriginCountry("CA");
        flight.setDirection(FlightDirectionCode.I.name());
        flights.add(flight);
        
        flight = new Flight();
        flight.setId(FLIGHT_ID2);
        passengers = createPassengerAndDocument(flight, new long[]{29394L,29395L,29396L},
                new String[][]{
                {"YE","2012-01-15", "Iphsatz", "Zaglib", "PF", "YHZ","44"},
                {"US", "2010-01-15", "Loopy", "Lair", "US", "BOB","55"},
                {"GB", "2010-01-15", "Ikstar", "Crondite", "GB", "LHR","66"}
               }
                );
        flight.setPassengers(passengers);
        for(Passenger pas:passengers){
            pas.getFlights().add(flight);
        }

        flight.setCarrier("CO");//Continental
        flight.setDestination("HOD");
        Date flDate = null;
        try{
            flDate = DateCalendarUtils.parseJsonDate("2015-07-20");
            flDate = new Date(flDate.getTime()+36000000L);//add 10 hours
            flight.setFlightDate(flDate);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        flight.setFlightNumber("0017");
        flight.setOrigin("LHR");
        flight.setOriginCountry("GB");
        flight.setDirection(FlightDirectionCode.I.name());
        flights.add(flight);

        return flights;
    }
    private static Set<Document> createDocuments(long[] ids, String[] iso2Array, String[]issueDates){
        Set<Document> docs = new HashSet<Document>();
        int docCount = 0;
        for(int i = 0; i < iso2Array.length; ++i){
            String iso2 = iso2Array[i];
            Document doc = new Document();
            doc.setId(ids[docCount]);
            doc.setDocumentType(DocumentTypeCode.P.name());
            doc.setId(7786L);
            doc.setDocumentNumber(DOCUMENT_NUMBER);
            doc.setIssuanceCountry(iso2);
            try{
               doc.setIssuanceDate(DateCalendarUtils.parseJsonDate(issueDates[i]));
            }catch(ParseException pe){
                pe.printStackTrace();
            }
            docs.add(doc);
            docCount++;
        }
        return docs;
    }
}

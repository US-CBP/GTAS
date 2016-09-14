/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.dto.FlightDto;
import gov.gtas.dto.PaxDto;
import gov.gtas.parsers.exception.ParseException;

/**
 * Class MessageGeneratorIT generates the combined PNR and APIS messages for 
 * specific carrier and flight. Uncomment the @Test annotation and run by 
 * changing the for loop values.Also create a folder in your local to store the
 * messages in your local.(C:\\PNR) 
 * 
 * use testAllCases() by uncommenting the @Test annotation to generate test data for 
 * ebola,watch list etc.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CommonServicesConfig.class)
@Transactional
public class MessageGeneratorApp {

    @Autowired
    private ServiceUtil svc;
    private static List<FlightDto> flights=new ArrayList<FlightDto>();
    
    @Before
    public void setUp() throws Exception {
        
    }

    @After
    public void tearDown() throws Exception {
    }

   // @Test()
    public void testRunService() throws ParseException {
        int j=0;
        for(int i=1;i <=99;i++){
            j=i;
            FlightDto dto = new FlightDto();
            StringBuilder sb = new StringBuilder();
            String carrier=GenUtil.getCarrier();
            String origin=GenUtil.getAirport();
            String dest=GenUtil.getAirport();
            String temp=GenUtil.getUsAirport();
            if(!origin.equals(temp)){
                dest=temp;
            }

            String fNumber=GenUtil.getFlightNumber();
            String dString=GenUtil.getPnrDate();
            String originCountry=svc.getCountry(origin);
            String destCountry=svc.getCountry(dest);
            int numPax=GenUtil.getRandomNumber(3)+2;
            dto.setCarrier(carrier);
            dto.setDebark(dest);
            dto.setEmbark(origin);
            dto.setFlightNum(fNumber);
            dto.setEmbarkCountry(originCountry);
            dto.setDebarkCountry(destCountry);
            
            PnrGen.buildHeader(carrier,sb);
            PnrGen.buildMessage("22",sb);
            PnrGen.buildOrigDestinations(carrier, origin,dest,fNumber,dString,sb);
            PnrGen.buildEqn(numPax,sb);
            PnrGen.buildSrc(carrier, origin,dest,fNumber,dString,numPax,dto,sb);
            PnrGen.buildFooter(sb);
            flights.add(dto);
            System.out.println(sb.toString());  
            PnrGen.writeToFile(i,sb);
            sb=null;
        }
        PnrGen.buildApisMessages(flights,j);
    }
    
  //@Test()
    public void testEbolaFileService() throws ParseException {
        int j=0;
        for(int i=1000000;i <=1000020;i++){
            j=i;
            FlightDto dto = new FlightDto();
            StringBuilder sb = new StringBuilder();
            String carrier=GenUtil.getCarrier();
            String origin=GenUtil.getEbolaAirport();
            String dest=GenUtil.getUsAirport();
                    

            String fNumber=GenUtil.getFlightNumber();
            String dString=GenUtil.getPnrDate();
            String originCountry=GenUtil.getEbolaCountry(origin);
            String destCountry="USA";
            int numPax=GenUtil.getRandomNumber(3)+2;
            dto.setCarrier(carrier);
            dto.setDebark(dest);
            dto.setEmbark(origin);
            dto.setFlightNum(fNumber);
            dto.setEmbarkCountry(originCountry);
            dto.setDebarkCountry(destCountry);
            
            PnrGen.buildHeader(carrier,sb);
            PnrGen.buildMessage("22",sb);
            PnrGen.buildOrigDestinations(carrier, origin,dest,fNumber,dString,sb);
            PnrGen.buildEqn(numPax,sb);
            PnrGen.buildSrc(carrier, origin,dest,fNumber,dString,numPax,dto,sb);
            PnrGen.buildFooter(sb);
            flights.add(dto);
            System.out.println(sb.toString());  
            PnrGen.writeToFile(i,sb);
            sb=null;
        }
        PnrGen.buildApisMessages(flights,j);
    }
   // @Test()
    public void testTenFlightService() throws ParseException {
        int k=0;
        for(int i=1;i <=5;i++){
            
            FlightDto dto = new FlightDto();
            
            String carrier=GenUtil.getCarrier();
            String origin=GenUtil.getAirport();
            String dest=GenUtil.getAirport();
            String temp=GenUtil.getUsAirport();
            if(!origin.equals(temp)){
                dest=temp;
            }
            String fNumber=GenUtil.getFlightNumber();
            String dString=GenUtil.getPnrDate();
            String originCountry=svc.getCountry(origin);
            String destCountry=svc.getCountry(dest);
            dto.setToDay(dString);
            dto.setCarrier(carrier);
            dto.setDebark(dest);
            dto.setEmbark(origin);
            dto.setFlightNum(fNumber);
            dto.setEmbarkCountry(originCountry);
            dto.setDebarkCountry(destCountry);  
            List<PaxDto> paxList =GenUtil.getWatchList();
            
            //if(i < paxList.size()){
                //PaxDto pdto=paxList.get(i);
                //dto.getPaxList().add(pdto);
            //}
            for(int j=2222230;j <=2222233;j++){
                StringBuilder sb = new StringBuilder();
                k=k+j;
                int numPax=GenUtil.getRandomNumber(3)+1;
                PnrGen.buildHeader(carrier,sb);
                PnrGen.buildMessage("22",sb);
                PnrGen.buildOrigDestinations(carrier, origin,dest,fNumber,dString,sb);
                PnrGen.buildEqn(numPax,sb);
                PnrGen.buildSrc(carrier, origin,dest,fNumber,dString,numPax,dto,sb);
                PnrGen.buildFooter(sb);
                PnrGen.writeToFile(k,sb);
                sb=null;
            }
            flights.add(dto);
            
        }
        //PnrGen.buildApisMessages(flights,k);
    }
    
    @Test()
    public void testAllCases() throws ParseException {
        
        //build ten flights with ebloa data
        List<FlightDto> flightList = new ArrayList<>();
        buildFlightList(flightList);
        //build 150 passengers //Add watchlist
        buildPassengerList(flightList);
        //build pnr data
        GenHelper.preparePnrData(flightList);
        //build apis data
        GenHelper.prepareApisData(flightList);
        
    }
    
    private void buildPassengerList(List<FlightDto> flightList){
        
        for(FlightDto f : flightList){
            List<PaxDto> paxList = new ArrayList<PaxDto>();
            int j=GenUtil.getRandomNumber(99)+100;
            //int j=2;
            for(int i=1;i<=j;i++){
                PaxDto pax= new PaxDto();
                pax.setId(i);
                if(i == 2 ){//watch list data
                    pax=GenUtil.getPaxDto();
                }
                else{
                    pax.setFirstName(GenUtil.getFirstName());
                    pax.setLastName(GenUtil.getLastName());
                    pax.setDob(GenUtil.getBirthDate());
                }
                pax.setEmbark(f.getEmbark());
                pax.setDebark(f.getDebark());
                pax.setFlight(f);
                paxList.add(pax);
            }
            f.setPaxList(paxList);
            //System.out.println("################"+f.getPaxList().size()); 
        }
    }
    private void buildFlightList(List<FlightDto> flightList){
        for(int i=0;i < 1;i++){
            FlightDto dto = new FlightDto();
            String carrier=GenUtil.getCarrier();
            String origin=GenUtil.getAirport();
            String temp=GenUtil.getUsAirport();
            String dest=GenUtil.getAirport();
            if(!origin.equals(temp)){
                dest=temp;
            }
            String originCountry=svc.getCountry(origin);
            if(i == 9 ){
                origin=GenUtil.getEbolaAirport();
                originCountry=GenUtil.getEbolaCountry(origin);
            }
            
            String destCountry=svc.getCountry(dest);
            
            String fNumber=GenUtil.getFlightNumber();
            String dString=GenUtil.getPnrDate();

            dto.setToDay(dString);
            dto.setCarrier(carrier);
            dto.setDebark(dest);
            dto.setEmbark(origin);
            dto.setFlightNum(fNumber);
            dto.setEmbarkCountry(originCountry);
            dto.setDebarkCountry(destCountry);
            flightList.add(dto);
        }
        
    }
}

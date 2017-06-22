/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.segment.ABI;
import gov.gtas.parsers.pnrgov.segment.ADD;
import gov.gtas.parsers.pnrgov.segment.APD;
import gov.gtas.parsers.pnrgov.segment.DAT_G1;
import gov.gtas.parsers.pnrgov.segment.DAT_G10;
import gov.gtas.parsers.pnrgov.segment.DAT_G6;
import gov.gtas.parsers.pnrgov.segment.EBD;
import gov.gtas.parsers.pnrgov.segment.EQN;
import gov.gtas.parsers.pnrgov.segment.FAR;
import gov.gtas.parsers.pnrgov.segment.FOP;
import gov.gtas.parsers.pnrgov.segment.FOP.Payment;
import gov.gtas.parsers.pnrgov.segment.FTI;
import gov.gtas.parsers.pnrgov.segment.FTI.FrequentFlierDetails;
import gov.gtas.parsers.pnrgov.segment.IFT;
import gov.gtas.parsers.pnrgov.segment.LTS;
import gov.gtas.parsers.pnrgov.segment.MON;
import gov.gtas.parsers.pnrgov.segment.MSG;
import gov.gtas.parsers.pnrgov.segment.ORG;
import gov.gtas.parsers.pnrgov.segment.PTK;
import gov.gtas.parsers.pnrgov.segment.RCI;
import gov.gtas.parsers.pnrgov.segment.RCI.ReservationControlInfo;
import gov.gtas.parsers.pnrgov.segment.REF;
import gov.gtas.parsers.pnrgov.segment.RPI;
import gov.gtas.parsers.pnrgov.segment.SAC;
import gov.gtas.parsers.pnrgov.segment.SRC;
import gov.gtas.parsers.pnrgov.segment.SSD;
import gov.gtas.parsers.pnrgov.segment.SSR;
import gov.gtas.parsers.pnrgov.segment.SSR.SpecialRequirementDetails;
import gov.gtas.parsers.pnrgov.segment.TBD;
import gov.gtas.parsers.pnrgov.segment.TBD.BagDetails;
import gov.gtas.parsers.pnrgov.segment.TIF;
import gov.gtas.parsers.pnrgov.segment.TIF.TravelerDetails;
import gov.gtas.parsers.pnrgov.segment.TKT;
import gov.gtas.parsers.pnrgov.segment.TRA;
import gov.gtas.parsers.pnrgov.segment.TRI;
import gov.gtas.parsers.pnrgov.segment.TVL;
import gov.gtas.parsers.pnrgov.segment.TVL_L0;
import gov.gtas.parsers.pnrgov.segment.TXD;
import gov.gtas.parsers.util.FlightUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.parsers.vo.AddressVo;
import gov.gtas.parsers.vo.AgencyVo;
import gov.gtas.parsers.vo.BagVo;
import gov.gtas.parsers.vo.CreditCardVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.EmailVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.FrequentFlyerVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.PhoneVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.parsers.vo.SeatVo;


public final class PnrGovParser extends EdifactParser<PnrVo> {
   
    public PnrGovParser() {
        this.parsedMessage = new PnrVo();
    }

    @Override
    protected String getPayloadText() throws ParseException {
        return lexer.getMessagePayload("SRC", "UNT");
    }
    
    @Override
    public void parsePayload() throws ParseException {
        MSG msg = getMandatorySegment(MSG.class);
        if(msg != null && msg.getMessageTypeCode() != null){
            parsedMessage.setMessageCode(msg.getMessageTypeCode().getCode());
        }
        
        getMandatorySegment(ORG.class);
        TVL_L0 tvl = getMandatorySegment(TVL_L0.class, "TVL");
        getMandatorySegment(EQN.class);
        getMandatorySegment(SRC.class);       
        processGroup1_PnrStart(tvl);
    }

    /**
     * start of a new PNR
     */
    private void processGroup1_PnrStart(TVL_L0 tvl_l0) throws ParseException {
    	
        parsedMessage.setCarrier(tvl_l0.getCarrier());
        parsedMessage.setOrigin(tvl_l0.getOrigin());
        parsedMessage.setDepartureDate(tvl_l0.getEtd());
        
        RCI rci = getMandatorySegment(RCI.class);
        ReservationControlInfo controlInfo = rci.getReservations().get(0);
        parsedMessage.setRecordLocator(controlInfo.getReservationControlNumber());
        if(controlInfo.getTimeCreated() != null){
        	parsedMessage.setReservationCreateDate(controlInfo.getTimeCreated());
        }

        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class, "SSR");
            if (ssr == null) {
                break;
            }
        }

        DAT_G1 dat = getConditionalSegment(DAT_G1.class, "DAT");
        if (dat != null) {
            parsedMessage.setDateBooked(dat.getTicketIssueDate());
            parsedMessage.setDateReceived(dat.getPnrTransactionDate());
        }

        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            
            if (ift == null) {
                break;
            }
            processIft(ift);
        }

        ORG org = getMandatorySegment(ORG.class);
        processAgencyInfo(org);

        for (;;) {
            ADD add = getConditionalSegment(ADD.class);
            if (add == null) {
                break;
            }
            processAddress(add);
        }

        for (;;) {
            // excess baggage information for all passengers
            EBD ebd = getConditionalSegment(EBD.class);
            if (ebd == null) {
                break;
            }
            processExcessBaggage(ebd);
        }
        
        TIF tif = getMandatorySegment(TIF.class);
        processGroup2_Passenger(tif);
        for (;;) {
            tif = getConditionalSegment(TIF.class);
            if (tif == null) {
                break;
            }
            processGroup2_Passenger(tif);
        }

        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
            processGroup5_Flight(tvl);
        }
    }

    /**
     * Passenger
     */
    private void processGroup2_Passenger(TIF tif) throws ParseException {
        FTI fti = getConditionalSegment(FTI.class);
        if (fti != null) {
            FrequentFlyerVo ffvo = new FrequentFlyerVo();
            FrequentFlierDetails ffdetails = fti.getFrequentFlierInfo().get(0);
            ffvo.setCarrier(ffdetails.getAirlineCode());
            ffvo.setNumber(ffdetails.getFreqTravelerNumber());
            if (ffvo.isValid()) {
                parsedMessage.getFrequentFlyerDetails().add(ffvo);
            }
        }
        
        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
            processIft(ift);
        }

        getConditionalSegment(REF.class);
        getConditionalSegment(EBD.class);

        for (;;) {
            FAR far = getConditionalSegment(FAR.class);
            if (far == null) {
                break;
            }
        }

        // SSR’s in GR.2 apply to the specific passenger.
        List<SSR> ssrDocs = new ArrayList<>();
        List<DocumentVo> visas = new ArrayList<>();
        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class);
            if (ssr == null) {
                break;
            }
            String code = ssr.getTypeOfRequest();
            if (SSR.DOCS.equals(code)) {
                ssrDocs.add(ssr);
            } else if (SSR.DOCA.equals(code)) {
                AddressVo addr = PnrUtils.createAddress(ssr);
                if (addr != null && addr.isValid()) {
                    parsedMessage.getAddresses().add(addr);
                }
            } else if (SSR.DOCO.equals(code)) {
                DocumentVo visa = PnrUtils.createVisa(ssr);
                if (visa != null && visa.isValid()) {
                    visas.add(visa);
                }
            }
        }

        if (!CollectionUtils.isEmpty(ssrDocs)) {
            PassengerVo p = PnrUtils.createPassenger(ssrDocs, tif);
            if (p != null && p.isValid()) {
                p.getDocuments().addAll(visas);
                parsedMessage.getPassengers().add(p);
                parsedMessage.setPassengerCount(parsedMessage.getPassengerCount() + 1);
            } else {
                throw new ParseException("Invalid passenger: " + p);
            }
        }
        
        for (;;) {
            ADD add = getConditionalSegment(ADD.class);
            if (add == null) {
                break;
            }
            processAddress(add);
        }

        for (;;) {
            TKT tkt = getConditionalSegment(TKT.class);
            if (tkt == null) {
                break;
            }
            processGroup3_TicketCost(tkt);
        }
    }

    /**
     * Ticket cost info. Repeats for each ticket associated with a passenger.
     * Not currently using this.
     */
    private void processGroup3_TicketCost(TKT tkt) throws ParseException {
        getConditionalSegment(MON.class);
        getConditionalSegment(PTK.class);

        for (;;) {
            TXD txd = getConditionalSegment(TXD.class);
            if (txd == null) {
                break;
            }
        }

        DAT_G1 dat = getConditionalSegment(DAT_G1.class, "DAT");
        if (dat != null) {
            if (parsedMessage.getDateBooked() == null) {
                parsedMessage.setDateBooked(dat.getTicketIssueDate());
            }
            if (parsedMessage.getDateReceived() == null) {
                parsedMessage.setDateReceived(dat.getPnrTransactionDate());
            }
        }
        
        // NB: IFT here is not part of the spec, but I've noticed a lot
        // of messages in production that stick IFTs in this location.
        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
            processIft(ift);
        }

        FOP fop = getConditionalSegment(FOP.class);
        processGroup4_FormOfPayment(fop);
    }

    /**
     * Form of payment info: get credit card if exists
     */
    private void processGroup4_FormOfPayment(FOP fop) throws ParseException {
        List<CreditCardVo> newCreditCards = new ArrayList<>();

        if (fop != null) {
            List<Payment> payments = fop.getPayments();
            if (!CollectionUtils.isEmpty(payments)) {
                // arbitrarily select first payment type
                parsedMessage.setFormOfPayment(payments.get(0).getPaymentType());
                for (Payment p : payments) {
                    if (p.isCreditCard()) {
                        CreditCardVo cc = new CreditCardVo();
                        cc.setCardType(p.getVendorCode());
                        cc.setExpiration(p.getExpirationDate());
                        cc.setNumber(p.getAccountNumber());
                        if (cc.isValid()) {
                            newCreditCards.add(cc);
                        }
                    } 
                }
            }
        }
        
        IFT ift = getConditionalSegment(IFT.class);
        if (ift != null) {
            if (CollectionUtils.isNotEmpty(newCreditCards) && ift.isSponsorInfo()) {
                List<String> msgs = ift.getMessages();
                if (CollectionUtils.isNotEmpty(msgs)) {
                    for (CreditCardVo cc : newCreditCards) {
                        cc.setAccountHolder(msgs.get(0));
                    }
                }
            }
        }

        parsedMessage.getCreditCards().addAll(newCreditCards);
        
        ADD add = getConditionalSegment(ADD.class);
        if (add != null) {
            processAddress(add);
        }
    }

    /**
     * Flight info: repeats for each flight segment in the passenger record’s
     * itinerary.
     */
    private void processGroup5_Flight(TVL tvl) throws ParseException {
        FlightVo f = new FlightVo();
        f.setCarrier(tvl.getCarrier());
        f.setDestination(tvl.getDestination());
        f.setOrigin(tvl.getOrigin());
        f.setEta(tvl.getEta());
        f.setEtd(tvl.getEtd());
        f.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(tvl.getFlightNumber()));
        Date flightDate = FlightUtils.determineFlightDate(tvl.getEtd(), tvl.getEta(), parsedMessage.getTransmissionDate());
        f.setFlightDate(flightDate);
        if (f.isValid()) {
            parsedMessage.getFlights().add(f);
        } else {
            throw new ParseException("Invalid flight: " + f);
        }
        
        processFlightSegments(tvl);
        
        if (StringUtils.isNotBlank(tvl.getOperatingCarrier())) {
            // codeshare flight: create a separate flight with the same
            // details except use the codeshare carrier and flight number.
            TVL cs_tvl = getMandatorySegment(TVL.class);
            
            FlightVo csFlight = new FlightVo();
            csFlight.setCarrier(tvl.getOperatingCarrier());
            csFlight.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(cs_tvl.getFlightNumber()));
            csFlight.setDestination(tvl.getDestination());
            csFlight.setOrigin(tvl.getOrigin());
            csFlight.setEta(tvl.getEta());
            csFlight.setEtd(tvl.getEtd());
            csFlight.setFlightDate(flightDate);
            csFlight.setMarketingFlightNumber(FlightUtils.padFlightNumberWithZeroes(tvl.getFlightNumber()));
            csFlight.setCodeShareFlight(true);
            f.setMarketingFlight(true);
            if (csFlight.isValid()) {
                parsedMessage.getFlights().add(csFlight);
            } else {
                throw new ParseException("Invalid flight: " + csFlight);
            }
            
            processFlightSegments(tvl);
        }
    }
    
    private void processFlightSegments(TVL tvl) throws ParseException {
        getConditionalSegment(TRA.class);
        getConditionalSegment(RPI.class);
        getConditionalSegment(APD.class);
        
        for (;;) {
            SSR ssr = getConditionalSegment(SSR.class);
            if (ssr == null) {
                break;
            }
            String code = ssr.getTypeOfRequest();
            if (SSR.SEAT.equals(code)) {
                if (!CollectionUtils.isEmpty(ssr.getDetails())) {
                    for (SpecialRequirementDetails details : ssr.getDetails()) {
                        String refNumber = details.getTravelerReferenceNumber();
                        if (refNumber == null) {
                            continue;
                        }
                        PassengerVo thePax = findPaxByReferenceNumber(refNumber);
                        if (thePax == null) {
                            continue;
                        }
    
                        SeatVo seat = new SeatVo();
                        seat.setTravelerReferenceNumber(refNumber);
                        seat.setNumber(details.getSpecialRequirementData());
                        seat.setOrigin(ssr.getBoardCity());
                        seat.setDestination(ssr.getOffCity());
                        if (seat.isValid()) {
                            thePax.getSeatAssignments().add(seat);
                        }
                    }
                } else if (StringUtils.isNotBlank(ssr.getFreeText())) {
                    // TODO: figure out seats
                }
            }
        }

        getConditionalSegment(RCI.class);

        for (;;) {
            IFT ift = getConditionalSegment(IFT.class);
            if (ift == null) {
                break;
            }
            processIft(ift);
        }

        for (;;) {
            DAT_G6 dat = getConditionalSegment(DAT_G6.class, "DAT");
            if (dat == null) {
                break;
            }
            processGroup6_Agent(dat, tvl);
        }
        
        for (;;) {
            EQN eqn = getConditionalSegment(EQN.class);
            if (eqn == null) {
                break;
            }
            processGroup8_SplitPassenger(eqn);
        }

        for (;;) {
            MSG msg = getConditionalSegment(MSG.class);
            if (msg == null) {
                break;
            }
            processGroup9_NonAir(msg);
        }

        for (;;) {
            ABI abi = getConditionalSegment(ABI.class);
            if (abi == null) {
                break;
            }
            processGroup10_History(abi);
        }

        for (;;) {
            LTS lts = getConditionalSegment(LTS.class);
            if (lts == null) {
                break;
            }
            extractContactInfo(lts.getText());
        }
    }
    
    /**
     * the agent info that checked-in the passenger
     */
    private void processGroup6_Agent(DAT_G6 dat, TVL tvl) throws ParseException {
        ORG org = getConditionalSegment(ORG.class, "ORG");
        processAgencyInfo(org);
        
        TRI tri = getMandatorySegment(TRI.class);
        processGroup7_SeatInfo(tri, tvl);
        for (;;) {
            tri = getConditionalSegment(TRI.class);
            if (tri == null) {
                break;
            }
            processGroup7_SeatInfo(tri, tvl);
        }        
    }
    
    /**
     * boarding, seat number and checked bag info
     */
    private void processGroup7_SeatInfo(TRI tri, TVL tvl) throws ParseException {
        PassengerVo thePax = null;
        String refNumber = tri.getTravelerReferenceNumber();
        if (refNumber != null) {
            thePax = findPaxByReferenceNumber(refNumber);
        }
        
        TIF tif = getConditionalSegment(TIF.class);
        if (thePax == null && tif != null) {
            // try finding pax based on tif info
            String surname = tif.getTravelerSurname();
            List<TravelerDetails> td = tif.getTravelerDetails();
            if (CollectionUtils.isNotEmpty(td)) {
                String firstName = td.get(0).getTravelerGivenName();
                for (PassengerVo pax : parsedMessage.getPassengers()) {
                    if (surname.equals(pax.getLastName()) && firstName.equals(pax.getFirstName())) {
                        thePax = pax;
                        break;
                    }
                }
            }
        }
        
        // TODO: how does this relate to ssr:seat?
        SSD ssd = getConditionalSegment(SSD.class);
        if (thePax != null && ssd != null) {
            SeatVo seat = new SeatVo();
            seat.setTravelerReferenceNumber(thePax.getTravelerReferenceNumber());
            seat.setNumber(ssd.getSeatNumber());
            seat.setOrigin(tvl.getOrigin());
            seat.setDestination(tvl.getDestination());
            if (seat.isValid()) {
                thePax.getSeatAssignments().add(seat);
            }
        }
        
        TBD tbd = getConditionalSegment(TBD.class);
        if (tbd == null) {
            return;
        }
        TIF tif1 = getConditionalSegment(TIF.class);    
        Integer n = tbd.getNumBags();
        Double weight= (tbd.getBaggageWeight()) == null ?0:tbd.getBaggageWeight();
        if (n != null) {
            parsedMessage.setBagCount(n);
            parsedMessage.setBaggageWeight(weight);
            parsedMessage.setBaggageUnit(tbd.getUnitQualifier());
        }
        parsedMessage.setBags(getBagVosFromTBD(tbd.getBagDetails(),tif1));
    }
    
    private List<BagVo> getBagVosFromTBD(List<BagDetails> bDetails,TIF tif){
    	if(bDetails == null || bDetails.size()==0){
    		return null;
    	}
    	List<BagVo> bags = new ArrayList();
    	PassengerVo pvo=PnrUtils.getPaxFromTIF(tif,parsedMessage.getPassengers());
    	for (BagDetails bd : bDetails){
    		BagVo bvo=new BagVo();
    		if(bd.isMemberPool()){
    			bvo.setAirline(parsedMessage.getCarrier());
    			bvo.setBagId(bd.getTagNumber());
    			bvo.setData_source("PNR");
    			bvo.setDestinationAirport(pvo.getDebarkation());
    			bvo.setFirstName(pvo.getFirstName());
    			bvo.setLastName(pvo.getLastName());
    		}
    		else{
    			bvo=new BagVo(bd.getTagNumber(),"PNR",bd.getDestAirport(),bd.getAirline(),pvo.getFirstName(),pvo.getLastName());
    		}
    		bags.add(bvo);
    	}
    	return bags;
    }
    private void processGroup8_SplitPassenger(EQN eqn) throws ParseException {
        getMandatorySegment(RCI.class);
    }

    /**
     * non-air segments: car, hotel, rail.  Not used.
     */
    private void processGroup9_NonAir(MSG msg) throws ParseException {
        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
        }
    }

    private void processGroup10_History(ABI abi) throws ParseException {
        getConditionalSegment(DAT_G10.class, "DAT");
        for (;;) {
            SAC sac = getConditionalSegment(SAC.class);
            if (sac == null) {
                break;
            }
            processGroup11_HistoryCredit(sac);
        }        
    }

    private void processGroup11_HistoryCredit(SAC sac) throws ParseException {
        getConditionalSegment(TIF.class);
        getConditionalSegment(SSR.class);
        getConditionalSegment(IFT.class);
        getConditionalSegment(TBD.class);
        for (;;) {
            TVL tvl = getConditionalSegment(TVL.class);
            if (tvl == null) {
                break;
            }
            processGroup12_HistoryFlightInfo(tvl);
        }        

    }

    private void processGroup12_HistoryFlightInfo(TVL tvl) throws ParseException {
        getConditionalSegment(RPI.class);
    }
    
    private void processExcessBaggage(EBD ebd) {
        if (ebd != null) {
            Integer n = ParseUtils.returnNumberOrNull(ebd.getNumberInExcess());
            if (n != null) {
                parsedMessage.setBagCount(parsedMessage.getBagCount() + n);
            }
        }
    }
    
    private void processIft(IFT ift) {
        if (ift.isOtherServiceInfo()) {
            List<String> msgs = ift.getMessages();
            for (String txt : msgs) {
            	extractContactInfo(txt);
            }
        }
        else{
        	//gets executed for email format IFT+CTCE SOME.MCCLAUGHRY//GMAIL.COM
        	 List<String> msgs = ift.getMessages();
             for (String txt : msgs) {
            	 if(StringUtils.isNotBlank(txt) && txt.startsWith(IFT.CONTACT_EMAIL)){
            		 extractEmailInfo(txt);
            	 }
             	
             }
        	
        }
    }
    
    private void extractEmailInfo(String txt){
    	String tmp = getContactInfo(IFT.CONTACT_EMAIL, txt);
    	
    	 if (StringUtils.isNotBlank(tmp)) {
    		 //implement future parsing here based on incoming email formats
    		if(tmp.indexOf("//") != -1){
    			tmp=tmp.replace("//", "@");
    		}
    		if(tmp.indexOf("EK ") != -1){
    			tmp=tmp.replace("EK ", "");
    		}
             EmailVo email = new EmailVo();
             email.setAddress(tmp);
             if(tmp.indexOf("@") != -1){
            	 email.setDomain(tmp.substring(tmp.indexOf("@")+1, tmp.length()));
             }
            
             parsedMessage.getEmails().add(email);
         }
    }
    private void extractContactInfo(String txt) {
        if (StringUtils.isBlank(txt)) {
            return;
        }
        
        if (txt.contains(IFT.CONTACT_EMAIL)) {
        	//gets executed for email format IFT+4:28::YY+CTCE SOMEBODY//GMAIL.COM'
        	extractEmailInfo(txt);
        	
        } else if (txt.contains(IFT.CONTACT_ADDR)) {
        	String tmp = getContactInfo(IFT.CONTACT_ADDR, txt);
            if (StringUtils.isNotBlank(tmp)) {
                AddressVo addr = new AddressVo();
                addr.setLine1(tmp);
                parsedMessage.getAddresses().add(addr);
            }
        } else if (txt.contains(IFT.CONTACT)) {
        	// The remaining contact types are telephone numbers
            String tmp = ParseUtils.prepTelephoneNumber(txt);
            if (StringUtils.isNotBlank(tmp)) {
                PhoneVo phone = new PhoneVo();
                phone.setNumber(tmp);
                parsedMessage.getPhoneNumbers().add(phone);
            }
            if(txt.contains(IFT.CONTACT_CITY)){
            	String[] tmpArray=getAgencyInfo(txt);
            	AgencyVo a=new AgencyVo();
            	StringBuilder sb=new StringBuilder();
            	String name="";
            	for(int i =0;i<tmpArray.length;i++){
            		String s=tmpArray[i];
            		if(i ==0 ){
               			continue;
            		}
            		else if(i == 1 && s.length() ==3){
            			a.setCity(s);
            			a.setLocation(s);
            		}
            		else if(i > 2){
            			sb.append(s);
            			sb.append(" ");
            		}
            	}
            	if(StringUtils.isNotBlank(tmp) || StringUtils.isNotBlank(name) || StringUtils.isNotBlank(a.getCity())){
            		a.setName(sb.toString());
            		a.setPhone(tmp);
            		parsedMessage.getAgencies().add(a);
            	}
            }
        }
    }
    
    private String[] getAgencyInfo(String text){
    	return text.split(" ");
    }
    private String getContactInfo(String ctcCode, String text) {
    	return text.replace(ctcCode, "").replace("\\s+", "");
    }
    
    private void processAgencyInfo(ORG org) {
        if (org == null) {
            return;
        }
        
        AgencyVo agencyVo = new AgencyVo();
        agencyVo.setName(org.getAirlineCode());
        agencyVo.setLocation(org.getLocationCode());
        agencyVo.setIdentifier(org.getTravelAgentIdentifier());
        agencyVo.setCountry(org.getOriginatorCountryCode());
        if(StringUtils.isNotEmpty(org.getAgentLocationCode())){
        	agencyVo.setCity(org.getAgentLocationCode());
        }else{
        	agencyVo.setCity(org.getLocationCode());
        }
 
        if (agencyVo.isValid()) {
            parsedMessage.getAgencies().add(agencyVo);
        }
    }
    
    private void processAddress(ADD add) {
        AddressVo address = PnrUtils.createAddress(add);
        if (address.isValid()) {
            parsedMessage.getAddresses().add(address);
        }
        if (address.getPhoneNumber() != null) {
            PhoneVo p = PnrUtils.createPhone(address.getPhoneNumber());
            if (p.isValid()) {
                parsedMessage.getPhoneNumbers().add(p);
            }
        } 
        if(StringUtils.isNotBlank(address.getEmail())){
        	extractEmailInfo(address.getEmail());
        }
    }
    
    private PassengerVo findPaxByReferenceNumber(String refNumber) {
        for (PassengerVo pax : parsedMessage.getPassengers()) {
            if (refNumber.equals(pax.getTravelerReferenceNumber())) {
                return pax;
            }
        }
        return null;
    }
    
}

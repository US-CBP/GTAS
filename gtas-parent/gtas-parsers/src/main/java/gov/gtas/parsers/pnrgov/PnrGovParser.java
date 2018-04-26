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
import gov.gtas.parsers.vo.CodeShareVo;
import gov.gtas.parsers.vo.CreditCardVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.EmailVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.FrequentFlyerVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.PaymentFormVo;
import gov.gtas.parsers.vo.PhoneVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.parsers.vo.SeatVo;
import gov.gtas.parsers.vo.TicketFareVo;


public final class PnrGovParser extends EdifactParser<PnrVo> {
   
	private PassengerVo currentPassenger=null;
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
        System.out.println("After processGroup2_Passenger(tif) = " +tif);
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
            if(validTvl(tvl)) {
            	processGroup5_Flight(tvl);
            }
            
        }
        processLTS();
    }
    private boolean validTvl(TVL tvl) {
    	boolean check=true;
    	System.out.println("XXXXXXXX TVL = " +tvl.getCarrier());
    	System.out.println("XXXXXXXXXX TVL = " +tvl.getDestination());
    	System.out.println("XXXXXXXXX TVL = " +tvl.getFlightNumber());
    	if(StringUtils.isBlank(tvl.getCarrier()) && StringUtils.isBlank(tvl.getDestination())
    			&& StringUtils.isBlank(tvl.getFlightNumber())) {
    		check=false;
    		System.out.println("before processGroup5 TVL = " +check);
    	}
    	
    	return check;
    }

    /**
     * Passenger
     */
    private void processGroup2_Passenger(TIF tif) throws ParseException {
    	System.out.println("in processGroup2" );
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
            	if(StringUtils.isNotBlank(ssr.getFreeText())){
            		ssrDocs.add(ssr);
            	}
                
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
            if(SSR.CTCE.equals(code)){
            	String emailText=ssr.getFreeText();
            	if(emailText.indexOf("-1") > -1){
                	emailText=emailText.substring(0,emailText.indexOf("-1"));
            	}
            	extractEmailInfo(emailText);
            }
            if(SSR.FQTV.equals(code)){
            	//SSR+FQTV:HK::DL' (just the carrier code of the frequent flyer program)
            	//SSR+FQTV:HK:1:OZ:::::/DL1234567890-LASTNAME/FIRSTNAMEMI'
            	//SSR+FQTV:HK:1:UA:::::SSRFQTVUAHK/LH1234567890-LASTNAME/FIRSTNAME 1.1'
            	String freeText=ssr.getFreeText();
            	if(StringUtils.isBlank(freeText) && StringUtils.isNotBlank(ssr.getCarrier())){
            		//Case:1
            		FrequentFlyerVo ffvo = new FrequentFlyerVo();
                    ffvo.setCarrier(ssr.getCarrier());
                    ffvo.setNumber("None");
                    if (ffvo.isValid()) {
                        parsedMessage.getFrequentFlyerDetails().add(ffvo);
                    }
            	}else if(StringUtils.isNotBlank(freeText) && StringUtils.isNotBlank(ssr.getCarrier())){
            		String ffString=PnrUtils.getFrequentFlyertextFromFreeText(freeText);
            		if(StringUtils.isNotBlank(ffString) && ffString.length() > 3){
                		FrequentFlyerVo ffvo = new FrequentFlyerVo();
                        ffvo.setCarrier(ffString.substring(0, 2));
                        ffvo.setNumber(ffString.substring(2, ffString.length()));
                        if (ffvo.isValid()) {
                            parsedMessage.getFrequentFlyerDetails().add(ffvo);
                        }
            		}
            	}
            }
            if(SSR.CTCM.equals(code) && StringUtils.isNotBlank(ssr.getFreeText())){
            	String phone=PnrUtils.getPhoneNumberFromFreeText(ssr.getFreeText());
            	if(StringUtils.isNotBlank(phone)){
            		PhoneVo pvo=new PhoneVo();
            		pvo.setNumber(phone);
            		parsedMessage.getPhoneNumbers().add(pvo);
            	}
            }
        }

        if (!CollectionUtils.isEmpty(ssrDocs)) {
            PassengerVo p = PnrUtils.createPassenger(ssrDocs, tif);
            if (p != null && p.isValid()) {
                p.getDocuments().addAll(visas);
                parsedMessage.getPassengers().add(p);
                parsedMessage.setPassengerCount(parsedMessage.getPassengerCount() + 1);
                currentPassenger=p;
                
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
    	MON mon = getConditionalSegment(MON.class);
        getConditionalSegment(PTK.class);
    	if(tkt != null){
    		TicketFareVo tvo = new TicketFareVo();
    		if(tkt.isTicketless()){
    			tvo.setTicketNumber("0");
    			tvo.setTicketless(tkt.isTicketless());
    		}else{
        		tvo.setTicketType(tkt.getTicketType());
        		tvo.setTicketNumber(tkt.getTicketNumber());
        		tvo.setNumberOfBooklets(tkt.getNumberOfBooklets());
        		tvo.setTicketless(false);   			
    		}

    		if(mon != null){
    			tvo.setCurrencyCode(mon.getCurrencyCode());
    			tvo.setPaymentAmount(mon.getPaymentAmount());
    		}
    		if(tvo.isValid() && currentPassenger != null && currentPassenger.isValid()){
    			currentPassenger.getTickets().add(tvo);
    		}
    	}

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
        List<PaymentFormVo> newForms = new ArrayList<>();
        if (fop != null) {
            List<Payment> payments = fop.getPayments();
            if (!CollectionUtils.isEmpty(payments)) {
            	
            	// arbitrarily select first payment type
                parsedMessage.setFormOfPayment(payments.get(0).getPaymentType());
                for (Payment p : payments) {
                	PaymentFormVo pfvo=new PaymentFormVo();
                	pfvo.setPaymentType(p.getPaymentType());
                	pfvo.setPaymentAmount(p.getPaymentAmount());
                    if (p.isCreditCard()) {
                        CreditCardVo cc = new CreditCardVo();
                        cc.setCardType(p.getVendorCode());
                        cc.setExpiration(p.getExpirationDate());
                        cc.setNumber(p.getAccountNumber());
                        if (cc.isValid()) {
                            newCreditCards.add(cc);
                        }
                    } 
                    parsedMessage.addFormOfPayments(pfvo);
                    
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
                        if(msgs.size()>1){
                        	cc.setAccountHolderAddress(msgs.get(1));
                        	cc.setAccountHolderPhone(msgs.get(2));
                        }
                        
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
    	System.out.println("processGroup5_Flight");
        FlightVo f = new FlightVo();
        f.setCarrier(tvl.getCarrier());
        f.setDestination(tvl.getDestination());
        f.setOrigin(tvl.getOrigin());
        f.setEta(tvl.getEta());
        f.setEtd(tvl.getEtd());
        //PNR data can be received without ETA issue #546 fix.
        if(f.getEta() == null){
        	f.setEta(f.getEtd());
        }
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
             CodeShareVo cso=new CodeShareVo(tvl.getCarrier(),FlightUtils.padFlightNumberWithZeroes(tvl.getFlightNumber())
             		,tvl.getOperatingCarrier(),FlightUtils.padFlightNumberWithZeroes(cs_tvl.getFlightNumber()));
             FlightVo csFlight = new FlightVo();
             csFlight.setCarrier(tvl.getOperatingCarrier());
             csFlight.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(cs_tvl.getFlightNumber()));
             csFlight.setDestination(tvl.getDestination());
             csFlight.setOrigin(tvl.getOrigin());
             csFlight.setEta(tvl.getEta());
             csFlight.setEtd(tvl.getEtd());
             //PNR data can be received without ETA issue #546 fix.
             if(csFlight.getEta() == null){
            	 csFlight.setEta(csFlight.getEtd());
             }
             csFlight.setFlightDate(flightDate);
             csFlight.setMarketingFlightNumber(FlightUtils.padFlightNumberWithZeroes(tvl.getFlightNumber()));
             csFlight.setCodeShareFlight(true);
             //f.setMarketingFlight(true);
             parsedMessage.getCodeshares().add(cso);
             if (csFlight.isValid()) {
                 parsedMessage.getFlights().add(csFlight);
                 parsedMessage.getFlights().remove(f);
               
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
            if(SSR.CTCE.equals(code)){
            	String emailText=ssr.getFreeText();
            	if(emailText.indexOf("-1") > -1){
                	emailText=emailText.substring(0,emailText.indexOf("-1"));
            	}
            	extractEmailInfo(emailText);
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

//        for (;;) {
//            LTS lts = getConditionalSegment(LTS.class);
//            if (lts == null) {
//                break;
//            }
//            if(lts.isAgency()){
//            	processAgencyInfo(lts.getTheText());
//            }else if(lts.isPhone()){
//            	processPhoneInfo(lts.getTheText());
//            }
//            else if(lts.isFormPayment()){
//            	processFormOfPayment(lts.getTheText(),lts.isCashPayment());
//            }
//            else if(lts.isEmail()){
//            	extractEmailInfo(lts.getTheText());
//            }
//            else if(lts.isSeat()){
//            	extractSeatInfo(lts.getTheText());
//            }
//            if(lts.isFrequentFlyer()){
//            	FrequentFlyerVo ffvo=getFrequentFlyerFromLtsText(lts.getTheText());
//            	if(ffvo != null && ffvo.isValid()){
//            		parsedMessage.getFrequentFlyerDetails().add(ffvo);
//            	}
//            }
//            extractContactInfo(lts.getText());
//        }
    }

    private void processLTS() throws ParseException{
        for (;;) {
            LTS lts = getConditionalSegment(LTS.class);
            if (lts == null) {
                break;
            }
            if(lts.isAgency()){
            	processAgencyInfo(lts.getTheText());
            }else if(lts.isPhone()){
            	processPhoneInfo(lts.getTheText());
            }
            else if(lts.isFormPayment()){
            	processFormOfPayment(lts.getTheText(),lts.isCashPayment());
            }
            else if(lts.isEmail()){
            	extractEmailInfo(lts.getTheText());
            }
            else if(lts.isSeat()){
            	extractSeatInfo(lts.getTheText());
            }
            if(lts.isFrequentFlyer()){
            	FrequentFlyerVo ffvo=getFrequentFlyerFromLtsText(lts.getTheText());
            	if(ffvo != null && ffvo.isValid()){
            		parsedMessage.getFrequentFlyerDetails().add(ffvo);
            	}
            }
            extractContactInfo(lts.getText());
        }
    }
    
    /**
     * the agent info that checked-in the passenger
     */
    private void processGroup6_Agent(DAT_G6 dat, TVL tvl) throws ParseException {
    	System.out.println("processGroup6_Agent");
        ORG org = getConditionalSegment(ORG.class, "ORG");
        processAgencyInfo(org);
        /**
        TRI tri=null;
		try {
			tri = getMandatorySegment(TRI.class);
			processGroup7_SeatInfo(tri, tvl);
		} catch (Exception e) {
			 System.out.println("got In-valid MANDATORY TRI");
		}
       **/
        System.out.println("After processGroup7_SeatInfo");
        for (;;) {
            TRI tri = getConditionalSegment(TRI.class);
            if (tri == null) {
                break;
            }
            System.out.println("#######  got conditional TRI" + tri.toString());
            processGroup7_SeatInfo(tri, tvl);
        }        
    }
    
    /**
     * boarding, seat number and checked bag info
     */
    private void processGroup7_SeatInfo(TRI tri, TVL tvl) throws ParseException {
    	 System.out.println("in processGroup7" );
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
        //TIF tif1 = getConditionalSegment(TIF.class);    
        Integer n = tbd.getNumBags()== null ? 0 : tbd.getNumBags();
        Double weight= (tbd.getBaggageWeight()) == null ?0:tbd.getBaggageWeight();
        if (n != null) {
            parsedMessage.setBagCount(n);
            if(parsedMessage.getTotal_bag_count()!= null && parsedMessage.getTotal_bag_count() >0){
            	parsedMessage.setTotal_bag_count(parsedMessage.getTotal_bag_count()+n);
            }else{
            	parsedMessage.setTotal_bag_count(n);
            }
            parsedMessage.setBaggageWeight(weight);
            parsedMessage.setBaggageUnit(tbd.getUnitQualifier());
        }
        getBagVosFromTBD(tbd.getBagDetails(),tif,weight,n,tbd.isHeadOrMemberPool());
       
    }
    
    private void getBagVosFromTBD(List<BagDetails> bDetails,TIF tif,Double weight,Integer numBags,boolean headPool){
    	if(!(bDetails == null || bDetails.size()==0)){
    	if(CollectionUtils.isNotEmpty(parsedMessage.getPassengers())){
    	PassengerVo pvo=PnrUtils.getPaxFromTIF(tif,parsedMessage.getPassengers());
   		pvo.setBagNum(numBags.toString());
   		pvo.setTotalBagWeight(weight.toString());
    	for (BagDetails bd : bDetails){
    		BagVo bvo=new BagVo();
    		if(headPool){
    			bvo.setAirline(parsedMessage.getCarrier());
    			bvo.setBagId(bd.getTagNumber());
    			bvo.setData_source("PNR");
    			bvo.setDestinationAirport(pvo.getDebarkation());
    			bvo.setFirstName(pvo.getFirstName());
    			bvo.setLastName(pvo.getLastName());
    			bvo.setHeadPool(true);
    			parsedMessage.setHeadPool(true);
    		}
    		else{
    			bvo=new BagVo(bd.getTagNumber(),"PNR",bd.getDestAirport(),bd.getAirline(),pvo.getFirstName(),pvo.getLastName());
    		}
    		parsedMessage.getBags().add(bvo);
    	}
    	}
    	
    	}
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
                parsedMessage.setTotal_bag_count(parsedMessage.getBagCount() + n);
                
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
   
    private void processPhoneInfo(String text){
    	String number=PnrUtils.getPhoneNumberFromLTS(text);
    	if(StringUtils.isNotBlank(number)){
    		PhoneVo pvo=new PhoneVo();
    		pvo.setNumber(number);
    		parsedMessage.getPhoneNumbers().add(pvo);
    	}
    }
    
    private void processAgencyInfo(String theText){
    	if(StringUtils.isNotBlank(theText) && theText.contains("CTCT")){
    		String untilCT=theText.substring(0, theText.indexOf("CTCT"));//0/O/28/OSI YY
    		String afterCT=theText.substring(theText.indexOf("CTCT")+4,theText.length());//DCA 123 456-7890 TRAVEL AGENCY
    		AgencyVo vo=new AgencyVo();
     		if(afterCT.contains("TRAVEL") && afterCT.contains("AGENCY")){
    			vo.setIdentifier("TRAVEL AGENCY");
    			vo.setType("TRAVEL");
    		}else if(afterCT.endsWith("A")){
    			vo.setIdentifier("TRAVEL AGENCY");
    			vo.setType("TRAVEL");
    		}
    		afterCT=afterCT.replace("TRAVEL", "");
    		afterCT=afterCT.replace("AGENCY", "");
    		String[] afterCTs=afterCT.split(" ");
    		String[] untilCTs=untilCT.split(" ");
    		StringBuilder b=new StringBuilder();
    		for(String s : afterCTs){
    			s=s.replace("-", "");
    			if(StringUtils.isNoneBlank(s) && s.length() >= 3 && StringUtils.isNumericSpace(s)){
    				b.append(s);
    			}
    			if(StringUtils.isNoneBlank(s) && s.length() == 3 && StringUtils.isAlpha(s)){
    				vo.setLocation(s);
    			}
    		}
    		vo.setPhone(b.toString());
    		if(StringUtils.isBlank(vo.getLocation()) ){
    			afterCT=afterCT.replaceAll("\\s+", "");
    			vo.setLocation(afterCT.substring(0,3));
    		}
    		for(String s : untilCTs){
    			if(StringUtils.isNoneBlank(s) && s.length() == 2 && StringUtils.isAlpha(s)){
    				vo.setName(s);
    			}
    		}
    		if(StringUtils.isBlank(vo.getName())){
    			vo.setName(untilCT);
    		}
    		if(vo.isValid()){
    			parsedMessage.getAgencies().add(vo);
    		}
    	}
    }
    private void extractEmailInfo(String txt){
    	String tmp="";
    	if(txt.contains(LTS.APE)){
    		//LTS+0/O/5/APE FIRST.LAST@YAHOO.COM/FIRST/LAST MRS'
    		tmp=txt.substring(txt.indexOf(LTS.APE)+4, txt.length());
    		if(tmp.indexOf("/") != -1){
    			tmp=tmp.substring(0,tmp.indexOf("/"));
    		}
    		
    	}else{
    		tmp = getEmailFromtext(IFT.CONTACT_EMAIL, txt);
		
    	}
    	//Fix Data | trim email address in back-end #547
    	String[] extras=tmp.split(" ");
    	if(extras != null && extras.length >1){
    		for(String chk:extras){
    			if(chk.contains("//") || chk.contains("@")){
    				tmp=chk;
    				break;
    			}
    		}
    	}
   	 	if (StringUtils.isNotBlank(tmp)) {
    		 //implement future parsing here based on incoming email formats
    		if(tmp.indexOf("//") != -1){
    			tmp=tmp.replace("//", "@");
    		}
    		if(tmp.indexOf("EK ") != -1){
    			tmp=tmp.replace("EK ", "");
    		}
    		if(tmp.indexOf("..") != -1){
    			tmp=tmp.replace("..", "_");
    		}
    		if(tmp.indexOf("/") != -1){
    			tmp=tmp.replace("/", "");
    		}
             EmailVo email = new EmailVo();
             email.setAddress(tmp);
             if(tmp.lastIndexOf("@") != -1){
            	 email.setDomain(tmp.substring(tmp.lastIndexOf("@")+1, tmp.length()));
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
    private String getEmailFromtext(String ctcCode, String text){
    	return text.replace(ctcCode, "");
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
        	agencyVo.setType("TRAVEL");
        }else{
        	agencyVo.setCity(org.getLocationCode());
        }
        if(StringUtils.isNotBlank(agencyVo.getIdentifier())){
        	agencyVo.setType("TRAVEL");
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
    
    private void processFormOfPayment(String textString,boolean isCash){
    	if(StringUtils.isNoneBlank(textString) ){
    		if(isCash){
    			String[] tokens=textString.split("/");
    			if(tokens.length >=2){
    				String tmp=tokens[1];
    				PaymentFormVo pfvo=new PaymentFormVo();
    				pfvo.setPaymentType("CA");
    				pfvo.setPaymentAmount(tmp);
    				parsedMessage.addFormOfPayments(pfvo);
    			}

    		}else{
				if(textString.contains("CHECK")){
					PaymentFormVo pfvo=new PaymentFormVo();
    				pfvo.setPaymentType("CK");
    				pfvo.setPaymentAmount("0.0");
    				parsedMessage.addFormOfPayments(pfvo);
				}else if(textString.contains("FP CC")){
					String[] tokens=textString.split(" ");
					    if(tokens.length >=2){
					    	String tmp=tokens[1];
					    	PaymentFormVo pfvo=new PaymentFormVo();
					    	pfvo.setPaymentType(tmp.substring(0, 2));
		    				pfvo.setPaymentAmount("0.0");
		    				parsedMessage.addFormOfPayments(pfvo);
		    		    	if(tmp.length() >4 && tmp.indexOf("/") > -1){
		    		    		CreditCardVo cc = new CreditCardVo();
		    		    		cc.setCardType(tmp.substring(2, 4));
		    		    		cc.setNumber(tmp.substring(4, tmp.indexOf("/")));
		    		    		cc.setExpiration(ParseUtils.parseExpirationDateForCC(tmp.substring(tmp.indexOf("/")+1, tmp.length()), "MMyy"));
		    		    		parsedMessage.getCreditCards().add(cc);
		    		    	}
		                    
	    			    }
				}
				
			}
    	}
    }  

    private PassengerVo findPaxByName(String name) {
    	String[] tokens=name.split("/");
    	if(tokens.length >=2){
            for (PassengerVo pax : parsedMessage.getPassengers()) {
                if ((tokens[0].contains(pax.getFirstName()) &&  tokens[1].contains(pax.getLastName()))
                		|| (tokens[0].contains(pax.getLastName()) &&  tokens[1].contains(pax.getFirstName()))) {
                    return pax;
                }
            }	
    	}

        return null;
    }
    
    private PassengerVo findPaxByReferenceNumber(String refNumber) {
        for (PassengerVo pax : parsedMessage.getPassengers()) {
            if (refNumber.equals(pax.getTravelerReferenceNumber())) {
                return pax;
            }
        }
        return null;
    }
    
    private static FrequentFlyerVo getFrequentFlyerFromLtsText(String fftext){
    	if(StringUtils.isNotBlank(fftext ) && fftext.indexOf("FQTV") >0){
    		FrequentFlyerVo vo=new FrequentFlyerVo();
    		fftext=fftext.substring(fftext.indexOf("FQTV")+4,fftext.length());
    		String carrier=fftext.substring(0,2);
    		vo.setCarrier(carrier);
    		fftext=fftext.replaceAll("/", " ");
    		String[] tokens=fftext.split(" ");
    		for(String s:tokens){
    			s=s.replaceAll("\\s+", "");
    			if(s.startsWith(carrier) && (!s.contains("HK"))){
    				vo.setNumber(s.substring(2, s.length()));
    				break;
    			}
    		}
    		return vo;
    	}
    	return null;
    }
    private void extractSeatInfo(String ltsText){
   	 	//LTS+SEAT RS 17B LASTNAME/FIRSTNAME DL 123 DDMMMYY JFKLHR'
   	 	//LTS+SEAT NR/RS 20D LASTNAME/FIRSTNAMEMIDDLENAME DL1234 16AUG17 ATLJFK'
    	if(ltsText.contains("NR/RS")){
    		ltsText=ltsText.substring(ltsText.indexOf("NR/RS")+6, ltsText.length());
    	}else if(ltsText.contains(" RS")){
    		ltsText=ltsText.substring(ltsText.indexOf(" RS")+3, ltsText.length());
    	}
    	String[] tokens=ltsText.split(" ");
    	if(tokens.length == 5){
    		SeatVo seat = new SeatVo();
    		PassengerVo thePax =this.findPaxByName(tokens[1]);
            seat.setNumber(tokens[0]);
            if(tokens[4] != null && tokens[4].length() >4){
        	   seat.setOrigin(tokens[4].substring(3, tokens[4].length()));
        	   seat.setDestination(tokens[4].substring(0,3));
           }
            if(thePax != null && seat.isValid() ){
               thePax.getSeatAssignments().add(seat);
           }
    	}else if(tokens.length == 6){
    		SeatVo seat = new SeatVo();
    		PassengerVo thePax =this.findPaxByName(tokens[1]);
    		seat.setNumber(tokens[0]);
    		if(tokens[5] != null && tokens[5].length() >4){
    			seat.setOrigin(tokens[5].substring(3, tokens[5].length()));
    			seat.setDestination(tokens[5].substring(0,3));
    		}
    		if(thePax != null && seat.isValid() ){
    			thePax.getSeatAssignments().add(seat);
    		}    		
    	}
   }
}

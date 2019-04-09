/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.paxlst.segment.unedifact.ATT;
import gov.gtas.parsers.paxlst.segment.unedifact.BGM;
import gov.gtas.parsers.paxlst.segment.unedifact.CNT;
import gov.gtas.parsers.paxlst.segment.unedifact.COM;
import gov.gtas.parsers.paxlst.segment.unedifact.CPI;
import gov.gtas.parsers.paxlst.segment.unedifact.CTA;
import gov.gtas.parsers.paxlst.segment.unedifact.DOC;
import gov.gtas.parsers.paxlst.segment.unedifact.DTM;
import gov.gtas.parsers.paxlst.segment.unedifact.DTM.DtmCode;
import gov.gtas.parsers.paxlst.segment.unedifact.EMP;
import gov.gtas.parsers.paxlst.segment.unedifact.FTX;
import gov.gtas.parsers.paxlst.segment.unedifact.GEI;
import gov.gtas.parsers.paxlst.segment.unedifact.LOC;
import gov.gtas.parsers.paxlst.segment.unedifact.LOC.LocCode;
import gov.gtas.parsers.paxlst.segment.unedifact.MEA;
import gov.gtas.parsers.paxlst.segment.unedifact.MEA.MeasurementCodeQualifier;
import gov.gtas.parsers.paxlst.segment.unedifact.MEA.MeasurementUnitCode;
import gov.gtas.parsers.paxlst.segment.unedifact.NAD;
import gov.gtas.parsers.paxlst.segment.unedifact.NAT;
import gov.gtas.parsers.paxlst.segment.unedifact.QTY;
import gov.gtas.parsers.paxlst.segment.unedifact.RFF;
import gov.gtas.parsers.paxlst.segment.unedifact.TDT;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.FlightUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.parsers.vo.AddressVo;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.ReportingPartyVo;
import gov.gtas.parsers.vo.SeatVo;
import gov.gtas.util.MathUtils;

public final class PaxlstParserUNedifact extends EdifactParser<ApisMessageVo> {
    public PaxlstParserUNedifact() {
        this.parsedMessage = new ApisMessageVo();
    }

    protected String getPayloadText() throws ParseException {
        return lexer.getMessagePayload("BGM", "UNT");
    }

    @Override
    protected void parsePayload() throws ParseException {
        BGM bgm = getMandatorySegment(BGM.class);
        parsedMessage.setMessageCode(bgm.getCode());

        getConditionalSegment(RFF.class);

        for (;;) {
            DTM dtm = getConditionalSegment(DTM.class);
            if (dtm == null) {
                break;
            }
        }

        for (;;) {
            NAD nad = getConditionalSegment(NAD.class);
            if (nad == null) {
                break;
            }
            processReportingParty(nad);
        }

        // at least one TDT is mandatory
        TDT tdt = getMandatorySegment(TDT.class);
        processFlight(tdt);
        for (;;) {
            tdt = getConditionalSegment(TDT.class);
            if (tdt == null) {
                break;
            }
            processFlight(tdt);
        }

        for (;;) {
            NAD nad = getConditionalSegment(NAD.class);
            if (nad == null) {
                break;
            }
            processPax(nad);
        }

        getMandatorySegment(CNT.class);
    }

    /**
     * Segment group 1: reporting party
     */
    private void processReportingParty(NAD nad) throws ParseException {
        ReportingPartyVo rp = new ReportingPartyVo();
        parsedMessage.addReportingParty(rp);
        String partyName = nad.getProfileName();
        if (partyName == null) {
            partyName = nad.getFirstName() + " " + nad.getLastName();
        }
        rp.setPartyName(partyName);

        getConditionalSegment(CTA.class);

        for (;;) {
            COM com = getConditionalSegment(COM.class);
            if (com == null) {
                break;
            }
            rp.setTelephone(ParseUtils.prepTelephoneNumber(com.getPhoneNumber()));
            rp.setFax(ParseUtils.prepTelephoneNumber(com.getFaxNumber()));
        }
    }

    /**
     * Segment group 2: flight details
     */
    @SuppressWarnings("incomplete-switch")
    private void processFlight(TDT tdt) throws ParseException {
        if (tdt.isMasterCrewList()) {
            // Master crew lists (MCLs) are part of TSA regulations
            // and not something we handle.
            throw new ParseException("Master crew lists (MCLs) not handled at this time");
        }

        String dest = null;
        String origin = null;
        Date eta = null;
        Date etd = null;
        boolean loc92Seen = false;

        for (;;) {
            DTM dtm = getConditionalSegment(DTM.class);
            if (dtm == null) {
                break;
            }
        }

        // Segment group 3: loc-dtm loop
        for (;;) {
            LOC loc = getConditionalSegment(LOC.class);
            if (loc == null) {
                break;
            }

            LocCode locCode = loc.getFunctionCode();
            String airport = loc.getLocationNameCode();

            switch (locCode) {
            case DEPARTURE_AIRPORT:
                origin = airport;
                break;
            case ARRIVAL_AIRPORT:
                dest = airport;
                break;
            case BOTH_DEPARTURE_AND_ARRIVAL_AIRPORT:
                if (loc92Seen) {
                    dest = airport;
                    loc92Seen = false;
                } else {
                    origin = airport;
                    loc92Seen = true;
                }
                break;
            case FINAL_DESTINATION:
                if (loc92Seen) {
                    dest = airport;
                    loc92Seen = false;
                } else {
                    throw new ParseException("LOC+" + LocCode.FINAL_DESTINATION + " found but no corresponding LOC+"
                            + LocCode.BOTH_DEPARTURE_AND_ARRIVAL_AIRPORT);
                }
                break;
            }

            // get corresponding DTM, if it exists
            DTM dtm = getConditionalSegment(DTM.class);
            if (dtm != null) {
                Date d = dtm.getDtmValue();
                DtmCode dtmCode = dtm.getDtmCode();
                if (dtmCode == DtmCode.DEPARTURE) {
                    etd = d;
                } else if (dtmCode == DtmCode.ARRIVAL) {
                    eta = d;
                }
            }

            if (origin != null && dest != null) {
                FlightVo f = new FlightVo();
                f.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(tdt.getFlightNumber()));
                f.setCarrier(tdt.getC_carrierIdentifier());
                f.setOrigin(origin);
                f.setDestination(dest);
                f.setEta(eta);
                f.setEtd(etd);

                if (f.isValid()) {
                    parsedMessage.addFlight(f);
                } else {
                    throw new ParseException("Invalid flight: " + f);
                }

                dest = null;
                origin = null;
                eta = null;
                etd = null;
                loc92Seen = false;
            }
        }
    }

    /**
     * Segment group 4: passenger details
     */
    @SuppressWarnings("incomplete-switch")
    private void processPax(NAD nad) throws ParseException {
        PassengerVo p = new PassengerVo();
        p.setFirstName(nad.getFirstName());
        p.setLastName(nad.getLastName());
        p.setMiddleName(nad.getMiddleName());

        createPassengerAddress(nad, p);

        String paxType = null;
        if (nad.getNadCode() == null) {
            paxType = "P";
        } else {
            switch (nad.getNadCode()) {
            case CREW_MEMBER:
            case INTRANSIT_CREW_MEMBER:
                paxType = "C";
                break;
            case INTRANSIT_PASSENGER:
                paxType = "I";
                break;
            default:
                paxType = "P";
                break;
            }
        }
        p.setPassengerType(paxType);

        if (p.isValid()) {
            parsedMessage.addPax(p);
        } else {
            throw new ParseException("Invalid passenger: " + nad);
        }

        for (;;) {
            ATT att = getConditionalSegment(ATT.class);
            if (att == null) {
                break;
            }
            switch (att.getFunctionCode()) {
            case GENDER:
                p.setGender(att.getAttributeDescriptionCode());
                break;
            }
        }

        for (;;) {
            DTM dtm = getConditionalSegment(DTM.class);
            if (dtm == null) {
                break;
            }
            DtmCode dtmCode = dtm.getDtmCode();
            if (dtmCode == DtmCode.DATE_OF_BIRTH) {
                Date dob = dtm.getDtmValue();
                if (dob != null) {
                    p.setDob(dob);
                    p.setAge(DateUtils.calculateAge(dob));
                }
            }
        }
        
        /**
         * APIS 16B | Parsing and Loading New bag information #815
         * 
         * FTX segment below ignores bag information if bagsCountedFromMEA is true, meaning ... it was already parsed from MEA segment and avoids overriding the information from FTX segment.
         *  
         */
        boolean bagsCountedFromMEA =false;

        for (;;) {
            MEA mea = getConditionalSegment(MEA.class);
            if (mea == null) {
                break;
            }
            
            /**
             * APIS 16B | Parsing and Loading New bag information #815
             */
	            if(mea.isSegmentIncludedInAPISMessage()) {
	            	bagsCountedFromMEA=true;
	            }
	            
	            if(MeasurementCodeQualifier.CT.equals(mea.getCode())) {
	            	p.setBagNum(mea.getNumBags());
	            }
	            if(MeasurementCodeQualifier.WT.equals(mea.getCode())) {
	            	
	            	if(mea.getWeightUnit().equals(MeasurementUnitCode.LBR)) {
	            		// Convert pounds to kilograms
	            		double kilograms = MathUtils.poundsToKilos(Double.parseDouble(mea.getBagWeight()));
	            		p.setTotalBagWeight(String.valueOf(kilograms));
	            }else
	            		p.setTotalBagWeight(mea.getBagWeight());
	            }
            
            /**
             * ENDS #815
             */
        }

        for (;;) {
            GEI gei = getConditionalSegment(GEI.class);
            if (gei == null) {
                break;
            }
        }

        for (;;) {
            FTX ftx = getConditionalSegment(FTX.class);
            if (ftx == null) {
                break;
            }
            String bagId = ftx.getBagId();
            if (StringUtils.isNotBlank(bagId)) {
            	if(!bagsCountedFromMEA) // APIS 16B | Ignore bag weight if already parsed from MEA segment #815. 
            	p.setTotalBagWeight(ftx.getBagWeight());
            	p.setBagId(bagId);
                List<String> bags = new ArrayList<>();
                
                if (ftx.getNumBags() != null ) {
                	if(!bagsCountedFromMEA) // APIS 16B | Ignore bag count if already parsed from MEA segment #815. 
                    p.setBagNum(ftx.getNumBags());
                    int numBags = Integer.parseInt(ftx.getNumBags());
                    String airlineCode = bagId.substring(0, Math.min(bagId.length(), 2));
                    if(StringUtils.isNumeric(airlineCode) && StringUtils.isNumeric(bagId)){
                        for (int i = 0; i < numBags - 1; i++) {
                        	int j=1+i;
                            String temp = bagId+":"+j;
                            bags.add(temp);
                        }                   	
                    }else{
                    int startNum = getNum(bagId);
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < numBags - 1; i++) {
                        sb.setLength(0);
                        String temp = sb.append(airlineCode).append(++startNum).toString();
                        bags.add(temp);
                    }
                }
                }
                else{
                   	bags.add(bagId);
                	if(!bagsCountedFromMEA)
                   	p.setBagNum("1");
                }
                p.setBags(bags);
            }
        }

        String birthCountry = null;
        for (;;) {
            LOC loc = getConditionalSegment(LOC.class);
            if (loc == null) {
                break;
            }

            String val = loc.getLocationNameCode();
            switch (loc.getFunctionCode()) {
            case PORT_OF_DEBARKATION:
                p.setDebarkation(val);
                break;
            case PORT_OF_EMBARKATION:
                p.setEmbarkation(val);
                break;
            case COUNTRY_OF_RESIDENCE:
                p.setResidencyCountry(val);
                break;
            case PLACE_OF_BIRTH:
                birthCountry = val;
                break;
            }
        }

        getConditionalSegment(COM.class);

        for (;;) {
            EMP emp = getConditionalSegment(EMP.class);
            if (emp == null) {
                break;
            }
        }

        for (;;) {
            NAT nat = getConditionalSegment(NAT.class);
            if (nat == null) {
                if (p.getCitizenshipCountry() == null && birthCountry != null) {
                    p.setCitizenshipCountry(birthCountry);
                }
                break;
            }
            p.setCitizenshipCountry(nat.getNationalityCode());
        }

        for (;;) {
            RFF rff = getConditionalSegment(RFF.class);
            if (rff == null) {
                break;
            }
            switch (rff.getReferenceCodeQualifier()) {
            case ASSIGNED_SEAT:
                if (CollectionUtils.isEmpty(parsedMessage.getFlights())) {
                    break;
                }
                SeatVo seat = new SeatVo();
                seat.setApis(Boolean.valueOf(true));
                seat.setNumber(rff.getReferenceIdentifier());
                FlightVo firstFlight = parsedMessage.getFlights().get(0);
                seat.setOrigin(firstFlight.getOrigin());
                seat.setDestination(firstFlight.getDestination());
                if (seat.isValid()) {
                    p.getSeatAssignments().add(seat);
                }

                break;

            case CUSTOMER_REF_NUMBER:
                // possibly freq flyer #
                break;
                
            case RESERVATION_REF_NUMBER:
            	p.setReservationReferenceNumber(rff.getReferenceIdentifier());
            	break;
            }
            
        }

        for (;;) {
            DOC doc = getConditionalSegment(DOC.class);
            if (doc == null) {
                break;
            }
            processDocument(p, doc);
        }
    }

    private void createPassengerAddress(NAD nad, PassengerVo p) {
    	//TODO passenger address is stored in Address table.modify in future to store separately
    	AddressVo avo=new AddressVo();
        StringJoiner sj = new StringJoiner(" ");
        if (nad.getNumberAndStreetIdentifier() != null) {
        	avo.setLine1(nad.getNumberAndStreetIdentifier());
            sj.add(nad.getNumberAndStreetIdentifier());
        }
        if (nad.getCity() != null) {
        	avo.setCity(nad.getCity());
            sj.add(nad.getCity());
        }
        if (nad.getCountrySubCode() != null) {
        	avo.setState(nad.getCountrySubCode());
        	sj.add(nad.getCountrySubCode());
        }
        if (nad.getPostalCode() != null) {
        	avo.setPostalCode(nad.getPostalCode());
            sj.add(nad.getPostalCode());
        }
        if (nad.getCountryCode() != null) {
        	avo.setCountry(nad.getCountryCode());
            sj.add(nad.getCountryCode());
        }
        p.setAddress(sj.toString());
    }

    private int getNum(String s) {
        int res = 0;
        int p = 1;
        int i = s.length() - 1;
        while (i >= 0) {
            int d = s.charAt(i) - '0';
            if (d >= 0 && d <= 9)
                res += d * p;
            else
                break;
            i--;
            p *= 10;
        }

        return res;
    }

    /**
     * Segment group 5: Passenger documents
     */
    private void processDocument(PassengerVo p, DOC doc) throws ParseException {
        DocumentVo d = new DocumentVo();

        d.setDocumentType(doc.getDocCode());
        d.setDocumentNumber(doc.getDocumentIdentifier());

        for (;;) {
            DTM dtm = getConditionalSegment(DTM.class);
            if (dtm == null) {
                break;
            }
            DtmCode dtmCode = dtm.getDtmCode();
            if (dtmCode == DtmCode.PASSPORT_EXPIRATION_DATE) {
                d.setExpirationDate(dtm.getDtmValue());
            }
        }

        for (;;) {
            GEI gei = getConditionalSegment(GEI.class);
            if (gei == null) {
                break;
            }
        }

        for (;;) {
            RFF rff = getConditionalSegment(RFF.class);
            if (rff == null) {
                break;
            }
        }

        for (;;) {
            LOC loc = getConditionalSegment(LOC.class);
            if (loc == null) {
                break;
            }
            LocCode locCode = loc.getFunctionCode();
            if (locCode == LocCode.PLACE_OF_DOCUMENT_ISSUE) {
                d.setIssuanceCountry(loc.getLocationNameCode());

                if (p.getCitizenshipCountry() == null) {
                    // wasn't set by NAD:LOC, so derive it here from issuance
                    // country
                    if ("P".equals(d.getDocumentType())) {
                        p.setCitizenshipCountry(d.getIssuanceCountry());
                    }
                }
            }
        }

        getConditionalSegment(CPI.class);

        for (;;) {
            QTY qty = getConditionalSegment(QTY.class);
            if (qty == null) {
                break;
            }
        }
        if (ParseUtils.isValidDocument(d)) {
            p.addDocument(d);
        }

    }

}

/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.paxlst.segment.usedifact.CTA;
import gov.gtas.parsers.paxlst.segment.usedifact.DTM;
import gov.gtas.parsers.paxlst.segment.usedifact.DTM.DtmCode;
import gov.gtas.parsers.paxlst.segment.usedifact.LOC;
import gov.gtas.parsers.paxlst.segment.usedifact.LOC.LocCode;
import gov.gtas.parsers.paxlst.segment.usedifact.PDT;
import gov.gtas.parsers.paxlst.segment.usedifact.PDT.DocType;
import gov.gtas.parsers.paxlst.segment.usedifact.PDT.PersonStatus;
import gov.gtas.parsers.paxlst.segment.usedifact.TDT;
import gov.gtas.parsers.util.FlightUtils;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.ReportingPartyVo;

public final class PaxlstParserUSedifact extends EdifactParser<ApisMessageVo> {
    private static final Logger logger = LoggerFactory.getLogger(PaxlstParserUSedifact.class);
    
    protected enum GROUP {
        NONE,
        HEADER,
        REPORTING_PARTY,
        FLIGHT,
        PAX
    }
    
    protected GROUP currentGroup;
    
    // TODO
    protected String getPayloadText() throws ParseException {
        return lexer.getMessagePayload("BGM", "UNT");
    }
    
    public PaxlstParserUSedifact() { 
        this.parsedMessage = new ApisMessageVo();
    }
    
    public void parsePayload() {
        currentGroup = GROUP.NONE;
        
        for (ListIterator<Segment> i=segments.listIterator(); i.hasNext(); ) {
            Segment s = i.next();
//            System.out.println(s);
            
            switch (s.getName()) {
            case "CTA":
                if (currentGroup == GROUP.NONE || currentGroup == GROUP.REPORTING_PARTY) {
                    currentGroup = GROUP.REPORTING_PARTY;
                    processReportingParty(s);
                } else {
                    handleUnexpectedSegment(s);
                    return;
                }
                break;

            case "TDT":
                if (currentGroup == GROUP.HEADER 
                    || currentGroup == GROUP.REPORTING_PARTY
                    || currentGroup == GROUP.FLIGHT) {
                    
                    currentGroup = GROUP.FLIGHT;
                    processFlight(s, i);
                } else {
                    handleUnexpectedSegment(s);
                    return;
                }
                break;
                
            case "UNS":
                currentGroup = GROUP.PAX;
                break;
            
            case "PDT":
                if (currentGroup == GROUP.PAX) {
                    processPax(s);
                } else {
                    // missing UNS segment
                    handleUnexpectedSegment(s);
                    return;
                }
                break;
                
            case "UNZ":
                currentGroup = GROUP.NONE;
                break;
            }
        }        
    }

    private void processFlight(Segment seg, ListIterator<Segment> i) {
        TDT tdt = (TDT)seg;
        FlightVo f = new FlightVo();
        parsedMessage.addFlight(f);

        f.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(tdt.getC_flightNumber()));
        f.setCarrier(tdt.getC_airlineCode());

        while (i.hasNext()) {
            Segment s = i.next();
//            System.out.println("\t" + s);
            switch (s.getName()) {
            case "LOC":
                LOC loc = (LOC)s;
                LocCode locCode = loc.getLocationCode();
                String country = loc.getIataCountryCode();
                String  airport = loc.getIataAirportCode();
                if (locCode == LocCode.DEPARTURE) {
//                    f.setOriginCountry(country);
                    f.setOrigin(airport);
                } else if (locCode == LocCode.ARRIVAL) {
//                    f.setDestinationCountry(country);
                    f.setDestination(airport);
                }
                break;
            
            case "DTM":
                DTM dtm = (DTM)s;
                DtmCode dtmCode = dtm.getDtmCode();
                if (dtmCode == DtmCode.DEPARTURE_DATETIME) {
                    f.setEtd(dtm.getC_dateTime());
                } else if (dtmCode == DtmCode.ARRIVAL_DATETIME) {
                    f.setEta(dtm.getC_dateTime());
                }
                break;
                
            default:
                i.previous();
                return;
            }
        }
    }

    private void processPax(Segment s) {
        PassengerVo p = new PassengerVo();
        parsedMessage.addPax(p);

        PDT pdt = (PDT)s;
        p.setFirstName(pdt.getLastName());
        p.setLastName(pdt.getLastName());
        p.setMiddleName(pdt.getC_middleNameOrInitial());
        p.setDob(pdt.getDob());
        p.setGender(pdt.getGender());
        PersonStatus status = pdt.getPersonStatus();
        p.setPassengerType(status.toString());
//        if (status == PersonStatus.PAX) {
//            p.setType(PaxType.PAX);
//        } else if (status == PersonStatus.CREW) {
//            p.setType(PaxType.CREW);
//        } else {
//            p.setType(PaxType.OTHER);
//        }

        DocumentVo d = new DocumentVo();
        p.addDocument(d);
        d.setDocumentNumber(pdt.getDocumentNumber());
        d.setExpirationDate(pdt.getC_dateOfExpiration());
        DocType docType = pdt.getDocumentType();
        d.setDocumentType(docType.toString());
//        if (docType == DocType.PASSPORT) {
//            d.setDocumentType(DocumentType.P);  
//        } else {
//            // TODO
//        }        
//        System.out.println("\t" + p);
    }

    private void processReportingParty(Segment s) {
        CTA cta = (CTA)s;
        ReportingPartyVo rp = new ReportingPartyVo();
        parsedMessage.addReportingParty(rp);
        rp.setPartyName(cta.getName());
        rp.setTelephone(cta.getTelephoneNumber());
        rp.setFax(cta.getFaxNumber());
    }
    
    private void handleUnexpectedSegment(Segment s) {
        logger.error("unexpected segment " + s);
    }    
}

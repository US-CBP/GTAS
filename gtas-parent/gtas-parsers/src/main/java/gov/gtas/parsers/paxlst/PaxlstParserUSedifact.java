/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import java.util.Date;

import gov.gtas.config.ParserConfig;
import gov.gtas.parsers.edifact.EdifactParser;
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
import gov.gtas.parsers.paxlst.segment.usedifact.UNS;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.FlightUtils;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.ReportingPartyVo;

public final class PaxlstParserUSedifact extends EdifactParser<ApisMessageVo> {

	private ParserConfig parserConfig;

	public PaxlstParserUSedifact(ParserConfig parserConfig) {
		this.parsedMessage = new ApisMessageVo();
		this.parserConfig = parserConfig;
	}

	@Override
	protected String getPayloadText() throws ParseException {
		return lexer.getMessagePayload("CTA", "UNT");
	}

	@Override
	public void parsePayload() throws ParseException {
		CTA cta = getMandatorySegment(CTA.class);
		processReportingParty(cta);
		for (;;) {
			cta = getConditionalSegment(CTA.class);
			if (cta == null) {
				break;
			}
			processReportingParty(cta);
		}

		TDT tdt = getMandatorySegment(TDT.class);
		processFlight(tdt);
		for (;;) {
			tdt = getConditionalSegment(TDT.class);
			if (tdt == null) {
				break;
			}
			processFlight(tdt);
		}

		getMandatorySegment(UNS.class);

		PDT pdt = getMandatorySegment(PDT.class);
		processPax(pdt);
		for (;;) {
			pdt = getConditionalSegment(PDT.class);
			if (pdt == null) {
				break;
			}
			processPax(pdt);
		}
	}

	private void processFlight(TDT tdt) throws ParseException {
		String dest = null;
		String origin = null;
		Date eta = null;
		Date etd = null;
		boolean ptFound = false;

		for (;;) {
			LOC loc = getConditionalSegment(LOC.class);
			if (loc == null) {
				break;
			}

			LocCode locCode = loc.getLocationCode();
			String airport = loc.getIataAirportCode();

			if (dest != null && ptFound) {
				// new flight's origin is last flight's dest
				origin = dest;
				dest = airport;
			} else {
				if (locCode == LocCode.DEPARTURE) {
					origin = airport;
				} else if (locCode == LocCode.ARRIVAL) {
					dest = airport;
				} else if (locCode == LocCode.PLACE_OF_TRANSIT) {
					ptFound = true;
					dest = airport;
				}
			}

			DTM dtm = getConditionalSegment(DTM.class);
			if (dtm != null) {
				DtmCode dtmCode = dtm.getDtmCode();
				Date dateTime = dtm.getC_dateTime();

				if (eta != null && ptFound) {
					// new flight's etd is last flight's eta
					etd = eta;
					eta = dateTime;
				} else {
					if (dtmCode == DtmCode.DEPARTURE_DATETIME) {
						etd = dateTime;
					} else if (dtmCode == DtmCode.ARRIVAL_DATETIME) {
						eta = dateTime;
					}
				}
			}

			if (origin != null && dest != null) {
				FlightVo f = new FlightVo();

				f.setFlightNumber(FlightUtils.padFlightNumberWithZeroes(tdt.getC_flightNumber()));
				f.setCarrier(tdt.getC_airlineCode());
				f.setOrigin(origin);
				f.setDestination(dest);
				f.setLocalEtaDate(eta);
				f.setLocalEtdDate(etd);

				if (f.isValid()) {
					parsedMessage.addFlight(f);
				} else {
					throw new ParseException("Invalid flight: " + f);
				}
			}
		}
	}

	private void processPax(PDT pdt) throws ParseException {
		PassengerVo p = new PassengerVo();
		parsedMessage.addPax(p);

		p.setFirstName(pdt.getFirstName());
		p.setLastName(pdt.getLastName());
		p.setMiddleName(pdt.getC_middleNameOrInitial());
		if (pdt.getDob() != null) {
			p.setDob(pdt.getDob());
			p.setAge(DateUtils.calculateAge(pdt.getDob()));
		}
		p.setGender(pdt.getGender());
		p.setEmbarkation(pdt.getIataEmbarkationAirport());
		p.setDebarkation(pdt.getIataDebarkationAirport());

		PersonStatus status = pdt.getPersonStatus();
		if (status == null) {
			p.setPassengerType("P");
		} else {
			p.setPassengerType(status.toString());
			if (status == PersonStatus.CREW) {
				p.setPassengerType("C");
			} else if (status == PersonStatus.IN_TRANSIT) {
				p.setPassengerType("I");
			} else {
				p.setPassengerType("P");
			}
		}

		DocumentVo d = new DocumentVo();
		p.addDocument(d);
		d.setDocumentNumber(pdt.getDocumentNumber());
		d.setExpirationDate(pdt.getC_dateOfExpiration());
		DocType docType = pdt.getDocumentType();
		d.setDocumentType(docType.toString());
		if (docType == DocType.PASSPORT) {
			d.setDocumentType("P");
		} else if (docType == DocType.VISA) {
			d.setDocumentType("V");
		}
	}

	private void processReportingParty(CTA cta) {
		ReportingPartyVo rp = new ReportingPartyVo();
		parsedMessage.addReportingParty(rp);
		rp.setPartyName(cta.getName());
		rp.setTelephone(cta.getTelephoneNumber());
		rp.setFax(cta.getFaxNumber());
	}
}

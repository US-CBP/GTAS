/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import java.util.Date;
import java.util.StringJoiner;

import gov.gtas.config.ParserConfig;
import gov.gtas.parsers.pnrgov.enums.MeasurementQualifier;
import gov.gtas.parsers.vo.*;
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
import gov.gtas.util.MathUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaxlstParserUNedifact extends EdifactParser<ApisMessageVo> {

	private Logger logger = LoggerFactory.getLogger(PaxlstParserUNedifact.class);

	private ParserConfig parserConfig;

	public PaxlstParserUNedifact(ParserConfig parserConfig) {
		this.parsedMessage = new ApisMessageVo();
		this.parserConfig = parserConfig;
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
					logger.error("Final destination error. No LOC 92 seen before LOC 130 "
							+ "Arrival country booking details are likely incomplete for flight!");
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
				f.setLocalEtaDate(eta);
				f.setLocalEtdDate(etd);

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
		// bags counted and weighed from measurement take priority over bag count from
		// ID.
		// Keep a flag to see if we got measurement information.
		boolean bagsCountedFromMEA = false;
		BagMeasurementsVo bagMeasurementsVo = new BagMeasurementsVo();
		for (;;) {
			MEA mea = getConditionalSegment(MEA.class);
			if (mea == null) {
				break;
			}
			if (mea.isSegmentIncludedInAPISMessage()) {
				bagsCountedFromMEA = true;
			}

			if (MeasurementCodeQualifier.CT.equals(mea.getCode())) {
				String numBagsAsString = mea.getNumBags();
				p.setBagNum(numBagsAsString);
				Integer bagCount = bagMeasurementsVo.getBagCountFromString(numBagsAsString);
				bagMeasurementsVo.setQuantity(bagCount);
			}
			if (MeasurementCodeQualifier.WT.equals(mea.getCode())) {
				String bagWeight = mea.getBagWeight();
				Double weight = bagMeasurementsVo.getBagWeightFromString(bagWeight);
				bagMeasurementsVo.setRawWeight(weight);

				if (mea.getWeightUnit().equals(MeasurementUnitCode.LBR)) {
					// Convert pounds to kilograms
					bagMeasurementsVo.setMeasurementType(MeasurementQualifier.LBS.getEnglishName());
					if (weight != null) {
						double kilograms = MathUtils.poundsToKilos(weight);
						p.setTotalBagWeight(String.valueOf(kilograms));
						bagMeasurementsVo.setWeightInKilos(kilograms);
					}
				} else {
					p.setTotalBagWeight(mea.getBagWeight());
					bagMeasurementsVo.setWeightInKilos(weight);
					bagMeasurementsVo.setMeasurementType(MeasurementQualifier.KILOS.getEnglishName());
				}
			}
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
				p.setBagId(bagId);
				if (!bagsCountedFromMEA) {
					Integer bagCountFromString = bagMeasurementsVo.getBagCountFromString(ftx.getNumBags());
					bagMeasurementsVo.setQuantity(bagCountFromString);
					Double weight = bagMeasurementsVo.getBagWeightFromString(ftx.getBagWeight());
					bagMeasurementsVo.setWeightInKilos(weight);
					bagMeasurementsVo.setRawWeight(weight);
					bagMeasurementsVo.setMeasurementType(MeasurementQualifier.UNKNOWN.getEnglishName());
					p.setTotalBagWeight(ftx.getBagWeight());
					String bagNum = ftx.getNumBags() == null ? "1" : ftx.getNumBags();
					p.setBagNum(bagNum);
				} else {
					Double bagWeight = bagMeasurementsVo.getWeightInKilos();
					if (bagWeight != null) {
						int newWeight = bagWeight.intValue();
						p.setTotalBagWeight(Integer.toString(newWeight));
					}
					Integer bagCount = bagMeasurementsVo.getQuantity();
					if (bagCount != null) {
						p.setBagNum(Integer.toString(bagCount));
					}
				}
				int repeatedBagIdentification = getNumberOfBagVosToMake(ftx);
				String airlineCodePartOfBagId = bagId.substring(0, Math.min(bagId.length(), 2));
				String numberPartOfBagIdAsString = getNumericPartOfBagId(bagId);
				int numericPartOfBagId;
				try {
					// first bag ID is always what is given. we infer the rest as they are
					// sequential.
					numericPartOfBagId = Integer.parseInt(numberPartOfBagIdAsString);
					for (int i = 0; i < repeatedBagIdentification; i++) {
						String inferredBagId = addZerosToGetFour(Integer.toString(numericPartOfBagId));
						BagVo bagVo = getBagVo(p, bagMeasurementsVo, airlineCodePartOfBagId, inferredBagId);
						parsedMessage.addBagVo(bagVo);
						numericPartOfBagId++;
					}
				} catch (Exception ignored) {
					// Not expected to happen but if ID can not be generated use the given bag Id to
					// create a bagvo.
					BagVo bagVo = getBagVo(p, bagMeasurementsVo, airlineCodePartOfBagId, bagId);
					parsedMessage.addBagVo(bagVo);
				}
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
				if (p.getNationality() == null && birthCountry != null) {
					p.setNationality(birthCountry);
				}
				break;
			}
			p.setNationality(nat.getNationalityCode());
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
				seat.setNumber(trimSeatNumber(rff.getReferenceIdentifier()));
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

	protected String trimSeatNumber(String seatNumber) {
		if (!seatNumber.startsWith("0"))
			return seatNumber;

		return trimSeatNumber(seatNumber.substring(1));

	}

	private String addZerosToGetFour(String bagId) {
		StringBuilder bagIdNumberBuilder = new StringBuilder(bagId);
		while (bagIdNumberBuilder.length() < 4) {
			bagIdNumberBuilder.insert(0, "0");
		}
		return bagIdNumberBuilder.toString();
	}

	private String getNumericPartOfBagId(String bagId) {
		String numberPartOfBagId = "";
		if (bagId.length() > 2) {
			numberPartOfBagId = bagId.substring(2);
		}
		return numberPartOfBagId;
	}

	private int getNumberOfBagVosToMake(FTX ftx) {
		int repeatedBagIdentification;
		if (ftx.getNumBags() == null) {
			repeatedBagIdentification = 1;
		} else {
			try {
				repeatedBagIdentification = Integer.parseInt(ftx.getNumBags());
			} catch (Exception ignored) {
				repeatedBagIdentification = 1;
			}
		}
		return repeatedBagIdentification;
	}

	private BagVo getBagVo(PassengerVo p, BagMeasurementsVo bagMeasurementsVo, String airlineCode, String bagId) {
		BagVo bagVo = new BagVo();
		bagVo.setData_source("APIS");
		bagVo.setPrimeFlight(true); // apis bags always are prime flight.
		bagVo.setAirline(airlineCode);
		bagVo.setBagId(bagId);
		bagVo.setBagMeasurementsVo(bagMeasurementsVo);
		bagVo.setBagMeasurementUUID(bagMeasurementsVo.getUuid());
		bagVo.setPassengerId(p.getPassengerVoUUID());
		return bagVo;
	}

	private void createPassengerAddress(NAD nad, PassengerVo p) {
		// TODO passenger address is stored in Address table.modify in future to store
		// separately
		AddressVo avo = new AddressVo();
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

	/**
	 * Segment group 5: Passenger documents
	 */
	private void processDocument(PassengerVo p, DOC doc) throws ParseException {
		DocumentVo d = new DocumentVo();
		if (parserConfig.getEnabled()) {
			// loosely parse records without document codes
			if (StringUtil.isBlank(doc.getDocCode())) {
				doc.setDocCode(parserConfig.getDefaultDocType());
			}
		}

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

				if (p.getNationality() == null) {
					// wasn't set by NAD:LOC, so derive it here from issuance
					// country
					if ("P".equals(d.getDocumentType())) {
						p.setNationality(d.getIssuanceCountry());
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

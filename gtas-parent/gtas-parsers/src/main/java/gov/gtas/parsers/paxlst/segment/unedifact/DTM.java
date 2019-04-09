/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * DTM DATE/TIME/PERIOD
 * <p>
 * Function: To specify date, and/or time, or period. In Group 3 (LOC-DTM) this
 * segment specifies the departure and arrival date/time of a flight. In Group
 * 5, it specifies the date of birth of a passenger or crew member.
 * <p>
 * Examples: DTM+329:640217' Indicates the date of birth of the passenger or
 * crew member (i.e. February 17, 1964.)
 */
public class DTM extends Segment {
    private static final String DATE_ONLY_FORMAT = DateUtils.DATE_FORMAT_YEAR_FIRST;
    private static final String DATE_TIME_FORMAT = DateUtils.DT_FORMAT_YEAR_FIRST;

    public enum DtmCode {
        DEPARTURE("189"), 
        ARRIVAL("232"), 
        ARRIVAL_AND_DEPARTURE_MCL("554"),

        DATE_OF_BIRTH("329"), 
        PASSPORT_EXPIRATION_DATE("36");
        
        private final String code;
        private DtmCode(String code) { this.code = code; }        
        public String getCode() { return code; }
        
        private static final Map<String, DtmCode> BY_CODE_MAP = new LinkedHashMap<>();
        static {
            for (DtmCode rae : DtmCode.values()) {
                BY_CODE_MAP.put(rae.code, rae);
            }
        }

        public static DtmCode forCode(String code) {
            return BY_CODE_MAP.get(code);
        }                
    }

    private DtmCode dtmCode;
    private Date dtmValue;

    public DTM(List<Composite> composites) throws ParseException {
        super(DTM.class.getSimpleName(), composites);
        Composite c = getComposite(0);
        if (c != null) {
            this.dtmCode = DtmCode.forCode(c.getElement(0));

            String d = c.getElement(1);
            if (d != null) {
                String dateFormat = (d.length() == DATE_TIME_FORMAT.length()) ? DATE_TIME_FORMAT : DATE_ONLY_FORMAT;
                if (this.dtmCode == DtmCode.DATE_OF_BIRTH) {
                    this.dtmValue = ParseUtils.parseBirthday(d, dateFormat);
                } else {
                    this.dtmValue = ParseUtils.parseDateTime(d, dateFormat);
                }
            }
        }
    }

    public DtmCode getDtmCode() {
        return dtmCode;
    }

    public Date getDtmValue() {
        return dtmValue;
    }
}

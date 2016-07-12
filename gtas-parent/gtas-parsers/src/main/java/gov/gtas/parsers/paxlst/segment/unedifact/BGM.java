/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

/**
 * <p>
 * BGM: BEGINNING OF MESSAGE
 * <p>
 * A segment to indicate the type and function of the message.
 * <p>
 * This segment is mandatory. The BGM Segment is used to determine the nature of
 * the transaction as it applies to Passenger information reporting, Flight
 * Reporting, or Crew reporting. If a duplicate transmission is received for a
 * passenger or crew who was previously reported and cleared for the flight, DHS
 * will use the data supplied in the duplicate message to replace the previous
 * version. If a full replacement is sent, all required data elements must be
 * sent with the new transmission.
 * <p>
 * <ul>
 * <li>BGM+745'
 * <li>BGM+745+CP' Clear Passenger Request (Message type used to identify new
 * passengers to DHS or to obtain updated ESTA status)
 * </ul>
 */
public class BGM extends Segment {
    public enum DocumentNameCode {
        PASSENGER_LIST("745"),
        CREW_LIST("250"),
        FLIGHT_STATUS_UPDATE("266"),
        MASTER_CREW_LIST("336"),
        GATE_PASS_REQUEST("655");
        
        private final String code;
        private DocumentNameCode(String code) { this.code = code; }        
        public String getCode() { return code; }
        
        private static final Map<String, DocumentNameCode> BY_CODE_MAP = new LinkedHashMap<>();
        static {
            for (DocumentNameCode rae : DocumentNameCode.values()) {
                BY_CODE_MAP.put(rae.code, rae);
            }
        }

        public static DocumentNameCode forCode(String code) {
            return BY_CODE_MAP.get(code);
        }        
    }

    public enum DocumentIdentifier {
        // PASSENGER_LIST
        CHANGE_PAX_DATA("CP"),
        CANCEL_RESERVATION("XR"),
        REDUCTION_IN_PARTY("RP"),
        
        // FLIGHT_STATUS_UPDATE
        FLIGHT_CLOSE("CL"),
        FLIGHT_CLOSE_WITH_PAX_NOT_ON_BOARD("CLNB"),
        FLIGHT_CLOSE_WITH_PAX_ON_BOARD("CLOB"),
        CANCEL_FLIGHT("XF"),
        CHANGE_FLIGHT("CF"),

        // CREW_LIST
        PASSENGER_FLIGHT_REGULAR_SCHEDULED_CREW("C"),
        PASSENGER_FLIGHT_CREW_CHANGE("CC"),
        CARGO_FLIGHT_REGULAR_SCHEDULED_CREW("B"),
        CARGO_FLIGHT_CREW_CHANGE("BC"),
        OVERFLIGHT_OF_PASSENGER_FLIGHT("A"),
        OVERFLIGHT_OF_CARGO_FLIGHT("D"),
        DOMESTIC_CONTINUANCE_OF_PASSENGER_FLIGHT_REGULAR_SCHEDULED_CREW("E"),
        DOMESTIC_CONTINUANCE_OF_PASSENGER_FLIGHT_CREW_CHANGE("EC"),
        DOMESTIC_CONTINUANCE_OF_CARGO_FLIGHT_REGULAR_SCHEDULED_CREW("F"),
        DOMESTIC_CONTINUANCE_OF_CARGO_FLIGHT_CREW_CHANGE("FC"),
        
        // MASTER_CREW_LIST
        ADD("G"),
        DELETE("H"),
        CHANGE("I");
        
        private String code;
        private DocumentIdentifier(String code) { this.code = code; }  
        public String getCode() { return code; }

        private static final Map<String, DocumentIdentifier> BY_CODE_MAP = new LinkedHashMap<>();
        static {
            for (DocumentIdentifier rae : DocumentIdentifier.values()) {
                BY_CODE_MAP.put(rae.code, rae);
            }
        }

        public static DocumentIdentifier forCode(String code) {
            return BY_CODE_MAP.get(code);
        }        
    }

    /** mandatory: overall message type */
    private DocumentNameCode documentCode;
    
    /**
     * optional: a secondary code that gives more info about the message type
     */
    private DocumentIdentifier documentSubCode;
    
    public BGM(List<Composite> composites) throws ParseException {
        super(BGM.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.documentCode = DocumentNameCode.forCode(c.getElement(0));
                if (this.documentCode == null) {
                    throw new ParseException("BGM unknown document name code: " + c.getElement(0));
                }
                break;
 
            case 1:
                this.documentSubCode = DocumentIdentifier.forCode(c.getElement(0));
                break;
            }
        }
    }

    public DocumentNameCode getDocumentNameCode() {
        return documentCode;
    }

    public DocumentIdentifier getDocumentSubCode() {
        return documentSubCode;
    }

    public String getCode() {
        String id = (this.documentSubCode != null) ? this.documentSubCode.getCode() : "";
        return String.valueOf(this.documentCode.getCode()) + id;
    }
}

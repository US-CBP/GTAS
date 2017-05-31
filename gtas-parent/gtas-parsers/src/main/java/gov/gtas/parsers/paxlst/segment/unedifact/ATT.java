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

/**
 * <p>
 * ATT ATTRIBUTE
 * <p>
 * Function: To identify a specific attribute of a passenger, such as gender.
 */
public class ATT extends Segment {
    public enum AttCode {
        GENDER("2");

        private final String code;
        private AttCode(String code) { this.code = code; }
        public String getCode() { return code; }
        private static final Map<String, AttCode> BY_CODE_MAP = new LinkedHashMap<>();

        static {
            for (AttCode rae : AttCode.values()) {
                BY_CODE_MAP.put(rae.code, rae);
            }
        }

        public static AttCode forCode(String code) {
            return BY_CODE_MAP.get(code);
        }
    }

    private AttCode functionCode;
    private String attributeDescriptionCode;

    public ATT(List<Composite> composites) {
        super(ATT.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.functionCode = AttCode.forCode(c.getElement(0));
                break;
            case 2:
                this.attributeDescriptionCode = c.getElement(0);
                break;
            }
        }
    }

    public AttCode getFunctionCode() {
        return functionCode;
    }

    public String getAttributeDescriptionCode() {
        return attributeDescriptionCode;
    }
}

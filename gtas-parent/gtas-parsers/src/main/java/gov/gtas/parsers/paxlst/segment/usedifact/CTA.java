/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

public class CTA extends Segment {
    private String contactFunctionCode;
    private String c_departmentOrEmployee;
    private String telephoneNumber;
    private String faxNumber;
    public CTA(List<Composite> composites) {
        super(CTA.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.contactFunctionCode = c.getElement(0);
                break;
            case 1:
                this.c_departmentOrEmployee = c.getElement(1);
                break;
            case 2:
            case 3:
                String code = c.getElement(1);
                if (code != null) {
                    if (code.equals("TE")) {
                        this.telephoneNumber = c.getElement(0);
                    } else if (code.equals("FX")) {
                        this.faxNumber =c.getElement(0);
                    }
                }
                break;
            }
        }
    }

    public String getContactFunctionCode() {
        return contactFunctionCode;
    }
    public String getC_departmentOrEmployee() {
        return c_departmentOrEmployee;
    }
    public String getTelephoneNumber() {
        return telephoneNumber;
    }
    public String getFaxNumber() {
        return faxNumber;
    }
}

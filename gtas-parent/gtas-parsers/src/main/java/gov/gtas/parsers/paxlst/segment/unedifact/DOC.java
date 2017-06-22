/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * DOC DOCUMENT/MESSAGE DETAILS
 * <p>
 * Function: To identify the official travel document and/or other document used
 * for travel.
 * <ul>
 * <li>P: Indicates that the document type is a passport and its number.
 * <li>V: Indicates that the document type is a visa and its number.
 * <li>I: Indicates that the document type is state issued document of identity
 * and its number.
 * </ul>
 */
public class DOC extends Segment {
    private String docCode;
    private String documentIdentifier;

    public DOC(List<Composite> composites) {
        super(DOC.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                // DOC+L:110:111+AA299167
                // DOC+P+QG176295
                this.docCode = c.getElement(0);
                break;
            case 1:
                this.documentIdentifier = c.getElement(0);
                break;
            }
        }
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }
}

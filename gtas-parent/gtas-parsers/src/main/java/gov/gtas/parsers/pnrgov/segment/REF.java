/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * REF: REFERENCE INFORMATION
 *
 * <p>Example:The unique passenger reference identifier is 4928506894.
 * (REF+:4928506894')
 */
public class REF extends Segment {
    private List<String> referenceIds;

    public REF(List<Composite> composites) {
        super(REF.class.getSimpleName(), composites);
        this.referenceIds = new ArrayList<>();
        for (Composite c : getComposites()) {
            String refId = c.getElement(1);
            if (refId != null) {
                this.referenceIds.add(refId);
            }
        }
    }

    public List<String> getReferenceIds() {
        return referenceIds;
    }
}

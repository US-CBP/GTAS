/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * TXD: TAX DETAILS
 * <p>
 * Examples: Tax details for departure taxes for Great Britain.(TXD++5:GB::9')
 * Tax information related to the given fare.(TXD++6.27::USD')
 */
public class TXD extends Segment {
    public TXD(List<Composite> composites) {
        super(TXD.class.getSimpleName(), composites);
    }
}

/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.Date;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.exception.ParseException;

/**
 * <p>
 * DAT: DATE AND TIME INFORMATION
 * <p>
 * To convey information regarding estimated or actual dates and times of
 * operational events.
 * <p>
 * DAT at GR10 will hold PNR History transaction date/time
 * <p>
 * Unless specifically stated otherwise in bilateral agreement, the time is in
 * Universal Time Coordinated (UTC)
 */
public class DAT_G10 extends DAT {
    private static final String PNR_HISTORY = "T";
    private Date pnrHistorytDateTime;
    
    public DAT_G10(List<Composite> composites) throws ParseException {
        super(composites);
        
        for (DatDetails d : getDateTimes()) {
            if (PNR_HISTORY.equals(d.getType())) {
                this.pnrHistorytDateTime = d.getDateTime();
            }
        }
    }

    public Date getPnrHistorytDateTime() {
        return pnrHistorytDateTime;
    }
}

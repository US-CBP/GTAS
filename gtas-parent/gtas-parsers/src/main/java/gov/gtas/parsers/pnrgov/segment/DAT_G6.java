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
 * DAT at GR6 will be check-in transaction date/time as stored by RES systems
 * holding DC data C688/2005 will be used to specify that date/time is in free
 * text format in data element C688/9916.
 * <p>
 * Unless specifically stated otherwise in bilateral agreement, the time is in
 * Universal Time Coordinated (UTC)
 * <p>
 * Examples:
 * <ul>
 * <li>Check-in transaction date/time (DAT+2:010604:1800’)
 * <li>Check-in including date time is expressed as free text 
 * (DAT+3:L FT WW D014357 12AUG121423Z 1D5723')
 * </ul>
 * 
 * TODO: handle free text date time
 */
public class DAT_G6 extends DAT {
    private static final String CHECKIN_CODE = "2";
    private static final String CHECKIN_FREE_TEXT_CODE = "3";

    private Date checkinTime;

    public DAT_G6(List<Composite> composites) throws ParseException {
        super(composites);
        
        for (DatDetails d : getDateTimes()) {
            String code = d.getType();
            if (CHECKIN_CODE.equals(code) || CHECKIN_FREE_TEXT_CODE.equals(code)) {
                this.checkinTime = d.getDateTime();
            }
        }
    }

    public Date getCheckinTime() {
        return checkinTime;
    }
}

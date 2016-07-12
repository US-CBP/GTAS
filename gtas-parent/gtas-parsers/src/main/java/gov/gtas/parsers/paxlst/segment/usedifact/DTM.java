/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.Date;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.ParseUtils;

public class DTM extends Segment {
    public enum DtmCode {
        DEPARTURE_DATETIME,
        ARRIVAL_DATETIME
    }
    private DtmCode dtmCode;
    private String date;
    private String time;
    private Date c_dateTime;
    private String c_timezone;
    
    public DTM(List<Composite> composites) throws ParseException {
        super(DTM.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                switch (c.getElement(0)) {
                case "136":
                    this.dtmCode = DtmCode.DEPARTURE_DATETIME;
                    break;
                case "132":
                    this.dtmCode = DtmCode.ARRIVAL_DATETIME;
                    break;
                default:
                    logger.error("unknown dtm code: " + c.getElement(0));
                    return;
                }
                break;
                
            case 1:
                this.date = c.getElement(0);
                break;
            case 2:
                this.time = c.getElement(0);
                break;
            case 3:
                // TODO: handle timezone
                break;
            }
        }
        
        String tmp = this.date + this.time;
        this.c_dateTime = ParseUtils.parseDateTime(tmp, DateUtils.DT_FORMAT_YEAR_FIRST);
    }

    public DtmCode getDtmCode() {
        return dtmCode;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Date getC_dateTime() {
        return c_dateTime;
    }

    public String getC_timezone() {
        return c_timezone;
    }
}

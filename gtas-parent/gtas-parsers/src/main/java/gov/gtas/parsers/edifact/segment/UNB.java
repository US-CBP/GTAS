/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact.segment;

import java.util.Date;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * UNB: INTERCHANGE HEADER
 * <p>
 * Function: To start, identify and specify an interchange. Specifies the sender
 * and intended recipient of the message.
 * <p>
 * Segment format should be standard across message types.
 * <p>
 * Examples:
 * <ul>
 * <li>UNB+UNOA:4+AIRLINE1+NZCS+130628:0900+000000001’
 * <li>UNB+IATA:1+DL++101209:2100+020A07'
 * </ul>
 */
public class UNB extends Segment {
    private static final String DATE_TIME_FORMAT = DateUtils.DT_FORMAT_YEAR_FIRST; 

    private String sender;
    private String recipient;
    private Date dateAndTimeOfPreparation;

    public UNB(List<Composite> composites) throws ParseException {
        super(UNB.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 1:
                this.sender = c.getElement(0);
                break;
            case 2:
                this.recipient = c.getElement(0);
                break;
            case 3:
                String tmp = c.getElement(0);
                if (tmp != null) {
                    String time = c.getElement(1);
                    if (time != null) {
                        tmp += time;
                    }
                }
                this.dateAndTimeOfPreparation = ParseUtils.parseDateTime(tmp, DATE_TIME_FORMAT);
                break;
            }
        }
    }

    public String getSenderIdentification() {
        return sender;
    }

    public String getRecipientIdentification() {
        return recipient;
    }

    public Date getDateAndTimeOfPreparation() {
        return dateAndTimeOfPreparation;
    }
}

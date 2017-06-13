/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.parsers.vo.DocumentVo;

public final class ParseUtils {
    private static final Logger logger = LoggerFactory.getLogger(ParseUtils.class);

    private ParseUtils() { }
    
    /**
     * Some telecommunications transmission protocols require various
     * communication type headers and trailers to facilitate addressing,
     * routing, security, and other purposes.
     * 
     * These headers and trailers are typically delimited by special control
     * characters STX and ETX. This method removes the header and trailer from
     * the message. See https://en.wikipedia.org/wiki/Control_characters
     * 
     * @param text
     * @return message text without header or footer
     */
    public static String stripStxEtxHeaderAndFooter(String text) {
        String rv = text;
        final int STX_CODEPOINT = 2;
        final int ETX_CODEPOINT = 3;
        
        int stxIndex = rv.indexOf(STX_CODEPOINT);
        if (stxIndex != -1) {
            rv = rv.substring(stxIndex + 1);
        }
        int etxIndex = rv.indexOf(ETX_CODEPOINT);
        if (etxIndex != -1) {
            rv = rv.substring(0, etxIndex);
        }       
        
        return rv;
    }
    
    public static Date parseDateTime(String dt, String format) {
        try {
            DateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            return timeFormat.parse(dt);
        } catch (java.text.ParseException pe) {
            logger.warn(String.format("Could not parse date %s using format %s", dt, format));
        }
        
        return null;
    }

    public static Date parseExpirationDateForCC(String dt, String format) {
        try {
            DateFormat timeFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            Date parsedDate=timeFormat.parse(dt);
            Calendar c = Calendar.getInstance();
            c.setTime(parsedDate);
            int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            c.add(Calendar.DATE, daysInMonth-1);
            return c.getTime();
        } catch (java.text.ParseException pe) {
            logger.warn(String.format("Could not parse date %s using format %s", dt, format));
        }
        
        return null;
    }
    
    public static String prepTelephoneNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return null;
        }
        return number.replaceAll("[^0-9]", "");
    }
       
    public static Integer returnNumberOrNull(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static boolean isValidDocument(DocumentVo d) {
    	return (StringUtils.isNotEmpty(d.getDocumentNumber()) && StringUtils.isNotEmpty(d.getDocumentType()));
    }
}

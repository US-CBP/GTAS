/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json.util;

import gov.gtas.constant.RuleErrorConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.util.DateCalendarUtils;
import java.time.LocalDate;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Validation utilities for the UDR JSON.
 */
public class JsonValidationUtils {
    
    private final static String SUCCESS = "SUCCESS";
    private final static String START_DATE_BEFORE_NOW = "START_DATE_BEFORE_NOW";
    
    public static void validateMetaData(final MetaData metaData, final boolean checkStartDate){
        if (metaData == null) {
            throw new CommonServiceException(
                    RuleErrorConstants.NO_META_ERROR_CODE,
                    RuleErrorConstants.NO_META_ERROR_MESSAGE);
        }
        final String title = metaData.getTitle();
        if (StringUtils.isEmpty(title)) {
            throw new CommonServiceException(
                    RuleErrorConstants.NO_TITLE_ERROR_CODE,
                    RuleErrorConstants.NO_TITLE_ERROR_MESSAGE);
        }

        final Date startDate = metaData.getStartDate();
        final Date endDate = metaData.getEndDate();
        
        String message = validateDates(startDate, endDate, checkStartDate);
        
        // If the start date is somehow before the current date, just send message to change it to today's date.
        if (message.equals(START_DATE_BEFORE_NOW))
        {
             LocalDate ldate = LocalDate.now();
             Date date = java.sql.Date.valueOf(ldate);
             metaData.setStartDate(date);
        }
    }
    /**
     * Checks the start and end dates of the UDR meta data for validity.
     * @param startDate the start date.
     * @param endDate the end date.
     * @param checkStartDate if true checks that the start date is greater than or equal to today.
     */
    private static String validateDates(final Date startDate, final Date endDate, final boolean checkStartDate)
    {
        String returnString = SUCCESS;
        Date currentDate = new Date();
        Date now = new Date(currentDate.getTime());

        if (startDate == null) 
        {
            throw new CommonServiceException(
                    RuleErrorConstants.INVALID_START_DATE_ERROR_CODE,
                    RuleErrorConstants.INVALID_START_DATE_ERROR_MESSAGE);
        }
      
        // If the start date is somehow before the current date, just send message to change it to today's date.
        if(checkStartDate && DateCalendarUtils.dateRoundedGreater(now, startDate, Calendar.DATE))
        {
            returnString = START_DATE_BEFORE_NOW;
        }
        
        if(DateCalendarUtils.dateRoundedLess(endDate, startDate, Calendar.DATE))
        {
                throw new CommonServiceException(
                        RuleErrorConstants.END_LESS_START_DATE_ERROR_CODE,
                        RuleErrorConstants.END_LESS_START_DATE_ERROR_MESSAGE);
                            
        }
        
        return returnString;
    }

}

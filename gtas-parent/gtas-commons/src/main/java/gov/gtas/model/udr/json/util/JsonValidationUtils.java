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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * Validation utilities for the UDR JSON.
 */
public class JsonValidationUtils {
    
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
        
        validateDates(startDate, endDate, checkStartDate);
    }
    /**
     * Checks the start aand end dates of the UDR meta data for validity.
     * @param startDate the start date.
     * @param endDate the end date.
     * @param checkStartDate if true checks that the start date is greater than or equal to today.
     */
    private static void validateDates(final Date startDate, final Date endDate, final boolean checkStartDate){
        Date now = new Date();
        if (startDate == null) {
            throw new CommonServiceException(
                    RuleErrorConstants.INVALID_START_DATE_ERROR_CODE,
                    RuleErrorConstants.INVALID_START_DATE_ERROR_MESSAGE);
        }
        if(checkStartDate && DateCalendarUtils.dateRoundedGreater(now, startDate, Calendar.DATE)){
            throw new CommonServiceException(
                    RuleErrorConstants.PAST_START_DATE_ERROR_CODE,
                    RuleErrorConstants.PAST_START_DATE_ERROR_MESSAGE);
            
        }
        if(DateCalendarUtils.dateRoundedLess(endDate, startDate, Calendar.DATE)){
                throw new CommonServiceException(
                        RuleErrorConstants.END_LESS_START_DATE_ERROR_CODE,
                        RuleErrorConstants.END_LESS_START_DATE_ERROR_MESSAGE);
                            
        }
    }

}

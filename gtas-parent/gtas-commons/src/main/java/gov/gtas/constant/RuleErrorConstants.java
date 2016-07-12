/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

/**
 * Constants used in the Rule Service module.
 */
public class RuleErrorConstants {
    // //////////////////////////////////////////////////////////////////////////////////////
    // ERROR CODES
    // //////////////////////////////////////////////////////////////////////////////////////
    /*
     * The Input JSON Query Specification from UI is missing the meta data summary.
     */
    public static final String NO_META_ERROR_CODE = "NO_META";
    /*
     * The Input JSON Query Specification from UI is missing the rule title in the meta data summary.
     */
    public static final String NO_TITLE_ERROR_CODE = "NO_TITLE";

    /*
     * The Input JSON Query Specification from UI is missing the start date in the meta data summary
     * or the start date is invalid.
     */
    public static final String INVALID_START_DATE_ERROR_CODE = "INVALID_START_DATE";
    public static final String PAST_START_DATE_ERROR_CODE = "PAST_START_DATE";
    public static final String END_LESS_START_DATE_ERROR_CODE = "END_LESS_THAN_START_DATE";

    /*
     * The Input JSON Query Specification from UI has an invalid rule detail structure.
     * (i.e., it is not a valid boolean tree structure.)
     */
    public static final String INVALID_RULE_STRUCTURE_ERROR_CODE = "INVALID_RULE_STRUCTURE";
    
    // //////////////////////////////////////////////////////////////////////////////////////
    // ERROR Messages
    // //////////////////////////////////////////////////////////////////////////////////////
    /*
     * The Input JSON Query Specification from UI is missing the meta data summary.
     */
    public static final String NO_META_ERROR_MESSAGE = "The JSON UDR specification is missing the summary meta data object.";
    /*
     * The Input JSON Query Specification from UI is missing the rule title in the meta data summary.
     */
    public static final String NO_TITLE_ERROR_MESSAGE = "The JSON UDR specification is missing a title field in the summary.";

    /*
     * The Input JSON Query Specification from UI is missing the start date in the meta data summary
     * or the start date is invalid.
     */
    public static final String INVALID_START_DATE_ERROR_MESSAGE = "The JSON UDR specification is missing the start date in the summary.";
    public static final String PAST_START_DATE_ERROR_MESSAGE = "The JSON UDR specification has a start date in the past.";
    public static final String END_LESS_START_DATE_ERROR_MESSAGE = "The JSON UDR specification has an end date before the start date.";

    /*
     * The Input JSON Query Specification from UI has an invalid rule detail structure.
     * (i.e., it is not a valid boolean tree structure.)
     */
    public static final String INVALID_RULE_STRUCTURE_ERROR_MESSAGE = "The JSON UDR specification has an invalid rule details structure.";
    
}

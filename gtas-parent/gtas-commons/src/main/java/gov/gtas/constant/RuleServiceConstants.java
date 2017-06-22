/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

/**
 * Constants used in the Rule Service module.
 */
public class RuleServiceConstants {
    public static final String DEFAULT_RULESET_NAME = "gov/gtas/rule/gts.drl";
    /*
     * All generated rules depend on this global object for returning results.
     * When a knowledge session is created the global object should be created
     * and associated with the session:
     * ksession.setGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME, new
     * ArrayList<Object>()); The global can then be accessed after the rules are
     * run by: (List<?>)
     * ksession.getGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME);
     */
    public static final String RULE_RESULT_LIST_NAME = "resultList";
    // //////////////////////////////////////////////////////////////////////////////////////
    // KNOWLEDGE Management
    // //////////////////////////////////////////////////////////////////////////////////////
    /* The Knowledge session name configured in META-INF/module.xml */
    public static final String KNOWLEDGE_SESSION_NAME = "GtasKS";

    /* The root path for the KieFileSystem files. */
    public static final String KIE_FILE_SYSTEM_ROOT = "src/main/resources/";
    // //////////////////////////////////////////////////////////////////////////////////////
    // ERROR CODES
    // //////////////////////////////////////////////////////////////////////////////////////
    /*
     * This error code indicates that the Rule Engine Runner failed due to an
     * unexpected exception.
     */
    public static final String RULE_ENGINE_RUNNER_ERROR_CODE = "RE_ERROR";
    /*
     * This is the error code for an internal system error indicating IO error
     * during the creation of the Knowledge Base.
     */
    public static final String KB_CREATION_IO_ERROR_CODE = "KB_CREATION_IO_ERROR";
    /*
     * This is the error code for an internal system error indicating that the
     * UDR generated rule could not be compiled.
     */
    public static final String RULE_COMPILE_ERROR_CODE = "RULE_COMPILE_ERROR";

    /*
     * This is the error code for an internal system error indicating that the
     * UDR generated knowledge base could not be retrieved from the data base.
     */
    public static final String KB_NOT_FOUND_ERROR_CODE = "KB_NOT_FOUND";
    /*
     * This is the error code for an internal system error indicating that an
     * API or PNR message intended for processing could not be retrieved from
     * the data base.
     */
    public static final String MESSAGE_NOT_FOUND_ERROR_CODE = "MESSAGE_NOT_FOUND";
    /*
     * This is the error code for an internal system error indicating that the
     * UDR generated knowledge base retrieved from the data base has invalid
     * content.
     */
    public static final String KB_INVALID_ERROR_CODE = "KB_INVALID_ERROR";
    /*
     * This is the error code for an internal system error indicating that the
     * UDR generated knowledge base could not de-serialized from the DB record.
     */
    public static final String KB_DESERIALIZATION_ERROR_CODE = "KB_DESERIALIZATION_ERROR";

    public static final String INCOMPLETE_TREE_ERROR_CODE = "INCOMPLETE_TREE_ERROR";

    public static final String NO_ENABLED_RULE_ERROR_CODE = "NO_ENABLED_RULE_ERROR";

    // //////////////////////////////////////////////////////////////////////////////////////
    // ERROR Messages
    // //////////////////////////////////////////////////////////////////////////////////////
    /*
     * This is the error message for an internal system error indicating IO
     * error during the creation of the Knowledge Base.
     */
    public static final String KB_CREATION_IO_ERROR_MESSAGE = "IO error while creating KIE Knowledge Base (details:%s).";
    /*
     * This is the error message for an internal system error indicating that
     * the UDR generated rule could not be compiled.
     */
    public static final String RULE_COMPILE_ERROR_MESSAGE = "The rule file '%s' could not be compiled.";
    /*
     * This is the error message for an internal system error indicating that
     * the UDR generated knowledge base could not be retrieved from the data
     * base.
     */
    public static final String KB_NOT_FOUND_ERROR_MESSAGE = "The Knowledge Base named '%s' could not be retrieved from the data base.";

    /*
     * This is the error messsage for an internal system error indicating that
     * an API or PNR message intended for processing could not be retrieved from
     * the data base.
     */
    public static final String MESSAGE_NOT_FOUND_ERROR_MESSAGE = "The API/PNR message with ID '%d' could not be retrieved from the data base.";

    /*
     * This is the error message for an internal system error indicating that
     * the UDR generated knowledge base retrieved from the data base has invalid
     * content.
     */
    public static final String KB_INVALID_ERROR_MESSAGE = "The Knowledge Base with name '%s' has invalid rule or KieBase content.";
    /*
     * This is the error code for an internal system error indicating that the
     * UDR generated knowledge base could not de-serialized from the DB record.
     */
    public static final String KB_DESERIALIZATION_ERROR_MESSAGE = "The Kie Knowledge Base could not be de-serialized from the DB record with ID = $d";

    public static final String INCOMPLETE_TREE_ERROR_MESSAGE = "The query tree is incomplete at level %d.";

    /*
     * This error message indicates that the indicated knowledge base contains
     * no enabled rule.
     */
    public static final String NO_ENABLED_RULE_ERROR_MESSAGE = "The Knowledge Base contains no enabled rule.";

    /*
     * This error message indicates that the Rule Engine Runner failed due to an
     * unexpected exception.
     */
    public static final String RULE_ENGINE_RUNNER_ERROR_MESSAGE = "The Rule Engine Runner failed to process APIS/PNR messages due to an unexpected exception.";

}

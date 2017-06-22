/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

/**
 * UDR and JSON related constants.
 */
public class RuleConstants {
    public static final String UDR_KNOWLEDGE_BASE_NAME = "UDR Knowledge Base";
    
    public static final String UDR_EXTERNAL_CHARACTER_ENCODING = "UTF-8";//StandardCharsets.UTF_8.name()
    
    public static final String UDR_DATE_FORMAT = "yyyy-MM-dd";  
    public static final String UDR_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";  
    public static final String RULE_ENGINE_DATE_FORMAT = "dd-MMM-yyyy";  
    public static final String RULE_ENGINE_DATETIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";  
    
    public static final String UDR_ID_ATTRIBUTE_NAME = "id";
    public static final String WL_ID_ATTRIBUTE_NAME = "id";
    public static final String WL_ITEM_IDS_ATTRIBUTE_NAME = "itemIds";
    public static final String WL_TITLE_ATTRIBUTE_NAME = "title";
    public static final String UDR_TITLE_ATTRIBUTE_NAME = "title";
    
    public static final String UDR_CREATE_OP_NAME = "Create UDR";
    public static final String UDR_UPDATE_OP_NAME = "Update UDR";
    public static final String UDR_DELETE_OP_NAME = "Delete UDR";
    
    public static final int UDR_TITLE_LENGTH = 20;
    public static final int UDR_MAX_NUMBER_COPIES = 100;
    
}

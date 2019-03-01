package gov.gtas.model;

public enum MessageStatusEnum
{
    RECEIVED("RECEIVED"),  // 0
    PARSED("PARSED"),  // 1
    LOADED("LOADED"),  // 2
    ANALYZED("ANALYZED"),  // 3
    FAILED_PARSING("FAILED_PARSE"),  // 4
    FAILED_LOADING("FAILED_LOAD"),  // 5
    FAILED_ANALYZING("FAILED_ANALYZE");  // 6
    String name;
    MessageStatusEnum(String name) {
        this.name = name;
    }
}
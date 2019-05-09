package gov.gtas.model;

public enum MessageStatusEnum
{
    RECEIVED("RECEIVED"),  // 0
    PARSED("PARSED"),  // 1
    LOADED("LOADED"),  // 2
    RUNNING_RULES("RUNNING_RULES"),  // 3
    ANALYZED("ANALYZED"),  // 4
    FAILED_PARSING("FAILED_PARSE"),  // 5
    FAILED_LOADING("FAILED_LOAD"),  // 6
    FAILED_ANALYZING("FAILED_ANALYZE"),  // 7
    PARTIAL_ANALYZE("PARTIAL_ANALYZE"),  // 8
    FAILED_PRE_PARSE("FAILED_PRE_PARSE"),  // 8
    FAILED_PRE_PROCESS("FAILED_PRE_PROCESS");  // 8

    public String getName() {
        return name;
    }

    String name;
    MessageStatusEnum(String name) {
        this.name = name;
    }
}
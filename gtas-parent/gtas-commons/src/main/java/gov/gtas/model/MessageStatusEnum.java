package gov.gtas.model;

public enum MessageStatusEnum {
	RECEIVED("RECEIVED"), // 0

	PARSED("PARSED"), // 1

	LOADED("LOADED"), // 2

	RUNNING_RULES("RUNNING_RULES"), // 3

	ANALYZED("ANALYZED"), // 4

	NEO_LOADED("NEO_LOADED"), // 5

	NEO_ANALYZED("NEO_ANALYZED"), // 6

	FAILED_PARSING("FAILED_PARSE"), // 7

	FAILED_LOADING("FAILED_LOAD"), // 8

	FAILED_ANALYZING("FAILED_ANALYZE"), // 9

	FAILED_NEO_4_J("FAILED_NEO_4_J"), // 10

	PARTIAL_ANALYZE("PARTIAL_ANALYZE"), // 11

	FAILED_PRE_PARSE("FAILED_PRE_PARSE"), // 12

	FAILED_PRE_PROCESS("FAILED_PRE_PROCESS"); // 13

	public String getName() {
		return name;
	}

	String name;

	MessageStatusEnum(String name) {
		this.name = name;
	}
}
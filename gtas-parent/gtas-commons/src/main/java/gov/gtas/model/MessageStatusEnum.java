package gov.gtas.model;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum MessageStatusEnum {
	RECEIVED("RECEIVED"), // 0

	PARSED("PARSED"), // 1

	LOADED("LOADED"), // 2

	RUNNING_RULES("RUNNING_RULES"), // 3

	ANALYZED("ANALYZED"), // 4

	NEO_LOADED("NEO_LOADED"), // 5

	NEO_ANALYZED("NEO_ANALYZED"), // 6

	FAILED_PARSING("FAILED_PARSING"), // 7

	FAILED_LOADING("FAILED_LOADING"), // 8

	FAILED_ANALYZING("FAILED_ANALYZING"), // 9

	FAILED_NEO_4_J("FAILED_NEO_4_J"), // 10

	PARTIAL_ANALYZE("PARTIAL_ANALYZE"), // 11

	FAILED_PRE_PARSE("FAILED_PRE_PARSE"), // 12

	FAILED_PRE_PROCESS("FAILED_PRE_PROCESS"), // 13

	PNR_DATA_MASKED("PNR_PII_MASKED"),

	APIS_DATA_MASKED("APIS_PII_MASKED"),

	PNR_DATA_DELETED("PNR_DATA_DELETED"),

	PNR_DELETE_ERROR("PNR_DELETE_ERROR"),

	APIS_DELETE_ERROR("APIS_DELETE_ERROR"),

	APIS_MASK_ERROR("APIS_MASK_ERROR"),

	PNR_MASK_ERROR("PNR_MASK_ERROR"),

	APIS_DATA_DELETED("APIS_PII_DELETED"),

	DUPLICATE_MESSAGE("DUPLICATE_MESSAGE");


	public String getName() {
		return name;
	}

	String name;

	MessageStatusEnum(String name) {
		this.name = name;
	}


	private static final Map<String, MessageStatusEnum> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<MessageStatusEnum> fromString(String messageStatus) {
		return Optional.ofNullable(stringToEnum.get(messageStatus));
	}

	@Override
	public String toString() {
		return name;
	}
}
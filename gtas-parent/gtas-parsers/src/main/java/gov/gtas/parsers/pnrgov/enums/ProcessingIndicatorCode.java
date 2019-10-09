package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum ProcessingIndicatorCode {
	HEAD_OF_POOL("HP"),

	MEMBER_OF_POOL("MP");

	private String code;

	ProcessingIndicatorCode(String code) {
		this.code = code;
	}

	private static final Map<String, ProcessingIndicatorCode> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<ProcessingIndicatorCode> fromString(String indicatorCode) {
		return Optional.ofNullable(stringToEnum.get(indicatorCode));
	}

	@Override
	public String toString() {
		return this.code;
	}

}

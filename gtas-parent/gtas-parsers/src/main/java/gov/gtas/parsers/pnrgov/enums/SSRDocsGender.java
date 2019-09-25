package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum SSRDocsGender {

	FEMALE_INFANT_PASSENGER("FI"),

	FEMALE_PASSENGER("F"),

	MALE_INFANT_PASSENGER("MI"),

	MALE_PASSENGER("M"),

	UNDISCLOSED("U"),

	// Not provided is not in spec doc. This is to help distinguish between
	// Undisclosed and a not given gender.
	NOT_PROVIDED("VV");

	private String gender;

	SSRDocsGender(String gender) {
		this.gender = gender;
	}

	private static final Map<String, SSRDocsGender> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<SSRDocsGender> fromString(String gender) {
		return Optional.ofNullable(stringToEnum.get(gender));
	}

	@Override
	public String toString() {
		return this.gender;
	}
}

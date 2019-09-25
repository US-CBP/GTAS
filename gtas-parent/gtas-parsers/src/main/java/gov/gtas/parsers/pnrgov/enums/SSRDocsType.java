package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum SSRDocsType {

	/*
	 * Order matches the spec doc. DO NOT CHANGE ORDER AS WE PICK WHICH DOCUMENT TO
	 * CREATE A PASSENGER WITH BY REVERSED ORDER OF THIS ENUM.
	 *
	 */
	// No doc defaults to not provided. VV is not in the spec and is only used by
	// our system.
	NOT_PROVIDED("VV"),

	NON_STANDARD("F"),

	IDENTITY_CARD_A("A"),

	IDENTITY_CARD_C("C"),

	IDENTITY_CARD_I("I"),

	PASSPORT_CARD("IP"),

	PASSPORT("P");

	private static final Map<String, SSRDocsType> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	private String docType;

	SSRDocsType(String docType) {
		this.docType = docType;
	}

	public static Optional<SSRDocsType> fromString(String docType) {
		return Optional.ofNullable(stringToEnum.get(docType));
	}

	@Override
	public String toString() {
		return this.docType;
	}
}

/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.enumtype;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum CommentType {

	GENERAL_PASSENGER("GENERAL_PASSENGER"),

	VETTING_COMMENT("VETTING_COMMENT");

	private String type;

	CommentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	private static final Map<String, CommentType> stringToEnum = Stream.of(values())
			.collect(toMap(o -> o.toString().toUpperCase(), e -> e));

	public static Optional<CommentType> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase()));
	}

	@Override
	public String toString() {
		return type;
	}

}

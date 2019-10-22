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

public enum HitSeverityEnum {

	// Using ordinal to sort.

	TOP("Top"),

	HIGH("High"),

	NORMAL("Normal");

	private final String hitSeverity;

	HitSeverityEnum(String hitSeverity) {
		this.hitSeverity = hitSeverity;
	}

	private static final Map<String, HitSeverityEnum> stringToEnum = Stream.of(values())
			.collect(toMap(o -> o.toString().toUpperCase(), e -> e));

	public static Optional<HitSeverityEnum> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase().trim()));
	}

	@Override
	public String toString() {
		return hitSeverity;
	}
}

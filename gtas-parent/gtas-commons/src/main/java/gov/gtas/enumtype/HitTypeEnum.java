/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum HitTypeEnum {

	USER_DEFINED_RULE("R"), // Rule Hit

	WATCHLIST("WL"), // General watchlist hit

	WATCHLIST_PASSENGER("P"), // Watchlist Passenger Hit

	WATCHLIST_DOCUMENT("D"), // Watchlist Document Hit

	PARTIAL_WATCHLIST("PWL"), // Watchlist Document Hit

	GRAPH_HIT("GH"), // Graph Database rule

	MANUAL_HIT("M"); // Manual Hit

	private final String hitType;

	HitTypeEnum(String hitType) {
		this.hitType = hitType;
	}

	private static final Map<String, HitTypeEnum> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<HitTypeEnum> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName));
	}

	@Override
	public String toString() {
		return hitType;
	}
}

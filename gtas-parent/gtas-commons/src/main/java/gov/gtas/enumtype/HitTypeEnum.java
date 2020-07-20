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

	USER_DEFINED_RULE("R", "R"), // Rule Hit

	WATCHLIST("WL", "WL"), // General watchlist hit

	WATCHLIST_PASSENGER("P", "WL"), // Watchlist Passenger Hit

	WATCHLIST_DOCUMENT("D", "WL"), // Watchlist Document Hit

	PARTIAL_WATCHLIST("PWL", "PWL"), // Watchlist Document Hit

	GRAPH_HIT("GH", "GH"), // Graph Database rule

	MANUAL_HIT("M", "M"), // Manual Hit

	EXTERNAL_HIT("EH", "EXTERNAL_HIT"), // External Hit

	NOT_USED("VV", "VV"); // Not used - for sorting in database.

	private final String hitType;
	private final String displayName;

	HitTypeEnum(String hitType, String displayName) {
		this.hitType = hitType;
		this.displayName = displayName;
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

	public String getDisplayName() {
		return displayName;
	}
}

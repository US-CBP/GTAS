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

public enum HitViewStatusEnum {

	PRIORITY("PRIORITY"),

	IN_PROGRESS("IN_PROGRESS"),

	NEW("NEW"),

	RE_OPENED("RE_OPENED"),

	DISMISSED("DISMISSED"),

	NOT_USED("NOT_USED");


	private String hitViewStatus;

	HitViewStatusEnum(String hitViewStatus) {
		this.hitViewStatus = hitViewStatus;

	}

	private static final Map<String, HitViewStatusEnum> stringToEnum = Stream.of(values())
			.collect(toMap(o -> o.toString().toUpperCase(), e -> e));

	public static Optional<HitViewStatusEnum> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase()));
	}

	@Override
	public String toString() {
		return hitViewStatus;
	}
}
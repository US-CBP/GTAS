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

public enum NotificationSend {

	IMMEDIATE("IMMEDIATE"),

	TWO_HOURS_BEFORE("TWO_HOURS_BEFORE"),

	ONE_HOUR_BEFORE("ONE_HOUR_BEFORE");

	private String notificationSend;

	NotificationSend(String notificationSend) {
		this.notificationSend = notificationSend;
	}

	private static final Map<String, NotificationSend> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<NotificationSend> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName));
	}

	@Override
	public String toString() {
		return notificationSend;
	}
}

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

public enum NotificationStatus {

	SENT("SENT"),

	FAILED("FAILED"),

	PENDING("PENDING");

	private String notificationStatus;

	NotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	private static final Map<String, NotificationStatus> stringToEnum = Stream.of(values())
			.collect(toMap(Object::toString, e -> e));

	public static Optional<NotificationStatus> fromString(String entityName) {
		return Optional.ofNullable(stringToEnum.get(entityName));
	}

	@Override
	public String toString() {
		return notificationStatus;
	}
}

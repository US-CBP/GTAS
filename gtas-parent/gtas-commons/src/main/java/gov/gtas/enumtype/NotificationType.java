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

public enum NotificationType {

    EMAIL("EMAIL"),

    SMS("SMS"),

    WEB_APP("WEB_APP");

    private String notificationType;

    NotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    private static final Map<String, NotificationType> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<NotificationType> fromString(String entityName) {
        return Optional.ofNullable(stringToEnum.get(entityName));
    }

    @Override
    public String toString() {
        return notificationType;
    }
}

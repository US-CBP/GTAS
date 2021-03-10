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

public enum POEStatusEnum {

    ACTIVE("Active"),

    ENROUTE("Enroute"),

    ENCOUNTERED("Encountered"),

    MISSED("Missed"),

    NEGATIVE("Negative"),

    POSITIVE("Positive"),

    REFERRED("Referrred"),

    DIDNOTBOARD("Did Not Board");

    private String POEStatus;

    POEStatusEnum(String POEStatus){
        this.POEStatus = POEStatus;
    }

    private static final Map<String, POEStatusEnum> stringToEnum = Stream.of(values())
            .collect(toMap(o -> o.toString().toUpperCase(), e -> e));

    public static Optional<POEStatusEnum> fromString(String entityName) {
        return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase()));
    }

    @Override
    public String toString() { return this.POEStatus; }


}

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

public enum LookoutStatusEnum {

    ACTIVE("Active"),

    ENROUTE("Enroute"),

    ENCOUNTERED("Encountered"),

    MISSED("Missed"),

    NEGATIVE("Negative"),

    POSITIVE("Positive"),

    REFERRED("Referrred"),

    DIDNOTBOARD("Did Not Board"),

    INACTIVE("Inactive"),

    UNCATEGORIZED("Uncategorized");

    private String POEStatus;

    LookoutStatusEnum(String POEStatus){
        this.POEStatus = POEStatus;
    }

    private static final Map<String, LookoutStatusEnum> stringToEnum = Stream.of(values())
            .collect(toMap(o -> o.toString().toUpperCase(), e -> e));

    public static Optional<LookoutStatusEnum> fromString(String entityName) {
        return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase()));
    }

    @Override
    public String toString() { return this.POEStatus; }


}

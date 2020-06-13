package gov.gtas.summary;


import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum MessageAction {


    RAW_PNR("RAW_PNR"),
    RAW_APIS("RAW_APIS"),
    RAW("RAW"),
    PROCESSED_RAW("PROCESSED_RAW"),
    PROCESSED_PNR("PROCESSED_PNR"),
    PROCESSED_MESSAGE("PROCESSED_MESSAGE"),
    PROCESSED_APIS("PROCESSED_APIS"),
    HIT("HIT"),
    ERROR("ERROR"),
    PASSENGER("PASSENGER");


    private final String type;

    MessageAction(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    private static final Map<String, MessageAction> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<MessageAction> fromString(String entityName) {
        return Optional.ofNullable(stringToEnum.get(entityName));
    }
}

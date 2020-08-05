package gov.gtas.enumtype;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum MessageType {

    APIS("APIS"),

    PNR("PNR"),

    NO_TYPE("NO_TYPE");

    private String messageType;

    MessageType(String messageType) {
        this.messageType = messageType;

    }

    private static final Map<String, MessageType> stringToEnum = Stream.of(values())
            .collect(toMap(o -> o.toString().toUpperCase(), e -> e));

    public static Optional<MessageType> fromString(String entityName) {
        if (entityName == null) {
           return Optional.of(NO_TYPE);
        }
        return Optional.ofNullable(stringToEnum.get(entityName.toUpperCase()));
    }

    @Override
    public String toString() {
        return messageType;
    }
}

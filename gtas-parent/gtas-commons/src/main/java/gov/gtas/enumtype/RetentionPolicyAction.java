package gov.gtas.enumtype;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum  RetentionPolicyAction {

    NO_ACTION_RELEVANT_APIS("NO_ACTION_RELEVANT_APIS"),

    NO_ACTION_RELEVANT_PNR("NO_ACTION_RELEVANT_PNR"),

    NO_ACTION_RELEVANT_MESSAGE("NO_ACTION_RELEVANT_MESSAGE"),

    NO_ACTION_MARKED_FOR_RETENTION("NO_ACTION_MARKED_FOR_RETENTION"),

    APIS_DATA_MARKED_TO_DELETE("APIS_DATA_MARKED_TO_DELETE"),

    PNR_DATA_MARKED_TO_DELETE("PNR_DATA_MARKED_TO_DELETE"),

    NO_ACTION_NO_APIS("NO_ACTION_NO_APIS"),

    NO_ACTION_NO_PNR("NO_ACTION_NO_PNR"),

    PERFORMED_UNMASKING("PERFORMED_UNMASKING"),

    NO_ACTION("NO_ACTION"),

    DELETED("DELETE");

    private final String actionType;

    RetentionPolicyAction(String actionType ) {
        this.actionType = actionType;
    }

    private static final Map<String, RetentionPolicyAction> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<RetentionPolicyAction> fromString(String entityName) {
        return Optional.ofNullable(stringToEnum.get(entityName));
    }

    @Override
    public String toString() {
        return actionType;
    }

    public String getActionType() {
        return actionType;
    }

}

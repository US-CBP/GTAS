package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum PhoneCodes {
    BUSINESS_PHONE("CTCB"),
    HOME_PHONE("CTCH"),
    MOBILE_PHONE("CTCM"),
    TRAVEL_AGENT_PHONE("CTCT"),
    PHONE_NATURE_NOT_KNOWN("CTCP"),
    DATA_FAX_PHONE("CTCF"),
    ;

    private String code;

    PhoneCodes(String code) {
        this.code = code;
    }

    private static final Map<String, PhoneCodes> stringToEnum =
            Stream.of(values()).collect(
                    toMap(Object::toString, e -> e));

    public static Optional<PhoneCodes> fromString(String phone) {
        return Optional.ofNullable(stringToEnum.get(phone));
    }

    public static boolean textContainsPhoneNumberString(String txt) {
        return txt.contains(HOME_PHONE.toString()) ||
                txt.contains(BUSINESS_PHONE.toString())  ||
                txt.contains(MOBILE_PHONE.toString())  ||
                txt.contains(TRAVEL_AGENT_PHONE.toString())  ||
                txt.contains(PHONE_NATURE_NOT_KNOWN.toString())  ||
                txt.contains(DATA_FAX_PHONE.toString());
    }

    @Override
    public String toString() {
        return this.code;
    }

}

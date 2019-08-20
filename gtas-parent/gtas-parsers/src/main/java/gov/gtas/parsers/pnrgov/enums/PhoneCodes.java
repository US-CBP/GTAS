package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum PhoneCodes {
    BUSINESS_PHONE_A("CTCB"),
    BUSINESS_PHONE_B("CTC B"),
    HOME_PHONE_A("CTCH"),
    HOME_PHONE_B("CTC H"),
    MOBILE_PHONE_A("CTCM"),
    MOBILE_PHONE_B("CTC M"),
    TRAVEL_AGENT_PHONE_A("CTCT"),
    TRAVEL_AGENT_PHONE_B("CTC T"),
    PHONE_NATURE_NOT_KNOWN_A("CTCP"),
    PHONE_NATURE_NOT_KNOWN_B("CTC P"),
    DATA_FAX_PHONE_A("CTCF"),
    DATA_FAX_PHONE_B("CTC F")
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
        return txt.contains(HOME_PHONE_A.toString()) ||
               txt.contains(HOME_PHONE_B.toString()) ||
                txt.contains(BUSINESS_PHONE_A.toString())  ||
                txt.contains(BUSINESS_PHONE_B.toString())  ||
                txt.contains(MOBILE_PHONE_A.toString())  ||
                txt.contains(MOBILE_PHONE_B.toString())  ||
                txt.contains(TRAVEL_AGENT_PHONE_A.toString())  ||
                txt.contains(TRAVEL_AGENT_PHONE_B.toString())  ||
                txt.contains(PHONE_NATURE_NOT_KNOWN_A.toString())  ||
                txt.contains(PHONE_NATURE_NOT_KNOWN_B.toString())  ||
                txt.contains(DATA_FAX_PHONE_A.toString())  ||
                txt.contains(DATA_FAX_PHONE_B.toString());
    }

    @Override
    public String toString() {
        return this.code;
    }

}

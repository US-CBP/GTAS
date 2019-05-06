package gov.gtas.parsers.pnrgov.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum  MeasurementQualifier {
    KILOS("700", "Kgs"),
    LBS("701", "Lbs"),
    UNKNOWN("702", "Unk");

    private String code;
    private String qualifier;
    MeasurementQualifier(String code, String qualifier) {
        this.code = code;
        this.qualifier = qualifier;
    }

    private static final Map<String, MeasurementQualifier> stringToEnum =
            Stream.of(values()).collect(
                    toMap(Object::toString, e -> e));

    public static Optional<MeasurementQualifier> fromString(String indicatorCode) {
        return Optional.ofNullable(stringToEnum.get(indicatorCode));
    }

    public String getEnglishName() {
        return this.qualifier;
    }

    @Override
    public String toString() {
        return this.code;
    }

}

package gov.gtas.parsers.pnrgov.enums;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public enum SSRDocoType {

    /*
    * According to the specs there is only one SSRDoco type, and that is Visa.
    *
    * */
    //No doc defaults to not provided. VV is not in the spec and is only used by our system.
    VISA("V"), NOT_PROVIDED("VV");

    private static final Map<String, SSRDocoType> stringToEnum =
            Stream.of(values()).collect(
                    toMap(Object::toString, e -> e));


    private String docType;
    SSRDocoType(String docType){
        this.docType = docType;
    }

    public static Optional<SSRDocoType> fromString(String docType) {
        return Optional.ofNullable(stringToEnum.get(docType));
    }

    @Override
    public String toString() {
        return this.docType;
    }

}

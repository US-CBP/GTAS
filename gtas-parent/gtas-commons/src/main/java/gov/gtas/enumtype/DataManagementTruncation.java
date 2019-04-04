package gov.gtas.enumtype;

public enum DataManagementTruncation {
    ALL ("ALL"),
    APIS_ONLY ("APIS_ONLY"),
    PNR_ONLY ("PNR_ONLY");

    private String type;

    private DataManagementTruncation(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static DataManagementTruncation getEnum(String value) {

        for (DataManagementTruncation typeEnum : DataManagementTruncation.values()) {
            if(typeEnum.name().equalsIgnoreCase(value)) {
                return typeEnum;
            }
        }

        throw new IllegalArgumentException();
    }

}

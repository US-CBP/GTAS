package gov.gtas.enumtype;

public enum EncounteredStatusEnum {
	NOT_ENCOUNTERED("Not Encountered Yet"),

	IN_PROGRESS("In Progress - add user name to a list"),

	DENIED_BOARDERING("Denied Boarding"),

	REFUSED_ENTRY("Refused Entry"),

	SECONDARY_POSITIVE("Secondary Positive"),

	SECONDARY_NEGATIVE("Secondary Negative"),

	MISSED("Missed");

	String type;

	private EncounteredStatusEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static EncounteredStatusEnum getEnum(String value) {
		for (EncounteredStatusEnum encouteredStatusEnum : EncounteredStatusEnum.values()) {
			if (encouteredStatusEnum.getType().equalsIgnoreCase(value)) {
				return encouteredStatusEnum;
			}
		}

		// If reached here, value is not one of the available Encountered Status options
		throw new IllegalArgumentException();
	}

}

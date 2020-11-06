package gov.gtas.enumtype;

public enum SignupRequestStatus {

	NEW("NEW"), REVIEWED("REVIEWED"), APPROVED("APPROVED"), REJECTED("REJECTED");

	private String name;

	public String getName() {
		return name;
	}

	SignupRequestStatus(String name) {
		this.name = name;
	}
}

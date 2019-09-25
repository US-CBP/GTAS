package gov.gtas.model;

public enum CommentType {

	GENERAL("GENERAL"),

	TARGET("TARGET");

	private String type;

	CommentType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}

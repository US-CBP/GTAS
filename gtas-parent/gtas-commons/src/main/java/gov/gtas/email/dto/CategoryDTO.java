package gov.gtas.email.dto;

public class CategoryDTO {

    private String severity;
    private String category;
    private String rule;

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDescription() {
        return severity + " | " + category + " | " + rule;
    }

}
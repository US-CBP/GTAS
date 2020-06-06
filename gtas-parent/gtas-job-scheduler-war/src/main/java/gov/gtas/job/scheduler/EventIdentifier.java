package gov.gtas.job.scheduler;

import java.util.List;

public class  EventIdentifier {

    private String identifier;
    private String[] identifierArray;
    private List<String> identifierArrayList;
    private String eventType;
    private String countryOrigin;
    private String countryDestination;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String[] getIdentifierArray() {
        return identifierArray;
    }

    public void setIdentifierArray(String[] identifierArray) {
        this.identifierArray = identifierArray;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<String> getIdentifierArrayList() {
        return identifierArrayList;
    }

    public void setIdentifierArrayList(List<String> identifierArrayList) {
        this.identifierArrayList = identifierArrayList;
    }

    public String getCountryOrigin() {
        return countryOrigin;
    }

    public void setCountryOrigin(String countryOrigin) {
        this.countryOrigin = countryOrigin;
    }

    public String getCountryDestination() {
        return countryDestination;
    }

    public void setCountryDestination(String countryDestination) {
        this.countryDestination = countryDestination;
    }
}

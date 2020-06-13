package gov.gtas.summary;

import java.util.*;

public class  EventIdentifier {

    private String identifier;
    private List<String> identifierArrayList;
    private String eventType;
    private String countryOrigin;
    private String countryDestination;

    public EventIdentifier() {}


    //identifier list = origin port, dest port, carreier, number, etd date no timestamp


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

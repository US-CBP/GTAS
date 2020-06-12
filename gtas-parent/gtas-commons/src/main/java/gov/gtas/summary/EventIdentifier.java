package gov.gtas.summary;

import gov.gtas.model.Flight;

import java.util.*;

public class  EventIdentifier {

    private String identifier;
    private List<String> identifierArrayList;
    private String eventType;
    private String countryOrigin;
    private String countryDestination;

    public EventIdentifier() {}

    public static Date stripTime(Date d) {
        if (d == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    //identifier list = origin port, dest port, carreier, number, etd date no timestamp
    public static EventIdentifier from(Flight flight) {
        EventIdentifier eventIdentifier = new EventIdentifier();
        eventIdentifier.setEventType("BC_EVENT");
        eventIdentifier.setCountryOrigin(flight.getOriginCountry());
        eventIdentifier.setCountryDestination(flight.getDestinationCountry());
        List<String> identList = new ArrayList<>(6);
        identList.add(flight.getOrigin());
        identList.add(flight.getDestination());
        identList.add(flight.getCarrier());
        identList.add(flight.getFlightNumber());
        identList.add(Long.toString(stripTime(flight.getEtdDate()).getTime()));
        identList.add(Long.toString(flight.getEtdDate().getTime()));
        eventIdentifier.setIdentifierArrayList(identList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(identList.get(i));
        }
        eventIdentifier.setIdentifier(sb.toString());
        return eventIdentifier;
    }

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

package gov.gtas;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.PassengerVo;

import java.util.List;
import java.util.Set;

public class PaxProcessingDto {

    List<PassengerVo> passengers;
    Set<Flight> messageFlights;
    java.util.Set<Passenger> messagePassengers;

    public Set<Passenger> getNewPassengers() {
        return newPassengers;
    }

    public void setNewPassengers(Set<Passenger> newPassengers) {
        this.newPassengers = newPassengers;
    }

    java.util.Set<Passenger> newPassengers;
    Set<BookingDetail> bookingDetails;
    Set<PassengerVo> existingPassengers;
    Flight primeFlight;

    public boolean isPrimeFlightNew() {
        return isPrimeFlightNew;
    }

    public void setPrimeFlightNew(boolean primeFlightNew) {
        isPrimeFlightNew = primeFlightNew;
    }

    boolean isPrimeFlightNew;


    public List<PassengerVo> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerVo> passengers) {
        this.passengers = passengers;
    }

    public Set<Flight> getMessageFlights() {
        return messageFlights;
    }

    public void setMessageFlights(Set<Flight> messageFlights) {
        this.messageFlights = messageFlights;
    }

    public Set<Passenger> getMessagePassengers() {
        return messagePassengers;
    }

    public void setMessagePassengers(Set<Passenger> messagePassengers) {
        this.messagePassengers = messagePassengers;
    }

    public Set<BookingDetail> getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(Set<BookingDetail> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public Set<PassengerVo> getExistingPassengers() {
        return existingPassengers;
    }

    public void setExistingPassengers(Set<PassengerVo> existingPassengers) {
        this.existingPassengers = existingPassengers;
    }

    public Flight getPrimeFlight() {
        return primeFlight;
    }

    public void setPrimeFlight(Flight primeFlight) {
        this.primeFlight = primeFlight;
    }

}

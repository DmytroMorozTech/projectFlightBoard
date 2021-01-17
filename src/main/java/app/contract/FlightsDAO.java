package app.contract;

import app.domain.Flight;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public interface FlightsDAO {

    HashMap<String, Flight> getAllFlights();

    Optional<HashMap<String, Flight>> getFilteredFlights(String destinationPlace,
                                                         LocalDateTime departureDateTime,
                                                         int freeSeats);

    Flight getFlightById(String idOfFlight);

    Optional<HashMap<String, Flight>> getFlightsForNext24Hours(LocalDateTime now);

    void printFlightsToConsole(Optional<HashMap<String, Flight>> flightsOptional);

    void applyReservation4Flight(String idOfFlight, int numbOfSeats);

    void cancelReservation4Flight(String idOfFlight, int numbOfSeats);

    boolean flightsWereUploaded();

}

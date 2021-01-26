package app.contract;

import app.domain.Booking;
import app.domain.Flight;
import app.domain.FlightRoute;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FlightsDAO {

    HashMap<String, Flight> getAllFlights();

    Optional<List<FlightRoute>> getFilteredFlights(String departurePlace,
                                                   String destinationPlace,
                                                   LocalDateTime departureDateTime,
                                                   int freeSeats);

    Optional<Flight> getFlightById(String idOfFlight);

    Optional<List<Flight>> getFlightsForNext24Hours(LocalDateTime now);

    void printFlightsToConsole(Optional<List<Flight>> flightsOptional);

    void applySeatsReserve4Booking(Booking booking);

    void cancelSeatsReserve4Booking(Booking booking);

    boolean flightsWereUploaded();

}

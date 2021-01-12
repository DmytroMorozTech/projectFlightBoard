package app.contract;

import app.domain.Flight;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FlightsDAO {

    void createFlight(Flight flight) throws BookingOverflowException, FlightOverflowException, UsersOverflowException, IOException;

    boolean deleteFlight(Flight flight);

    HashMap<String, Flight> getAllFlights();

    HashMap<String, Flight> findFlights(String destinationPlace, ZonedDateTime departureDateTime,
                                    int freeSeats);

    Flight getFlightById(int idOfFlight);

    Optional<HashMap<String,Flight>> getFlightsForNext24Hours();

    void printFlightsToConsole(Optional<HashMap<String,Flight>> flightsOptional);

    void applyReservation4Flight(int idOfFlight, int numbOfSeats);

    void cancelReservation4Flight(int idOfFlight, int numbOfSeats);

    boolean flightsWereUploaded();

}

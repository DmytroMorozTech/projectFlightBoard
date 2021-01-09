package app.contract;

import app.domain.Flight;

import java.time.ZonedDateTime;
import java.util.List;

public interface FlightsDAO {

    public boolean createFlight(Flight flight);

    public boolean deleteFlight(Flight flight);

    public List<Flight> getAllFlights();

    public List<Flight> findFlights(String destinationPlace, ZonedDateTime departureDateTime,
                                    int freeSeats);

    public Flight getFlightById(int idOfFlight);

    public List<Flight> getFlightsForNext24Hours();

    public void applyReservation4Flight(int idOfFlight, int numbOfSeats);

    public void cancelReservation4Flight(int idOfFlight, int numbOfSeats);

}

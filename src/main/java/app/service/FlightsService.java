package app.service;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.repos.CollectionFlightsDAO;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;

public class FlightsService implements FlightsDAO, CanWorkWithFileSystem {
    private CollectionFlightsDAO flightsDAO;

    public FlightsService() {
        this.flightsDAO = new CollectionFlightsDAO();
    }

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        flightsDAO.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        return flightsDAO.saveDataToFile();
    }


    @Override
    public boolean deleteFlight(Flight flight) {
        return flightsDAO.deleteFlight(flight);
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        return flightsDAO.getAllFlights();
    }

    @Override
    public HashMap<String, Flight> findFlights(String destinationPlace,
                                               ZonedDateTime departureDateTime, int freeSeats) {
        return null;
    }

    @Override
    public Flight getFlightById(int idOfFlight) {
        return null;
    }

    @Override
    public Optional<HashMap<String, Flight>> getFlightsForNext24Hours() {
        return flightsDAO.getFlightsForNext24Hours();
    }

    @Override
    public void applyReservation4Flight(int idOfFlight, int numbOfSeats) {

    }

    @Override
    public void cancelReservation4Flight(int idOfFlight, int numbOfSeats) {

    }


    @Override
    public void createFlight(Flight flight) throws BookingOverflowException, FlightOverflowException, IOException, UsersOverflowException {
        flightsDAO.createFlight(flight);
    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightsOptional) {
        flightsDAO.printFlightsToConsole(flightsOptional);
    }

    @Override
    public boolean flightsWereUploaded() {
        return flightsDAO.flightsWereUploaded();
    }

}

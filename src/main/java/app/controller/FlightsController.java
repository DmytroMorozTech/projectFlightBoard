package app.controller;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.FlightsService;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;

public class FlightsController implements FlightsDAO, CanWorkWithFileSystem {
    private FlightsService flightsService;

    public FlightsController() {
        this.flightsService = new FlightsService();
    }

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        flightsService.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        return flightsService.saveDataToFile();
    }

    @Override
    public void createFlight(Flight flight) throws BookingOverflowException, FlightOverflowException, UsersOverflowException, IOException {
        flightsService.createFlight(flight);
    }

    @Override
    public boolean deleteFlight(Flight flight) {
        return flightsService.deleteFlight(flight);
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        return flightsService.getAllFlights();
    }

    @Override
    public HashMap<String, Flight> findFlights(String destinationPlace, ZonedDateTime departureDateTime, int freeSeats) {
        return null;
    }

    @Override
    public Flight getFlightById(int idOfFlight) {
        return null;
    }

    @Override
    public Optional<HashMap<String, Flight>> getFlightsForNext24Hours() {
        return flightsService.getFlightsForNext24Hours();
    }


    @Override
    public void applyReservation4Flight(int idOfFlight, int numbOfSeats) {

    }

    @Override
    public void cancelReservation4Flight(int idOfFlight, int numbOfSeats) {

    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightsOptional) {
        flightsService.printFlightsToConsole(flightsOptional);
    }

    @Override
    public boolean flightsWereUploaded() {
        return flightsService.flightsWereUploaded();
    }
}

package app.controller;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Booking;
import app.domain.Flight;
import app.domain.FlightRoute;
import app.service.FlightsService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FlightsController implements FlightsDAO, CanWorkWithFileSystem {
    private FlightsService flightsService;

    public FlightsController() {
        this.flightsService = new FlightsService();
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        return flightsService.getAllFlights();
    }

    @Override
    public Optional<List<FlightRoute>> getFilteredFlights(String departurePlace,
                                                          String destinationPlace,
                                                          LocalDateTime departureDateTime,
                                                          int freeSeats) {
        return flightsService.getFilteredFlights(departurePlace, destinationPlace,
                                                 departureDateTime, freeSeats);
    }

    @Override
    public Optional<Flight> getFlightById(String idOfFlight) {
        return flightsService.getFlightById(idOfFlight);
    }

    @Override
    public Optional<List<Flight>> getFlightsForNext24Hours(LocalDateTime now) {
        return flightsService.getFlightsForNext24Hours(now);
    }

    @Override
    public void applySeatsReserve4Booking(Booking booking) {
        flightsService.applySeatsReserve4Booking(booking);
    }

    @Override
    public void cancelSeatsReserve4Booking(Booking booking) {
        flightsService.cancelSeatsReserve4Booking(booking);
    }

    @Override
    public void printFlightsToConsole(Optional<List<Flight>> flightsOptional) {
        flightsService.printFlightsToConsole(flightsOptional);
    }

    @Override
    public boolean flightsWereUploaded() {
        return flightsService.flightsWereUploaded();
    }

    @Override
    public void loadData() {
        flightsService.loadData();
    }

    @Override
    public boolean saveDataToFile() {
        return flightsService.saveDataToFile();
    }

    public List<Flight> convertHashMapToList(HashMap<String, Flight> hashMap) {
        Collection<Flight> flightsCollection = hashMap.values();
        List<Flight> filteredFlights = flightsCollection
                .stream()
                .sorted(Comparator.comparingLong(Flight::getDepartureTime))
                .collect(Collectors.toList());

        return new ArrayList<Flight>(filteredFlights);
    }

    public void printIndexedList(List<FlightRoute> flightRoutes) {
        int counter = 1;
        for (FlightRoute flightRoute : flightRoutes) {
            String outputStr = "<<< " + counter + " >>>\n" + flightRoute.prettyFormat();
            System.out.println(outputStr);
            counter++;
        }
    }
}

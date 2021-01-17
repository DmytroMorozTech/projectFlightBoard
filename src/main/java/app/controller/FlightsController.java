package app.controller;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
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
    public Optional<HashMap<String, Flight>> getFilteredFlights(String destinationPlace,
                                                                LocalDateTime departureDateTime,
                                                                int freeSeats) {
        return flightsService.getFilteredFlights(destinationPlace, departureDateTime, freeSeats);
    }

    @Override
    public Flight getFlightById(String idOfFlight) {
        return flightsService.getFlightById(idOfFlight);
    }

    @Override
    public Optional<HashMap<String, Flight>> getFlightsForNext24Hours(LocalDateTime now) {
        return flightsService.getFlightsForNext24Hours(now);
    }

    @Override
    public void applyReservation4Flight(String idOfFlight, int numbOfSeats) {
        flightsService.applyReservation4Flight(idOfFlight, numbOfSeats);
    }

    @Override
    public void cancelReservation4Flight(String idOfFlight, int numbOfSeats) {
        flightsService.cancelReservation4Flight(idOfFlight, numbOfSeats);
    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightsOptional) {
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

    public void printIndexedList(List<Flight> flights) {
        int counter = 1;
        for (Flight f : flights) {
            String outputStr = counter + ". " + f.prettyFormat();
            System.out.println(outputStr);
            counter++;
        }
    }
}

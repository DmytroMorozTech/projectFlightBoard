package app.service;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Booking;
import app.domain.Flight;
import app.domain.FlightRoute;
import app.exceptions.FlightOverflowException;
import app.repos.CollectionFlightsDAO;
import app.service.loggerService.LoggerService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FlightsService implements FlightsDAO, CanWorkWithFileSystem {
    private CollectionFlightsDAO flightsDAO;

    public FlightsService() {
        this.flightsDAO = new CollectionFlightsDAO();
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        LoggerService.info("Запрос на получение всех рейсов.");
        return flightsDAO.getAllFlights();
    }

    @Override
    public Optional<List<FlightRoute>> getFilteredFlights(String departurePlace,
                                                          String destinationPlace,
                                                          LocalDateTime departureDateTime,
                                                          int freeSeats) {
        LoggerService.info("Поиск рейсов по заданным критериям.");
        return flightsDAO.getFilteredFlights(departurePlace, destinationPlace, departureDateTime,
                                             freeSeats);
    }

    @Override
    public Optional<Flight> getFlightById(String idOfFlight) {
        LoggerService.info("Поиск рейса по его номеру(ID).");
        return flightsDAO.getFlightById(idOfFlight);
    }

    @Override
    public Optional<List<Flight>> getFlightsForNext24Hours(LocalDateTime now) {
        LoggerService.info("Формирование списка рейсов на ближайшие 24 часа");
        return flightsDAO.getFlightsForNext24Hours(now);
    }

    @Override
    public void applySeatsReserve4Booking(Booking booking) {
        flightsDAO.applySeatsReserve4Booking(booking);
        LoggerService.info("Резервирование мест на рейсе в связи с оформлением брони");
    }

    @Override
    public void cancelSeatsReserve4Booking(Booking booking) {
        flightsDAO.cancelSeatsReserve4Booking(booking);
        LoggerService.info("Освобождение мест на рейсе в связи с отменой брони.");
    }

    @Override
    public void printFlightsToConsole(Optional<List<Flight>> flightsOptional) {
        flightsDAO.printFlightsToConsole(flightsOptional);
        LoggerService.info("Вывод данных о полетах в консоль приложения.");
    }

    @Override
    public boolean flightsWereUploaded() {
        return flightsDAO.flightsWereUploaded();
    }

    @Override
    public void loadData() {
        try {
            flightsDAO.loadData();
        }
        catch (FlightOverflowException ex) {
            LoggerService.error("FlightOverflowException: " + ex.getMessage());
        }
    }

    @Override
    public boolean saveDataToFile() {
        try {
            return flightsDAO.saveDataToFile();
        }
        catch (FlightOverflowException ex) {
            LoggerService.error("FlightOverflowException: " + ex.getMessage());
            return false;
        }
    }

    public void loadDataForTesting() {
        try {
            flightsDAO.loadDataForTesting();
        }
        catch (FlightOverflowException e) {
            e.printStackTrace();
        }
    }
}

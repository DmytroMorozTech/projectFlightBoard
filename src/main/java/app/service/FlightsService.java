package app.service;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
import app.exceptions.FlightOverflowException;
import app.repos.CollectionFlightsDAO;
import app.service.loggerService.LoggerService;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public Optional<HashMap<String, Flight>> getFilteredFlights(String destinationPlace,
                                                                LocalDateTime departureDateTime,
                                                                int freeSeats) {
        LoggerService.info("Поиск рейсов по заданным критериям.");
        return flightsDAO.getFilteredFlights(destinationPlace, departureDateTime, freeSeats);
    }

    @Override
    public Flight getFlightById(String idOfFlight) {
        LoggerService.info("Поиск рейса по его номеру(ID).");
        return flightsDAO.getFlightById(idOfFlight);
    }

    @Override
    public Optional<HashMap<String, Flight>> getFlightsForNext24Hours() {
        LoggerService.info("Формирование списка рейсов на ближайшие 24 часа");
        return flightsDAO.getFlightsForNext24Hours();
    }

    @Override
    public void applyReservation4Flight(String idOfFlight, int numbOfSeats) {
        flightsDAO.applyReservation4Flight(idOfFlight,numbOfSeats);
        LoggerService.info("Резервирование мест на рейсе в связи с оформлением брони");
    }

    @Override
    public void cancelReservation4Flight(String idOfFlight, int numbOfSeats) {
        flightsDAO.cancelReservation4Flight(idOfFlight,numbOfSeats);
        LoggerService.info("Освобождение мест на рейсе в связи с отменой брони.");
    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightsOptional) {
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
        } catch (FlightOverflowException ex) {
            LoggerService.error("FlightOverflowException: " + ex.getMessage());
            return false;
        }
    }
}

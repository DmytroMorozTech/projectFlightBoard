package app.repos;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
import app.exceptions.FlightOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionFlightsDAO implements FlightsDAO, CanWorkWithFileSystem {

    private HashMap<String, Flight> flights = new HashMap<>();
    private final String nameOfFile = "flights.bin";

    public CollectionFlightsDAO() {
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        return flights;
    }

    @Override
    public Optional<HashMap<String, Flight>> getFilteredFlights(String destinationPlace,
                                                                LocalDateTime departureDateTime, int freeSeats) {

        long departureDtZoned = convertLocalDtToZonedDt(departureDateTime);
        long departureDtZonedPlus24H = convertLocalDtToZonedDt(departureDateTime.plusHours(24));

        Map<String, Flight> filteredMap =
                flights.entrySet().stream()   //Stream<Map.Entry<String, Flight>>
                       .filter(f -> f.getValue().getDestinationPlace().equals(destinationPlace)
                               && f.getValue().getDepartureTime() > departureDtZoned
                               && f.getValue().getDepartureTime() < departureDtZonedPlus24H
                               && f.getValue().getNumberOfFreeSeats() >= freeSeats)
                       .collect(Collectors.toMap(
                               f -> f.getKey(),
                               f -> f.getValue()
                       ));
        HashMap<String, Flight> filteredHashMap = (HashMap<String, Flight>) filteredMap;
        return Optional.ofNullable(filteredHashMap);
    }

    @Override
    public Flight getFlightById(String idOfFlight) {
        return flights.get(idOfFlight);
    }

    @Override
    public Optional<HashMap<String, Flight>> getFlightsForNext24Hours() {
        long currentTimeZoned = convertLocalDtToZonedDt(LocalDateTime.now());
        long currentTimePlus24hZoned = convertLocalDtToZonedDt(LocalDateTime.now().plusHours(24));

        Map<String, Flight> filteredMap =
                flights.entrySet().stream()   //Stream<Map.Entry<String,Flight>>
                       .filter(f -> f.getValue().getDepartureTime() > currentTimeZoned
                               && f.getValue().getDepartureTime() < currentTimePlus24hZoned)
                       .collect(Collectors.toMap(f -> f.getKey(), f -> f.getValue()));
        HashMap<String, Flight> filteredHashMap = (HashMap<String, Flight>) filteredMap;

        return Optional.of(filteredHashMap);
    }

    @Override
    public void applyReservation4Flight(String idOfFlight, int numbOfSeats) {
        flights.get(idOfFlight).applyReservation4Flight(numbOfSeats);
    }

    @Override
    public void cancelReservation4Flight(String idOfFlight, int numbOfSeats) {
        flights.get(idOfFlight).cancelReservation4Flight(numbOfSeats);
    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightOptional) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        String formattedDateTime = dtf.format(currentDateTime);

        System.out.println("<<< АЭРОПОРТ, ГОРОД КИЕВ >>>");
        System.out.println(formattedDateTime);
        System.out.println("*************************************************************");

        if (flightOptional.isEmpty())
            System.out.println("Запрошенные рейсы не были найдены");
        else if (flightOptional.isPresent()) {
            Collection<Flight> foundFlights = flightOptional.get().values();
            for (Flight f : foundFlights)
                System.out.println(f.prettyFormat());
        }
    }

    @Override
    public boolean flightsWereUploaded() {
        return flights.size() > 0;
    }

    @Override
    public void loadData() throws FlightOverflowException {
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                flights = (HashMap<String, Flight>) dataFromFS;
            }
            LoggerService.info("Загрузка файла " + nameOfFile + " с жесткого диска.");
        }
        catch (IOException | ClassNotFoundException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                                                      " с жесткого диска.");
        }
    }

    @Override
    public boolean saveDataToFile() throws FlightOverflowException {

        try {
            FileSystemService fs = new FileSystemService();
            fs.saveDataToFile(nameOfFile, flights);
            LoggerService.info("Сохранение данных на жесткий диск в файл " + nameOfFile);
            return true;
        }
        catch (IOException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                                                      " на жесткий диск компьютера.");
        }
    }

    private long convertLocalDtToZonedDt(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}

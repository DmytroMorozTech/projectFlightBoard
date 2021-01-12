package app.repos;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Flight;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionFlightsDAO implements FlightsDAO, CanWorkWithFileSystem {

    private HashMap<String, Flight> flights = new HashMap<>();
    private final String nameOfFile = "flights.bin";

    public CollectionFlightsDAO() {
    }

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        LoggerService.info("Загрузка файла " + nameOfFile + " с жесткого диска.");

        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                flights = (HashMap<String, Flight>) dataFromFS;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                                                      " с жесткого диска.");
        }
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        LoggerService.info("Сохранение данных на жесткий диск в файл " + nameOfFile);

        try {
            FileSystemService fs = new FileSystemService();
            fs.saveDataToFile(nameOfFile, flights);
            return true;
        }
        catch (IOException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                                                      " на жесткий диск компьютера.");
        }
    }


    @Override
    public boolean deleteFlight(Flight flight) {
        String idOfFlight = flight.getIdOfFlight();
        flights.remove(flight);
        return flights.containsKey(idOfFlight);
    }

    @Override
    public HashMap<String, Flight> getAllFlights() {
        return flights;
    }

    @Override
    public HashMap<String, Flight> findFlights(String destinationPlace,
                                               ZonedDateTime departureDateTime, int freeSeats) {
        ZonedDateTime ddtPlus24Hours = departureDateTime.plusDays(1); //departure date time plus 24h
        Map<String, Flight> collect =
                flights.entrySet().stream()   //Stream<Map.Entry<String, Flight>>
                       .filter(f -> f.getValue().getDestinationPlace().equals(destinationPlace)
                               && f.getValue().getDepartureTime() < ddtPlus24Hours.toInstant().toEpochMilli()
                               && f.getValue().getNumberOfFreeSeats() >= freeSeats)
                       .collect(Collectors.toMap(
                               f -> f.getKey(),
                               f -> f.getValue()
                       ));
        return (HashMap<String, Flight>) collect;
    }

    @Override
    public Flight getFlightById(int idOfFlight) {
        return null;
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
//        boolean dataFound = filteredMap.size()>0;
//        return  dataFound ? new Optional((HashMap<String, Flight>)filteredMap) :
    }

    @Override
    public void applyReservation4Flight(int idOfFlight, int numbOfSeats) {

    }

    @Override
    public void cancelReservation4Flight(int idOfFlight, int numbOfSeats) {

    }


    private static String toLowerCase(String s) {
        return s.toLowerCase();
    }

    @Override
    public void createFlight(Flight flight) throws BookingOverflowException, FlightOverflowException, UsersOverflowException, IOException {
        flights.put(flight.getIdOfFlight(), flight);
        saveDataToFile();
    }

    private long convertLocalDtToZonedDt(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public void printFlightsToConsole(Optional<HashMap<String, Flight>> flightOptional) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
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
}

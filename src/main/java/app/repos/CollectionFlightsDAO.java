package app.repos;

import app.contract.CanWorkWithFileSystem;
import app.contract.FlightsDAO;
import app.domain.Booking;
import app.domain.Flight;
import app.domain.FlightRoute;
import app.exceptions.FlightOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import javax.swing.text.html.Option;
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
    public Optional<List<FlightRoute>> getFilteredFlights(String departurePlace,
                                                          String destinationPlace,
                                                          LocalDateTime departureDateTime, int freeSeats) {

        List<FlightRoute> filteredFlightRoutes = new ArrayList<>();

        long departureDtZoned = convertLocalDtToZonedDt(departureDateTime);
        long departureDtZonedPlus24H = convertLocalDtToZonedDt(departureDateTime.plusHours(24));
        long departureDtZonedPlus36H = convertLocalDtToZonedDt(departureDateTime.plusHours(36));

        flights
                .entrySet()
                .stream()
                .map(f -> f.getValue())
                .filter(f -> f.getDeparturePlace().equals(departurePlace)
                        && f.getDestinationPlace().equals(destinationPlace)
                        && f.getDepartureTime() > departureDtZoned
                        && f.getDepartureTime() < departureDtZonedPlus24H
                        && f.getNumberOfFreeSeats() >= freeSeats)
                .forEach(f -> filteredFlightRoutes.add(new FlightRoute(f)));

        // Здесь мы нашли все прямые рейсы по запрошенным критериям поиска и добавили их
        // в качестве маршрутов(FlightRoute) в List<FlightRoute>. Туда же мы будем ниже добавлять
        // все найденные стыковочные маршруты.
//        ------------------------------------------------------------------------------------
        List<Flight> potentialDepartureFlights =
                flights
                        .entrySet()
                        .stream()
                        .map(f -> f.getValue())
                        .filter(f -> f.getDeparturePlace().equals(departurePlace)
                                && f.getDepartureTime() > departureDtZoned
                                && f.getDepartureTime() < departureDtZonedPlus24H
                                && f.getNumberOfFreeSeats() >= freeSeats)
                        .collect(Collectors.toList());
        // здесь мы сначала находим все рейсы, которые вылетают из указанного пункта отправления
        // в ближайшие 24 часа.

        Set<String> codesOfConnectingAPTs =
                potentialDepartureFlights
                        .stream()
                        .map(Flight::getCodeOfDestinationAPT)
                        .collect(Collectors.toSet());
        // - затем составляем набор уникальных кодов IATA (IATA - International Air Transport
        // Association), идентифицирующих аэропорт пункта назначения по найденным рейсам (например,
        // для Дюссельдорфа это "DUS", для аэропорта Борисполь - "KBP");

        List<Flight> potentialConnectingFlights =
                flights
                        .entrySet()
                        .stream()
                        .map(f -> f.getValue())
                        .filter(f -> f.getDestinationPlace().equals(destinationPlace)
                                && f.getDepartureTime() > departureDtZoned
                                && f.getDepartureTime() < departureDtZonedPlus36H
                                && f.getNumberOfFreeSeats() >= freeSeats)
                        .filter(f -> codesOfConnectingAPTs.contains(f.getCodeOfDepartureAPT()))
                        .collect(Collectors.toList());

        for (Flight f1 : potentialDepartureFlights) {
            for (Flight f2 : potentialConnectingFlights) {
                String f1DestinationAPT = f1.getCodeOfDestinationAPT();
                String f2DepartureAPT = f2.getCodeOfDepartureAPT();
                long numberOfMillisIn12H = 1000 * 60 * 60 * 12;
                boolean f2IsAfterF1 = f2.getDepartureTime() > f1.getArrivalTime();
                boolean waitingTimeIsLessThan12H =
                        (f2.getDepartureTime() - f1.getArrivalTime()) < numberOfMillisIn12H;

                if (f1DestinationAPT.equals(f2DepartureAPT) && f2IsAfterF1 && waitingTimeIsLessThan12H) {
                    FlightRoute flightRoute = new FlightRoute(f1, f2);
                    filteredFlightRoutes.add(flightRoute);
                }
            }
        }
//        ------------------------------------------------------------------------------------
        // Все найденные прямые рейсы, а также рейсы с пересадками, мы сортируем по возрастанию
        // даты вылета.
        List<FlightRoute> filteredFlightRoutesSorted = filteredFlightRoutes
                .stream()
                .sorted(Comparator.comparing(fr -> fr.getFlight1().getDepartureTime()))
                .collect(Collectors.toList());

        return Optional.ofNullable(filteredFlightRoutesSorted);
    }

    @Override
    public Optional<Flight> getFlightById(String idOfFlight) {
        return Optional.ofNullable(flights.get(idOfFlight));
    }

    @Override
    public Optional<List<Flight>> getFlightsForNext24Hours(LocalDateTime now) {
        long currentTimeZoned = convertLocalDtToZonedDt(now);
        long currentTimePlus24hZoned = convertLocalDtToZonedDt(now.plusHours(24));

        List<Flight> listOfFilteredFlights = flights
                .entrySet()
                .stream()
                .map(f -> f.getValue())
                .filter(f -> f.getDepartureTime() > currentTimeZoned
                        && f.getDepartureTime() < currentTimePlus24hZoned)
                .sorted(Comparator.comparing(f -> f.getDepartureTime()))
                .collect(Collectors.toList());
        return Optional.of(listOfFilteredFlights);
    }

    @Override
    public void applySeatsReserve4Booking(Booking b) {
        boolean hasConnectingFlight = !b.getFlightRoute().isDirectFlight();
        String idOfF1 = b.getFlightRoute().getFlight1().getIdOfFlight();
        int numbOfPassengers = b.getPassengerList().size();
        flights.get(idOfF1).applyReservation4Flight(numbOfPassengers);

        if (hasConnectingFlight) {
            String idOfF2 = b.getFlightRoute().getFlight2().getIdOfFlight();
            flights.get(idOfF2).applyReservation4Flight(numbOfPassengers);
        }
    }

    @Override
    public void cancelSeatsReserve4Booking(Booking b) {
        boolean hasConnectingFlight = !b.getFlightRoute().isDirectFlight();
        String idOfF1 = b.getFlightRoute().getFlight1().getIdOfFlight();
        int numbOfPassengers = b.getPassengerList().size();
        Optional<Flight> f1 = Optional.ofNullable(flights.get(idOfF1));
        f1.ifPresent(flight -> flight.cancelReservation4Flight(numbOfPassengers));

        if (hasConnectingFlight) {
            String idOfF2 = b.getFlightRoute().getFlight2().getIdOfFlight();
            Optional<Flight> f2 = Optional.ofNullable(flights.get(idOfF2));
            f2.ifPresent(flight -> flight.cancelReservation4Flight(numbOfPassengers));
        }
    }

    @Override
    public void printFlightsToConsole(Optional<List<Flight>> flightOptional) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        String formattedDateTime = dtf.format(currentDateTime);

        System.out.println("ТЕКУЩЕЕ ВРЕМЯ:");
        System.out.println(formattedDateTime);
        System.out.println("*************************************************************");

        if (flightOptional.isEmpty())
            System.out.println("Запрошенные рейсы не были найдены");
        else if (flightOptional.isPresent()) {
            List<Flight> foundFlights = flightOptional.get();
            List<Flight> foundFlightsSorted = foundFlights
                    .stream()
                    .sorted(Comparator.comparing(f -> f.getDepartureTime()))
                    .collect(Collectors.toList());
            for (Flight f : foundFlightsSorted)
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
        } catch (IOException | ClassNotFoundException e) {
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
        } catch (IOException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                    " на жесткий диск компьютера.");
        }
    }

    private long convertLocalDtToZonedDt(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    //    ---------------------------------------------------------------------------------
    // Мы делаем перегрузку (overload) данного метода для того, чтобы была возможность при
    // тестировании FlightsService загружать данные из отдельного файла с базой данных рейсов.
    public void loadDataForTesting() throws FlightOverflowException {
        String fileName = "flightsForTestingOnly.bin";
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(fileName);
            if (dataFromFS instanceof HashMap) {
                flights = (HashMap<String, Flight>) dataFromFS;
            }
            LoggerService.info("Загрузка файла " + fileName + " с жесткого диска. С целью " +
                    "тестирования FlightsService.");
        } catch (IOException | ClassNotFoundException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при чтении файла " + fileName +
                    " с жесткого диска.");
        }
    }
}

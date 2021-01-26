package app.service;

import app.domain.Flight;
import app.domain.FlightRoute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightsServiceTest {
    private static FlightsService service;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private static long strToDateTimeInMillis(String dateAsString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateAsString, dtf);
        long dateTimeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return dateTimeInMillis;
    }

    private static LocalDateTime strToLocalDateTime(String dateAsString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateAsString, dtf);
        long dateTimeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Timestamp(dateTimeInMillis).toLocalDateTime();
    }

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        // Here we reassign the standard output stream to a new PrintStream with a
        // ByteArrayOutputStream.
//        --------------------------------------------

        service = new FlightsService();
        service.loadDataForTesting();

    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
//        As the standard output stream is a shared static resource used by other parts of the system,
//        we should take care of restoring it to its original state when our test terminates.
    }

    @Test
    void getAllFlights() {
        System.out.println(">>> Running TEST of method getAllFlights(), class FlightsService.");
        // Given
        HashMap<String, Flight> allFlights;
        int expectedNumbOfFlights = 2000;

        // When
        allFlights = service.getAllFlights();
        int actualNumbOfFlights = allFlights.size();

        // Then
        assertEquals(expectedNumbOfFlights, actualNumbOfFlights);
    }

    @Test
    void getFilteredFlights() {
        System.out.println(">>> Running TEST of method getFilteredFlights(), class FlightsService.");
        // Given
        LocalDateTime departureTime1 = strToLocalDateTime("20/01/2021-17:30");
        LocalDateTime departureTime2 = strToLocalDateTime("21/01/2021-17:30");
        LocalDateTime departureTime3 = strToLocalDateTime("22/01/2021-10:00");
        int expectedNumbOfFilteredFl1 = 28;
        int expectedNumbOfFilteredFl2 = 4;
        int expectedNumbOfFilteredFl3 = 3;

        // When
        Optional<List<FlightRoute>> filteredFlights1 =
                service.getFilteredFlights("Киев", "Нью-Йорк", departureTime1, 2);
        int actualNumbOfFilteredFl1 = filteredFlights1.get().size();
//        -------------------------------------------------------------
        Optional<List<FlightRoute>> filteredFlights2 = service.getFilteredFlights("Киев",
                                                                                  "Стамбул", departureTime2, 2);
        int actualNumbOfFilteredFl2 = filteredFlights2.get().size();
//        -------------------------------------------------------------
        Optional<List<FlightRoute>> filteredFlights3 = service.getFilteredFlights("Стамбул",
                                                                                  "Нью-Йорк", departureTime3, 2);
        int actualNumbOfFilteredFl3 = filteredFlights3.get().size();

        //Then
        assertEquals(expectedNumbOfFilteredFl1, actualNumbOfFilteredFl1);
        assertEquals(expectedNumbOfFilteredFl2, actualNumbOfFilteredFl2);
        assertEquals(expectedNumbOfFilteredFl3, actualNumbOfFilteredFl3);
    }

    @Test
    void getFlightById() {
        System.out.println(">>> Running TEST of method getFlightById(), class FlightsService.");
        //Given
        String expectedDestination1 = "Нью-Йорк";
        int expectedNumbOfFreeSeats1 = 18;

        // When
        Flight flight1 = service.getFlightById("FL723L").get();
        String actualDestination1 = flight1.getDestinationPlace();
        int actualNumbOfFreeSeats1 = flight1.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedDestination1, actualDestination1);
        assertEquals(expectedNumbOfFreeSeats1, actualNumbOfFreeSeats1);

//        --------------------------------------------------------------
        //Given
        String expectedDestination2 = "Мадрид";
        int expectedNumbOfFreeSeats2 = 9;

        // When
        Flight flight2 = service.getFlightById("FL217K").get();
        String actualDestination2 = flight2.getDestinationPlace();
        int actualNumbOfFreeSeats2 = flight2.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedDestination2, actualDestination2);
        assertEquals(expectedNumbOfFreeSeats2, actualNumbOfFreeSeats2);
    }

    @Test
    void getFlightsForNext24Hours() {
        System.out.println(">>> Running TEST of method getFlightsForNext24Hours(), class FlightsService.");
        // Given
        LocalDateTime startingDateTime1 = strToLocalDateTime("15/01/2021-00:00");
        List<Flight> flights4Next24Hours1;
        int expectedNumbOfFlights1 = 96;

        // When
        flights4Next24Hours1 = service.getFlightsForNext24Hours(startingDateTime1).get();
        int actualNumbOfFlights1 = flights4Next24Hours1.size();

        // Then
        assertEquals(expectedNumbOfFlights1, actualNumbOfFlights1);
//        ---------------------------------------------------------------------
        // Given
        LocalDateTime startingDateTime2 = strToLocalDateTime("17/01/2021-15:20");
        List<Flight> flights4Next24Hours2;
        int expectedNumbOfFlights2 = 73;

        // When
        flights4Next24Hours2 = service.getFlightsForNext24Hours(startingDateTime2).get();
        int actualNumbOfFlights2 = flights4Next24Hours2.size();

        // Then
        assertEquals(expectedNumbOfFlights2, actualNumbOfFlights2);
    }

    @Test
    void applyReservation4Flight() {
        System.out.println(">>> Running TEST of method applyReservation4Flight(), class FlightsService.");
        // Given
        Flight flight = service.getFlightById("FL693D").get();
        int initialNumbOfFreeSeats = flight.getNumberOfFreeSeats();

        // When
        flight.applyReservation4Flight(4);

        // Then
        assertEquals(initialNumbOfFreeSeats - 4, flight.getNumberOfFreeSeats());
    }

    @Test
    void cancelReservation4Flight() {
        System.out.println(">>> Running TEST of method cancelReservation4Flight(), class FlightsService.");
        // Given
        Flight flight = service.getFlightById("FL379W").get();
        int initialNumbOfFreeSeats = flight.getNumberOfFreeSeats();

        // When
        flight.cancelReservation4Flight(8);

        // Then
        assertEquals(initialNumbOfFreeSeats + 8, flight.getNumberOfFreeSeats());
    }

    @Test
    void printFlightsToConsole() {
        //Given
        LocalDateTime minDepartureTime = strToLocalDateTime("22/01/2021-10:00");
        LocalDateTime maxDepartureTime = strToLocalDateTime("22/01/2021-11:00");
        long maxDepartureTimeInMillis =
                maxDepartureTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        String expectedOutput = "Номер рейса: FL662B  |  ОТКУДА: Франкфурт-на-Майне,FRA |  КУДА: " +
                "Мадрид,MAD             |  Вылет: 22/01/2021-10:10  |  Прибытие: 22/01/2021-12:45" +
                "  |  Свободные места:22  |Номер рейса: FL378W  |  ОТКУДА: Киев,KBP               " +
                "|  КУДА: Лондон,LCY             |  Вылет: 22/01/2021-10:30  " +
                "|  Прибытие: 22/01/2021-14:00" +
                "  |  Свободные места:04  |Номер рейса: FL819Z  |  ОТКУДА: Нью-Йорк,JFK" +
                "           |  КУДА: Амстердам,AMS          |  Вылет: 22/01/2021-10:30  |" +
                "  Прибытие: 22/01/2021-17:30  |  Свободные места:12  |Номер рейса: FL688F  |" +
                "  ОТКУДА: Стамбул,IST            |  КУДА: Киев,KBP               |" +
                "  Вылет: 22/01/2021-10:40  |  Прибытие: 22/01/2021-12:55  |  Свободные места:03  |";

        Optional<List<Flight>> filteredFlights =
                service.getFlightsForNext24Hours(minDepartureTime);

        List<Flight> severalFilteredFlights =
                filteredFlights.get()
                               .stream()
                               .filter(f -> f.getDepartureTime() < maxDepartureTimeInMillis)
                               .collect(Collectors.toList());
        // When
        service.printFlightsToConsole(Optional.of(severalFilteredFlights));

        final String cleanOutput =
                outputStreamCaptor
                        .toString()
                        .trim()
                        .replaceAll("\n", "")
                        .replaceAll("\r", "")
                        .replaceAll("\t", "");

        // поскольку в выводе метода printFlightsToConsole присутствуют поточные дата и время
        // (на момент выполнения метода), то мы вынуждены здесь обрезать эту текстовую часть,
        // чтобы иметь возможность сравнивать оставшуюся часть текста.
        String cleanOutputFinal = cleanOutput.substring(91);

        // Then
        Assertions.assertEquals(expectedOutput, cleanOutputFinal);
    }

    @Test
    void flightsWereUploaded() {
        // Given
        boolean expectedOutput = true;
        // мы ожидаем, что метод flightsWereUploaded() вернет true, так как в секции @BeforeEach
        // был запущен метод service.loadDataForTesting(), который уже должен быть подгрузить в
        // объект service данные с рейсами (flights).

        // When
        boolean actualOutput = service.flightsWereUploaded();

        // Then
        assertEquals(expectedOutput, actualOutput);

    }

    @Test
    void loadData() {
        // Given
        // Когда мы запускаем тесты, в объекте service в данном тесте наши рейсы еще не наполнены
        // данными.
        // Но так как мы в секции @BeforeEach запускаем service.loadDataForTesting(), то в данном
        // тесте следующие 2 строки прописывать не обязательно. Поэтому закомментируем их:
        // When
        // service.loadDataForTesting();

        // Then
        assertTrue(service.getAllFlights().size() > 0);
        // Если размер полученного HashMap<String, Flight> больше ноля, то данные о рейсах были
        // успешно загружены.
    }

}
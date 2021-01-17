package app.service;

import app.domain.Flight;
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
import java.util.Optional;

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
        int expectedNumbOfFlights = 500;

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
        LocalDateTime departureTime1 = strToLocalDateTime("17/01/2021-03:00");
        LocalDateTime departureTime2 = strToLocalDateTime("10/01/2021-00:00");
        LocalDateTime departureTime3 = strToLocalDateTime("13/01/2021-03:20");
        int expectedNumbOfFilteredFl1 = 1;
        int expectedNumbOfFilteredFl2 = 2;
        int expectedNumbOfFilteredFl3 = 1;

        // When
        Optional<HashMap<String, Flight>> filteredFlights1 = service.getFilteredFlights(
                "Дюссельдорф", departureTime1, 5);
        int actualNumbOfFilteredFl1 = filteredFlights1.get().size();
//        -------------------------------------------------------------
        Optional<HashMap<String, Flight>> filteredFlights2 = service.getFilteredFlights(
                "Эссен", departureTime2, 2);
        int actualNumbOfFilteredFl2 = filteredFlights2.get().size();
//        -------------------------------------------------------------
        Optional<HashMap<String, Flight>> filteredFlights3 = service.getFilteredFlights(
                "Милан", departureTime3, 40);
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
        String expectedDestination1 = "Ереван";
        int expectedNumbOfFreeSeats1 = 3;

        // When
        Flight flight1 = service.getFlightById("FL993D");
        String actualDestination1 = flight1.getDestinationPlace();
        int actualNumbOfFreeSeats1 = flight1.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedDestination1, actualDestination1);
        assertEquals(expectedNumbOfFreeSeats1, actualNumbOfFreeSeats1);

//        --------------------------------------------------------------
        //Given
        String expectedDestination2 = "Ивано-Франковск";
        int expectedNumbOfFreeSeats2 = 9;

        // When
        Flight flight2 = service.getFlightById("FL389Z");
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
        HashMap<String, Flight> flights4Next24Hours1;
        int expectedNumbOfFlights1 = 22;

        // When
        flights4Next24Hours1 = service.getFlightsForNext24Hours(startingDateTime1).get();
        int actualNumbOfFlights1 = flights4Next24Hours1.size();

        // Then
        assertEquals(expectedNumbOfFlights1, actualNumbOfFlights1);
//        ---------------------------------------------------------------------
        // Given
        LocalDateTime startingDateTime2 = strToLocalDateTime("13/01/2021-00:00");
        HashMap<String, Flight> flights4Next24Hours2;
        int expectedNumbOfFlights2 = 29;

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
        Flight flight = service.getFlightById("FL884P");
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
        Flight flight = service.getFlightById("FL349J");
        int initialNumbOfFreeSeats = flight.getNumberOfFreeSeats();

        // When
        flight.cancelReservation4Flight(8);

        // Then
        assertEquals(initialNumbOfFreeSeats + 8, flight.getNumberOfFreeSeats());
    }

    @Test
    void printFlightsToConsole() {
        //Given
        LocalDateTime departureTime = strToLocalDateTime("17/01/2021-03:00");
        String expectedOutput = "<<< АЭРОПОРТ, ГОРОД КИЕВ >>>16/01/2021-20:27*********************" +
                "****************************************Номер рейса: FL891R  |" +
                "  Пункт назначения: Анкара           |  Время вылета: 17/01/2021-03:50  |" +
                "  Время прибытия: 17/01/2021-05:55  |  Количество свободных мест: 35  |" +
                "Номер рейса: FL438D  |  Пункт назначения: Анкара           |" +
                "  Время вылета: 17/01/2021-05:30  |  Время прибытия: 17/01/2021-07:35  |" +
                "  Количество свободных мест: 07  |";

        Optional<HashMap<String, Flight>> filteredFlights = service.getFilteredFlights(
                "Анкара", departureTime, 5);

        // When
        service.printFlightsToConsole(filteredFlights);

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
        String cleanOutputFinal = cleanOutput.substring(50);
        String expectedOutputFinal = expectedOutput.substring(50);

        // Then
        Assertions.assertEquals(expectedOutputFinal, cleanOutputFinal);
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
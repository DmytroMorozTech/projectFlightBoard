package app.domain;

import app.service.flightsGenerator.ShortFlightData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {
    Flight testFlight1, testFlight2;

    private static long strToDateTimeInMillis(String dateAsString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateAsString, dtf);
        long dateTimeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return dateTimeInMillis;
    }

    @BeforeEach
    void setUp() {
        ShortFlightData flightData1 = new ShortFlightData(
                "Киев", "KBP",
                "Амстердам", "AMS", 185);
        long departureTime1 = strToDateTimeInMillis("17/01/2021-02:00");
        long arrivalTime1 = strToDateTimeInMillis("17/01/2021-05:05");
        String idOfFlight1 = "FL649K";
        int numbOfSeats1 = 36;
        testFlight1 = new Flight(flightData1, idOfFlight1,
                                 departureTime1, arrivalTime1, numbOfSeats1);
//        ----------------------------------------------------------------

        ShortFlightData flightData2 = new ShortFlightData(
                "Киев", "KBP",
                "Дюссельдорф", "DUS", 200);
        long departureTime2 = strToDateTimeInMillis("15/01/2021-19:40");
        long arrivalTime2 = strToDateTimeInMillis("15/01/2021-23:00");
        String idOfFlight2 = "FL448P";
        int numbOfSeats2 = 49;
        testFlight2 = new Flight(flightData2, idOfFlight2,
                                 departureTime2, arrivalTime2, numbOfSeats2);
    }

    @Test
    void applyReservation4Flight() {
        // Given
        System.out.println(">>> Running TEST of method applyReservation4Flight(), class Flight.");
        int expectedNumbOfSeats = testFlight1.getNumberOfFreeSeats() - 5;

        // When
        testFlight1.applyReservation4Flight(5);
        int actualNumbOfSeats = testFlight1.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedNumbOfSeats, actualNumbOfSeats);

    }

    @Test
    void cancelReservation4Flight() {
        // Given
        System.out.println(">>> Running TEST of method cancelReservation4Flight(), class Flight.");
        int expectedNumbOfSeats = testFlight1.getNumberOfFreeSeats() + 4;

        // When
        testFlight1.cancelReservation4Flight(4);
        int actualNumbOfSeats = testFlight1.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedNumbOfSeats, actualNumbOfSeats);
    }

    @Test
    void getDeparturePlace() {
        // Given
        System.out.println(">>> Running TEST of method getDeparturePlace(), class Flight.");
        String expectedOutput = "Киев";

        // When
        String actualOutput = testFlight1.getDeparturePlace();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getDestinationPlace() {
        // Given
        System.out.println(">>> Running TEST of method getDestinationPlace(), class Flight.");
        String expectedOutput = "Амстердам";

        // When
        String actualOutput = testFlight1.getDestinationPlace();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getIdOfFlight() {
        // Given
        System.out.println(">>> Running TEST of method getIdOfFlight(), class Flight.");
        String expectedOutput = "FL649K";

        // When
        String actualOutput = testFlight1.getIdOfFlight();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getDepartureTime() {
        // Given
        System.out.println(">>> Running TEST of method getDepartureTime(), class Flight.");
        long expectedOutput = strToDateTimeInMillis("17/01/2021-02:00");

        // When
        long actualOutput = testFlight1.getDepartureTime();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getArrivalTime() {
        // Given
        System.out.println(">>> Running TEST of method getArrivalTime(), class Flight.");
        long expectedOutput = strToDateTimeInMillis("17/01/2021-05:05");

        // When
        long actualOutput = testFlight1.getArrivalTime();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void getNumberOfFreeSeats() {
        // Given
        System.out.println(">>> Running TEST of method getNumberOfFreeSeats(), class Flight.");
        int expectedOutput = 36;

        // When
        int actualOutput = testFlight1.getNumberOfFreeSeats();

        // Then
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void testEquals() {
        System.out.println(">>> Running TEST of method equals(), class Flight.");
        assertTrue(testFlight1.equals(testFlight1));
        assertTrue(testFlight2.equals(testFlight2));

        assertFalse(testFlight1.equals(testFlight2));

        // Если мы передадим неправильное значение в метод equals, то должны будем получить false.
        assertFalse(testFlight1.equals(null));
        assertFalse(testFlight1.equals("String value"));
        assertFalse(testFlight1.equals(448));
    }

    @Test
    void testHashCode() {
        System.out.println(">>> Running TEST of method hashCode(), class Flight.");

        assertTrue(testFlight1.hashCode() == testFlight1.hashCode());
        assertTrue(testFlight2.hashCode() == testFlight2.hashCode());

        assertFalse(testFlight1.hashCode() == testFlight2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        System.out.println(">>> Running TEST of method toString(), class Flight.");
        String expectedOutput1 = "Flight { departurePlace='Киев'  |  destinationPlace='Амстердам'\t|" +
                "  idOfFlight=FL649K  |  departureTime=17/01/2021-02:00  |  arrivalTime=17/01/2021-05:05  |" +
                "  numberOfFreeSeats=36\t}";

        String expectedOutput2 = "Flight { departurePlace='Киев'  |  destinationPlace='Дюссельдорф'\t|" +
                "  idOfFlight=FL448P  |  departureTime=15/01/2021-19:40  |  arrivalTime=15/01/2021-23:00  |" +
                "  numberOfFreeSeats=49\t}";

        // When
        String actualOutput1 = testFlight1.toString();
        String actualOutput2 = testFlight2.toString();

        // Then
        assertEquals(expectedOutput1, actualOutput1);
        assertEquals(expectedOutput2, actualOutput2);
    }

    @Test
    void prettyFormat() {
        System.out.println(">>> Running TEST of method prettyFormat(), class Flight.");
        String expectedOutput1 = "Номер рейса: FL649K  |  ОТКУДА: Киев,KBP               |  КУДА:" +
                " Амстердам,AMS          |  Вылет: 17/01/2021-02:00  |  Прибытие: 17/01/2021-05:05" +
                "  |  Свободные места:36  |";

        String expectedOutput2 = "Номер рейса: FL448P  |  ОТКУДА: Киев,KBP               |  КУДА:" +
                " Дюссельдорф,DUS        |  Вылет: 15/01/2021-19:40  |  Прибытие: 15/01/2021-23:00  " +
                "|  Свободные места:49  |";

        // When
        String actualOutput1 = testFlight1.prettyFormat();
        String actualOutput2 = testFlight2.prettyFormat();

        // Then
        assertEquals(expectedOutput1, actualOutput1);
        assertEquals(expectedOutput2, actualOutput2);
    }
}
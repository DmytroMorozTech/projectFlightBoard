package app.service;

import app.domain.Booking;
import app.domain.FlightRoute;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

class BookingsServiceTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    BookingsService service;

//  Для тестирования было заранее подготовлено 2 бронирования:

//-----------------------------------------------------------------------------------------
//    Номер бронирования: BK318J  |  Login заказчика брони: Sergey
//    Пассажиры: Борис Джонсон, Майкл Джордан, Энтони Хопкинс
//    МАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:
//    Номер рейса: FL397H  |  ОТКУДА: Киев,KBP,                |  КУДА: Франкфурт-на-Майне,FRA |  Вылет: 20/01/2021-15:00  |  Прибытие: 20/01/2021-17:50  |  Свободные места: 32  |
//    Номер рейса: FL475W  |  ОТКУДА: Франкфурт-на-Майне,FRA,  |  КУДА: Монреаль,YUL           |  Вылет: 20/01/2021-20:40  |  Прибытие: 21/01/2021-04:40  |  Свободные места: 08  |
//-----------------------------------------------------------------------------------------
//    Номер бронирования: BK661G  |  Login заказчика брони: Dima
//    Пассажиры: Дмитрий Мороз, Сергей Романюк
//    МАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:
//    Номер рейса: FL305F  |  ОТКУДА: Киев,KBP,                |  КУДА: Милан,MIL              |  Вылет: 20/01/2021-19:50  |  Прибытие: 20/01/2021-22:50  |  Свободные места: 35  |
//    Номер рейса: FL129C  |  ОТКУДА: Милан,MIL,               |  КУДА: Нью-Йорк,JFK           |  Вылет: 21/01/2021-10:40  |  Прибытие: 21/01/2021-19:55  |  Свободные места: 46  |
//-----------------------------------------------------------------------------------------


    @BeforeEach
    void getDataForTesting() throws BookingOverflowException {
        System.setOut(new PrintStream(outputStreamCaptor));
        // Here we reassign the standard output stream to a new PrintStream with a
        // ByteArrayOutputStream.
//        --------------------------------------------

        service = new BookingsService();
        service.loadDataForTestingBooking();
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
//        As the standard output stream is a shared static resource used by other parts of the system,
//        we should take care of restoring it to its original state when our test terminates.
    }

    @Test
    void getBookingByItsId() {
        System.out.println(">>> Running TEST of method getBookingByItsId(), class BookingsService" +
                                   ".");
        // Given
        String expectedFlight1IDBK1 = "FL397H";
        String expectedFlight2IDBK2 = "FL129C";

        // When
        Booking actualBooking1 = service.getBookingByItsId("BK318J").get();
        String actualFlight1IDBK1 = actualBooking1.getFlightRoute().getFlight1().getIdOfFlight();

        Booking actualBooking2 = service.getBookingByItsId("BK661G").get();
        String actualFlight2IDBK2 = actualBooking2.getFlightRoute().getFlight2().getIdOfFlight();

        // Then
        Assertions.assertNotNull(actualBooking1);
        Assertions.assertNotNull(actualBooking2);
        Assertions.assertEquals(expectedFlight1IDBK1, actualFlight1IDBK1);
        Assertions.assertEquals(expectedFlight2IDBK2, actualFlight2IDBK2);
    }

    @Test
    void getAllUserBookings() {
        System.out.println(">>> Running TEST of method getAllUserBookings(), class " +
                                   "BookingsService.");
        //Given
        int expectedNumbOfBKsSergey = 2;
        HashMap<String, Booking> allBookingsSergey = new HashMap<>();
        int expectedNumbOfBKsDima = 4;
        HashMap<String, Booking> allBookingsDima = new HashMap<>();

        // When
        allBookingsSergey = service.getAllUserBookings("Sergey", "Сергей", "Романюк").get();
        allBookingsDima = service.getAllUserBookings("Dima", "Дмитрий", "Мороз").get();

        // Then
        Assertions.assertEquals(expectedNumbOfBKsSergey, allBookingsSergey.size());
        Assertions.assertEquals(expectedNumbOfBKsDima, allBookingsDima.size());
    }

    @Test
    void deleteBookingByItsId() {
        System.out.println(">>> Running TEST of method deleteBookingByItsId(), class " +
                                   "BookingsService.");
        // When
        boolean resultOfValidDeletion = service.deleteBookingByItsId("BK318J");
        boolean resultOfInvalidDeletion = service.deleteBookingByItsId("AAAAAA");

        // Then
        Assertions.assertTrue(resultOfValidDeletion);
        Assertions.assertFalse(resultOfInvalidDeletion);
    }

    @Test
    void createBooking() {
        System.out.println(">>> Running TEST of method createBooking(), class BookingsService.");
        // Given
        Booking testBooking = service.getBookingByItsId("BK661G").get();
        FlightRoute testFlightRoute = testBooking.getFlightRoute();

        List<Passenger> testPassengerList = new ArrayList<>();
        testPassengerList.add(new Passenger("Николас", "Науменко"));
        testPassengerList.add(new Passenger("Василий", "Петров"));

        // When
        Booking newBooking = new Booking("Nikolla", testFlightRoute, testPassengerList);
        service.createBooking(newBooking);
        Collection<Booking> allCurrentBookingsOfNikolla =
                service.getAllUserBookings("Nikolla", "Николас", "Науменко").get().values();

        // Then
        Assertions.assertTrue(allCurrentBookingsOfNikolla.contains(newBooking));
    }

    @Test
    void getPassengersDataFromUser() {
        System.out.println(">>> Running TEST of method getPassengersDataFromUser(), class BookingsService.");
        // Given
        List<Passenger> passengersList = new ArrayList<>();
        String passengerName1 = "Анна";
        String passengerSurname1 = "Зубрицкая";
        String passengerName2 = "Сергей";
        String passengerSurname2 = "Романюк";
        Passenger newPassenger1 = new Passenger(passengerName1, passengerSurname1);
        Passenger newPassenger2 = new Passenger(passengerName2, passengerSurname2);
        passengersList.add(newPassenger1);
        passengersList.add(newPassenger2);
        Assertions.assertEquals(2, passengersList.size());
    }

    @Test
    void printBookingsToConsole() {
        // Given
        String expectedOutput = "*************************************************************СПИСОК ЗАБРОНИРОВАННЫХ " +
                "РЕЙСОВ:~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Номер бронирования: BK318J  |" +
                "  Login заказчика брони: SergeyПассажиры: Борис Джонсон, Майкл Джордан," +
                " Энтони ХопкинсМАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:Номер рейса: FL397H  |  ОТКУДА: Киев,KBP," +
                "                |  КУДА: Франкфурт-на-Майне,FRA |  Вылет: 20/01/2021-15:00  |" +
                "  Прибытие: 20/01/2021-17:50  |  Свободные места: 32  |Номер рейса: FL475W  |" +
                "  ОТКУДА: Франкфурт-на-Майне,FRA,  |  КУДА: Монреаль,YUL           |" +
                "  Вылет: 20/01/2021-20:40  |  Прибытие: 21/01/2021-04:40  |" +
                "  Свободные места: 08  |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
                "Номер бронирования: BK661G  |  Login заказчика брони: DimaПассажиры: Дмитрий Мороз," +
                " Сергей РоманюкМАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:Номер рейса: FL305F  |  ОТКУДА: Киев,KBP," +
                "                |  КУДА: Милан,MIL              |  Вылет: 20/01/2021-19:50  |" +
                "  Прибытие: 20/01/2021-22:50  |  Свободные места: 35  |Номер рейса: FL129C  |" +
                "  ОТКУДА: Милан,MIL,               |  КУДА: Нью-Йорк,JFK           |" +
                "  Вылет: 21/01/2021-10:40  |  Прибытие: 21/01/2021-19:55  |  Свободные места: 46  |" +
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";


        Optional<HashMap<String, Booking>> allBookingsSergey =
                service.getAllUserBookings("Sergey", "Сергей", "Романюк");

        // When
        service.printBookingsToConsole(allBookingsSergey);
        final String cleanOutput =
                outputStreamCaptor
                        .toString()
                        .trim()
                        .replaceAll("\n", "")
                        .replaceAll("\r", "")
                        .replaceAll("\t", "");

        // поскольку в выводе метода printBookingsToConsole присутствуют поточные дата и время
        // (на момент выполнения метода), то мы вынуждены здесь обрезать эту текстовую часть,
        // чтобы иметь возможность сравнивать оставшуюся часть текста.
        String cleanOutputFinal = cleanOutput.substring(30);

        // Then
        Assertions.assertEquals(expectedOutput, cleanOutputFinal);
    }
}
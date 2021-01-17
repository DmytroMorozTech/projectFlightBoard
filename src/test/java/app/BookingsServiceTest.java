package app;

import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.service.BookingsService;
import app.service.fileSystemService.FileSystemService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

class BookingsServiceTest {
    BookingsService bookingService;
    private PrintStream old;

    // Для тестирования было заранее подготовлено 4 бронирования:

    //   ID=QQ876P Номер рейса: FL713A  |  Номер бронирования: BK335O  |  Пункт назначения: Вена
    //   Login заказчика бронирования: Aleksey
    //   Пассажиры: Катерина Кухар, Николас Науменко

    //   ID=FS541T Номер рейса: FL802Z  |  Номер бронирования: BK104W  |  Пункт назначения: Днепр
    //   Login заказчика бронирования: Anton
    //   Пассажиры: Петр Порошенко, Виктор Романюк

    //   ID=UY654P Номер рейса: FL438Z  |  Номер бронирования: BK403K  |  Пункт назначения: Лиссабон
    //   Login заказчика бронирования: Sergey
    //   Пассажиры: Сергей Оксимчук, Валентина Варшавская

    //   ID=DF321F Номер рейса: FL990N  |  Номер бронирования: BK113M  |  Пункт назначения: Лион
    //   Login заказчика бронирования: Sergey
    //   Пассажиры: Анна Зубрицкая, Валентина Романюк

    @BeforeEach
    void getDataForTesting() throws BookingOverflowException {
        bookingService = new BookingsService();
        bookingService.loadDataForTestingBooking();
    }

    @Test
    void getBookingByItsId() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting getAllUserBookings():");
        Booking gotBooking1 = bookingService.getBookingByItsId("DF321F");
        Booking gotBooking3 = bookingService.getBookingByItsId("FS541T");
        Assertions.assertNotNull(gotBooking1);
        Assertions.assertNotNull(gotBooking3);
        System.out.println("All are OK!");
    }

    @Test
    void getAllUserBookings() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting getAllUserBookings():");
        String name1 = "Сергей";
        String surName1 = "Романюк";
        String userLogin1 = "Sergey";
        HashMap<String, Booking> filteredUserBookings1 = bookingService.getAllUserBookings(userLogin1, name1, surName1).get();

        // напечатаем
//        Iterator<Map.Entry<String, Booking>> entries = filteredUserBookings1.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, Booking> entry = entries.next();
//            System.out.println("ID=" + entry.getKey() + " " + entry.getValue().prettyFormat());
//        }
        Assertions.assertEquals(2, filteredUserBookings1.size());

        String name2 = "Антон";
        String surName2 = "Озирский";
        String userLogin2 = "Anton";
        HashMap<String, Booking> filteredUserBookings2 = bookingService.getAllUserBookings(userLogin2, name2, surName2).get();
        Assertions.assertEquals(1, filteredUserBookings2.size());
        System.out.println("All are OK!");
    }

    @Test
    void deleteBookingByItsId() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting deleteBookingByItsId():");
        boolean bookingWasDeletedCorrect1 = bookingService.deleteBookingByItsId("DF321F");
        Assertions.assertTrue(bookingWasDeletedCorrect1);
        boolean bookingWasDeletedCorrect2 = bookingService.deleteBookingByItsId("AAAAAA");
        Assertions.assertFalse(bookingWasDeletedCorrect2);
        System.out.println("All are OK!");
    }

    @Test
    void createBooking() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting createBooking():");
        //подготовим данные для новой брони полета
        List<Passenger> list5 = new ArrayList<>();
        Passenger passenger8 = new Passenger("Николас", "Науменко");
        list5.add(passenger8);
        Booking newBooking = new Booking("Aleksey", "FL713A", list5, "Вена");
        bookingService.createBooking(newBooking);
        HashMap<String, Booking> checkIfNewBooking = bookingService.getAllUserBookings("Aleksey", "Николас", "Науменко").get();
        String newBookingId = checkIfNewBooking.entrySet().iterator().next().getKey();
        Booking newBookingValue = checkIfNewBooking.entrySet().iterator().next().getValue();
        Assert.assertEquals(bookingService.getBookingByItsId(newBookingId), newBookingValue);
        System.out.println("All are OK!");
    }

    @Test
    void getPassengersDataFromUser() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting createBooking():");
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
        System.out.println("All are OK!");
    }

    @Test
    void printBookingsToConsole() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting createBooking():");
        Optional<HashMap<String, Booking>> gotSomeBooking = bookingService.getAllUserBookings("Aleksey", "Николас", "Науменко");
        bookingService.printBookingsToConsole(gotSomeBooking);
        System.out.println("All are OK!");
    }
}
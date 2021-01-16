package app;

import app.controller.BookingsController;
import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.service.validationService.ValidationService.readString;

class BookingsServiceTest {
    private static BookingsController bookingsController;

    private Booking booking1, booking2, booking3, booking4;
    private Passenger passenger1, passenger2, passenger3, passenger4, passenger5, passenger6, passenger7;
    public static HashMap<String, Booking> bookingsTest = new HashMap<>();
    String nameOfFile = "bookingsTest.bin";

    // база бронирований будет доступна перед запуском каждого теста
    @BeforeEach
    void createBooking() throws BookingOverflowException {
        bookingsController = new BookingsController();

        // создадим для тестированний несколько бронирований
        List<Passenger> list1 = new ArrayList<>();
        List<Passenger> list2 = new ArrayList<>();
        List<Passenger> list3 = new ArrayList<>();
        List<Passenger> list4 = new ArrayList<>();
        passenger1 = new Passenger("Анна", "Зубрицкая");
        passenger2 = new Passenger("Валентина", "Романюк");
        passenger3 = new Passenger("Петр", "Порошенко");
        passenger4 = new Passenger("Виктор", "Романюк");
        passenger5 = new Passenger("Сергей", "Оксимчук");
        passenger6 = new Passenger("Валентина", "Варшавская");
        passenger7 = new Passenger("Катерина", "Кухар");
        Passenger passenger8 = new Passenger("Николас", "Науменко");
        list1.add(passenger1);
        list1.add(passenger2);
        list2.add(passenger3);
        list2.add(passenger4);
        list3.add(passenger5);
        list3.add(passenger6);
        list4.add(passenger7);
        list4.add(passenger8);
        booking1 = new Booking("Sergey", "FL990N", list1, "Лион");
        booking2 = new Booking("Anton", "FL802Z", list2, "Днепр");
        booking3 = new Booking("Sergey", "FL438Z", list3, "Лиссабон");
        booking4 = new Booking("Aleksey", "FL713A", list4, "Вена");

        bookingsTest.put("DF321F", booking1);
        bookingsTest.put("FS541T", booking2);
        bookingsTest.put("UY654P", booking3);
        bookingsTest.put("QQ876P", booking4);

        // напечатаем созданные брони, чтобы убедиться, что они там есть
//        Iterator<Map.Entry<String, Booking>> entries = bookings.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, Booking> entry = entries.next();
//            System.out.println("ID=" + entry.getKey() + " " + entry.getValue().prettyFormat());
//        }
        Assertions.assertTrue(bookingsTest.containsKey("DF321F"));
        Assertions.assertTrue(bookingsTest.containsKey("FS541T"));
    }

    @Test
    void getBookingByItsId() {
        System.out.println("\nTesting getBookingByItsId():");
        Booking gotBooking1 = bookingsTest.get("DF321F");
        Assertions.assertEquals(booking1, gotBooking1);

        Booking gotBooking3 = bookingsTest.get("UY654P");
        Assertions.assertNotEquals(booking3, gotBooking1);
        Assertions.assertEquals(booking3, gotBooking3);
        System.out.println("All are OK!");
    }

    @Test
    void getAllUserBookings() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting getAllUserBookings():");
        String userLogin = "Sergey";
        Map<String, Booking> filteredMap =
                bookingsTest.entrySet().stream()
                        .filter(b -> b.getValue().getUserLogin().equals(userLogin)
                                | b.getValue().getPassengerList().contains(passenger1))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // юзер "Sergey" забронировал 2 рейса, давайте проверим.
        Assertions.assertEquals(2, filteredMap.size());

        // юзер "Anton" забронировал 1 рейс, давайте проверим.
        String userLogin2 = "Anton";
        Map<String, Booking> filteredMap2 =
                bookingsTest.entrySet().stream()
                        .filter(b -> b.getValue().getUserLogin().equals(userLogin2)
                                | b.getValue().getPassengerList().contains(passenger3))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Assertions.assertEquals(1, filteredMap2.size());
        System.out.println("All are OK!");

    }

    @Test
    void deleteBookingByItsId() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting deleteBookingByItsId():");
        Assertions.assertEquals(4, bookingsTest.size());
        bookingsTest.remove("DF321F");
        Assertions.assertEquals(3, bookingsTest.size());
        bookingsTest.remove("QQ876P");
        Assertions.assertEquals(2, bookingsTest.size());

        //печатаем, чтобы убедиться, что юзеры с указанными "ID" удаляются
//        Iterator<Map.Entry<String, Booking>> entries = bookingsTest.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry<String, Booking> entry = entries.next();
//            System.out.println("ID=" + entry.getKey() + " " + entry.getValue().prettyFormat());
//        }
        System.out.println("All are OK!");

    }

    @Test
    void saveDataToFile() throws BookingOverflowException {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting saveDataToFile():");
        try {
            FileSystemService fs = new FileSystemService();
            fs.saveDataToFile(nameOfFile, bookingsTest);
        } catch (IOException e) {
            throw new BookingOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                    " на жесткий диск компьютера.");
        }
        int bookingsTestLength = bookingsTest.size();
        Assertions.assertEquals(4, bookingsTestLength);
        Assertions.assertNotEquals(5, bookingsTestLength);
        Booking gotBooking1 = bookingsTest.get("DF321F");
        Assertions.assertEquals(booking1, gotBooking1);
        System.out.println("All are OK!");

    }

    @Test
    void loadData() throws BookingOverflowException {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting loadData():");
        HashMap<String, Booking> bookingsTesting = new HashMap<>();
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                bookingsTesting = (HashMap<String, Booking>) dataFromFS;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new BookingOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                    " с жесткого диска.");
        }
        int bookingsTestLength = bookingsTesting.size();
        Assertions.assertEquals(4, bookingsTestLength);
        Assertions.assertNotEquals(5, bookingsTestLength);
        System.out.println("All are OK!");
    }


    @Test
    void getPassengersDataFromUser() {
        System.out.println("*******************************************************************");
        System.out.println("\nTesting loadData():");
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
    }
}
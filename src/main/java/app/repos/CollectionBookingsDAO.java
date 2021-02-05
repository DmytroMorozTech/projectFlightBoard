package app.repos;

import app.contract.BookingsDAO;
import app.contract.CanWorkWithFileSystem;
import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import static app.service.validationService.ValidationService.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionBookingsDAO implements BookingsDAO, CanWorkWithFileSystem {
    private HashMap<String, Booking> bookings = new HashMap<>();
    private final String nameOfFile = "bookings.bin";

    @Override
    public Optional<Booking> getBookingByItsId(String idOfBooking) {
        return Optional.ofNullable(bookings.get(idOfBooking));
    }

    @Override
    public Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                                 String surname) {
        Passenger userAsPassenger = new Passenger(name, surname);
        // нам нужен этот объект, чтобы перепроверить, в каком рейсе наш пользователь, который
        // осуществлял бронирование рейса, мог выступать в роли пассажира.

        Map<String, Booking> filteredMap =
                bookings.entrySet().stream()   //Stream<Map.Entry<String,Booking>>
                        .filter(b -> b.getValue().getUserLogin().equals(userLogin)
                                | b.getValue().getPassengerList().contains(userAsPassenger))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        HashMap<String, Booking> filteredHashMap = (HashMap<String, Booking>) filteredMap;

        return Optional.of(filteredHashMap);
    }

    @Override
    public boolean deleteBookingByItsId(String idOfBooking) {
        if (!bookings.containsKey(idOfBooking)) {
            System.out.println("Не удалось найти бронь билетов с указанным ID брони. Ошибка " +
                    "удаления брони.");
            return false;
        }

        bookings.remove(idOfBooking);
        LoggerService.info("Бронь под номером " + idOfBooking + " была удалена.");
        System.out.println("Бронь под номером " + idOfBooking + " была удалена.");
        return !bookings.containsKey(idOfBooking);
    }

    @Override
    public void createBooking(Booking booking) {
        bookings.put(booking.getIdOfBooking(), booking);
    }

    @Override
    public List<Passenger> getPassengersDataFromUser(int numbOfPassengers) {
        List<Passenger> passengersList = new ArrayList<>();
        Passenger newPassenger;

        for (int i = 1; i <= numbOfPassengers; i++) {
            System.out.printf("Пожалуйста, введите данные о пассажире №%d :\n", i);
            String passengerName = readString("Имя: ");
            String passengerSurname = readString("Фамилия: ");
            newPassenger = new Passenger(passengerName, passengerSurname);
            passengersList.add(newPassenger);
        }
        return passengersList;
    }

    @Override
    public void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        String formattedDateTime = dtf.format(currentDateTime);

        System.out.println("ТЕКУЩЕЕ ВРЕМЯ:");
        System.out.println(formattedDateTime);
        System.out.println("*************************************************************");

        if (bookingsOptional.isEmpty() | bookingsOptional.get().size() == 0) {
            System.out.println("По Вашему запросу не было найдено забронированных рейсов.");
        } else {
            System.out.println("СПИСОК ЗАБРОНИРОВАННЫХ РЕЙСОВ:");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            Collection<Booking> foundBookings = bookingsOptional.get().values();
            for (Booking b : foundBookings) {
                System.out.println(b.prettyFormat());
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            }
        }
    }

    @Override
    public void loadData() throws BookingOverflowException {
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                bookings = (HashMap<String, Booking>) dataFromFS;
            }
            LoggerService.info("Загрузка файла " + nameOfFile + " с жесткого диска.");
        } catch (IOException | ClassNotFoundException e) {
            throw new BookingOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                    " с жесткого диска.");
        }
    }

    @Override
    public boolean saveDataToFile() throws BookingOverflowException {
        try {
            FileSystemService fs = new FileSystemService();
            new PrintWriter(nameOfFile).close(); // очищаем содержимое файла.
            fs.saveDataToFile(nameOfFile, bookings);
            LoggerService.info("Сохранение данных на жесткий диск в файл " + nameOfFile);
            return true;
        } catch (IOException e) {
            throw new BookingOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                    " на жесткий диск компьютера.");
        }
    }

    public void loadDataForTestingBooking() throws BookingOverflowException {
        String fileName = "bookingsTest.bin";
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(fileName);
            if (dataFromFS instanceof HashMap) {
                bookings = (HashMap<String, Booking>) dataFromFS;
            }
            LoggerService.info("Загрузка файла " + fileName + " с жесткого диска. С целью " +
                    "тестирования BookingService.");
        } catch (IOException | ClassNotFoundException e) {
            throw new BookingOverflowException("Возникла ОШИБКА при чтении файла " + fileName +
                    " с жесткого диска.");
        }
    }

}

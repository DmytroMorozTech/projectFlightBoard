package app.service;

import app.contract.BookingsDAO;
import app.contract.CanWorkWithFileSystem;
import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.repos.CollectionBookingsDAO;
import app.service.loggerService.LoggerService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BookingsService implements BookingsDAO, CanWorkWithFileSystem {
    private CollectionBookingsDAO bookingsDAO;

    public BookingsService() {
        this.bookingsDAO = new CollectionBookingsDAO();
    }

    @Override
    public Optional<Booking> getBookingByItsId(String idOfBooking) {
        LoggerService.info("Получение бронирования по его номеру (ID).");
        return bookingsDAO.getBookingByItsId(idOfBooking);
    }

    @Override
    public Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                                 String surname) {
        LoggerService.info("Получение всех бронирований конкретного пользователя.");
        return bookingsDAO.getAllUserBookings(userLogin, name, surname);
    }

    @Override
    public boolean deleteBookingByItsId(String idOfBooking) {
        boolean wasDeleted = bookingsDAO.deleteBookingByItsId(idOfBooking);
        if (!wasDeleted) {
            LoggerService.error("Не удалось найти бронь билетов с указанным ID брони. Ошибка " +
                    "удаления брони.");
            return false;
        }

        LoggerService.info("Удаление брони рейса по id.");
        return true;
    }

    @Override
    public void createBooking(Booking booking) {
        LoggerService.info("Бронирование билетов на рейс.");
        bookingsDAO.createBooking(booking);
    }

    @Override
    public List<Passenger> getPassengersDataFromUser(int numbOfPassengers) {
        LoggerService.info("Получение от пользователя данных о пассажирах рейса.");
        return bookingsDAO.getPassengersDataFromUser(numbOfPassengers);
    }

    @Override
    public void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional) {
        LoggerService.info("Вывод списка забронированных рейсов в консоль.");
        bookingsDAO.printBookingsToConsole(bookingsOptional);
    }

    @Override
    public void loadData() {
        try {
            bookingsDAO.loadData();
        } catch (BookingOverflowException ex) {
            LoggerService.error("BookingOverflowException: " + ex.getMessage());
        }
    }

    @Override
    public boolean saveDataToFile() {
        try {
            return bookingsDAO.saveDataToFile();
        } catch (BookingOverflowException ex) {
            LoggerService.error("BookingOverflowException: " + ex.getMessage());
            return false;
        }
    }

    public void loadDataForTestingBooking() throws BookingOverflowException {
        try {
            bookingsDAO.loadDataForTestingBooking();
        } catch (BookingOverflowException ex) {
            LoggerService.error("BookingOverflowException: " + ex.getMessage());
        }
    }
}
package app.controller;

import app.contract.BookingsDAO;
import app.contract.CanWorkWithFileSystem;
import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.BookingsService;
import app.service.UsersService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BookingsController implements BookingsDAO, CanWorkWithFileSystem {
    private BookingsService bookingsService;

    public BookingsController() {
        this.bookingsService = new BookingsService();
    }

    @Override
    public Booking getBookingByItsId(String idOfBooking) {
        return bookingsService.getBookingByItsId(idOfBooking);
    }

    @Override
    public Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                                 String surname) {
        return bookingsService.getAllUserBookings(userLogin,name,surname);
    }

    @Override
    public boolean deleteBookingByItsId(String idOfBooking) {
        return bookingsService.deleteBookingByItsId(idOfBooking);
    }

    @Override
    public boolean deleteBookingByObj(Booking booking) {
        return bookingsService.deleteBookingByObj(booking);
    }

    public void createBooking(Booking booking) {
        bookingsService.createBooking(booking);
    }

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        bookingsService.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        return bookingsService.saveDataToFile();
    }

    @Override
    public List<Passenger> getPassengersDataFromUser(int numbOfPassengers) {
        return bookingsService.getPassengersDataFromUser(numbOfPassengers);
    }

    @Override
    public void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional) {
        bookingsService.printBookingsToConsole(bookingsOptional);
    }
}

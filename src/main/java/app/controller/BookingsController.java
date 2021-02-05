package app.controller;

import app.contract.BookingsDAO;
import app.contract.CanWorkWithFileSystem;
import app.domain.Booking;
import app.domain.Passenger;
import app.service.BookingsService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BookingsController implements BookingsDAO, CanWorkWithFileSystem {
    private BookingsService bookingsService;

    public BookingsController() {
        this.bookingsService = new BookingsService();
    }

    @Override
    public Optional<Booking> getBookingByItsId(String idOfBooking) {
        return bookingsService.getBookingByItsId(idOfBooking);
    }

    @Override
    public Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                                 String surname) {
        return bookingsService.getAllUserBookings(userLogin, name, surname);
    }

    @Override
    public boolean deleteBookingByItsId(String idOfBooking) {
        return bookingsService.deleteBookingByItsId(idOfBooking);
    }

    public void createBooking(Booking booking) {
        bookingsService.createBooking(booking);
    }

    @Override
    public List<Passenger> getPassengersDataFromUser(int numbOfPassengers) {
        return bookingsService.getPassengersDataFromUser(numbOfPassengers);
    }

    @Override
    public void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional) {
        bookingsService.printBookingsToConsole(bookingsOptional);
    }

    @Override
    public void loadData() {
        bookingsService.loadData();
    }

    @Override
    public boolean saveDataToFile() {
        return bookingsService.saveDataToFile();
    }
}

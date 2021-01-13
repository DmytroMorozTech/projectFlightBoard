package app.service;

import app.contract.BookingsDAO;
import app.contract.CanWorkWithFileSystem;
import app.domain.Booking;
import app.domain.Passenger;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.repos.CollectionBookingsDAO;
import app.repos.CollectionFlightsDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BookingsService implements BookingsDAO, CanWorkWithFileSystem {
    private CollectionBookingsDAO bookingsDAO;

    public BookingsService() {
        this.bookingsDAO = new CollectionBookingsDAO();
    }

    @Override
    public Booking getBookingByItsId(String idOfBooking) {
        return bookingsDAO.getBookingByItsId(idOfBooking);
    }

    @Override
    public Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                                 String surname) {
        return bookingsDAO.getAllUserBookings(userLogin,name,surname);
    }

    @Override
    public boolean deleteBookingByItsId(String idOfBooking) {
        return bookingsDAO.deleteBookingByItsId(idOfBooking);
    }

    @Override
    public boolean deleteBookingByObj(Booking booking) {
        return bookingsDAO.deleteBookingByObj(booking);
    }

    @Override
    public void createBooking(Booking booking) {
        bookingsDAO.createBooking(booking);
    }

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        bookingsDAO.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        return bookingsDAO.saveDataToFile();
    }

    @Override
    public List<Passenger> getPassengersDataFromUser(int numbOfPassengers) {
        return bookingsDAO.getPassengersDataFromUser(numbOfPassengers);
    }

    @Override
    public void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional) {
        bookingsDAO.printBookingsToConsole(bookingsOptional);
    }
}

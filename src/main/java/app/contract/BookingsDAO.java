package app.contract;

import app.domain.Booking;
import app.domain.Passenger;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface BookingsDAO {
    Optional<Booking> getBookingByItsId(String idOfBooking);

    Optional<HashMap<String, Booking>> getAllUserBookings(String userLogin, String name,
                                                          String surname);

    boolean deleteBookingByItsId(String idOfBooking);

    void createBooking(Booking booking);

    List<Passenger> getPassengersDataFromUser(int numbOfPassengers);

    void printBookingsToConsole(Optional<HashMap<String, Booking>> bookingsOptional);

}

package app.contract;

import app.domain.Booking;

public interface BookingsDAO {
    Booking getBookingByItsId(int idOfBooking);
}

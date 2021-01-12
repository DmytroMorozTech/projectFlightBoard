package app.contract;

import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;

import java.io.IOException;

public interface CanWorkWithFileSystem {
    void loadData() throws IOException, BookingOverflowException,
            FlightOverflowException, UsersOverflowException;

    boolean saveDataToFile() throws IOException, BookingOverflowException,
            FlightOverflowException, UsersOverflowException;

}

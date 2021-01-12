package app;

import app.controller.BookingsController;
import app.controller.FlightsController;
import app.controller.UsersController;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;

public class Main {
    public static FlightsController fc;
    public static BookingsController bc;
    public static UsersController uc;

    public static void main(String[] args) throws UsersOverflowException {
        fc = new FlightsController();
        bc = new BookingsController();
        uc = new UsersController();

        try {
            // делаем dependency injection:
            // через конструктор класса Console передаем внутрь
            // три заранее подготовленных контроллера.
            Console console = new Console(fc, bc, uc);
            console.main(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            System.out.println(cause);
        }
        catch (FlightOverflowException e) {
            e.printStackTrace();
        }
        catch (BookingOverflowException e) {
            e.printStackTrace();
        }
    }
}

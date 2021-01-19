package app.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class Booking implements Serializable {
    private String idOfBooking;
    private String userLogin; // login пользователя, который оформил бронирование рейса
    private List<Passenger> passengerList;
    private FlightRoute flightRoute;


    public Booking(String userLogin, FlightRoute flightRoute, List<Passenger> passengersList) {
        this.flightRoute = flightRoute;
        this.idOfBooking = generateIdOfBooking();
        this.userLogin = userLogin;
        this.passengerList = passengersList;
    }

    public Booking() {
    }

    private static int generateRandomInt(int minValue, int maxValue) {
        return (int) (Math.random() * (maxValue - minValue + 1) + minValue);
    }

    private static String generateIdOfBooking() {
        char randomCapitalCharacter = (char) (generateRandomInt(65, 90));
        String idOfBooking =
                "BK" + String.valueOf(generateRandomInt(100, 1000)) + randomCapitalCharacter;
        return idOfBooking;
    }
    // метод для генерации случайного буквенно-числового ID для брони рейса(Booking).

    private String convertListToStr(List<Passenger> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Passenger p : list) {
            String name = p.getName();
            String surname = p.getSurname();
            stringBuilder.append(name)
                         .append(" ")
                         .append(surname).append(", ");
        }
        String s = stringBuilder.toString();
        return s.substring(0, s.length() - 2);
    }

    public String getIdOfBooking() {
        return idOfBooking;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public FlightRoute getFlightRoute() {
        return flightRoute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(idOfBooking, booking.idOfBooking) && Objects.equals(userLogin, booking.userLogin) && Objects.equals(passengerList, booking.passengerList) && Objects.equals(flightRoute, booking.flightRoute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfBooking, userLogin, passengerList, flightRoute);
    }

    private static String getPrettyFormattedDate(long timeInUnixMillis) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeInUnixMillis),
                TimeZone.getDefault().toZoneId()
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        return dtf.format(dateTime);

        // Данный метод конвертирует входящее значение long timeInUnixMillis в объект LocalDateTime.
        // А после этого, используя DateTimeFormatter, конвертирует в читабельное строковое
        // представление данной даты с учетом указанного паттерна DateTimeFormatter.
    }

    private static String formatFreeSeats(int numbOfFreeSeats) {
        if (numbOfFreeSeats < 10) return "0" + String.valueOf(numbOfFreeSeats);
        return String.valueOf(numbOfFreeSeats);
    }

    private static String generateSpaces(String cityName) {
        int numbOfSpaces = 24 - cityName.length();
        String spaces = "";
        for (int i = 1; i < numbOfSpaces; i++) spaces += " ";
        return spaces;
    }


    private static String prettyFormatOneFlight(Flight f) {
        String departurePlaceInclAPT =
                f.getDeparturePlace() + "," + f.getCodeOfDepartureAPT();
        String destinationPlaceInclAPT =
                f.getDestinationPlace() + "," + f.getCodeOfDestinationAPT();

        String spacesDeparture = generateSpaces(departurePlaceInclAPT);
        String spacesDestination = generateSpaces(destinationPlaceInclAPT);

        return
                "Номер рейса: " + f.getIdOfFlight() + "  |  " +
                        "ОТКУДА: " + departurePlaceInclAPT + ", " + spacesDeparture + "|  " +
                        "КУДА: " + destinationPlaceInclAPT + spacesDestination + "|  " +
                        "Вылет: " + getPrettyFormattedDate(f.getDepartureTime()) + "  |  " +
                        "Прибытие: " + getPrettyFormattedDate(f.getArrivalTime()) + "  |  " +
                        "Свободные места: " + formatFreeSeats(f.getNumberOfFreeSeats()) + "  |\n";
    }

    public String prettyFormat() {
        String resultingStr = "Номер рейса: " + flightRoute.getFlight1().getIdOfFlight() + "  |  " +
                "Номер бронирования: " + idOfBooking + "  |  " +
                "Пункт назначения: " + flightRoute.getFlight1().getDestinationPlace() + "\n" +
                "Login заказчика брони: " + userLogin + "\n" +
                "Пассажиры: " + convertListToStr(passengerList) + "\n";

        if (flightRoute.isDirectFlight()) {
            resultingStr += "Данные о рейсе:\n";
            resultingStr += prettyFormatOneFlight(flightRoute.getFlight1()) + "\n";
            return resultingStr;
        }

        String resultingStr2 =
                "Номер бронирования: " + idOfBooking + "  |  " +
                        "Login заказчика брони: " + userLogin + "\n" +
                        "Пассажиры: " + convertListToStr(passengerList) + "\n";
        resultingStr2 += "МАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:\n" + prettyFormatOneFlight(flightRoute.getFlight1())
                + prettyFormatOneFlight(flightRoute.getFlight2());
        return resultingStr2;
    }

}

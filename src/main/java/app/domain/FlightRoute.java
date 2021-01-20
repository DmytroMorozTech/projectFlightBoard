package app.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class FlightRoute implements Serializable {
    private Flight flight1;
    private Flight flight2;
    boolean isDirectFlight;

    public FlightRoute(Flight flight1, Flight flight2) {
        this.flight1 = flight1;
        this.flight2 = flight2;
        this.isDirectFlight = false;
    }

    public FlightRoute(Flight flight1) {
        this.flight1 = flight1;
        this.isDirectFlight = true;
    }

    public String prettyFormat() {
        if (isDirectFlight) return prettyFormatOneFlight(flight1);
        return "МАРШРУТ С ОДНОЙ ПЕРЕСАДКОЙ:\n" + prettyFormatOneFlight(flight1) + prettyFormatOneFlight(flight2);

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

    public Flight getFlight1() {
        return flight1;
    }

    public Flight getFlight2() {
        return flight2;
    }

    public boolean isDirectFlight() {
        return isDirectFlight;
    }

    @Override
    public String toString() {
        return prettyFormat();
    }
}

package app.domain;

import app.service.flightsGenerator.ShortFlightData;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;

public class Flight implements Serializable {
    private String departurePlace;
    private String codeOfDepartureAPT;
    private String destinationPlace;
    private String codeOfDestinationAPT;
    private String idOfFlight;
    private long departureTime;
    private long arrivalTime;
    private int numberOfFreeSeats;


    public Flight(ShortFlightData flightData, String idOfFlight,
                  long departureTime, long arrivalTime, int numberOfFreeSeats) {
        this.departurePlace = flightData.getDeparturePlace();
        this.codeOfDepartureAPT = flightData.getCodeOfDepartureAPT();
        this.destinationPlace = flightData.getDestinationPlace();
        this.codeOfDestinationAPT = flightData.getCodeOfDestinationAPT();

        this.idOfFlight = idOfFlight;
        this.departureTime = departureTime; // Unix Millis Timestamp
        this.arrivalTime = arrivalTime;     // Unix Millis Timestamp
        this.numberOfFreeSeats = numberOfFreeSeats;
    }

    public Flight() {
    }

    public void applyReservation4Flight(int numbOfSeats) {
        numberOfFreeSeats = numberOfFreeSeats - numbOfSeats;
    }

    public void cancelReservation4Flight(int numbOfSeats) {
        numberOfFreeSeats = numberOfFreeSeats + numbOfSeats;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getDestinationPlace() {
        return destinationPlace;
    }

    public String getCodeOfDepartureAPT() {
        return codeOfDepartureAPT;
    }

    public String getCodeOfDestinationAPT() {
        return codeOfDestinationAPT;
    }

    public String getIdOfFlight() {
        return idOfFlight;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getNumberOfFreeSeats() {
        return numberOfFreeSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return idOfFlight == flight.idOfFlight && departureTime == flight.departureTime
                && arrivalTime == flight.arrivalTime && numberOfFreeSeats == flight.numberOfFreeSeats
                && Objects.equals(departurePlace, flight.departurePlace)
                && Objects.equals(destinationPlace, flight.destinationPlace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departurePlace, destinationPlace, idOfFlight,
                            departureTime, arrivalTime, numberOfFreeSeats);
    }

    @Override
    public String toString() {
        return String.format("Flight { departurePlace='%s'  |  destinationPlace='%s'\t|  " +
                                     "idOfFlight=%s  |  " + "departureTime=%s  |  " +
                                     "arrivalTime=%s  |  numberOfFreeSeats=%d\t}",
                             departurePlace, destinationPlace, idOfFlight,
                             getPrettyFormattedDate(departureTime),
                             getPrettyFormattedDate(arrivalTime), numberOfFreeSeats);
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

    public String prettyFormat() {
        String departurePlaceInclAPT =
                departurePlace + "," + codeOfDepartureAPT;
        String destinationPlaceInclAPT =
                destinationPlace + "," + codeOfDestinationAPT;

        String spacesDeparture = generateSpaces(departurePlaceInclAPT);
        String spacesDestination = generateSpaces(destinationPlaceInclAPT);

        return
                "Номер рейса: " + idOfFlight + "  |  " +
                        "ОТКУДА: " + departurePlaceInclAPT + spacesDeparture + "|  " +
                        "КУДА: " + destinationPlaceInclAPT + spacesDestination + "|  " +
                        "Вылет: " + getPrettyFormattedDate(departureTime) + "  |  " +
                        "Прибытие: " + getPrettyFormattedDate(arrivalTime) + "  |  " +
                        "Свободные места:" + formatFreeSeats(numberOfFreeSeats) + "  |";
    }

    private static String generateSpaces(String cityName) {
        int numbOfSpaces = 24 - cityName.length();
        String spaces = "";
        for (int i = 1; i < numbOfSpaces; i++) spaces += " ";
        return spaces;
    }

    private static String formatFreeSeats(int numbOfFreeSeats) {
        if (numbOfFreeSeats < 10) return "0" + String.valueOf(numbOfFreeSeats);
        return String.valueOf(numbOfFreeSeats);
    }

}

package app.domain;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;

public class Flight implements Serializable {
    private String departurePlace; // in our case it is always "Kyiv"
    private String destinationPlace;
    private int idOfFlight;
    private long departureTime;
    private long arrivalTime;
    private int numberOfFreeSeats;

    public Flight(String departurePlace, String destinationPlace, int idOfFlight,
                  long departureTime, long arrivalTime, int numberOfFreeSeats) {
        this.departurePlace = departurePlace;
        this.destinationPlace = destinationPlace;
        this.idOfFlight = idOfFlight;
        this.departureTime = departureTime; // Unix Millis Timestamp (number of Millis since 01.01.1970).
        this.arrivalTime = arrivalTime;     // Unix Millis Timestamp (number of Millis since 01.01.1970).
        this.numberOfFreeSeats = numberOfFreeSeats;
    }

    public Flight() {
    }

//    public static void main(String[] args) {
//        // Let's test how method getPrettyFormattedDate() works.
//        long l = LocalDateTime.now()
//                              .atZone(ZoneId.systemDefault())
//                              .toInstant().toEpochMilli();
//        System.out.println(getPrettyFormattedDate(l));
//    }

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

    public int getIdOfFlight() {
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
        return String.format("Flight{departurePlace='%s', destinationPlace='%s', idOfFlight=%d, " +
                                     "departureTime=%s," +
                                     " arrivalTime=%s, numberOfFreeSeats=%d}",
                             departurePlace, destinationPlace, idOfFlight,
                             getPrettyFormattedDate(departureTime),
                             getPrettyFormattedDate(arrivalTime), numberOfFreeSeats);
    }

    private static String getPrettyFormattedDate(long timeInUnixMillis) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeInUnixMillis),
                TimeZone.getDefault().toZoneId()
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return dtf.format(dateTime);

        // This method converts the incoming time (in Unix Millis) into a LocalDateTime object.
        // Then using this object it creates a String with easily readable representation of the
        // date and time (that was passed as an argument to this method).
    }

    public String prettyFormat() {
        return "__________________________________________\n" +
                "Departure from: " + departurePlace + "\n" +
                "Destination: " + destinationPlace + "\n" +
                "Departure time: " + getPrettyFormattedDate(departureTime) + "\n" +
                "Arrival time: " + getPrettyFormattedDate(arrivalTime) + "\n" +
                "Number of free seats: " + numberOfFreeSeats + "\n" +
                "ID of flight: " + idOfFlight + "\n" +
                "__________________________________________\n";
    }

}

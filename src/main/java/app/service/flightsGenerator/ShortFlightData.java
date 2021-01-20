package app.service.flightsGenerator;

public class ShortFlightData {
    private String departurePlace;
    private String codeOfDepartureAPT;
    private String destinationPlace;
    private String codeOfDestinationAPT;
    private int flightDurationMin;
    // APT stands for airport.
    // IATA (International Air Transport Association)

    public ShortFlightData(String departurePlace, String codeOfDepartureAPT,
                           String destinationPlace, String codeOfDestinationAPT,
                           int flightDurationMin) {
        this.departurePlace = departurePlace;
        this.codeOfDepartureAPT = codeOfDepartureAPT;
        this.destinationPlace = destinationPlace;
        this.codeOfDestinationAPT = codeOfDestinationAPT;
        this.flightDurationMin = flightDurationMin;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getCodeOfDepartureAPT() {
        return codeOfDepartureAPT;
    }

    public String getDestinationPlace() {
        return destinationPlace;
    }

    public String getCodeOfDestinationAPT() {
        return codeOfDestinationAPT;
    }

    public int getFlightDurationMin() {
        return flightDurationMin;
    }

}

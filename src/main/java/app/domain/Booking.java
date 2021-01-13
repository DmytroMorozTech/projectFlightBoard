package app.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Booking implements Serializable {
    private String idOfFlight;
    private String idOfBooking;
    private String userLogin; // login пользователя, который оформил бронирование рейса
    private List<Passenger> passengerList;
    private String destinationPlace;

    public Booking(String userLogin, String idOfFlight, List<Passenger> passengersList,
                   String destinationPlace) {
        this.idOfFlight = idOfFlight;
        this.idOfBooking = generateIdOfBooking();
        this.userLogin = userLogin;
        this.passengerList = passengersList;
        this.destinationPlace = destinationPlace;
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
    // метод для генерации рандомного буквенно-числового ID для брони рейса(Booking).


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

    public String prettyFormat() {
        return
                "Номер рейса: " + idOfFlight + "  |  " +
                        "Номер бронирования: " + idOfBooking + "  |  " +
                        "Пункт назначения: " + destinationPlace + "\n" +
                        "Login заказчика бронирования: " + userLogin + "\n" +
                        "Пассажиры: " + convertListToStr(passengerList) + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(idOfFlight, booking.idOfFlight)
                && Objects.equals(idOfBooking, booking.idOfBooking)
                && Objects.equals(userLogin, booking.userLogin)
                && Objects.equals(passengerList, booking.passengerList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfFlight, idOfBooking, userLogin, passengerList);
    }

    public String getIdOfFlight() {
        return idOfFlight;
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

//    public boolean checkIfPersonIsAPassenger(String name, String surname) {
//        String pas = passengerList.loString();
//    }
}

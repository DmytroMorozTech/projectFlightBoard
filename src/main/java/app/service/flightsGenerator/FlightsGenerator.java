package app.service.flightsGenerator;

import app.domain.Flight;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FlightsGenerator {
    private static HashMap<String, Flight> flights = new HashMap<>();
    private static final String nameOfFile = "flights.bin";

    private static List<ShortFlightData> sampleFlights = new ArrayList<>();

    private static void fillSampleFlights() {
        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Амстердам", "AMS", 185));
        sampleFlights.add(new ShortFlightData("Амстердам", "AMS", "Нью-Йорк", "JFK", 495));
        sampleFlights.add(new ShortFlightData("Нью-Йорк", "JFK", "Амстердам", "AMS", 420));
        sampleFlights.add(new ShortFlightData("Амстердам", "AMS", "Киев", "KBP", 160));

        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Франкфурт-на-Майне", "FRA", 170));
        sampleFlights.add(new ShortFlightData("Франкфурт-на-Майне", "FRA", "Монреаль", "YUL", 480));
        sampleFlights.add(new ShortFlightData("Монреаль", "YUL", "Франкфурт-на-Майне", "FRA", 425));
        sampleFlights.add(new ShortFlightData("Франкфурт-на-Майне", "FRA", "Киев", "KBP", 145));

        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Стамбул", "IST", 130));
        sampleFlights.add(new ShortFlightData("Стамбул", "IST", "Мальта", "MLA", 145));
        sampleFlights.add(new ShortFlightData("Мальта", "MLA", "Стамбул", "IST", 155));
        sampleFlights.add(new ShortFlightData("Стамбул", "IST",  "Киев", "KBP",  135));

        sampleFlights.add(new ShortFlightData("Стамбул", "IST", "Амстердам", "AMS", 220));
        sampleFlights.add(new ShortFlightData("Амстердам", "AMS", "Мадрид", "MAD", 160));
        sampleFlights.add(new ShortFlightData("Стамбул", "IST", "Франкфурт-на-Майне", "FRA", 80));
        sampleFlights.add(new ShortFlightData("Франкфурт-на-Майне", "FRA", "Мадрид", "MAD", 155));


        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Лондон", "LHR", 215));
        sampleFlights.add(new ShortFlightData("Лондон", "LHR", "Мадрид", "MAD", 150));
        sampleFlights.add(new ShortFlightData("Лондон", "LHR", "Нью-Йорк", "JFK", 495));
        sampleFlights.add(new ShortFlightData("Лондон", "LCY", "Нью-Йорк", "JFK", 490));
        sampleFlights.add(new ShortFlightData("Нью-Йорк", "JFK", "Лондон", "LHR", 490));
        sampleFlights.add(new ShortFlightData("Нью-Йорк", "JFK", "Лондон", "LCY", 490));


        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Лондон", "LCY", 210));
        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Лондон", "LHR", 225));
        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Вена", "VIE", 120));
        sampleFlights.add(new ShortFlightData("Вена", "VIE", "Киев", "KBP",120));
        sampleFlights.add(new ShortFlightData("Киев", "KBP", "Милан", "MIL",180));
        sampleFlights.add(new ShortFlightData("Милан", "MIL","Нью-Йорк", "JFK", 555));


//        sampleFlights.add(new ShortFlightData("Киев", "KBP","Ивано-Франковск", "IFO", 95));
//        sampleFlights.add(new ShortFlightData("Ивано-Франковск", "IFO","Лондон", "LHR", 120));
    }

    public static boolean saveDataToFile() {
        try {
            FileSystemService fs = new FileSystemService();
            new PrintWriter(nameOfFile).close(); // очищаем содержимое файла.

            fs.saveDataToFile(nameOfFile, flights);
            // записываем в файл все те рейсы, которые были сгенерированы в методе generateFlights()
            LoggerService.info("Сохранение новых сгенерированных рейсов на жесткий диск в файл " + nameOfFile);
            return true;
        }
        catch (IOException ex) {
            LoggerService.error("Возникла ошибка при записи на жесткий диск файла " + nameOfFile);
            return false;
        }
    }

    private static long convertLocalDtToZonedDt(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private static long generateRandomLong(long minValue, long maxValue) {
        return (long) (Math.random() * (maxValue - minValue + 1) + minValue);
    }

    private static int generateRandomInt(int minValue, int maxValue) {
        return (int) (Math.random() * (maxValue - minValue + 1) + minValue);
    }

    private static int generateNumbOfFreeSeats() {
        return generateRandomInt(1, 50);
    }

    private static long generateRandDepartureTime() {
        long currentTimeZoned = convertLocalDtToZonedDt(LocalDateTime.now());
        long currentTimePlus10days = convertLocalDtToZonedDt(LocalDateTime.now().plusDays(10));
        long currentTimeMinus10days = convertLocalDtToZonedDt(LocalDateTime.now().minusDays(10));
        long randomTime = generateRandomLong(currentTimeMinus10days, currentTimePlus10days);

        ZonedDateTime zonedDt = convertFromUnixToLocalDate(randomTime);
        ZonedDateTime zdtTruncatedSec = zonedDt.truncatedTo(ChronoUnit.MINUTES);
        int minutes = zdtTruncatedSec.getMinute();
        int deltaMinutes = minutes % 10;
        ZonedDateTime zonedDateTime = zdtTruncatedSec.minusMinutes(deltaMinutes);
//        System.out.printf("Random time was generated: %s\n", zonedDateTime);

        return zonedDateTime.toInstant().toEpochMilli();
    }
    // метод generateRandDepartureTime() случайным образом генерирует время отправления рейса в
    // заданном временном диапазоне. В данном случае это временной интервал:
    // нижняя граница диапазона:    -10 дней от текущего момента.
    // верхняя граница диапазона :  +10 дней после текущего момента.

    private static String generateIdOfFlight() {
        char randomCapitalCharacter = (char) (generateRandomInt(65, 90));
        String idOfFlight =
                "FL" + String.valueOf(generateRandomInt(100, 999)) + randomCapitalCharacter;
        return idOfFlight;
    }
    // метод для генерации рандомного буквенно-числового ID для рейса.

    private static String getRandomMapKey(HashMap<String, Integer> citiesOfArrival) {
        Object[] keysArray = citiesOfArrival.keySet().toArray();
        int randArrIndex = generateRandomInt(0, keysArray.length - 1);

        return (String) keysArray[randArrIndex];
    }
    // Поскольку HashMap является неитерабельной структурой данных, то нам пригодится данный
    // вспомагательный метод. Он помагает получить случайный ключ из переданной HashMap<String, Integer>

    public static ZonedDateTime convertFromUnixToLocalDate(long timeInUnixMillis) {
        return new Timestamp(timeInUnixMillis).toLocalDateTime().atZone(ZoneId.systemDefault());
    }

    public static void generateFlights(int numberOfFlights) {
        final long millisIn1Min = 1000 * 60;

        fillSampleFlights();

        while (flights.size() != numberOfFlights) {
            int randomIndex = generateRandomInt(0, sampleFlights.size() - 1);
            ShortFlightData flightData = sampleFlights.get(randomIndex);
            long departureTime = generateRandDepartureTime();
            long arrivalTime = departureTime + flightData.getFlightDurationMin() * millisIn1Min;
            String idOfFlight = generateIdOfFlight();
            int numbOfSeats = generateNumbOfFreeSeats();
            Flight generatedFlight = new Flight(flightData, idOfFlight,
                                                departureTime, arrivalTime, numbOfSeats);
            flights.put(idOfFlight, generatedFlight);
            System.out.println("Random flight was generated.");
        }
    }

}

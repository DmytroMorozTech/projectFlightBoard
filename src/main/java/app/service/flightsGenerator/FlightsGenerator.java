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

    private static HashMap<String, Integer> citiesOfArrival = new HashMap<>();
    // key - city of arrival; value - duration of flight (in minutes);

    private static void fillCitiesOfArrival() {
        citiesOfArrival.put("Одесса", 80);
        citiesOfArrival.put("Стамбул", 120);
        citiesOfArrival.put("Днепр", 80);
        citiesOfArrival.put("Львов", 95);
        citiesOfArrival.put("Минск", 60);
        citiesOfArrival.put("Прага", 130);
        citiesOfArrival.put("Париж", 210);
        citiesOfArrival.put("Эссен", 540);
        citiesOfArrival.put("Лион", 190);
        citiesOfArrival.put("Мале", 540);
        citiesOfArrival.put("Анкара", 125);
        citiesOfArrival.put("Ереван", 165);
        citiesOfArrival.put("Рига", 105);
        citiesOfArrival.put("Лондон", 215);
        citiesOfArrival.put("Вена", 120);
        citiesOfArrival.put("Милан", 180);
        citiesOfArrival.put("София", 135);
        citiesOfArrival.put("Доха", 300);
        citiesOfArrival.put("Батуми", 150);
        citiesOfArrival.put("Ивано-Франковск", 95);
        citiesOfArrival.put("Запорожье", 75);
        citiesOfArrival.put("Дюссельдорф", 200);
        citiesOfArrival.put("Лиссабон", 305);
        citiesOfArrival.put("Амстердам", 185);
        citiesOfArrival.put("Тбилиси", 345);
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
        System.out.printf("Random time was generated: %s\n", zonedDateTime);

        return zonedDateTime.toInstant().toEpochMilli();
    }
    // метод generateRandDepartureTime() случайным образом генерирует время отправления рейса в
    // заданном временном диапазоне. В данном случае это временной интервал:
    // нижняя граница диапазона:    -10 дней от текущего момента.
    // верхняя граница диапазона :  +10 дней после текущего момента.

    private static String generateIdOfFlight() {
        char randomCapitalCharacter = (char) (generateRandomInt(65, 90));
        String idOfFlight =
                "FL" + String.valueOf(generateRandomInt(100, 1000)) + randomCapitalCharacter;
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
        final String departureCity = "Киев";

        fillCitiesOfArrival();

        while (flights.size() != numberOfFlights) {
            String destinationPlace = getRandomMapKey(citiesOfArrival);
            long departureTime = generateRandDepartureTime();
            long arrivalTime = departureTime + citiesOfArrival.get(destinationPlace) * millisIn1Min;
            String idOfFlight = generateIdOfFlight();
            int numbOfSeats = generateNumbOfFreeSeats();
            Flight generatedFlight = new Flight(departureCity, destinationPlace, idOfFlight,
                                                departureTime, arrivalTime, numbOfSeats);
            flights.put(idOfFlight, generatedFlight);
        }
    }

}

package app;

import app.controller.BookingsController;
import app.controller.FlightsController;
import app.controller.UsersController;
import app.domain.Booking;
import app.domain.Flight;
import app.domain.Passenger;
import app.domain.User;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.flightsGenerator.FlightsGenerator;
import app.service.loggerService.LoggerService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;

import static app.service.validationService.ValidationService.*;

public class Console {
    private static User activeUser;
    private static boolean userIsAuthorized = false;
    private static List<Flight> flightsConsideredAtTheMoment;

    private static HashMap<String, Callable<Void>> mainMenuCommands;
    private static HashMap<String, Callable<Void>> loginMenuCommands;
    private static HashMap<String, Callable<Void>> bookingMenuCommands;

    private static FlightsController flightsController;
    private static BookingsController bookingsController;
    private static UsersController usersController;

    static String inputCommand;
    static String loginMenuCommandsStr =
            "\n\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "СЕРВИС БРОНИРОВАНИЯ АВИАБИЛЕТОВ:\n" +
                    "1.\tВход в систему.\n" +
                    "2.\tРегистрация\n" +
                    "3.\tЗавершить работу программы\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

    static String mainMenuCommandsStr =
            "\n\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                    "СЕРВИС БРОНИРОВАНИЯ АВИАБИЛЕТОВ:\n" +
                    "1.\tОтобразить онлайн-табло.\n" +
                    "2.\tПосмотреть информацию о рейсе\n" +
                    "3.\tПоиск и бронирование рейса\n" +
                    "4.\tОтменить бронирование\n" +
                    "5.\tМои рейсы\n" +
                    "6.\tСохранить внесенные изменения\n" +
                    "7.\tЗавершить сессию\n" +
                    "8.\tЗавершить работу программы\n" +
                    "9.\tСгенерировать рейсы случайным образом\n" +
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

    public Console(FlightsController fc, BookingsController bc, UsersController uc) {
        flightsController = fc;
        bookingsController = bc;
        usersController = uc;

    }

    public static void main(String[] args) throws Exception, FlightOverflowException, BookingOverflowException, UsersOverflowException {
        if (!usersController.usersWereUploaded())
            initialization();

        loginAndRegistration();

        while (userIsAuthorized) {
            if (!flightsController.flightsWereUploaded()) {
                flightsController.loadData();
                bookingsController.loadData();
            }

            // если пользователь был успешно авторизован, а список рейсов все еще не загружен
            // в систему, то загружаем эти данные с диска. Также загружаем данные по бронированиям.

            System.out.println(mainMenuCommandsStr);
            inputCommand = readCommand("main");
            executeCommandByName("main", inputCommand);
        }

        main(null);
    }

    private static void initialization() throws BookingOverflowException, FlightOverflowException, UsersOverflowException, IOException {
        addCommands4LoginMenu();
        addCommands4MainMenu();
        addCommands4BookingMenu();
        usersController.loadData();
    }

    public static void executeCommandByName(String section, String commandName) throws Exception {
        if (section.equals("main")) {
            Callable<Void> commandToBeExecuted = mainMenuCommands.get(commandName);
            commandToBeExecuted.call();
        } else if (section.equals("loginMenu")) {
            Callable<Void> commandToBeExecuted = loginMenuCommands.get(commandName);
            commandToBeExecuted.call();
        } else if (section.equals("bookingMenu")) {
            Callable<Void> commandToBeExecuted = bookingMenuCommands.get(commandName);
            commandToBeExecuted.call();
        }
    }

    public static void addCommands4MainMenu() {
        mainMenuCommands = new HashMap<>();

        mainMenuCommands.put("1", () -> {
            System.out.println("<<< Вы выбрали команду №1 - ОТОБРАЗИТЬ ОНЛАЙН-ТАБЛО >>>");
            Optional<HashMap<String, Flight>> flightsForNext24Hours = flightsController.getFlightsForNext24Hours();
            flightsController.printFlightsToConsole(flightsForNext24Hours);
            return null;
        });

        mainMenuCommands.put("2", () -> {
            System.out.println("<<< Вы выбрали команду №2 - ПОСМОТРЕТЬ ИНФОРМАЦИЮ О РЕЙСЕ >>>");
            String idOfFlight = readFlightId("Введите номер интересующего Вас рейса (его ID):");
            Flight f = flightsController.getFlightById(idOfFlight);
            System.out.println(f.prettyFormat());
            return null;
        });

        mainMenuCommands.put("3", () -> {
            System.out.println("<<< Вы выбрали команду №3 - ПОИСК И БРОНИРОВАНИЕ РЕЙСА >>>");
            Optional<HashMap<String, Flight>> foundFlights = findFlights();
            if (foundFlights.isEmpty()) {
                System.out.println("По заданным критериям НЕ БЫЛО НАЙДЕНО РЕЙСОВ.");
                return null;
            }
            List<Flight> flights = flightsController.convertHashMapToList(foundFlights.get());
            flightsController.printIndexedList(flights);
//          Все найденные рейсы здесь выводятся на экран.
            flightsConsideredAtTheMoment = flights;

            String bookingMenuCommandsStr =
                    "\n\n" +
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                            "Дальнейшие действия:\n" +
                            "1.\tВыбрать рейс для бронирования.\n" +
                            "2.\tВернуться в предыдущее меню.\n" +
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            System.out.println(bookingMenuCommandsStr);

            inputCommand = readCommand("bookingMenu");
            executeCommandByName("bookingMenu", inputCommand);

//          Если пользователь решает забронировать рейс, ему необходимо ввести данные (имя и
//          фамилия) для того количества пассажиров, которое было указано при поиске. Для этого
//          мы будем обращаться к bookingsController.

            return null;
        });

        mainMenuCommands.put("4", () -> {
            System.out.println("<<< Вы выбрали команду №4 - ОТМЕНИТЬ БРОНИРОВАНИЕ >>>");
            String idOfBooking = readFlightId("Введите ID Вашего бронирования");
            bookingsController.deleteBookingByItsId(idOfBooking);
//            - Если такое бронирование было найдено - оно отменяется. Если нет - пользователю
//            выводится соответствующее сообщение "Бронирование не было найдено".
//            - После этого снова отображается главное меню. Пользователь может отменить любое бронирование.

            return null;
        });

        mainMenuCommands.put("5", () -> {
            System.out.println("<<< Вы выбрали команду №5 - МОИ РЕЙСЫ >>>");
            Optional<HashMap<String, Booking>> allBookingsOfActiveUser =
                    bookingsController.getAllUserBookings(activeUser.getLogin(), activeUser.getName(),
                                                          activeUser.getSurname());
            bookingsController.printBookingsToConsole(allBookingsOfActiveUser);

//            - Поскольку в нашей программе пользователь сможет работать только после входа в систему,
//            то здесь мы не будем спрашивать фамилию и имя пользователя. Мы возьмем эту
//            информацию из переменной и просто выведем на экран информацию о его бронированиях.

            return null;
        });

        mainMenuCommands.put("6", () -> {
            System.out.println("<<< Вы выбрали команду №6 - СОХРАНИТЬ ВНЕСЕННЫЕ ИЗМЕНЕНИЯ >>>");
            try {
                usersController.saveDataToFile();
                flightsController.saveDataToFile();
                bookingsController.saveDataToFile();
            }
            catch (BookingOverflowException | FlightOverflowException | UsersOverflowException e) {
                e.printStackTrace();
            }

            return null;
        });

        mainMenuCommands.put("7", () -> {

            //            System.out.println("<<< Вы выбрали команду №7 - ЗАВЕРШИТЬ СЕССИЮ >>>");
            logOut();
//          Происходит Logout пользователя и он попадает в loginMenu.
            return null;
        });

        mainMenuCommands.put("8", () -> {
            LoggerService.info("Завершение работы приложения.");
//            System.out.println("<<< Вы выбрали команду №8 - ЗАВЕРШИТЬ РАБОТУ ПРИЛОЖЕНИЯ >>>");
            System.exit(0);
            return null;
        });

        mainMenuCommands.put("9", () -> {
            System.out.println("<<< Вы выбрали команду №9 - Сгенерировать рейсы случайным образом >>>");
            try {
                generateNewFlights();
            }
            catch (BookingOverflowException | FlightOverflowException | UsersOverflowException e) {
                e.printStackTrace();
            }

//        {
//            System.out.println("Рейс был успешно создан");
//        } else {
//            System.out.println("Возникла ОШИБКА при создании рейса");
//        }
            return null;
        });
    }

    public static void addCommands4LoginMenu() {
        loginMenuCommands = new HashMap<>();

        loginMenuCommands.put("1", () -> {
            System.out.println("<<< Вы выбрали команду №1 - ВХОД В СИСТЕМУ >>>");
            logIn();
            return null;
        });

        loginMenuCommands.put("2", () -> {
            System.out.println("<<< Вы выбрали команду №2 - РЕГИСТРАЦИЯ >>>");
            registration();
            return null;
        });

        loginMenuCommands.put("3", () -> {
            LoggerService.info("Завершение работы приложения.");
            System.exit(0);
            return null;
        });
    }

    public static void addCommands4BookingMenu() {
        bookingMenuCommands = new HashMap<>();

        bookingMenuCommands.put("1", () -> {
            System.out.println("<<< Вы выбрали команду №1 - Забронировать билеты на рейс по его " +
                                       "порядковому номеру в списке>>>");
            createBooking();
            return null;
        });

        bookingMenuCommands.put("2", () -> {
            System.out.println("<<< Вы выбрали команду №2 - Вернуться в главное меню >>>");
            return null;
        });
    }

    public static void createBooking() {
        int maxNumbOfFlightInList = flightsConsideredAtTheMoment.size();
        int chosenItemInList = readNumber("Введите порядковый номер рейса в данном списке:",
                                          1, maxNumbOfFlightInList);
        int indexOfItemInList = chosenItemInList - 1;
        // из-за того, что нумерация отфильтрованных рейсов на экране начинается с единицы, а
        // индексы в структуре данных ArrayList начинаются с ноля. Поэтому мы и делаем эту
        // корректировку на -1.
        String flightId = flightsConsideredAtTheMoment.get(indexOfItemInList).getIdOfFlight();

        int numbOfPassengers = readNumber("Введите количество пассажиров, для которых Вы " +
                                                  "хотите приобрести билеты:", 1, 10);
        List<Passenger> passengerList =
                bookingsController.getPassengersDataFromUser(numbOfPassengers);
        String destinationOfFlight = flightsController.getFlightById(flightId).getDestinationPlace();
        Booking newBooking = new Booking(activeUser.getLogin(), flightId, passengerList, destinationOfFlight);
        bookingsController.createBooking(newBooking);

        System.out.println("Вы успешно забронировали билеты. Ниже информация о Вашей брони:");
        System.out.println("****************************************************************");
        System.out.println(newBooking.prettyFormat());
        System.out.println("****************************************************************");
    }


    private static void loginAndRegistration() throws Exception {
        while (!userIsAuthorized) {
            System.out.println(loginMenuCommandsStr);
            inputCommand = readCommand("loginMenu");
            executeCommandByName("loginMenu", inputCommand);
        }
    }

    private static void logIn() {
        System.out.println("Пожалуйста, введите Ваши данные для входа в учетную запись");
        String login = readString("ЛОГИН: ");
        String password = readPassword("ПАРОЛЬ: ");
        boolean successfulLogin = usersController.logIn(login, password);
        if (successfulLogin) {
            activeUser = usersController.getUserByLogin(login);
            userIsAuthorized = true;
            System.out.printf("%s, Вы успешно вошли в систему.", activeUser.getLogin());
        } else {
            System.out.println("Вы ввели неправильный логин или пароль! Повторите попытку.");
        }
    }

    private static void registration() {
        System.out.println("Пожалуйста, введите Ваши данные для регистрации новой учетной записи");
        String login = readString("ЛОГИН: ");
        boolean loginIsFree = usersController.getLoginIsFreeStatus(login);
        if (!loginIsFree) {
            System.out.println("Данный ЛОГИН уже занят. Попробуйте повторить регистрацию.");
            return;
        }

        String password = readPassword("ПАРОЛЬ: ");
        String name = readString("Введите Ваше имя:");
        String surname = readString("Введите Вашу фамилию:");
        boolean successfulRegistration
                = usersController.registerNewUser(login, password, name, surname);
        if (successfulRegistration) {
            LoggerService.info("Регистрация нового пользователя прошла успешно.");

            System.out.println("Регистрация прошла успешно.");
            System.out.println("Теперь Вам нужно войти в систему, используя ЛОГИН и ПАРОЛЬ, " +
                                       "указанные при регистрации.");
        } else {
            System.out.println("Произошла ошибка при регистрации нового пользователя.");
            System.out.println("Попробуйте повторить процедуру.");
        }
    }

    private static void logOut() {
        LoggerService.info("Завершение рабочей сессии(User's logout).");
        activeUser = null;
        userIsAuthorized = false;
    }

    private static int generateRandomInt(int minValue, int maxValue) {
        return (int) (Math.random() * (maxValue - minValue + 1) + minValue);
    }

    private static void generateNewFlights() throws BookingOverflowException, FlightOverflowException, UsersOverflowException, IOException {
        FlightsGenerator.generateFlights(500);
        FlightsGenerator.saveDataToFile();
    }

    private static Optional<HashMap<String, Flight>> findFlights() {
        String destination = readString("Введите пункт Вашего назначения(город): ");
        long dateAndTime = readDate("Введите дату и время желаемого вылета в " +
                                            "формате в формате dd/MM/yyyy-HH:mm:");
        int numbOfRequestedSeats = readNumber("Введите количество посадочных " +
                                                      "мест, которые Вы хотели бы " +
                                                      "забронировать: ", 1, 10);
        LocalDateTime localDateTime = convertFromUnixMillisToLocalDateT(dateAndTime);

        Optional<HashMap<String, Flight>> filteredFlights =
                flightsController.getFilteredFlights(destination, localDateTime, numbOfRequestedSeats);
//            - Здесь мы находим все рейсы по критериям поиска, заданным пользователем.

        return filteredFlights;
    }

    private static LocalDateTime convertFromUnixMillisToLocalDateT(long timeInUnixMillis) {
        return new Timestamp(timeInUnixMillis).toLocalDateTime();
    }


}

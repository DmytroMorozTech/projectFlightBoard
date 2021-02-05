package app;

import app.controller.BookingsController;
import app.controller.FlightsController;
import app.controller.UsersController;
import app.domain.*;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.flightsGenerator.FlightsGenerator;
import app.service.loggerService.LoggerService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;

import static app.service.validationService.ValidationService.*;

public class Console {
    private static List<FlightRoute> flightsConsideredAtTheMoment;

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
                    "3.\tПоиск и бронирование рейсов\n" +
                    "4.\tОтменить бронирование\n" +
                    "5.\tМои маршруты\n" +
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

        while (usersController.getUserIsAuthorizedStatus()) {
            if (!flightsController.flightsWereUploaded()) {
                flightsController.loadData();
                bookingsController.loadData();
            }

            System.out.println(mainMenuCommandsStr);
            inputCommand = readCommand("main");
            executeCommandByName("main", inputCommand);
        }

        main(null);
    }

    private static void initialization() {
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
            Optional<List<Flight>> flightsForNext24Hours =
                    flightsController.getFlightsForNext24Hours(LocalDateTime.now());
            flightsController.printFlightsToConsole(flightsForNext24Hours);
            return null;
        });

        mainMenuCommands.put("2", () -> {
            System.out.println("<<< Вы выбрали команду №2 - ПОСМОТРЕТЬ ИНФОРМАЦИЮ О РЕЙСЕ >>>");
            String idOfFlight = readFlightId("Введите номер интересующего Вас рейса (его ID):");
            Optional<Flight> f = flightsController.getFlightById(idOfFlight);
            if (f.isEmpty()) {
                System.out.printf("В базе данных отсутствует информация о рейсе %s.\n", idOfFlight);
                return null;
            }
            System.out.println(f.get().prettyFormat());
            return null;
        });

        mainMenuCommands.put("3", () -> {
            System.out.println("<<< Вы выбрали команду №3 - ПОИСК И БРОНИРОВАНИЕ РЕЙСОВ >>>");
            Optional<List<FlightRoute>> foundFlightRoutes = findFlightRoutes();
            if (foundFlightRoutes.isEmpty() | foundFlightRoutes.get().size() == 0) {
                System.out.println("По заданным критериям НЕ БЫЛО НАЙДЕНО РЕЙСОВ.");
                return null;
            }
            List<FlightRoute> foundFlightsList = foundFlightRoutes.get();
            flightsController.printIndexedList(foundFlightsList);
//          Все найденные маршруты здесь выводятся на экран.
//          Маршрут отличается от рейса тем, что маршрут может состоять из нескольких рейсов. То
//          есть мы учитываем, что при построении маршрута из точки А в точку В у нас может быть
//          пересадка в точке С.
            flightsConsideredAtTheMoment = foundFlightsList;

            String bookingMenuCommandsStr =
                    "\n\n" +
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
                            "Дальнейшие действия:\n" +
                            "1.\tВыбрать маршрут для бронирования.\n" +
                            "2.\tВернуться в предыдущее меню.\n" +
                            "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            System.out.println(bookingMenuCommandsStr);

            inputCommand = readCommand("bookingMenu");
            executeCommandByName("bookingMenu", inputCommand);

            return null;
        });

        mainMenuCommands.put("4", () -> {
            System.out.println("<<< Вы выбрали команду №4 - ОТМЕНИТЬ БРОНИРОВАНИЕ >>>");
            String idOfBooking = readFlightId("Введите ID Вашего бронирования");
            Optional<Booking> bookingToBeDeleted = bookingsController.getBookingByItsId(idOfBooking);
            bookingToBeDeleted.ifPresent(b -> flightsController.cancelSeatsReserve4Booking(b));

            bookingsController.deleteBookingByItsId(idOfBooking);
            return null;
        });

        mainMenuCommands.put("5", () -> {
            System.out.println("<<< Вы выбрали команду №5 - МОИ МАРШРУТЫ >>>");
            User activeUser = usersController.getActiveUser();
            Optional<HashMap<String, Booking>> allBookingsOfActiveUser =
                    bookingsController.getAllUserBookings(activeUser.getLogin(), activeUser.getName(),
                            activeUser.getSurname());
            bookingsController.printBookingsToConsole(allBookingsOfActiveUser);
            return null;
        });

        mainMenuCommands.put("6", () -> {
            System.out.println("<<< Вы выбрали команду №6 - СОХРАНИТЬ ВНЕСЕННЫЕ ИЗМЕНЕНИЯ >>>");
            usersController.saveDataToFile();
            flightsController.saveDataToFile();
            bookingsController.saveDataToFile();

            return null;
        });

        mainMenuCommands.put("7", () -> {
            System.out.println("<<< Вы выбрали команду №7 - ЗАВЕРШИТЬ СЕССИЮ >>>");
            usersController.logOut();
            return null;
        });

        mainMenuCommands.put("8", () -> {
            LoggerService.info("Завершение работы приложения.");
            System.out.println("<<< Вы выбрали команду №8 - ЗАВЕРШИТЬ РАБОТУ ПРИЛОЖЕНИЯ >>>");
            System.exit(0);
            return null;
        });

        mainMenuCommands.put("9", () -> {
            System.out.println("<<< Вы выбрали команду №9 - Сгенерировать рейсы случайным образом >>>");
            generateNewFlights();
            return null;
        });
    }

    public static void addCommands4LoginMenu() {
        loginMenuCommands = new HashMap<>();

        loginMenuCommands.put("1", () -> {
            System.out.println("<<< Вы выбрали команду №1 - ВХОД В СИСТЕМУ >>>");
            System.out.println("Пожалуйста, введите Ваши данные для входа в учетную запись");
            String login = readString("ЛОГИН: ");
            String password = readPassword("ПАРОЛЬ: ");
            usersController.logIn(login, password);
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
            System.out.println("<<< Вы выбрали команду №1 - Забронировать билеты на маршрут " +
                    "по его порядковому номеру в списке>>>");
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
        int chosenItemInList = readNumber("Введите порядковый номер маршрута в данном списке:",
                1, maxNumbOfFlightInList);
        int indexOfItemInList = chosenItemInList - 1;
        // из-за того, что нумерация отфильтрованных маршрутов на экране начинается с единицы, а
        // индексы в структуре данных ArrayList начинаются с ноля. Поэтому мы и делаем эту
        // корректировку на -1.
        FlightRoute flightRoute = flightsConsideredAtTheMoment.get(indexOfItemInList);

        int numbOfPassengers = readNumber("Введите количество пассажиров, для которых Вы " +
                "хотите приобрести билеты:", 1, 10);
        List<Passenger> passengerList =
                bookingsController.getPassengersDataFromUser(numbOfPassengers);
        String activeUserLogin = usersController.getActiveUser().getLogin();
        Booking newBooking = new Booking(activeUserLogin, flightRoute, passengerList);
        bookingsController.createBooking(newBooking);
        flightsController.applySeatsReserve4Booking(newBooking);

        System.out.println("Вы успешно забронировали билеты. Ниже информация о Вашей брони:");
        System.out.println("****************************************************************");
        System.out.println(newBooking.prettyFormat());
        System.out.println("****************************************************************");
    }


    private static void loginAndRegistration() throws Exception {
        while (!usersController.getUserIsAuthorizedStatus()) {
            System.out.println(loginMenuCommandsStr);
            inputCommand = readCommand("loginMenu");
            executeCommandByName("loginMenu", inputCommand);
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
            System.out.println("Регистрация прошла успешно.");
            System.out.println("Теперь Вам нужно войти в систему, используя ЛОГИН и ПАРОЛЬ, " +
                    "указанные при регистрации.");
        } else {
            System.out.println("Произошла ошибка при регистрации нового пользователя.");
            System.out.println("Попробуйте повторить процедуру.");
        }
    }

    private static Optional<List<FlightRoute>> findFlightRoutes() {
        String departurePlace = readCityName("Введите начальную точку Вашего маршрута (название " +
                "города): ");
        String destinationPlace = readCityName("Введите пункт Вашего назначения(название города): ");
        long dateAndTime = readDate("Введите дату и время желаемого вылета в " +
                "формате в формате dd/MM/yyyy-HH:mm:");
        int numbOfRequestedSeats = readNumber("Введите количество посадочных " +
                "мест, которые Вы хотели бы " +
                "забронировать: ", 1, 10);
        LocalDateTime localDateTime = convertFromUnixMillisToLocalDateT(dateAndTime);

        Optional<List<FlightRoute>> filteredFlights =
                flightsController.getFilteredFlights(departurePlace, destinationPlace,
                        localDateTime, numbOfRequestedSeats);
//        Здесь мы находим все маршруты по критериям поиска, заданным пользователем. Это могут
//        быть как маршруты с прямыми рейсами, так и маршруты, состоящие из нескольких рейсов (с
//        пересадками).

        return filteredFlights;
    }

    private static LocalDateTime convertFromUnixMillisToLocalDateT(long timeInUnixMillis) {
        return new Timestamp(timeInUnixMillis).toLocalDateTime();
    }

    private static void generateNewFlights() {
        FlightsGenerator.generateFlights(2000);
        FlightsGenerator.saveDataToFile();
    }
}

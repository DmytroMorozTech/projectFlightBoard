package app;

import app.controller.BookingsController;
import app.controller.FlightsController;
import app.controller.UsersController;
import app.domain.User;

import java.util.*;
import java.util.concurrent.Callable;

import static app.service.validationService.ValidationService.*;

public class Console {
    private static User activeUser;
    private static boolean userIsAuthorized = false;

    private static HashMap<String, Callable<Void>> mainMenuCommands;
    private static HashMap<String, Callable<Void>> loginMenuCommands;

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
                    "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

    public Console(FlightsController fc, BookingsController bc, UsersController uc) {
        this.flightsController = fc;
        this.bookingsController = bc;
        this.usersController = uc;
    }

    public static void main(String[] args) throws Exception {
        addCommands4LoginMenu();
        addCommands4MainMenu();
        loginAndRegistration();

        while (userIsAuthorized) {
            System.out.println(mainMenuCommandsStr);
            inputCommand = readCommand("main");
            executeCommandByName("main", inputCommand);
        }

        main(null);
    }

    public static void executeCommandByName(String section, String commandName) throws Exception {
        if (section.equals("main")) {
            Callable<Void> commandToBeExecuted = mainMenuCommands.get(commandName);
            commandToBeExecuted.call();
        } else if (section.equals("loginMenu")) {
            Callable<Void> commandToBeExecuted = loginMenuCommands.get(commandName);
            commandToBeExecuted.call();
        }
    }

    public static void addCommands4MainMenu() {
        mainMenuCommands = new HashMap<>();

        mainMenuCommands.put("1", () -> {
            System.out.println("<<< Вы выбрали команду №1 - ОТОБРАЗИТЬ ОНЛАЙН-ТАБЛО >>>");

//            flightsController.getFlightsForNext24Hours()
            //  Здесь мы через flightsController должны вызвать метод, который отобразит на
            //  экране информацию про все рейсы из Киева на ближайшие 24 часа.
            return null;
        });

        mainMenuCommands.put("2", () -> {
            System.out.println("<<< Вы выбрали команду №2 - ПОСМОТРЕТЬ ИНФОРМАЦИЮ О РЕЙСЕ >>>");

//            - Здесь мы должны вызвать метод flightsController.getFlightById()
//            Пользователю предлагается ввести ID рейса. Далее по этому рейсу выводится вся информация:
//            дата,    время,    место назначения,    количество свободных мест.
//            - После этого снова отображается главное меню.
            return null;
        });

        mainMenuCommands.put("3", () -> {
            System.out.println("<<< Вы выбрали команду №3 - ПОИСК И БРОНИРОВАНИЕ РЕЙСА >>>");

            String destination = readString("Введите пункт Вашего назначения(город): ");
            long dateAndTime = readDate("Введите дату и время желаемого вылета в " +
                                                "формате в формате dd/MM/yyyy-HH:mm:\n");
            int numbOfRequestedSeats = readNumber("Введите количество посадочных " +
                                                          "мест, которые Вы хотели бы " +
                                                          "забронировать: ", 1, 6);
//            - Здесь мы должны вызвать flightsController.findFlights() , чтобы найти все рейсы,
//            которые отвечают критериям, заданным пользователем.
//            - Все найденные рейсы должны быть выведены на экран.
//            - После этого пользователь может выбрать один из найденных рейсов, указав его
//            порядковый номер, либо вернуться в главное меню (выбрав пункт 0).

//            - Если пользователь решает забронировать рейс, ему необходимо ввести данные (имя и
//            фамилия) для того количества пассажиров, которое было указано при поиске. Для этого
//            мы будем обращаться к bookingsController.


            return null;
        });

        mainMenuCommands.put("4", () -> {
            System.out.println("<<< Вы выбрали команду №4 - ОТМЕНИТЬ БРОНИРОВАНИЕ >>>");
            int idOfBooking = readNumber("Введите ID Вашего бронирования", 1, 1000);

//            - Здесь мы должны вызвать bookingsController.getBookingByItsId().
//            - Если такое бронирование было найдено - оно отменяется. Если нет - пользователю
//            выводится соответствующее сообщение "Бронирование не было найдено".
//            - После этого снова отображается главное меню. Пользователь может отменить любое бронирование.

            return null;
        });

        mainMenuCommands.put("5", () -> {
            System.out.println("<<< Вы выбрали команду №5 - МОИ РЕЙСЫ >>>");
            int idOfBooking = readNumber("Введите ID Вашего бронирования", 1, 1000);


//            - Поскольку в нашей программе пользователь сможет работать только после входа в систему,
//            то здесь мы не будем спрашивать фамилию и имя пользователя. Мы возьмем эту
//            информацию из переменной и просто выведем на экран информацию о его бронированиях.

            return null;
        });

        mainMenuCommands.put("6", () -> {
            System.out.println("<<< Вы выбрали команду №6 - СОХРАНИТЬ ВНЕСЕННЫЕ ИЗМЕНЕНИЯ >>>");

//            - Нужно, чтобы Дима реализовал класс для работы с файловой системой fileSystemService.

            return null;
        });

        mainMenuCommands.put("7", () -> {
            System.out.println("<<< Вы выбрали команду №7 - ЗАВЕРШИТЬ СЕССИЮ >>>");
            logOut();
//          Происходит Logout пользователя и он попадает в loginMenu.
            return null;
        });

        mainMenuCommands.put("8", () -> {
            System.out.println("<<< Вы выбрали команду №8 - ЗАВЕРШИТЬ РАБОТУ ПРИЛОЖЕНИЯ >>>");
            System.exit(1);
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
            System.out.println("<<< Вы выбрали команду №3 - ЗАВЕРШИТЬ РАБОТУ ПРИЛОЖЕНИЯ >>>");
            System.exit(1);
            return null;
        });
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
        String password = readString("ПАРОЛЬ: ");
        boolean successfulLogin = usersController.logIn(login, password);
        if (successfulLogin) {
            activeUser = usersController.getUserByLogin(login);
            userIsAuthorized = true;
        } else {
            System.out.println("Вы ввели неправильный логин или пароль! Повторите попытку.");
        }
    }

    private static void registration() {
        System.out.println("Пожалуйста, введите Ваши данные для регистрации новой учетной записи");
        String login = readString("ЛОГИН: ");
        String password = readString("ПАРОЛЬ: ");
        boolean successfulRegistration = usersController.registerNewUser(login, password);
        if (successfulRegistration) {
            System.out.println("Регистрация прошла успешно.");
            System.out.println("Теперь Вам нужно войти в систему, используя ЛОГИН и ПАРОЛЬ, " +
                                       "указанные при регистрации.");
        } else {
            System.out.println("Данный ЛОГИН уже занят. Попробуйте повторить регистрацию.");
        }
    }

    private static void logOut() {
        activeUser = null;
        userIsAuthorized = false;
    }

}

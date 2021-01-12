package app.service.validationService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationService {
    private static Scanner scanner = new Scanner(System.in);

    public static String readCommand(String section) {
        String input;
        List<String> allowedCommands = new ArrayList<>(
                Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9")
        );
        if (section.equals("loginMenu")) {
            allowedCommands = new ArrayList<>(
                    Arrays.asList("1", "2", "3"));
        }

        while (true) {
            System.out.println("\nВведите комманду: ");
            input = scanner.nextLine().trim().toLowerCase().split(" ")[0];
            if (allowedCommands.contains(input)) return input;
            System.out.println("Вы ввели недопустимую комманду. Пожалуйста, попробуйте снова.");
            System.out.println("Список разрешенных комманд выведен на экран.");
        }
    }

    public static int readNumber(String prompt, int min, int max) {
        String input;
        int value = Integer.MIN_VALUE;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            try {
                value = Integer.valueOf(input);
            }
            catch (NumberFormatException e) {
                System.out.println("You've entered the wrong data format!");
                value = Integer.MIN_VALUE;
            }
            if (value >= min && value <= max)
                break;
            System.out.println("Enter an integer value between " + min + " and " + max);
        }
        return value;
    }

    public static String readString(String prompt) {
        String input;

        Pattern pattern = Pattern.compile("\\d", Pattern.CASE_INSENSITIVE);
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();

            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                System.out.println("Вы ввели неправильный формат данных. В данной строке не " +
                                           "должно быть цифр.");
                continue;
            }

            if (input.length() > 17 || input.length() < 2) {
                System.out.println("Размер введенной Вами строки должен быть в диапазоне от 2 до " +
                                           "17 символов.");
            }

            if (!matcher.find() && input.length() < 17 && input.length() > 2)
                break;
        }
        input = input.toLowerCase();
        String formattedInput = input.substring(0, 1).toUpperCase() + input.substring(1);
        return formattedInput;
    }

    public static String readPassword(String prompt) {
        String password;

        while (true) {
            System.out.println(prompt);
            password = scanner.nextLine();

            if (password.length() < 5 || password.length() > 15) {
                System.out.println("Пожалуйста, введите пароль длинной от 5 до 15 символов");
                continue;
            } else if (password.length() >= 5 || password.length() <= 15) break;
        }
        return password;
    }

    private static long strToDateTimeInMillis(String dateAsString) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateAsString, dtf);
        long dateTimeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return dateTimeInMillis;
    }

    public static long readDate(String prompt) {
        String input;
        Pattern pattern = Pattern.compile(
                "^([1-9]|([012][0-9])|(3[01]))\\/([0]{0,1}[1-9]|1[012])\\/\\d\\d\\d\\d-([0-1]?[0-9]|2?[0-3]):([0-5]\\d)$");
        // это регулярное выражения для даты в формате dd/MM/yyyy-HH:mm

        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();

            boolean isValidDate = pattern.matcher(input).find();
            if (!isValidDate) {
                System.out.println("Вы ввели дату в неправильном формате. Введите, пожалуйста, " +
                                           "дату еще раз в формате dd/MM/yyyy-HH:mm");
                continue;
            }

            return strToDateTimeInMillis(input);
        }
    }


}

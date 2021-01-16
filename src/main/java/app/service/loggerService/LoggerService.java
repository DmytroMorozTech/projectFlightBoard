package app.service.loggerService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class LoggerService {
    private static final String info = "[DEBUG]";
    private static final String err = "[ERROR]";

    private static void addLogEntry(String logType, String logText) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), TimeZone.getDefault().toZoneId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dateTimeFormatted = dtf.format(localDateTime);

        String textMessageForLog =
                dateTimeFormatted + " " + logType + "\n" +
                        logText + "\n" +
                        "__________________________________________________________________ \n";

//        System.out.println(textMessageForLog);
        // за счет этой строки те данные лога, которые будут сохраняться в файл application.log,
        // во время работы программы также будут выводиться в консоль.

        File file = new File("application.log");

        try (
                FileWriter fwr = new FileWriter(file, true);
                BufferedWriter bwr = new BufferedWriter(fwr)) {
            bwr.append(textMessageForLog);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String logText) {
        addLogEntry(info, logText);
    }

    public static void error(String logText) {
        addLogEntry(err, logText);
    }
}

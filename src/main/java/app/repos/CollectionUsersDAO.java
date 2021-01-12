package app.repos;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.Flight;
import app.domain.User;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public final class CollectionUsersDAO implements UsersDAO, CanWorkWithFileSystem {
    private HashMap<String, User> users;
    private final String nameOfFile = "users.bin";

    public CollectionUsersDAO() throws UsersOverflowException {
        this.users = new HashMap<>();
    }

    @Override
    public boolean logIn(String login, String password) {
        User user = users.get(login);
        if (Objects.nonNull(user)) {
            return user.verifyPassword(password);
        }
        return false;
    }

    @Override
    public boolean registerNewUser(String login, String password) {
        if (users.containsKey(login)) {
            return false;
        } else {
            users.put(login, new User(login, password));
            try {
                saveDataToFile();
            }
            catch (UsersOverflowException | IOException | FlightOverflowException e) {
                return false;
            }
            return true;
        }
    }

    @Override
    public User getUserByLogin(String login) {
        return users.get(login);
    }


    @Override
    public void loadData() throws UsersOverflowException {
        LoggerService.info("Загрузка файла " + nameOfFile + " с жесткого диска.");

        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                users = (HashMap<String, User>) dataFromFS;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            throw new UsersOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                                                     " с жесткого диска.");
        }
    }

    @Override
    public boolean saveDataToFile() throws IOException, UsersOverflowException, FlightOverflowException {
        LoggerService.info("Сохранение данных на жесткий диск в файл " + nameOfFile);

        try {
            FileSystemService fs = new FileSystemService();
            fs.saveDataToFile(nameOfFile, users);
            return true;
        }
        catch (IOException e) {
            throw new FlightOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                                                      " на жесткий диск компьютера.");
        }
    }

    @Override
    public boolean usersWereUploaded() {
        return users.size() != 0;
    }
}

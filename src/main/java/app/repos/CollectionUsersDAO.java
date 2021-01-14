package app.repos;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.User;
import app.exceptions.UsersOverflowException;
import app.service.fileSystemService.FileSystemService;
import app.service.loggerService.LoggerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public final class CollectionUsersDAO implements UsersDAO, CanWorkWithFileSystem {
    private HashMap<String, User> users;
    private final String nameOfFile = "users.bin";
    private User activeUser;
    private boolean userIsAuthorized;

    public CollectionUsersDAO() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean logIn(String login, String password) {
        User user = users.get(login);
        if (Objects.nonNull(user) && user.verifyPassword(password)) {
            activeUser = user;
            userIsAuthorized = true;
            System.out.printf("%s, Вы успешно вошли в систему.", activeUser.getLogin());
            return true;
        }

        System.out.println("Вы ввели неправильный логин или пароль! Повторите попытку.");
        return false;
    }

    @Override
    public void logOut() {
        activeUser = null;
        userIsAuthorized = false;
    }

    @Override
    public boolean getUserIsAuthorizedStatus() {
        return userIsAuthorized;
    }

    @Override
    public User getActiveUser() {
        return activeUser;
    }

    @Override
    public boolean registerNewUser(String login, String password, String name, String surname) {
        if (users.containsKey(login)) {
            return false;
        }

        users.put(login, new User(login, password, name, surname));
        return true;
    }

    @Override
    public User getUserByLogin(String login) {
        return users.get(login);
    }

    @Override
    public boolean usersWereUploaded() {
        return users.size() != 0;
    }

    @Override
    public boolean getLoginIsFreeStatus(String login) {
        return !users.containsKey(login);
    }

    @Override
    public void loadData() throws UsersOverflowException {
        try {
            FileSystemService fs = new FileSystemService();
            Object dataFromFS = fs.getDataFromFile(nameOfFile);
            if (dataFromFS instanceof HashMap) {
                users = (HashMap<String, User>) dataFromFS;
            }
            LoggerService.info("Загрузка файла " + nameOfFile + " с жесткого диска.");
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new UsersOverflowException("Возникла ОШИБКА при чтении файла " + nameOfFile +
                                                     " с жесткого диска.");
        }
    }

    @Override
    public boolean saveDataToFile() throws UsersOverflowException {
        try {
            FileSystemService fs = new FileSystemService();
            fs.saveDataToFile(nameOfFile, users);
            LoggerService.info("Сохранение данных на жесткий диск в файл " + nameOfFile);
            return true;
        }
        catch (IOException ex) {
            throw new UsersOverflowException("Возникла ОШИБКА при сохранении файла " + nameOfFile +
                                                     " на жесткий диск компьютера.");
        }
    }
}

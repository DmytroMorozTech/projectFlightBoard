package app.service;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.User;
import app.exceptions.UsersOverflowException;
import app.repos.CollectionUsersDAO;
import app.service.loggerService.LoggerService;

public class UsersService implements UsersDAO, CanWorkWithFileSystem {
    private CollectionUsersDAO usersDAO;

    public UsersService() {
        this.usersDAO = new CollectionUsersDAO();
    }

    public boolean logIn(String login, String password) {
        LoggerService.info("Вход пользователя в систему.");
        return usersDAO.logIn(login, password);
    }

    @Override
    public void logOut() {
        usersDAO.logOut();
        LoggerService.info("Завершение рабочей сессии(User's logout).");
    }

    @Override
    public boolean getUserIsAuthorizedStatus() {
        LoggerService.info("Проверка наличия авторизованного пользователя.");
        return usersDAO.getUserIsAuthorizedStatus();
    }

    @Override
    public User getActiveUser() {
        LoggerService.info("Получение данных об авторизованном пользователе.");
        return usersDAO.getActiveUser();
    }

    public boolean registerNewUser(String login, String password, String name, String surname) {
        LoggerService.info("Регистрация нового пользователя.");
        return usersDAO.registerNewUser(login, password, name, surname);
    }

    public User getUserByLogin(String login) {
        LoggerService.info("Получение пользователя по его логину.");
        return usersDAO.getUserByLogin(login);
    }

    @Override
    public boolean usersWereUploaded() {
        return usersDAO.usersWereUploaded();
    }

    @Override
    public boolean getLoginIsFreeStatus(String login) {
        return usersDAO.getLoginIsFreeStatus(login);
    }

    @Override
    public void loadData() {
        try {
            usersDAO.loadData();
        }
        catch (UsersOverflowException ex) {
            LoggerService.error("UsersOverflowException: " + ex.getMessage());
        }
    }

    @Override
    public boolean saveDataToFile() {
        try {
            return usersDAO.saveDataToFile();
        }
        catch (UsersOverflowException ex) {
            LoggerService.error("UsersOverflowException: " + ex.getMessage());
            return false;
        }
    }
}

package app.controller;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.User;
import app.service.UsersService;

public class UsersController implements UsersDAO, CanWorkWithFileSystem {
    private UsersService usersService;

    public UsersController() {
        this.usersService = new UsersService();
    }

    public boolean logIn(String login, String password) {
        return usersService.logIn(login, password);
    }

    @Override
    public void logOut() {
        usersService.logOut();
    }

    @Override
    public boolean getUserIsAuthorizedStatus() {
        return usersService.getUserIsAuthorizedStatus();
    }

    @Override
    public User getActiveUser() {
        return usersService.getActiveUser();
    }

    public boolean registerNewUser(String login, String password, String name, String surname) {
        return usersService.registerNewUser(login, password, name, surname);
    }

    public User getUserByLogin(String login) {
        return usersService.getUserByLogin(login);
    }

    @Override
    public boolean usersWereUploaded() {
        return usersService.usersWereUploaded();
    }

    @Override
    public boolean getLoginIsFreeStatus(String login) {
        return usersService.getLoginIsFreeStatus(login);
    }

    @Override
    public void loadData() {
        usersService.loadData();
    }

    @Override
    public boolean saveDataToFile() {
        return usersService.saveDataToFile();
    }
}

package app.controller;

import app.domain.User;
import app.repos.CollectionUsersDAO;
import app.service.UsersService;

public class UsersController {
    private UsersService usersService;

    public UsersController() {
        this.usersService = new UsersService();
    }

    public boolean logIn(String login, String password) {
        return usersService.logIn(login, password);
    }

    public boolean registerNewUser(String login, String password) {
        return usersService.registerNewUser(login, password);
    }

    public User getUserByLogin(String login) {
        return usersService.getUserByLogin(login);
    }
}

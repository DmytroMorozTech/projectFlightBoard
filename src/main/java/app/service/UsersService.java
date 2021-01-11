package app.service;

import app.domain.User;
import app.repos.CollectionUsersDAO;

import java.util.Objects;

public class UsersService {
    private CollectionUsersDAO usersDAO;

    public UsersService() {
        this.usersDAO = new CollectionUsersDAO();
    }

    public boolean logIn(String login, String password) {
        return usersDAO.logIn(login, password);
    }

    public boolean registerNewUser(String login, String password) {
        return usersDAO.registerNewUser(login, password);
    }

    public User getUserByLogin(String login) {
        return usersDAO.getUserByLogin(login);
    }
}

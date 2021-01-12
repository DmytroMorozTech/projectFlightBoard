package app.service;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.User;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.repos.CollectionUsersDAO;

import java.io.IOException;

public class UsersService implements UsersDAO, CanWorkWithFileSystem {
    private CollectionUsersDAO usersDAO;

    public UsersService() throws UsersOverflowException {
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

    @Override
    public void loadData() throws IOException, UsersOverflowException {
        usersDAO.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, UsersOverflowException, FlightOverflowException {
        return usersDAO.saveDataToFile();
    }

    @Override
    public boolean usersWereUploaded() {
        return usersDAO.usersWereUploaded();
    }
}

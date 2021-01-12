package app.controller;

import app.contract.CanWorkWithFileSystem;
import app.contract.UsersDAO;
import app.domain.User;
import app.exceptions.BookingOverflowException;
import app.exceptions.FlightOverflowException;
import app.exceptions.UsersOverflowException;
import app.service.UsersService;

import java.io.IOException;

public class UsersController implements UsersDAO, CanWorkWithFileSystem {
    private UsersService usersService;

    public UsersController() throws UsersOverflowException {
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

    @Override
    public void loadData() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        usersService.loadData();
    }

    @Override
    public boolean saveDataToFile() throws IOException, BookingOverflowException, FlightOverflowException, UsersOverflowException {
        return usersService.saveDataToFile();
    }

    @Override
    public boolean usersWereUploaded() {
        return usersService.usersWereUploaded();
    }
}

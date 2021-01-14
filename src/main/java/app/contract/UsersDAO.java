package app.contract;

import app.domain.User;

public interface UsersDAO {

    boolean logIn(String login, String password);

    void logOut();

    boolean getUserIsAuthorizedStatus();

    User getActiveUser();

    boolean registerNewUser(String login, String password, String name, String surname);

    User getUserByLogin(String login);

    boolean usersWereUploaded();

    boolean getLoginIsFreeStatus(String login);
}

package app.repos;

import app.contract.UsersDAO;
import app.domain.User;

import java.util.HashMap;
import java.util.Objects;

public final class CollectionUsersDAO implements UsersDAO {
    private final HashMap<String, User> users;

    public CollectionUsersDAO() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean logIn(String login, String password) {
        User user = users.get(login);
        if (Objects.nonNull(user)) {
            return user.verifyPassword(password);
        }
        return false;
    } //ok

    @Override
    public boolean registerNewUser(String login, String password) {
        if (users.containsKey(login)) {
            return false;
        } else {
            users.put(login, new User(login, password));
//            Дима будет заниматься классом для работы с файловой системой.
//            Нужно будет дописать закомментированную часть ниже.
//            try {
//                saveDataToFile();
//            } catch (UsersOverflowException e) {
//                return false;
//            }
            return true;
        }
    } //ok

    @Override
    public User getUserByLogin(String login) {
        return users.get(login);
    } //ok
}

package bg.sofia.uni.fmi.mjt.wish.list.manager.registrar;

import bg.sofia.uni.fmi.mjt.wish.list.manager.user.User;
import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class Registrar {
    private final Map<Integer, User> registeredUsers;

    public Registrar() {
        this.registeredUsers = new HashMap<>();
    }

    public void register(User user) {
        Validator.validateNotNull(user, "user");

        this.registeredUsers.put(user.hashCode(), user);
    }

    public User getUser(String username) {
        return this.registeredUsers.get(username.hashCode());
    }

    public boolean isRegistered(String username) {
        Validator.validateNotNull(username, "username");

        return this.registeredUsers.containsKey(username.hashCode());
    }

    public boolean containsCombination(String username, String password) {
        Validator.validateNotNull(username, "username");
        Validator.validateNotNull(password, "password");

        if (!this.isRegistered(username)) {
            return false;
        }

        User user = this.registeredUsers.get(username.hashCode());

        return user.password().equals(password);
    }
}
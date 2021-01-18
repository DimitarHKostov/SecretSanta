package bg.sofia.uni.fmi.mjt.wish.list.manager;

import bg.sofia.uni.fmi.mjt.wish.list.manager.registrar.Registrar;
import bg.sofia.uni.fmi.mjt.wish.list.manager.user.User;
import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Registrar registrar;
    private final Map<SocketChannel, User> loggedUserChannel;

    public UserManager() {
        this.registrar = new Registrar();
        this.loggedUserChannel = new HashMap<>();
    }

    public void register(String username, String password) {
        Validator.validateNotNull(username, "username");
        Validator.validateNotNull(password, "password");

        this.registrar.register(new User(username, password));
    }

    public boolean isRegistered(String username) {
        Validator.validateNotNull(username, "username");

        return this.registrar.isRegistered(username);
    }

    public void logIn(SocketChannel channel, String username) {
        Validator.validateNotNull(channel, "channel");
        Validator.validateNotNull(username, "username");

        this.loggedUserChannel.put(channel, this.registrar.getUser(username));
    }

    public boolean isCombinationPresent(String username, String password) {
        Validator.validateNotNull(username, "username");
        Validator.validateNotNull(password, "password");

        return this.registrar.containsCombination(username, password);
    }

    public String getUsername(SocketChannel channel) {
        Validator.validateNotNull(channel, "channel");

        return this.loggedUserChannel.get(channel).username();
    }

    public boolean isLoggedIn(SocketChannel channel) {
        Validator.validateNotNull(channel, "channel");

        return this.loggedUserChannel.containsKey(channel);
    }

    public void logOut(SocketChannel channel) {
        Validator.validateNotNull(channel, "channel");

        this.loggedUserChannel.remove(channel);
    }

    public void logOutAll() {
        this.loggedUserChannel.clear();
    }
}

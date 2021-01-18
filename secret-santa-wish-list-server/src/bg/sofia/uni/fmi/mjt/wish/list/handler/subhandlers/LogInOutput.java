package bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers;

import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

public class LogInOutput {
    private static final String INVALID_COMBINATION = "[ Invalid username/password combination ]";
    private static final String SUCCESSFUL_LOGIN_PREFIX = "[ User ";
    private static final String SUCCESSFUL_LOGIN_SUFFIX = " successfully logged in ]";
    private static final String NOT_LOGGED_IN = "[ You are not logged in ]";
    private static final String ALREADY_LOGGED_IN = "[ You are already logged in ]";

    public String formatWrongCombination() {
        return INVALID_COMBINATION;
    }

    public String formatSuccessfulLogIn(String username) {
        Validator.validateNotNull(username, "username");

        return SUCCESSFUL_LOGIN_PREFIX + username + SUCCESSFUL_LOGIN_SUFFIX;
    }

    public String formatNotLoggedIn() {
        return NOT_LOGGED_IN;
    }

    public String formatAlreadyLoggedIn() {
        return ALREADY_LOGGED_IN;
    }
}

package bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers;

import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

public class RegisterOutput {
    private static final String USERNAME_TAKEN_PREFIX = "[ Username ";
    private static final String USERNAME_TAKEN_SUFFIX = " is already taken, select another one ]";
    private static final String INVALID_USERNAME_PREFIX = "[ Username ";
    private static final String INVALID_USERNAME_SUFFIX = " is invalid, select a valid one ]";
    private static final String SUCCESSFUL_REGISTER_PREFIX = "[ Username ";
    private static final String SUCCESSFUL_REGISTER_SUFFIX = " successfully registered ]";

    public String formatUsernameTaken(String username) {
        Validator.validateNotNull(username, "username");

        return USERNAME_TAKEN_PREFIX + username + USERNAME_TAKEN_SUFFIX;
    }

    public String formatInvalidUsername(String username) {
        Validator.validateNotNull(username, "username");

        return INVALID_USERNAME_PREFIX + username + INVALID_USERNAME_SUFFIX;
    }

    public String formatSuccessfulRegister(String username) {
        Validator.validateNotNull(username, "username");

        return SUCCESSFUL_REGISTER_PREFIX + username + SUCCESSFUL_REGISTER_SUFFIX;
    }
}

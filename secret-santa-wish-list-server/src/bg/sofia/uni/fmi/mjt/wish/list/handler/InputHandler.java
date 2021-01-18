package bg.sofia.uni.fmi.mjt.wish.list.handler;

import bg.sofia.uni.fmi.mjt.wish.list.enums.CommandType;
import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

public class InputHandler {
    public static CommandType getCommandType(String message) {
        Validator.validateNotNull(message, "message");

        if (message.contains(CommandType.GET_WISH.getCommand())) {
            return CommandType.GET_WISH;
        } else if (message.contains(CommandType.POST_WISH.getCommand())) {
            return CommandType.POST_WISH;
        } else if (message.contains(CommandType.LOGIN.getCommand())) {
            return CommandType.LOGIN;
        } else if (message.contains(CommandType.LOGOUT.getCommand())) {
            return CommandType.LOGOUT;
        } else if (message.contains(CommandType.DISCONNECT.getCommand())) {
            return CommandType.DISCONNECT;
        } else if (message.contains(CommandType.REGISTER.getCommand())) {
            return CommandType.REGISTER;
        } else {
            return CommandType.UNKNOWN;
        }
    }

    public static String extractUsername(String message) {
        Validator.validateNotNull(message, "message");

        return message.split(" ")[1];
    }

    public static String extractPassword(String message) {
        Validator.validateNotNull(message, "message");

        String[] tokens = message.split(" ");
        return message.substring(message.indexOf(tokens[2]));
    }

    public static String extractWish(String message) {
        Validator.validateNotNull(message, "message");

        String[] tokens = message.split(" ");

        return message.substring(message.indexOf(tokens[2]));
    }
}

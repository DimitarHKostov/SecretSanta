package bg.sofia.uni.fmi.mjt.wish.list.enums;

public enum CommandType {
    POST_WISH("post-wish"),
    GET_WISH("get-wish"),
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    DISCONNECT("disconnect"),
    UNKNOWN("unknown");

    private final String command;

    CommandType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }
}
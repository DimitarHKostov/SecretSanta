package bg.sofia.uni.fmi.mjt.wish.list.handler;

import bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers.GetWishOutput;
import bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers.LogInOutput;
import bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers.LogOutOutput;
import bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers.PostWishOutput;
import bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers.RegisterOutput;

public class OutputHandler {
    public static final RegisterOutput REGISTER_OUTPUT = new RegisterOutput();
    public static final LogInOutput LOG_IN_OUTPUT = new LogInOutput();
    public static final LogOutOutput LOG_OUT_OUTPUT = new LogOutOutput();
    public static final PostWishOutput POST_WISH_OUTPUT = new PostWishOutput();
    public static final GetWishOutput GET_WISH_OUTPUT = new GetWishOutput();
    public static final String INVALID_NUMBER_OF_ARGUMENTS = "[ Invalid number of arguments ]";
    public static final String DISCONNECT_OUTPUT = "[ Disconnected from server ]";
    public static final String UNKNOWN_COMMAND_OUTPUT = "[ Unknown command ]";
}

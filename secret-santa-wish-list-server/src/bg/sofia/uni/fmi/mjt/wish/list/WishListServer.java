package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.enums.CommandType;
import bg.sofia.uni.fmi.mjt.wish.list.exceptions.InterruptedServerLoadException;
import bg.sofia.uni.fmi.mjt.wish.list.handler.InputHandler;
import bg.sofia.uni.fmi.mjt.wish.list.handler.OutputHandler;
import bg.sofia.uni.fmi.mjt.wish.list.manager.UserManager;
import bg.sofia.uni.fmi.mjt.wish.list.manager.WishManager;
import bg.sofia.uni.fmi.mjt.wish.list.validator.ValidSymbolsContainer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WishListServer {
    private static final int BUFFER_SIZE = 2048;
    private final UserManager userManager;
    private final WishManager wishManager;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final AtomicBoolean hasStarted = new AtomicBoolean(false);
    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;
    private Thread serverThread;

    public WishListServer(int port) {
        this.userManager = new UserManager();
        this.wishManager = new WishManager();

        try {
            this.selector = Selector.open();
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void start() {
        if (!this.isStopped.get()) {
            this.serverThread = new Thread(() -> {
                try {
                    this.loadServer();
                } catch (IOException e) {
                    throw new InterruptedServerLoadException("Server loading failed.");
                }
            });

            this.hasStarted.set(true);
            this.serverThread.start();
        }
    }

    public void stop() {
        this.isStopped.set(true);

        if (this.hasStarted.get()) {
            this.userManager.logOutAll();

            try {
                this.serverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                this.selector.close();
                this.serverSocketChannel.close();
            } catch (IOException e) {
                throw new ClosedSelectorException();
            }
        }

    }

    private void loadServer() throws IOException {
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        while (!this.isStopped.get()) {
            int readyChannels = this.selector.select(10);
            if (readyChannels <= 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    this.readKey(key);
                } else if (key.isAcceptable()) {
                    this.acceptKey(key);
                }

                keyIterator.remove();
            }
        }
    }

    private void acceptKey(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel channel = ssc.accept();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);
    }

    private void readKey(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();

        try {
            this.buffer.clear();
            int readBytes = channel.read(this.buffer);

            if (readBytes == -1) {
                return;
            }

            this.buffer.flip();
            String messageFromClient = StandardCharsets.UTF_8.decode(this.buffer).toString();
            this.buffer.clear();
            String response = this.handleMessage(messageFromClient, channel);
            this.buffer.put((response + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            this.buffer.flip();
            channel.write(this.buffer);

            if (response.equals(OutputHandler.DISCONNECT_OUTPUT)) {
                channel.close();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private String handleMessage(String message, SocketChannel channel) {
        message = message.trim().replace("\n", "");
        CommandType type = InputHandler.getCommandType(message);

        if (type == CommandType.REGISTER) {
            return this.handleRegisterCommand(message, channel);
        } else if (type == CommandType.LOGIN) {
            return this.handleLoginCommand(message, channel);
        } else if (type == CommandType.LOGOUT) {
            return this.handleLogoutCommand(message, channel);
        } else if (type == CommandType.POST_WISH) {
            return this.handlePostWishCommand(message, channel);
        } else if (type == CommandType.GET_WISH) {
            return this.handleGetWishCommand(message, channel);
        } else if (type == CommandType.DISCONNECT) {
            return this.handleDisconnectCommand(message, channel);
        } else {
            return this.handleUnknownCommand();
        }
    }

    private String handleRegisterCommand(String message, SocketChannel channel) {
        if (this.userManager.isLoggedIn(channel)) {
            return OutputHandler.LOG_IN_OUTPUT.formatAlreadyLoggedIn();
        }

        String username = InputHandler.extractUsername(message);

        if (!this.isUsernameValid(username)) {
            return OutputHandler.REGISTER_OUTPUT.formatInvalidUsername(username);
        }

        if (this.userManager.isRegistered(username)) {
            return OutputHandler.REGISTER_OUTPUT.formatUsernameTaken(username);
        }

        String password = InputHandler.extractPassword(message);
        this.userManager.register(username, password);
        this.userManager.logIn(channel, username);

        return OutputHandler.REGISTER_OUTPUT.formatSuccessfulRegister(username);
    }

    private boolean isUsernameValid(String username) {
        for (int i = 0; i < username.length(); i++) {
            if (!this.isValidSymbol(username.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidSymbol(char symbol) {
        boolean isUpperCaseLetter = (symbol >= ValidSymbolsContainer.FIRST_UPPER_CASE_LETTER)
                && (symbol <= ValidSymbolsContainer.LAST_UPPER_CASE_LETTER);

        boolean isLowerCaseLetter = (symbol >= ValidSymbolsContainer.FIRST_LOWER_CASE_LETTER)
                && (symbol <= ValidSymbolsContainer.LAST_LOWER_CASE_LETTER);

        boolean isDigit = symbol >= ValidSymbolsContainer.FIRST_DIGIT && symbol <= ValidSymbolsContainer.LAST_DIGIT;

        boolean isSpacial = symbol == ValidSymbolsContainer.SPECIAL_SYMBOL_DASH
                || symbol == ValidSymbolsContainer.SPECIAL_SYMBOL_DOT
                || symbol == ValidSymbolsContainer.SPECIAL_SYMBOL_UNDERLINE;

        return isUpperCaseLetter || isLowerCaseLetter || isDigit || isSpacial;
    }

    private String handleLoginCommand(String message, SocketChannel channel) {
        if (this.userManager.isLoggedIn(channel)) {
            return OutputHandler.LOG_IN_OUTPUT.formatAlreadyLoggedIn();
        }

        String username = InputHandler.extractUsername(message);
        String password = InputHandler.extractPassword(message);

        if (!this.userManager.isCombinationPresent(username, password)) {
            return OutputHandler.LOG_IN_OUTPUT.formatWrongCombination();
        }

        this.userManager.logIn(channel, username);

        return OutputHandler.LOG_IN_OUTPUT.formatSuccessfulLogIn(username);
    }

    private String handleLogoutCommand(String message, SocketChannel channel) {
        if (!this.userManager.isLoggedIn(channel)) {
            return OutputHandler.LOG_IN_OUTPUT.formatNotLoggedIn();
        }

        if (!this.isNumberOfArgumentsValid(message, 1)) {
            return OutputHandler.INVALID_NUMBER_OF_ARGUMENTS;
        }

        this.userManager.logOut(channel);

        return OutputHandler.LOG_OUT_OUTPUT.formatSuccessfulLogOut();
    }

    private String handlePostWishCommand(String message, SocketChannel channel) {
        if (!this.userManager.isLoggedIn(channel)) {
            return OutputHandler.LOG_IN_OUTPUT.formatNotLoggedIn();
        }

        int spaces = message.split(" ").length - 1;

        if (spaces < 2) {
            return OutputHandler.INVALID_NUMBER_OF_ARGUMENTS;
        }

        String recipient = InputHandler.extractUsername(message);

        if (!this.userManager.isRegistered(recipient)) {
            return OutputHandler.POST_WISH_OUTPUT.formatRecipientNotRegistered(recipient);
        }

        String wish = InputHandler.extractWish(message);

        if (this.wishManager.isWishAlreadySubmitted(recipient, wish)) {
            return OutputHandler.POST_WISH_OUTPUT.formatWishAlreadyExist(recipient);
        }

        this.wishManager.addWish(recipient, wish);

        return OutputHandler.POST_WISH_OUTPUT.formatSuccessfulPostWish(recipient, wish);
    }

    private String handleGetWishCommand(String message, SocketChannel channel) {
        if (!this.userManager.isLoggedIn(channel)) {
            return OutputHandler.LOG_IN_OUTPUT.formatNotLoggedIn();
        }

        if (!this.isNumberOfArgumentsValid(message, 1)) {
            return OutputHandler.INVALID_NUMBER_OF_ARGUMENTS;
        }

        String username = this.userManager.getUsername(channel);

        if (!this.wishManager.containsOtherStudents(username)) {
            return OutputHandler.GET_WISH_OUTPUT.formatNoPresentStudents();
        }

        String randomRecipient = this.wishManager.getRandomRecipient(username);
        List<String> wishes = this.wishManager.getWishes(randomRecipient);

        return OutputHandler.GET_WISH_OUTPUT.formatSuccessfulGetWish(randomRecipient, wishes);
    }

    private String handleDisconnectCommand(String message, SocketChannel channel) {
        if (!this.isNumberOfArgumentsValid(message, 1)) {
            return OutputHandler.INVALID_NUMBER_OF_ARGUMENTS;
        }

        this.userManager.logOut(channel);

        return OutputHandler.DISCONNECT_OUTPUT;
    }

    private String handleUnknownCommand() {
        return OutputHandler.UNKNOWN_COMMAND_OUTPUT;
    }

    private boolean isNumberOfArgumentsValid(String message, int validNumberOfArguments) {
        int tokens = message.split(" ").length;
        return tokens == validNumberOfArguments;
    }
}
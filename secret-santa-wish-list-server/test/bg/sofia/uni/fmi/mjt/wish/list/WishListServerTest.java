package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class WishListServerTest {
    private static final int PORT = 4444;
    private static WishListServer wishListServer;
    private static final String SERVER_HOST = "localhost";
    private static Thread serverStarterThread;

    @Before
    public void prepareServerStart() {
        wishListServer = new WishListServer(PORT);

        serverStarterThread = new Thread() {
            public void run() {
                wishListServer.start();
            }
        };

        serverStarterThread.start();
    }

    @After
    public void stopServer() {
        wishListServer.stop();
        serverStarterThread.interrupt();
    }

    // ------ REGISTER START ------
    @Test
    public void testRegisterSuccessful() {
        assertEquals("[ Username pesho successfully registered ]",
                this.getResponseNotLoggedIn("register pesho asd"));
    }

    @Test
    public void testRegisterUsernameTaken() {
        this.registerUser("pesho", "pass123dasjn");

        assertEquals("[ Username pesho is already taken, select another one ]",
                this.getResponseNotLoggedIn("register pesho na2sdjjn1dasj"));
    }

    @Test
    public void testRegisterInvalidUsername() {
        assertEquals("[ Username cars& is invalid, select a valid one ]",
                this.getResponseNotLoggedIn("register cars& pass123nnbadsasdasd"));
    }

    @Test
    public void testRegisterAlreadyLoggedIn() {
        this.registerUser("pesho", "pass");
        assertEquals("[ You are already logged in ]",
                this.getResponseWhenLoggedIn("pesho", "pass", "register gosho asdasd"));
    }

    // ------ REGISTER END ------

    // ------ LOGIN START ------

    @Test
    public void testLoginInvalidCombination() {
        assertEquals("[ Invalid username/password combination ]",
                getResponseNotLoggedIn("login pesho asd"));
    }

    @Test
    public void testLogInSuccessful() {
        this.registerUser("pesho", "pass123dasjn");

        assertEquals("[ User pesho successfully logged in ]",
                this.getResponseNotLoggedIn("login pesho pass123dasjn"));
    }

    @Test
    public void testLoginAlreadyLoggedIn() {
        this.registerUser("pesho", "pass");
        assertEquals("[ You are already logged in ]",
                this.getResponseWhenLoggedIn("pesho", "pass", "login gosho asdasd"));
    }

    // ------ LOGIN END ------

    // ------ LOGOUT START ------

    @Test
    public void testLogOutFailedBecauseNotLoggedIn() {
        assertEquals("[ You are not logged in ]", this.getResponseNotLoggedIn("logout pesho"));
    }

    @Test
    public void testLogOutSuccessful() {
        this.registerUser("pesho", "pass123dasjn");
        assertEquals("[ Successfully logged out ]",
                this.getResponseWhenLoggedIn("pesho", "pass123dasjn", "logout"));
    }

    @Test
    public void testLogOutWrongNumberOfArguments() {
        this.registerUser("pesho", "pass");
        assertEquals("[ Invalid number of arguments ]",
                this.getResponseWhenLoggedIn("pesho", "pass", "logout a"));
    }

    // ------ LOGOUT END ------

    // ------ FAST TESTS START ------

    @Test
    public void testUnknownCommandExpected() {
        assertEquals("[ Unknown command ]", getResponseNotLoggedIn("as123%d"));
    }

    @Test
    public void testDisconnectCommandExpected() {
        this.registerUser("usernameasd", "nqejjnqwenj123jkn");
        assertEquals("[ Disconnected from server ]",
                this.getResponseWhenLoggedIn("usernameasd", "nqejjnqwenj123jkn", "disconnect"));
    }

    // ------ FAST TESTS END ------

    // ------ POST WISH START ------

    @Test
    public void testPostWishSuccessful() {
        this.registerUser("pesho", "pass123");
        this.registerUser("gosho", "123pass");

        assertEquals("[ Gift kolelo for student pesho submitted successfully ]",
                this.getResponseWhenLoggedIn("gosho", "123pass", "post-wish pesho kolelo"));
    }

    @Test
    public void testPostWishNotLoggedIn() {
        assertEquals("[ You are not logged in ]", this.getResponseNotLoggedIn("post-wish pesho asd"));
    }

    @Test
    public void testPostWishAlreadySubmittedWish() {
        this.registerUser("pesho", "pass123");
        this.registerUser("gosho", "123pass");
        this.registerUser("ivan", "1pass1");

        this.getResponseWhenLoggedIn("pesho", "pass123", "post-wish gosho topka");

        assertEquals("[ The same gift for studentgosho was already submitted ]",
                this.getResponseWhenLoggedIn("ivan", "1pass1", "post-wish gosho topka"));
    }

    @Test
    public void testPostWishNotRegisteredRecipient() {
        this.registerUser("pesho", "asd");
        assertEquals("[ Student with username gosho is not registered ]",
                this.getResponseWhenLoggedIn("pesho", "asd", "post-wish gosho topka"));
    }

    @Test
    public void testPostWishWrongNumberOfArguments() {
        this.registerUser("pesho", "asd");
        assertEquals("[ Invalid number of arguments ]",
                this.getResponseWhenLoggedIn("pesho", "asd", "post-wish a"));
    }

    // ------ POST WISH END ------

    // ------ GET WISH START ------

    @Test
    public void testGetWishNoPresentStudents() {
        this.registerUser("pesho", "asd");
        assertEquals("[ There are no students present in the wish list ]",
                this.getResponseWhenLoggedIn("pesho", "asd", "get-wish"));
    }

    @Test
    public void testGetWishNoPresentOtherStudents() {
        this.registerUser("pesho", "asd");
        this.registerUser("ivan", "bcd");

        this.getResponseWhenLoggedIn("ivan", "bcd", "post-wish pesho wish");

        assertEquals("[ There are no students present in the wish list ]",
                this.getResponseWhenLoggedIn("pesho", "asd", "get-wish"));
    }

    @Test
    public void testGetWishExactOneWish() {
        this.registerUser("pesho", "asd");
        this.registerUser("ivan", "bcd");
        this.registerUser("user3", "asdasd");

        this.getResponseWhenLoggedIn("ivan", "bcd", "post-wish pesho topka");

        assertEquals("[ pesho: [topka] ]",
                this.getResponseWhenLoggedIn("user3", "asdasd", "get-wish"));
    }

    @Test
    public void testGetWishExactTwoWish() {
        this.registerUser("pesho", "asd");
        this.registerUser("ivan", "bcd");
        this.registerUser("user3", "asdasd");
        this.registerUser("user4", "asdasd1");

        this.getResponseWhenLoggedIn("ivan", "bcd", "post-wish pesho topka");
        this.getResponseWhenLoggedIn("user4", "asdasd1", "post-wish pesho kolelo");

        assertEquals("[ pesho: [topka, kolelo] ]", this.getResponseWhenLoggedIn("user3", "asdasd", "get-wish"));
    }

    // ------ GET WISH END ------

    // -------------------- CUSTOM ----------------------

    private void registerUser(String username, String password) {
        try (Socket socket = new Socket(SERVER_HOST, PORT); BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out =
                        new PrintWriter(socket.getOutputStream())) {

            out.println("register " + username + " " + password);
            out.flush();
            in.readLine();

            out.println("logout");
            out.flush();
            in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseWhenLoggedIn(String username, String password, String line) {
        String response = "";
        try (Socket socket = new Socket(SERVER_HOST, PORT); BufferedReader in
                = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out =
                        new PrintWriter(socket.getOutputStream())) {

            out.println("login " + username + " " + password);
            out.flush();
            in.readLine();

            out.println(line);
            out.flush();

            response = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getResponseNotLoggedIn(String line) {
        String response = "";
        try (Socket socket = new Socket(SERVER_HOST, PORT); BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out =
                        new PrintWriter(socket.getOutputStream())) {

            out.println(line);
            out.flush();

            response = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}

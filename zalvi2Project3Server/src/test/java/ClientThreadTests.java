import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClientThreadTests {

    // below here, tests for checkValidCharacter method in ClientThread class.
    @Test
    public void testCheckValidCharacterMethod() {
        Server server = new Server();
        Server.ClientThread c = server.new ClientThread(null, 1);

        String input = "s";
        String word = "soup";
        int letterIndex = c.checkValidCharacter(input, word);

        assertEquals(0, letterIndex);
    }

    @Test
    public void testCheckValidCharacterMethodNotIncorrect() {
        Server server = new Server();
        Server.ClientThread c = server.new ClientThread(null, 1);

        String input = "s";
        String word = "soup";
        int letterIndex = c.checkValidCharacter(input, word);

        assertNotEquals(1, letterIndex);
    }

    @Test
    public void testCheckValidCharacterMethodIndexNotFound() {
        Server server = new Server();
        Server.ClientThread c = server.new ClientThread(null, 1);

        String input = "d";
        String word = "soup";
        int letterIndex = c.checkValidCharacter(input, word);

        assertEquals(-1, letterIndex);
    }

    @Test
    public void testCheckValidCharacterMethodIndexNotFoundNotIncorrect() {
        Server server = new Server();
        Server.ClientThread c = server.new ClientThread(null, 1);

        String input = "d";
        String word = "soup";
        int letterIndex = c.checkValidCharacter(input, word);

        assertNotEquals(0, letterIndex);
    }

    @Test
    public void testCheckValidCharacterMethodIndexNotFoundNotNull() {
        Server server = new Server();
        Server.ClientThread c = server.new ClientThread(null, 1);

        String input = "d";
        String word = "soup";
        int letterIndex = c.checkValidCharacter(input, word);

        assertNotNull(letterIndex);
    }

    // below here, tests for the replaceCharAtIndex method in ClientThread class.

    @Test
    public void testReplaceCharAtIndex() {
        Server s = new Server();
        Server.ClientThread c = s.new ClientThread(null, 1);

        int idx = 0;
        String hidden = "-----";
        String revealed = "texas";
        String expected = "t----";
        String actual = c.replaceCharAtIndex(idx, hidden, revealed);
        assertEquals(expected, actual);
    }

    @Test
    public void testReplaceCharAtIndexNotIncorrect() {
        Server s = new Server();
        Server.ClientThread c = s.new ClientThread(null, 1);

        int idx = 0;
        String hidden = "-----";
        String revealed = "texas";
        String expected = "-t---";
        String actual = c.replaceCharAtIndex(idx, hidden, revealed);
        assertNotEquals(expected, actual);
    }

    @Test
    public void testReplaceCharAtIndexNotNull() {
        Server s = new Server();
        Server.ClientThread c = s.new ClientThread(null, 1);

        int idx = 0;
        String hidden = "-----";
        String revealed = "texas";
        assertNotNull(c.replaceCharAtIndex(idx, hidden, revealed));
    }

    @Test
    public void testReplaceCharAtIndexEmptyString() {
        Server s = new Server();
        Server.ClientThread c = s.new ClientThread(null, 1);

        int idx = 0;
        String hidden = "-----";
        String revealed = "-----";
        String expected = "-----";
        String actual = c.replaceCharAtIndex(idx, hidden, revealed);
        assertEquals(expected, actual);
    }

    @Test
    public void testReplaceCharAtIndexRealEmptyString() {
        Server s = new Server();
        Server.ClientThread c = s.new ClientThread(null, 1);

        int idx = 0;
        String hidden = " ";
        String revealed = " ";
        String expected = " ";
        String actual = c.replaceCharAtIndex(idx, hidden, revealed);
        assertEquals(expected, actual);
    }

}

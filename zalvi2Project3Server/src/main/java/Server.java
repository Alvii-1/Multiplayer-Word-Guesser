import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class Server {

    TheServer server;
    private Consumer<Serializable> callback;

    ArrayList<ClientThread> clientsList = new ArrayList<>();
    HashMap<String, ArrayList<String>> categoriesWords;
    int count = 1, serverPort;

    // Server constructor
    Server(Consumer<Serializable> call) {
        serverPort = GUIServer.getPort();
        callback = call;
        callback.accept("Server startup successful on port: " + serverPort);
        createCategories();
        server = new TheServer();
        server.start(); // calls the run method of TheServer class below.
    }

    // default constructor used for tests
    public Server() {}

    // method to initialize the categories' hashmap.
    public void createCategories() {
        categoriesWords= new HashMap<>();

        ArrayList<String> usStates = new ArrayList<>();
        usStates.add("iowa");
        usStates.add("florida");
        usStates.add("texas");

        ArrayList<String> foods = new ArrayList<>();
        foods.add("fries");
        foods.add("soup");
        foods.add("sandwich");

        ArrayList<String> codingLanguages = new ArrayList<>();
        codingLanguages.add("ruby");
        codingLanguages.add("c");
        codingLanguages.add("python");

        categoriesWords.put("US States", usStates);
        categoriesWords.put("Food", foods);
        categoriesWords.put("Coding Languages", codingLanguages);
    }

    public class TheServer extends Thread {
        // run method, executes code when .start() is called above

        public void run() {
            try (ServerSocket server = new ServerSocket(serverPort)) {
                callback.accept("Server is waiting for clients!");

                while (true) {
                    ClientThread newClient = new ClientThread(server.accept(), count);
                    callback.accept("New Client has connected to server: " + "Client #" + count);
                    clientsList.add(newClient);
                    newClient.start();
                    count += 1;
                }
            }
            catch (Exception e) {
                callback.accept("ERROR: Server socket did not launch.");
            }
        }
    }

    class ClientThread extends Thread {

        Socket clientConnection;
        ObjectInputStream clientInput;
        ObjectOutputStream clientOutput;

        boolean hasWon = false;
        String category, currentWord, hiddenWordOutput = "", continueOrStay = "M";
        int count, currentWordIndex = 0, incorrectGuessesLeft = 6, guessedWords = 0;

        ClientThread(Socket connectedSocket, int count) {
            this.clientConnection = connectedSocket;
            this.count = count;
        }

        public void run() {
            try {
                clientInput = new ObjectInputStream(clientConnection.getInputStream());
                clientOutput = new ObjectOutputStream(clientConnection.getOutputStream());
                clientConnection.setTcpNoDelay(true);
            }
            catch (Exception e) {
                callback.accept("Stream did not open for client #" + count);
            }

            IntroductoryMessage();
            explainGameMessage();

            while (true) {

                incorrectGuessesLeft = 6;

                if (Objects.equals(continueOrStay, "M")) {
                    category = getCategoryChoice();
                }

                currentWord = categoriesWords.get(category).get(currentWordIndex);
                outputHiddenWord();

                while (incorrectGuessesLeft > 0) {
                    try {
                        String input = clientInput.readObject().toString();
                        int letterIndex = checkValidCharacter(input, currentWord);
                        if (letterIndex != -1) {
                            hiddenWordOutput = replaceCharAtIndex(letterIndex, hiddenWordOutput, currentWord);
                            try {
                                clientOutput.writeObject(currentWord.charAt(letterIndex) + " is a part of the word! A letter is revealed: " + hiddenWordOutput + ". You have " + incorrectGuessesLeft + " incorrect guesses left");
                            } catch (Exception e) {}
                        }
                        else {
                            incorrectGuessesLeft -= 1;
                            try {
                                clientOutput.writeObject("Sorry, " + input + " is not part of the word. You have " + incorrectGuessesLeft + " incorrect guesses left");
                            } catch (Exception e) {}
                        }
                    } catch (Exception e) {}

                    if (Objects.equals(hiddenWordOutput, currentWord)) {
                        try {
                            categoriesWords.remove(category);
                            guessedWords += 1;
                            clientOutput.writeObject("You have correctly guessed the word " + hiddenWordOutput + " in category " + category);
                            clientOutput.writeObject("You can now move on to another category");
                            break;
                        }
                        catch (Exception e) {}
                    }
                }
                if (incorrectGuessesLeft == 0) {
                    try {
                        clientOutput.writeObject("Sorry, you did not guess the word in the allotted amount of guesses. The word was " + currentWord + ". You can either try another word in this category, or choose another category.");
                        clientOutput.writeObject("Input M to move onto another category. Input anything else to try another word in this category.");
                        continueOrStay = clientInput.readObject().toString();
                        if (Objects.equals(continueOrStay, "M")) {
                            continue;
                        }
                        else {
                            currentWordIndex += 1;

                        }
                    } catch (Exception e) {}
                }

                if (guessedWords == 3) {
                    try {
                        clientOutput.writeObject("Congratulations!!! You won the game by guessing a word from each category. Enter R to replay, or simply exit the application to quit.");
                        String choice = clientInput.readObject().toString();
                        if (Objects.equals(choice, "R")) {
                            createCategories();
                            continue;
                        }
                    } catch (Exception e) {}
                }
            }
        }

        // method to print out introductory message on how to play the game
        public void IntroductoryMessage() {
            try {
                clientOutput.writeObject("Welcome to the Word Guessing Game!");
            }
            catch (Exception e) {

            }
        }

        // method to print the game instructions
        public void explainGameMessage() {
            try {
                clientOutput.writeObject("You will be asked to choose a category, and then you will be given a word to guess. You will not be given the word itself, but rather a display of the number of letters in the word. The word is related to the category.");
                clientOutput.writeObject("You have 6 chances to guess each letter of the word. If you guess correctly, a letter will be added to replace an underscore. No chances are deducted for correct guesses");
                clientOutput.writeObject("If you guess incorrectly, your number of chances to correctly guess will be deducted by 1.");
                clientOutput.writeObject("If you guess the word correctly, you move onto another category. If not, you have 2 more words to guess at in this category. If you cannot guess any words in a category, you lose. If you guess one word from each category, you win.");
                clientOutput.writeObject("To play, you will be given choices on which category you'd like to guess words from. To begin, your choices are 'US States', 'Food', or 'Coding Languages'. You can choose by entering a category exactly as listed and entering your response in the textfield below, and hitting 'Send guess'. ");
            }
            catch (Exception e) {}
        }

        // method to get and return the string of the category chosen
        public String getCategoryChoice() {

            while (true) {
                try {
                    clientOutput.writeObject("Please enter a category, your choices are: " + categoriesWords.keySet().toString());
                    String data = clientInput.readObject().toString();
                    clientOutput.writeObject(">> You entered: " + data);
                    callback.accept("Asked Client #" + count + " to input a category");
                    callback.accept("Client #" + count + " said: " + data);

                    for (String key : categoriesWords.keySet()) {
                        if (Objects.equals(data, key)) {
                            callback.accept("Client #" + count + " entered category: " + key);
                            clientOutput.writeObject("Your category is: " + key);
                            return key;
                        }
                    }

                    callback.accept("Client #" + count + " entered invalid category");
                    clientOutput.writeObject("Oops, that's not a valid category. Your choices to input are " + categoriesWords.keySet().toString());
                }
                catch (Exception e) {}
            }
        }

        // method to create and output the hidden word (the word display that looks like ------)
        public void outputHiddenWord() {
            hiddenWordOutput = "";
            for (int i = 0; i < currentWord.length(); i++) {
                hiddenWordOutput += "-";
            }

            try {
                clientOutput.writeObject("Your word is: " + hiddenWordOutput);
                clientOutput.writeObject("You have " + incorrectGuessesLeft + " incorrect guesses left");
            }
            catch (Exception e) {e.printStackTrace();};
        }

        // method to check if the user entered char is a single, non digit char and is present in the word
        public int checkValidCharacter(String input, String currentWord) {
            if (input.length() == 1 && !Character.isDigit(input.charAt(0))) {
                char c1 = input.charAt(0);

                for (int i = 0; i < currentWord.length(); i++) {
                    if (c1 == currentWord.charAt(i)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        // method to replace the dash in the hidden word with the correctly entered character
        public String replaceCharAtIndex(int letterIndex, String hiddenWord, String currentWord) {
            StringBuilder revealedLetterString = new StringBuilder(hiddenWord);
            revealedLetterString.setCharAt(letterIndex, currentWord.charAt(letterIndex));
            return revealedLetterString.toString();
        }
    }
}



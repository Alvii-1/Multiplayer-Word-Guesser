import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIClient extends Application {

    // initializations
    public static int port;
    ListView<String> clientLogOutputList = new ListView<>();
    Scene clientConnectScreen, clientGameScreen;
    Button joinServer, checkPort, sendData;
    TextField portInputTextfield, clientInput;
    Client client;

    public static void main(String[] args) {
        launch(args);
    }

    // method to take the user's inputted port # and check its validity, if it is all digits and within port ranges
    public boolean checkValidPort(String userInput) {
        if (!userInput.matches("\\d+")) {
            return false;
        }

        port = Integer.parseInt(userInput);

        return port >= 1024 && port <= 65535;
    }

    // getter for the port number, used by the Client class
    public static int getPort() {return port;}

    // building the log-on screen
    public Scene buildClientConnectScreen() {
        Label label = new Label("> Please enter the port you of the server you would like to connect to. Enter digits between 1024-65535.");
        label.getStyleClass().add("label");

        joinServer = new Button("> Join Server");
        joinServer.setDisable(true);
        joinServer.getStyleClass().add("rounded-button");

        checkPort = new Button("> Check Port");
        checkPort.getStyleClass().add("rounded-button");

        portInputTextfield = new TextField();
        portInputTextfield.setPromptText("Enter port number here");
        portInputTextfield.getStyleClass().add("rounded-textfield");

        BorderPane mainWindow = new BorderPane();
        mainWindow.getStyleClass().add("borderPanes");

        HBox portInputArea = new HBox(10, portInputTextfield, checkPort);
        portInputArea.setAlignment(Pos.CENTER);

        VBox centerScreen = new VBox(10, label, portInputArea, joinServer);
        centerScreen.setAlignment(Pos.CENTER);

        mainWindow.setCenter(centerScreen);

        clientConnectScreen = new Scene(mainWindow, 500, 400);
        clientConnectScreen.getStylesheets().add("/styles.css");
        return clientConnectScreen;

    }

    // building the gameplay screen on the client-side application
    public Scene buildClientGameScreen() {
        clientInput = new TextField();
        clientInput.setPromptText("Enter guess here!");
        clientInput.getStyleClass().add("rounded-textfield");

        sendData = new Button("> Send guess");
        sendData.getStyleClass().add("rounded-button");

        // when the sendData button iis clicked, it clears the textfield on the game screen and resets prompt text
        sendData.setOnAction(e->{
            client.send(clientInput.getText());
            clientInput.clear();
            clientInput.setPromptText("Enter guess here!");
        });

        clientLogOutputList.setCellFactory(param -> new ListCell<String>() {
            {
                // enable text wrapping for listView cells
                setWrapText(true);
                prefWidthProperty().bind(clientLogOutputList.widthProperty().subtract(2)); // adjust margin
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item); // Set the text to the cell
            }
        });

        HBox inputs = new HBox(10, clientInput, sendData);
        inputs.setAlignment(Pos.CENTER);

        VBox clientScreen = new VBox(10, clientLogOutputList, inputs);
        clientScreen.setAlignment(Pos.CENTER);
        clientScreen.getStyleClass().add("borderPanes");

        clientGameScreen = new Scene(clientScreen, 500, 700);
        clientGameScreen.getStylesheets().add("/styles.css");
        return clientGameScreen;
    }

    // start method for javaFX
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Word Guessing Game (Client)");
        primaryStage.setScene(buildClientConnectScreen());

        // the checkPort button can be pressed to check validity of the port entered in the text field
        checkPort.setOnAction(e-> {
            if (checkValidPort(portInputTextfield.getText())) {
                joinServer.setDisable(false);                                       // if the port is valid, enable the joinServer button
                port = Integer.parseInt(portInputTextfield.getText());              // get the port number from the string given in the text field
                checkPort.setText("Port available!");                               // change the checkPort button to reflect valid port entry
            }
            else {
                joinServer.setDisable(true);                                        // if the port is not valid, the button remains disabled
                portInputTextfield.clear();                                         // the input field is cleared
                portInputTextfield.setPromptText("Please enter a valid port!");     // and the prompt text changes to reflect invalid port entry
            }
        });

        // once the joinServer button is clicked, the scene changes and runs the client application
        joinServer.setOnAction(e-> {
            primaryStage.setTitle("Word Guessing Game (Client connected on port: " + port + ")");
            primaryStage.setScene(buildClientGameScreen());
            client = new Client(data->{Platform.runLater(()->{clientLogOutputList.getItems().add(data.toString());});});
            client.start();
        });

        primaryStage.show();
    }
}
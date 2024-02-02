import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIServer extends Application {

    public static int port;
    ListView<String> serverLogOutputList = new ListView<>();
    Scene serverStartScreen, serverLogScreen;
    Server serverHost;
    Button startServer, checkPort;
    TextField portInputTextfield;

    public static void main(String[] args) {
        launch(args);
    }

    // getter method to return the port number entered by the user, after being checked. Is used by Server
    public static int getPort() {
        return port;
    }

    // method which will check the validity of the port num from user, in certain range and digits only
    public boolean checkValidPort(String userInput) {
        if (!userInput.matches("\\d+")) {
            return false;
        }

        port = Integer.parseInt(userInput);

        return port >= 1024 && port <= 65535;
    }

    // method to build the initial screen which asks user to enter the port
    public Scene buildServerStartupScreen() {

        Label label = new Label("> Please enter the port to host this server on. Enter digits between 1024-65535.");
        label.getStyleClass().add("label");

        startServer = new Button("> Start Server");
        startServer.setDisable(true);
        startServer.getStyleClass().add("rounded-button");

        checkPort = new Button("> Check Port");
        checkPort.getStyleClass().add("rounded-button");

        portInputTextfield = new TextField();
        portInputTextfield.setPromptText("Enter port number here.");
        portInputTextfield.getStyleClass().add("rounded-textfield");

        BorderPane mainWindow = new BorderPane();
        mainWindow.getStyleClass().add("borderPanes");

        HBox portInputArea = new HBox(10, portInputTextfield, checkPort);
        portInputArea.setAlignment(Pos.CENTER);

        VBox centerScreen = new VBox(10, label, portInputArea, startServer);
        centerScreen.setAlignment(Pos.CENTER);

        mainWindow.setCenter(centerScreen);

        serverStartScreen = new Scene(mainWindow, 500, 400);
        serverStartScreen.getStylesheets().add("/styles.css");
        return serverStartScreen;
    }

    // main screen, this is the log view for the server
    public Scene buildServerLogScreen() {

        VBox log = new VBox(10, serverLogOutputList);
        log.setAlignment(Pos.CENTER);
        log.getStyleClass().add("borderPanes");

        serverLogScreen = new Scene(log, 500, 700);
        serverLogScreen.getStylesheets().add("/styles.css");
        return serverLogScreen;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Word Guessing Game (Server)");
        primaryStage.setScene(buildServerStartupScreen());

        // the checkPort button can be pressed to check validity of the port entered in the text field
        checkPort.setOnAction(e-> {
            if (checkValidPort(portInputTextfield.getText())) {
                startServer.setDisable(false);
                port = Integer.parseInt(portInputTextfield.getText());
                checkPort.setText("Port available!");
            }
            else {
                startServer.setDisable(true);
                portInputTextfield.clear();
                portInputTextfield.setPromptText("Please enter a valid port!");
            }
        });

        // when checkPort ensures a port entry is valid, it enables the startServer button which makes the log view
        startServer.setOnAction(e-> {
            primaryStage.setTitle("Word Guessing Game (Server hosted on port: " + port + ")");
            primaryStage.setScene(buildServerLogScreen());
            serverHost = new Server(data -> Platform.runLater(()->serverLogOutputList.getItems().add(data.toString())));
        });

        primaryStage.show();
    }
}
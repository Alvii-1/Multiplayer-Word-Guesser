import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
    Socket clientSocket;
    ObjectOutputStream clientOutput;
    ObjectInputStream clientInput;
    private Consumer<Serializable> callback;
    int portInput;
    String hostAddress;

    // constructor initializaes callback, the host address, and the port with the getPort method
    Client (Consumer<Serializable> call) {
        callback = call;
        hostAddress = "127.0.0.1";
        portInput = GUIClient.getPort();
    }

    // run method for the Client class
    public void run() {
        try {
            clientSocket = new Socket(hostAddress, portInput);
            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInput = new ObjectInputStream(clientSocket.getInputStream());
        }
        catch (Exception e) {}

        while (true) {
            try {
                String msg = clientInput.readObject().toString();
                callback.accept(msg);
            }
            catch (Exception e) {}
        }
    }

    // send method to send the data from client to server
    public void send(String data) {
        try {
            clientOutput.writeObject(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ru.itis.kurguskina.client;
import ru.itis.kurguskina.GameLogic;
import javafx.scene.input.KeyCode;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private Socket socket;
    private ClientThread clientThread;
    private GameLogic gameLogic;

    public void setPacManModel(GameLogic gameLogic){
        this.gameLogic = gameLogic;
    }

    public boolean sendParameter(KeyCode code) {
        boolean isSuccessful = false;
        String direction = "";
        if (code == KeyCode.LEFT) {
            direction = "Left";
        } else if (code == KeyCode.RIGHT) {
            direction = "Right";
        } else if (code == KeyCode.UP) {
            direction = "Up";
        } else if (code == KeyCode.DOWN) {
            direction = "Down";
        } else {
            isSuccessful = false;
        }

        try {
            clientThread.getOutputStream().write(direction);
            clientThread.getOutputStream().flush();
            clientThread.run();
            isSuccessful = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccessful;
    }

    public void start() throws IOException {
        socket = new Socket("127.0.0.1", 9955);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        clientThread = new ClientThread(inputStream, outputStream, this, gameLogic);
        new Thread(clientThread).start();
    }
}

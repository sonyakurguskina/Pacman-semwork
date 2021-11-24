package ru.itis.kurguskina.client;
import ru.itis.kurguskina.GameLogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientThread implements Runnable{
    private final BufferedReader inputStream;
    private final BufferedWriter outputStream;
    private final GameLogic gameLogic;
    private final Client client;
    private boolean isRunning = true;

    public ClientThread(BufferedReader inputStream, BufferedWriter outputStream, Client client, GameLogic gameLogic) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.gameLogic = gameLogic;
        this.client = client;
    }

    public BufferedWriter getOutputStream() {
        return outputStream;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                String input = inputStream.readLine();
                if(input != null) {
                    GameLogic.Direction direction = GameLogic.Direction.NONE;
                    if (input.equals("Left")) {
                        direction = GameLogic.Direction.LEFT;
                    } else if (input.equals("Right")) {
                        direction = GameLogic.Direction.RIGHT;
                    } else if (input.equals("Up")) {
                        direction = GameLogic.Direction.UP;
                    } else if (input.equals("Down")) {
                        direction = GameLogic.Direction.DOWN;
                    }
                    gameLogic.setCurrentDirection(direction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

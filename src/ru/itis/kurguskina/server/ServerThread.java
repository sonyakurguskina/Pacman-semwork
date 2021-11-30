package ru.itis.kurguskina.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;

public class ServerThread implements Runnable {
    private final BufferedReader inputStream;
    private final BufferedWriter outputStream;
    private final Server server;
    private boolean isRunnung = true;

    public ServerThread(BufferedReader inputStream, BufferedWriter outputStream, Server server) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.server = server;
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    public BufferedWriter getOutputStream() {
        return outputStream;
    }

    public Server getServer() {
        return server;
    }

    public void stop() {
        isRunnung = false;
    }

    @Override
    public void run() {
        try {
            while (isRunnung) {
                String input = inputStream.readLine();
                server.sendParameter(input, this);
            }
        } catch (SocketException socketException) {
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

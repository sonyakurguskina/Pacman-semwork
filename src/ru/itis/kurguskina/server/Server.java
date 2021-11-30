package ru.itis.kurguskina.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 9999;
    private ServerSocket socket;
    private final List<ServerThread> clients = new ArrayList<>();
    private String parameter = "";
    private boolean isRunning = true;

    public void start() throws IOException {
        socket = new ServerSocket(PORT);

        while (isRunning) {
            Socket clientSocket = socket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            ServerThread serverThread = new ServerThread(input, output, this);
            clients.add(serverThread);
            new Thread(serverThread).start();
        }
    }

    public void sendParameter(String parameter, ServerThread sender) throws IOException {
        this.parameter = parameter;

        for (ServerThread client : clients) {
            if (client.equals(sender)){
                continue;
            }
            client.getOutputStream().write(parameter+ "\n");
            client.getOutputStream().flush();
        }
    }

    public void removeClient(ServerThread serverThread) {
        clients.remove(serverThread);
    }

    public static void main(String[] args) throws IOException {
        Server gameServer = new Server();
        gameServer.start();
    }

    public String getParameter() {
        return parameter;
    }

    public void stop() {
        for (ServerThread serverThread : clients) {
            serverThread.stop();
        }
        isRunning = false;
    }
}

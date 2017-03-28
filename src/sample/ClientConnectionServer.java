package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 100560820 on 3/27/2017.
 */
public class ClientConnectionServer extends Thread {
    private ServerSocket serverSocket;
    private int port;
    private boolean running;

    public ClientConnectionServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        running = true;
    }

    public void handleRequests() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("MoonServer v0.9 listening on port " + port);
                Socket socket = serverSocket.accept();
                System.out.println("Found");
                ClientConnectionHandler handler = new ClientConnectionHandler(socket);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
            }
        } catch (IOException e) {
            System.out.println("Socket Closed");
        }
    }

    public void quit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Socket Closed");
        }
    }

    @Override
    public void run() {

        try {
            this.handleRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

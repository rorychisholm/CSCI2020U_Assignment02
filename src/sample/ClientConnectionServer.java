package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by 100560820 on 3/27/2017.
 */
public class ClientConnectionServer extends Thread{
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
            int i = 0;
            Vector<Thread> handlerThread = new Vector<Thread>();
            while (!serverSocket.isClosed()) {
                System.out.println("MoonServer v0.9 listening on port " + port);
                Socket socket = serverSocket.accept();
                System.out.println("Found");
                ClientConnectionHandler handler = new ClientConnectionHandler(socket);
                handlerThread.add(i, new Thread(handler));
                handlerThread.get(i).start();
                i++;
            }
        } catch (IOException e) {
            System.out.println("Socket Closed");
        }
    }

    public void quit() {
        try {
            serverSocket.close();
            System.out.println("Socket Closing");
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

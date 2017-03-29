package sample;

import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by 100560820 on 3/28/2017.
 */
public class ClientConnection implements Runnable{
        public Socket socket;
        public BufferedReader in;
        public PrintWriter out;
        public Stage stage;
        int port;
        String hostName;

    public ClientConnection(int port, String hostName){
        this.port = port;
        this.hostName = hostName;
    }

    @Override
    public void run() {

        try {
            // connect to the server (3-way connection establishment handshake)
            socket = new Socket(hostName, port);

            // wrap the input streams into readers and writers
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            // send the HTTP request GET /yahoo/yahoo.html HTTP/1.0\n\n
            String request = "GET /Testing.txt HTTP/1.0"; // /yahoo/yahoo.html
            String header = "Host: " + hostName;
            String delim = "\r\n";
            out.print(request + delim + header + delim + delim); //"GET /yahoo/yahoo.html HTTP/1.0" + "\r\n" + "Host: " + hostName +"\r\n"+ "\r\n"
            out.flush();

            // read and print the response
            String response;
            System.out.println("Response:");
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }

            // close the connection (3-way tear down handshake)
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {
    private BorderPane layout;
    static int port = 8080;
    static String hostName = "127.0.0.1";
    public ObservableList<String> observServList, observClieList;

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();*/

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Assignment02");


        ListView<String> clientList = new ListView<String>();
        clientList.setEditable(true);

        ListView<String> serverList = new ListView<String>();
        serverList.setEditable(true);

        ////////////////////////////////////////BUTTONS////////////////////////////////////////
        GridPane editArea = new GridPane();

        // ADD BUTTON
        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    ClientConnectionServer server = new ClientConnectionServer(port);
                    server.start();
                    System.out.println("Uploading");
                    observServList = sendDIRCmd();
                    server.quit();
                    clientList.setItems(observClieList);
                    serverList.setItems(observServList);
                    primaryStage.setOnCloseRequest(oe -> server.quit());
                }catch(IOException ie){}
            }
        });
        editArea.add(uploadButton, 0, 0);

        // DOWNLOAD BUTTON
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                try {
                    ClientConnectionServer server = new ClientConnectionServer(port);
                    System.out.println(serverList.getEditingIndex());
                    server.start();
                    System.out.println("Downloading");
                    if (serverList.getEditingIndex() != -1) {
                        downloadFileCmd(observServList.get(serverList.getEditingIndex()));
                    }
                    observClieList = listFiles();
                    observServList = sendDIRCmd();
                    server.quit();
                    clientList.setItems(observClieList);
                    serverList.setItems(observServList);
                    primaryStage.setOnCloseRequest(oe -> server.quit());
                }catch (IOException ie){}

            }
        });
        editArea.add(downloadButton, 1, 0);
        ////////////////////////////////////////END OF BUTTONS////////////////////////////////////////

        updateList(clientList, serverList);
        SplitPane fileView = new SplitPane(clientList,serverList);
        fileView.setDividerPositions(0.50);

        layout = new BorderPane();
        layout.setTop(editArea);
        layout.setCenter(fileView);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();


        //primaryStage.setOnCloseRequest(e -> server.quit());
    }

    public void client() {
        Socket socket;
        BufferedReader in;
        PrintWriter out;

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

    public ObservableList<String> sendDIRCmd(){
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        try {
            // connect to the server (3-way connection establishment handshake)
            socket = new Socket(hostName, port);

            // wrap the input streams into readers and writers
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            // send the HTTP request GET /yahoo/yahoo.html HTTP/1.0\n\n
            out.println("DIR");
            out.flush();

            // read and print the response
            String response;
            System.out.println("Response:");
            response = in.readLine();
            //System.out.println(response);
            String[] responseParts = response.split(" ");
            ObservableList<String> tempList = FXCollections.observableArrayList();
            for (int i = 0; i < responseParts.length; i++) {
                tempList.add(responseParts[i]);
                System.out.println(responseParts[i]);
            }
            // close the connection (3-way tear down handshake)
            out.close();
            in.close();
            socket.close();
            return tempList;
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("NULL");
        return null;
    }

    public void downloadFileCmd(String fileName){
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        try {
            // connect to the server (3-way connection establishment handshake)
            socket = new Socket(hostName, port);

            // wrap the input streams into readers and writers
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            // send the HTTP request GET /yahoo/yahoo.html HTTP/1.0\n\n
            out.println("DOWNLOAD " +"/"+ fileName);
            out.flush();

            // read and print the response
            String response;
            File newFile = new File("ClientStorage" ,fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }else{
                newFile.delete();
                newFile.createNewFile();
            }
            PrintWriter fout = new PrintWriter(newFile);
            System.out.println("Response:");
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                fout.println(response);
            }
            fout.close();

            // close the connection (3-way tear down handshake)
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public ObservableList<String> listFiles(){
        File clientStorage = new File("ClientStorage");
        if(!clientStorage.isDirectory()){
            clientStorage.mkdir();
        }
        ObservableList<String> tempList = FXCollections.observableArrayList();
        File fileList[] = clientStorage.listFiles();
        for (int i = 0; i< fileList.length; i++){
            tempList.add(fileList[i].getName());
        }
        return tempList;
    }

    public void updateList(ListView<String> clientList, ListView<String> serverList){
        try {
            ClientConnectionServer server = new ClientConnectionServer(port);
            server.start();
            observClieList = listFiles();
            observServList = sendDIRCmd();
            server.quit();
            clientList.setItems(observClieList);
            serverList.setItems(observServList);
        }catch(IOException ie){}
    }


    public static void main(String[] args) {
        launch(args);
    }
}

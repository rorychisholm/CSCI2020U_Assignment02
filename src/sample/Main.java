package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private BorderPane layout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();*/

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Assignment02");

        ////////////////////////////////////////BUTTONS////////////////////////////////////////
        GridPane editArea = new GridPane();

        // ADD BUTTON
        Button uploadButton = new Button("Upload");
        uploadButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e) {

            }
        });
        editArea.add(uploadButton, 0, 0);

        // DOWNLOAD BUTTON
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e) {

            }
        });
        editArea.add(downloadButton, 1, 0);
        ////////////////////////////////////////END OF BUTTONS////////////////////////////////////////

        SplitPane fileView = new SplitPane();
        fileView.getItems();
        fileView.setDividerPositions(0.50);

        layout = new BorderPane();
        layout.setTop(editArea);
        layout.setCenter(fileView);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        int port = 8080;
        String hostName = "127.0.0.1";

        ClientConnectionServer server = new ClientConnectionServer(port);
        server.start();
        primaryStage.setOnCloseRequest(e -> server.quit());
    }



    public static void main(String[] args) {
        launch(args);
    }
}

package ru.gb;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.fxml.FXMLLoader.load;


public class NetChatApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = load(getClass().getResource("/NetChatApplication.fxml"));
        primaryStage.setTitle("My Net Chat");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();

    }
}

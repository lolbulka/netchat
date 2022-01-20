package ru.gb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NetChatApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("NetChatApplication.fxml"));
        primaryStage.setTitle("My Net Chat");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

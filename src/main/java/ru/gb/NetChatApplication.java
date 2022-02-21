package ru.gb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NetChatApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/NetChatApplication.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("My Net Chat");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
        NetChatController controller = loader.getController();
        primaryStage.setOnCloseRequest(controller.getCloseEventHandler());

    }
}

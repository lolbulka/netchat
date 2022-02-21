package ru.gb;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import ru.gb.client.ChatClient;

import java.io.*;

import static ru.gb.Command.*;

public class NetChatController {
    ChatClient client;
    FileWriter fileWriter;
    File file;
    BufferedReader bufferedReader;
    private String login = "";
    @FXML
    private Button changeNickButton;
    @FXML
    private TextField nickField;
    @FXML
    private HBox changeNickBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField loginField;
    @FXML
    private HBox messageBox;
    @FXML
    private ListView<String> clientList;
    @FXML
    private TextArea tipArea;
    @FXML
    private TextField answerField;
    @FXML
    private TextField answer;
    @FXML
    private Button loginButton;
    private final EventHandler<WindowEvent> closeEventHandler = event -> onDisconnectSelect();

    public NetChatController() {
        Platform.runLater(this::activateButton);
    }

    public void onClickSendButton(ActionEvent actionEvent) {
        final String message = answerField.getText();
        if (message != null && !message.isEmpty()) {
            client.sendMessage(message);
            answerField.clear();
            answerField.requestFocus();
        }
    }

    public void onClearChat() {
        tipArea.clear();
    }

    public void onDisconnectSelect() {
        if (client != null) {
            client.sendMessage(END.getCommand());
            answer.setVisible(false);
        }
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String message) {
        Platform.runLater(() -> tipArea.appendText(message + "\n"));
        writeInFile(message);
    }

    private void writeInFile(String message) {
        try {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void btnAuthClick() {
        client = new ChatClient(this);
        client.sendMessage(AUTH.getCommand() + " " + loginField.getText() + " " + passwordField.getText());
        login = loginField.getText();
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            answerField.clear();
            final String message = answerField.getText();
            final String client = clientList.getSelectionModel().getSelectedItem();
            answerField.setText(PRIVATE_MESSAGE.getCommand() + " " + client + " " + message);
            answerField.requestFocus();
            answerField.selectEnd();
        }
    }

    public void setAuth(boolean isAuthSuccess) {
        loginBox.setVisible(!isAuthSuccess);
        answer.setVisible(isAuthSuccess);
        changeNickBox.setVisible(isAuthSuccess);
        messageBox.setVisible(isAuthSuccess);
        if (!isAuthSuccess){
            client = null;
        }
    }

    public void readFile() {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            LineNumberReader count = new LineNumberReader(new FileReader(file));
            String line;
            try {
                count.skip(Long.MAX_VALUE);
                int result = count.getLineNumber() + 1;
                int strCount = 100;
                if (result > strCount) {
                    result -= strCount;
                    for (int i = 1; i < result; ++i)
                        bufferedReader.readLine();
                }
                while ((line = bufferedReader.readLine()) != null) {
                    String finalLine = line;
                    Platform.runLater(() -> tipArea.appendText(finalLine + "\n"));
                }
                count.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateClientList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }

    public void errMessage(String authMsg) {
        answer.setVisible(true);
        answer.clear();
        answer.appendText(authMsg);
    }

    public void activateButton(){
        loginButton.disableProperty().bind(
                Bindings.isEmpty(loginField.textProperty())
                        .or(Bindings.isEmpty(passwordField.textProperty()))
        );
        changeNickButton.disableProperty().bind(
                Bindings.isEmpty(nickField.textProperty())
        );
    }

    public void btnChangeNickClick() {
        client.sendMessage(CHANGENICK.getCommand() + " " + nickField.getText());

    }

    public void useFile() {
        String path = new File("").getAbsolutePath();
        String newDir = "\\history";
        boolean mkdir = new File(path + newDir).mkdir();
        file = new File(path + newDir + File.separator + "history_" + login + ".txt");
        if (!file.exists()) {
            try {
                final boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println(file.getAbsolutePath());
            System.out.println(file.getName());
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }
}


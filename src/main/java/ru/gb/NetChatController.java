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

import static ru.gb.Command.*;

public class NetChatController {
    ChatClient client;
    HistoryHandler historyHandler;
    private String login = "";
    JdbcWork jdbcWork;

    public TextArea getTipArea() {
        return tipArea;
    }

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
    private final EventHandler<WindowEvent> closeEventHandler = event -> {
        onDisconnectSelect();
    };

    public NetChatController() {
        jdbcWork = new JdbcWork();
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
        }
        if (historyHandler != null) {
            historyHandler.closeWriter();
        }
        answer.setVisible(false);
    }

    public void addMessage(String message) {
        Platform.runLater(() -> tipArea.appendText(message + "\n"));
        historyHandler.writeInFile(message);
    }

    //проверка ника и подключение нового клиента

    public void btnAuthClick() {
        login = loginField.getText();
        jdbcWork.connect();
        String nick = jdbcWork.getNickFromDB(loginField.getText(), passwordField.getText());
        if (nick != null && !(jdbcWork.isInUse(loginField.getText()))) {
            client = new ChatClient(this, historyHandler = new HistoryHandler(login));
            client.sendMessage(AUTH.getCommand() + " " + loginField.getText() + " " + passwordField.getText() +
                    " " + nick);
        } else {
            if (nick == null) {
                errMessage("Неверные логин или пароль");
            } else {
                errMessage("Ник уже занят");
            }
        }
        jdbcWork.disconnect();
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

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }
}


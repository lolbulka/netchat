package ru.gb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
public class NetChatController {


    @FXML
    private TextArea tipArea;
    @FXML
    private TextField answerField;

    public NetChatController() {

    }

    public void onClickSendButton(ActionEvent actionEvent) {
        final String text = answerField.getText();
        if (text != null && !text.isEmpty()) {
            tipArea.appendText(text + "\n");
            answerField.clear();
        }
    }

    public void onClearChat(ActionEvent actionEvent) {
        tipArea.clear();
    }

    public void onExitSelect(ActionEvent actionEvent) {
        System.exit(0);
    }
}

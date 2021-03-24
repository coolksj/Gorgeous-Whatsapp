package application.client.chatwindow;

import Util.StringUtil;
import application.client.login.LoginController;
import application.client.util.VoiceRecorder;
import application.client.util.VoiceUtil;
import application.messages.MessageBean;
import application.messages.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ChatPaneController implements Initializable  {

    @FXML
    private ListView chatPane;
    @FXML
    private TextArea messageBox;
    @FXML
    ImageView microphoneImageView;

    LinkedList<MessageBean> msgList = new LinkedList<>();

    Image microphoneActiveImage = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    Image microphoneInactiveImage = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());
    private User user_;

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }


    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            MessageBean textMsg = new MessageBean();
            textMsg.content = msg;
            textMsg.isMyself = true;
            chatPane.getItems().add(textMsg);

            LoginController.getInstance().GetEngine().SendText(user_.jid, msg);
            messageBox.clear();
        }
    }

    public void recordVoiceMessage() throws IOException {
        if (VoiceUtil.isRecording()) {
            Platform.runLater(() -> {
                        microphoneImageView.setImage(microphoneInactiveImage);
                    }
            );
            VoiceUtil.setRecording(false);
        } else {
            if (VoiceRecorder.captureAudio()) {
                Platform.runLater(() -> {
                            microphoneImageView.setImage(microphoneActiveImage);

                        }
                );
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* Added to prevent the enter from adding a new line to inputMessageBox */
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (ke.isControlDown()) {
                    messageBox.appendText("\r\n");
                } else {
                    try {
                        sendButtonAction();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ke.consume();
            }
        });
    }


    public void setMessageList() {
        ObservableList<MessageBean> msgs = FXCollections.observableList(msgList);
        chatPane.setItems(msgs);
        MessageCellRender render = new MessageCellRender();
        render.SetChatPane(chatPane);
        render.SetUserInfo(user_);
        chatPane.setCellFactory(render);
    }

    public void InitData(User user) {
        user_ = user;
        setMessageList();
    }

    public void AddMessage(Message.WhatsMessage.WhatsAppMessage msg) {
        String conversion = msg.getConversation();
        MessageBean textMsg = new MessageBean();
        textMsg.isMyself = false;
        if (StringUtil.isEmpty(conversion)) {
            textMsg.content = "unsupported message";
        } else {
            textMsg.content = conversion;
        }
        chatPane.getItems().add(textMsg);
    }

    public void OnSelectImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("SendImage");

        FileChooser.ExtensionFilter imageFilter =   new FileChooser.ExtensionFilter("Image (.jpg,.jpeg,.png)", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        fileChooser.setSelectedExtensionFilter(imageFilter);
        File file = fileChooser.showOpenDialog(messageBox.getScene().getWindow());
        if (file != null) {
            LoginController.getInstance().GetEngine().SendMedia(user_.jid, file.getAbsolutePath(), "image");
        }
    }
}

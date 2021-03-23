package application.client.chatwindow;

import application.client.login.LoginController;
import application.messages.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A Class for Rendering users images / name on the userlist.
 */
class CellRender implements Callback<ListView<User>,ListCell<User>>{
        @Override
    public ListCell<User> call(ListView<User> p) {
        ListCell<User> cell = new ListCell<User>(){
            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);
                if (user == null) {
                    return;
                }
                try {
                    HBox hBox = FXMLLoader.load(getClass().getClassLoader().getResource("views/ContactItem.fxml"));

                    ImageView statusImageView = (ImageView)hBox.lookup("#statusImage");
                    Image statusImage;
                    if (user.online) {
                        statusImage = new Image(getClass().getClassLoader().getResource("images/online.png" ).toString(), 16, 16,true,true);
                    } else {
                        statusImage = new Image(getClass().getClassLoader().getResource("images/busy.png").toString(), 16, 16,true,true);
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                            long l = Long.parseLong(user.last) * 1000;
                            String d = format.format(l);
                            Label last = (Label)hBox.lookup("#last");
                            last.setText(d);
                        }
                        catch (Exception e) {

                        }
                    }
                    statusImageView.setImage(statusImage);
                    //head
                    ImageView pictureImageView = (ImageView)hBox.lookup("#pictureImageView");
                    Image image;
                    if (user.picture == null) {
                        image =  new Image(getClass().getClassLoader().getResource("images/default.png").toString(),50,50,true,true);
                    } else {
                        InputStream buffin = new ByteArrayInputStream(user.picture );
                        image = new Image(buffin);
                    }
                    pictureImageView.setImage(image);
                    Label name = (Label)hBox.lookup("#name");
                    name.setText(user.name);

                    Button refreshHead = (Button)hBox.lookup("#refreshHead");
                    refreshHead.setOnAction(event -> LoginController.getInstance().GetEngine().GetHDHead(user.jid));

                    setGraphic(hBox);
                    setOnMouseClicked(event -> {
                        switch (event.getButton()) {
                            case PRIMARY:   {
                                ChatController.getInstance().AddChatPane(user);
                            }
                            break;
                            case SECONDARY: {
                                ShowMenu(hBox, event, user);
                            }
                            break;
                        }
                    });
                }
                    catch (Exception e) {
                }
            }
        };

        return cell;
    }

    void ShowMenu(HBox hBox ,MouseEvent event, User user) {
        ContextMenu contextMenu = new ContextMenu();
        // 菜单项
        MenuItem deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                ChatController.getInstance().DeleteSession(user);
            }
        });
        contextMenu.getItems().addAll(deleteMenu);
        contextMenu.show(hBox, event.getScreenX(), event.getScreenY());
    }
}
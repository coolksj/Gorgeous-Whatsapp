package application.client.chatwindow;

import application.messages.MessageBean;
import application.messages.User;
import application.messages.bubble.BubbleSpec;
import application.messages.bubble.BubbledLabel;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;


public class MessageCellRender implements Callback<ListView, ListCell> {
    ListView chatPane_;
    User user_;
    Image myselfImage;
    Image otherImage;
    @Override
    public ListCell call(ListView param) {
        ListCell<MessageBean> cell = new ListCell<MessageBean>(){
            @Override
            protected void updateItem(MessageBean msg, boolean bln) {
                super.updateItem(msg, bln);
                setGraphic(null);
                setText(null);
                if (null == msg) {
                    return;
                }

                try {
                    HBox x = new HBox();
                    if (msg.isMyself) {
                        ImageView profileImage = new ImageView(myselfImage);
                        profileImage.setFitHeight(32);
                        profileImage.setFitWidth(32);

                        BubbledLabel bl6 = new BubbledLabel();
                        bl6.setText(msg.content);

                        bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                                null, null)));
                        x.setMaxWidth(chatPane_.getWidth() - 20);
                        x.setAlignment(Pos.TOP_RIGHT);
                        bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                        x.getChildren().addAll(bl6, profileImage);
                    } else {
                        ImageView profileImage = new ImageView(otherImage);
                        profileImage.setFitHeight(32);
                        profileImage.setFitWidth(32);
                        BubbledLabel bl6 = new BubbledLabel();
                        bl6.setText(msg.content);

                        bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,null, null)));
                        bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                        x.getChildren().addAll(profileImage, bl6);
                    }
                    setGraphic(x);
                }
                catch (Exception e) {

                }
            }
        };
        return cell;
    }

    public void SetChatPane(ListView chatPane) {
        chatPane_ = chatPane;
    }

    public void SetUserInfo(User user) {
        user_ = user;
        if (user.picture != null) {
            otherImage = new Image(new ByteArrayInputStream(user.picture));
        } else {
            otherImage = new Image(getClass().getClassLoader().getResource("images/default.png").toString(),32,32,true,true);
        }

        myselfImage = new Image(getClass().getClassLoader().getResource("images/default.png").toString(),32,32,true,true);
    }
}

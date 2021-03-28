package application.client;

import Util.StringUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StageFactory {
    public static Stage Create(String fxml, int width, int height, String title) {
        return new Stage() {
            {
                try {
                    initModality(Modality.APPLICATION_MODAL);
                    Parent root = FXMLLoader.load(getClass().getResource(fxml));
                    Scene scene = new Scene(root, width, height);
                    scene.setFill(Color.TRANSPARENT);
                    setScene(scene);
                    if (!StringUtil.isEmpty(title)) {
                        setTitle(title);
                    }
                }
                catch (Exception e) {

                }
            }
        };
    }
}

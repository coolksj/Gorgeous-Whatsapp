package application.client;

import Util.StringUtil;
import application.client.login.LoginController;
import application.client.util.ToastUtil;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class AddContactController {
    public TextField cc;
    public TextField phone;
    public TextField filePath;

    public void OnAddContact(ActionEvent actionEvent) {
        String _cc = cc.getText();
        String _phone = phone.getText();
        if (StringUtil.isEmpty(_cc) || StringUtil.isEmpty(_phone)) {
            ToastUtil.toast("cc or phone can not empty");
            return;
        }

        ArrayList<String> phones = new ArrayList<>();
        if (_cc.startsWith("+")) {
            phones.add( _cc + _phone);
        } else {
            phones.add("+" + _cc + _phone);
        }

        LoginController.getInstance().GetEngine().SyncContact(phones);
        cc.getScene().getWindow().hide();
    }

    public void OnSelectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/out/"));
        File file = fileChooser.showOpenDialog(filePath.getScene().getWindow());
        if (file != null) {
            filePath.setText(file.getAbsolutePath());
        }
    }

    public void OnAddContactFromFile(ActionEvent actionEvent) {
        String _filePath = filePath.getText();
        if (StringUtil.isEmpty(_filePath)) {
            ToastUtil.toast("path can not be empty");
            return;
        }

        try {
            ArrayList<String> phones = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(_filePath));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("+")) {
                    phones.add(line);
                } else {
                    phones.add("+" + line);
                }
            }

            LoginController.getInstance().GetEngine().SyncContact(phones);
            cc.getScene().getWindow().hide();

            br.close();
        }
        catch (Exception e) {

        }
    }
}

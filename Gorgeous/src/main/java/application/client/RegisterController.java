package application.client;

import Env.DeviceEnv;
import Gorgeous.GorgeousEngine;
import Util.StringUtil;
import application.client.util.ToastUtil;
import axolotl.AxolotlManager;
import com.google.protobuf.ByteString;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import jni.Register;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.JSONObject;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.io.File;
import java.util.Base64;

public class RegisterController {
    public TextField cc;
    public TextField phone;
    public TextField smsCode;
    public Button registerBtn;
    public Button codeRequestBtn;
    public TextField configPath;

    AxolotlManager axolotlManager;
    Thread registerThread;


    public void OnClickCodeRequest(ActionEvent actionEvent) {
        if ((cc.getText().isEmpty()) || (phone.getText().isEmpty())) {
            ToastUtil.toast("cc or phone is empty");
            return;
        }

        if (null == axolotlManager) {
            String dataDir = System.getProperty("user.dir") + "/register";
            new File(dataDir).mkdirs();

            String dbPath = new File(dataDir, cc.getText() + phone.getText()).getAbsolutePath();
            configPath.setText(dbPath);
            axolotlManager = new AxolotlManager(dbPath);
        }
        codeRequestBtn.setDisable(true);
        registerThread = new Thread(() -> {
            String desc = "success";
            int code = -1;
            do {
                if (!CheckAccountExist()){
                    desc = "request exist failed";
                    break;
                }
                if (!RequestCode()) {
                    desc = "request code failed";
                    break;
                }
                desc = "wait and input code";
                code = 0;
            } while (false);

            String finalDesc = desc;
            int finalCode = code;
            Platform.runLater(() -> HandleResult(finalCode,finalDesc));
        });
        registerThread.start();
    }

    public void OnClickRegister(ActionEvent actionEvent) {
        if (smsCode.getText().isEmpty()) {
            return;
        }
        registerBtn.setDisable(true);
        registerThread = new Thread(() -> {
            JSONObject registerParams = new JSONObject();

            registerParams.put("cc", cc.getText());
            registerParams.put("in", phone.getText());

            registerParams.put("regid", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(axolotlManager.getLocalRegistrationId())));
            registerParams.put("identity", StringUtil.Base64UrlEncode(axolotlManager.GetIdentityKeyPair().getPublicKey().serialize(),1, 32));

            SignedPreKeyRecord signedPreKeyRecord = axolotlManager.LoadLatestSignedPreKey(false);
            registerParams.put("skey_id", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(signedPreKeyRecord.getId())));
            registerParams.put("skey_val",StringUtil.Base64UrlEncode(signedPreKeyRecord.getKeyPair().getPublicKey().serialize(),1 ,32));
            registerParams.put("skey_sig",StringUtil.Base64UrlEncode(signedPreKeyRecord.getSignature()));

            //sms code
            registerParams.put("code", smsCode.getText());

            String encodeParams = Register.Register(registerParams.toString(), axolotlManager.GetBytesSetting("env"));
            JSONObject encodeJosn = new JSONObject(encodeParams);
            if (encodeJosn.getInt("code") != 0) {
                return ;
            }
            //send http
            try {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(encodeJosn.getString("request"))
                        .addHeader("User-Agent", encodeJosn.getString("useragent"))
                        .addHeader("Accept-Charset", "UTF-8")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .get().build();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().hostnameVerifier((s, sslSession) -> true).build();;
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    DeviceEnv.AndroidEnv.Builder envBuilder = DeviceEnv.AndroidEnv.parseFrom(axolotlManager.GetBytesSetting("env")).toBuilder();
                    JSONObject responseJson = new JSONObject(response.body().string());
                    String status = responseJson.getString("status");
                    if (status.equals("ok")) {
                        envBuilder.setEdgeRoutingInfo(ByteString.copyFrom(Base64.getDecoder().decode(responseJson.getString("edge_routing_info"))));
                        String chatDnsDomain = responseJson.getString("chat_dns_domain");
                        if (StringUtil.isEmpty(chatDnsDomain)) {
                            envBuilder.setChatDnsDomain("fb");
                        } else {
                            envBuilder.setChatDnsDomain(chatDnsDomain);
                        }
                        axolotlManager.SetBytesSetting("env", envBuilder.build().toByteArray());
                        Platform.runLater(() -> ToastUtil.toast("register success, try login"));
                    }
                }
            }
            catch (Exception e) {

            }
        });
        registerThread.start();
    }


    void  HandleResult(int code ,String desc) {
        ToastUtil.toast(desc);
        codeRequestBtn.setDisable(false);
        smsCode.setText("");
        if (code == 0) {
            smsCode.setDisable(false);
            registerBtn.setDisable(false);
        } else {
            registerBtn.setDisable(true);
            smsCode.setDisable(true);
        }
    }

    boolean CheckAccountExist() {
        JSONObject existParams = new JSONObject();
        existParams.put("cc", cc.getText());
        existParams.put("in", phone.getText());

        existParams.put("regid", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(axolotlManager.getLocalRegistrationId())));
        existParams.put("identity", StringUtil.Base64UrlEncode(axolotlManager.GetIdentityKeyPair().getPublicKey().serialize(),1, 32));

        SignedPreKeyRecord signedPreKeyRecord = axolotlManager.LoadLatestSignedPreKey(false);
        existParams.put("skey_id", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(signedPreKeyRecord.getId())));
        existParams.put("skey_val",StringUtil.Base64UrlEncode(signedPreKeyRecord.getKeyPair().getPublicKey().serialize(),1 ,32));
        existParams.put("skey_sig",StringUtil.Base64UrlEncode(signedPreKeyRecord.getSignature()));


        String encodeParams = Register.EncodeExistRequest(existParams.toString(), axolotlManager.GetBytesSetting("env"));
        JSONObject encodeJosn = new JSONObject(encodeParams);
        if (encodeJosn.getInt("code") != 0) {
            return false;
        }
        axolotlManager.SetStringSetting("env", encodeJosn.getString("env"));

        //send http
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(encodeJosn.getString("request"))
                    .addHeader("User-Agent", encodeJosn.getString("useragent"))
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .get().build();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().hostnameVerifier((s, sslSession) -> true).build();;
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                DeviceEnv.AndroidEnv.Builder envBuilder = DeviceEnv.AndroidEnv.parseFrom(axolotlManager.GetBytesSetting("env")).toBuilder();
                JSONObject responseJson = new JSONObject(response.body().string());
                String status = responseJson.getString("status");
                if (status.equals("ok")) {
                    envBuilder.setEdgeRoutingInfo(ByteString.copyFrom(Base64.getDecoder().decode(responseJson.getString("edge_routing_info"))));
                    String chatDnsDomain = responseJson.getString("chat_dns_domain");
                    if (StringUtil.isEmpty(chatDnsDomain)) {
                        envBuilder.setChatDnsDomain("fb");
                    } else {
                        envBuilder.setChatDnsDomain(chatDnsDomain);
                    }
                    axolotlManager.SetBytesSetting("env", envBuilder.build().toByteArray());
                }
                return true;
            }
        }
        catch (Exception e) {

        }
      return false;
    }


    boolean RequestCode() {
        JSONObject existParams = new JSONObject();
        existParams.put("cc", cc.getText());
        existParams.put("in", phone.getText());

        existParams.put("regid", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(axolotlManager.getLocalRegistrationId())));
        existParams.put("identity", StringUtil.Base64UrlEncode(axolotlManager.GetIdentityKeyPair().getPublicKey().serialize(), 1, 32));

        SignedPreKeyRecord signedPreKeyRecord = axolotlManager.LoadLatestSignedPreKey(false);
        existParams.put("skey_id", StringUtil.Base64UrlEncode(GorgeousEngine.AdjustId(signedPreKeyRecord.getId())));
        existParams.put("skey_val", StringUtil.Base64UrlEncode(signedPreKeyRecord.getKeyPair().getPublicKey().serialize(), 1, 32));
        existParams.put("skey_sig", StringUtil.Base64UrlEncode(signedPreKeyRecord.getSignature()));


        String encodeParams = Register.CodeRequest(existParams.toString(), axolotlManager.GetBytesSetting("env"));
        JSONObject encodeJosn = new JSONObject(encodeParams);
        if (encodeJosn.getInt("code") != 0) {
            return false;
        }

        //send http
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(encodeJosn.getString("request"))
                    .addHeader("User-Agent", encodeJosn.getString("useragent"))
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .get().build();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().hostnameVerifier((s, sslSession) -> true).build();;
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return true;
            }
        }
        catch (Exception e) {

        }
        return  false;
    }
}

package application;

import application.messages.User;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserConfig {
    private Connection connection_;


    public UserConfig(String path) {
        String connectionPath = "jdbc:sqlite:" + path;
        try {
            boolean dbExist = new File(path).exists();
            connection_ = DriverManager.getConnection(connectionPath);
            if (!dbExist) {
                Init();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    void  Init() {
        try {
            Statement statement = connection_.createStatement();
            //创建数据表
            statement.execute("CREATE TABLE IF NOT EXISTS my (recipient_id INTEGER PRIMARY KEY, pushName text,  head BLOB)");
            statement.execute("CREATE TABLE IF NOT EXISTS friends (jid text PRIMARY KEY, pushName text, head BLOB, status text)");

            try {
                PreparedStatement preparedStatement = connection_.prepareStatement("INSERT OR REPLACE INTO my(recipient_id, pushName) VALUES(-1, 'No Name')");
                preparedStatement.execute();
            }
            catch (Exception e){
            }
        }
        catch (Exception e){

        }
    }

    public String GetPushName() {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("SELECT pushName from my where recipient_id = -1");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                return rs.getString(1);
            }
        }
        catch (Exception e){
        }
        return "No Name";
    }

    public byte[] GetHead() {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("SELECT head from my where recipient_id = -1");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                return rs.getBytes(1);
            }
        }
        catch (Exception e){
        }
        return null;
    }


    public void UpdateHead(byte[] head) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("UPDATE my SET head = ?  WHERE recipient_id = -1");
            preparedStatement.setBytes(1, head);
            preparedStatement.execute();
        }
        catch (Exception e){
        }
    }


    public void SaveContact(String jid, String pushName, String status) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("INSERT OR REPLACE INTO friends(jid, status, pushName) VALUES(?, ?, ?)");
            preparedStatement.setString(1, jid);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, pushName);
            preparedStatement.execute();
        }
        catch (Exception e){
        }
    }

    public List<User> GetContacts() {
        LinkedList<User> contats = new LinkedList<>();
        try {
            List<Integer> subDevice = new ArrayList<>();
            PreparedStatement preparedStatement = connection_.prepareStatement("SELECT * from friends");
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                User user = new User();
                user.jid = result.getString("jid");
                user.name = result.getString("pushName");
                user.picture = result.getBytes("head");
                user.status = result.getString("status");
                contats.add(user);
            }
        }
        catch (Exception e){
        }
        return contats;
    }

    public void UpdatePushName(String pushName) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("UPDATE my SET pushName = ?  WHERE recipient_id = -1");
            preparedStatement.setString(1, pushName);
            preparedStatement.execute();
        }
        catch (Exception e){
        }
    }

    public void UpdateContactHead(String jid, byte[] head) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("UPDATE friends SET head = ?  WHERE jid = ?");
            preparedStatement.setBytes(1, head);
            preparedStatement.setString(2, jid);
            preparedStatement.execute();
        }
        catch (Exception e){
        }

    }

}

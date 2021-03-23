package application;

import application.messages.User;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GorgeousConfig {
    public static class User {
        public String fullPhone;
        public String path;
        public byte[] head;

        @Override
        public String toString() {
            return fullPhone;
        }
    }

    private Connection connection_;
    private static GorgeousConfig instance = new GorgeousConfig();

    public static GorgeousConfig Instance() {
        return instance;
    }

    private GorgeousConfig() {
        String path = new File(System.getProperty("user.dir"), "gorgeous.db").getAbsolutePath();
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
            statement.execute("CREATE TABLE IF NOT EXISTS users (fullphone text PRIMARY KEY, path text,  head BLOB)");
        }
        catch (Exception e){

        }
    }

    public byte[] GetHead(String fullPhone) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("SELECT head from users where fullphone = ?");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                return rs.getBytes(1);
            }
        }
        catch (Exception e){
        }
        return null;
    }


    public void UpdateHead(String fullPhone, byte[] head) {
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("UPDATE users SET head = ?  WHERE fullphone = ?");
            preparedStatement.setBytes(1, head);
            preparedStatement.setString(2, fullPhone);
            preparedStatement.execute();
        }
        catch (Exception e){
        }
    }


    public void SaveUser(String fullPhone, String path) {
        boolean save = false;
        try {
            PreparedStatement preparedStatement = connection_.prepareStatement("update users set path = ? where fullphone=?");
            preparedStatement.setString(2, fullPhone);
            preparedStatement.setString(1, path);
            save = preparedStatement.execute();
        }
        catch (Exception e){

        }
        if (!save) {
            try {
                PreparedStatement preparedStatement = connection_.prepareStatement("INSERT OR REPLACE INTO users(fullphone, path) VALUES(?, ?)");
                preparedStatement.setString(1, fullPhone);
                preparedStatement.setString(2, path);
                preparedStatement.execute();
            }
            catch (Exception e){

            }
        }
    }

    public List<User> GetUsers() {
        LinkedList<User> contats = new LinkedList<>();
        try {
            List<Integer> subDevice = new ArrayList<>();
            PreparedStatement preparedStatement = connection_.prepareStatement("SELECT * from users");
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                User user = new User();
                user.fullPhone = result.getString("fullphone");
                user.path = result.getString("path");
                user.head = result.getBytes("head");
                contats.add(user);
            }
        }
        catch (Exception e){
        }
        return contats;
    }
}

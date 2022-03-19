package com.example.socialnetworkfx;

import com.example.socialnetworkfx.FriendsController;
import com.example.socialnetworkfx.Main;
import com.example.socialnetworkfx.DatabaseConnection;
import com.example.socialnetworkfx.SendMessageController;
import com.example.socialnetworkfx.domain.Message;
import com.example.socialnetworkfx.domain.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private ImageView userImageView;
    @FXML
    private Label fNameLabel;
    @FXML
    private Label lNameLabel;
    @FXML
    private Label usernameLabel;

    private User selectedUser;


    @FXML
    private TableView<Message> tableView;

    @FXML
    private TableColumn<Message, String> fromColumn;
    @FXML
    private TableColumn<Message, String> toColumn;
    @FXML
    private TableColumn<Message, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Message, String> textColumn;
    ObservableList<Message> data = FXCollections.observableArrayList();

    public void initData(User u){

        selectedUser = u;
        fNameLabel.setText(selectedUser.getFirst_name());
        lNameLabel.setText(selectedUser.getLast_name());
        usernameLabel.setText(selectedUser.getUsername());


        // CRIPTEAZA PAROLELE USERI-LOR EXISTENTI


        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

//        String sql1= "select * from users";
//        try {
//
//            PreparedStatement statement = connectDb.prepareStatement(sql1);
//            ResultSet qResult = statement.executeQuery();
//
//            while(qResult.next()) {
//                String sql2="update users set first_name=? ,last_name=?, username=?, password=? where id=?";
//                try {
//
//                    PreparedStatement statement2 = connectDb.prepareStatement(sql2);
//                    Long id = qResult.getLong("id");
//                    String firstName = qResult.getString("first_name");
//                    String lastName = qResult.getString("last_name");
//                    String username = qResult.getString("username");
//                    String password = qResult.getString("password");
//                    String hashPassword = Encrypt(password);
//
//                    statement2.setString(1, firstName);
//                    statement2.setString(2, lastName);
//                    statement2.setString(3, username);
//                    statement2.setString(4, hashPassword);
//                    statement2.setLong(5, id);
//
//                    statement2.executeUpdate();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//            e.getCause();
//        }



        String sql = "SELECT * from messages where to_user_id=? or from_user_id=?";
        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1, selectedUser.getId());
            statement.setLong(2, selectedUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String text = resultSet.getString("text");
                Long from_id = resultSet.getLong("from_user_id");
                Long to_id = resultSet.getLong("to_user_id");
                User from = findOne(from_id);
                User to = findOne(to_id);
                String fromUsername = from.getUsername();
                Date date = resultSet.getDate("date");
                Time time = resultSet.getTime("time");
                LocalDateTime localDateTime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
                ArrayList<User> toList = new ArrayList<User>();
                if(from_id==selectedUser.getId())
                {
                    toList.add(to);
                }
                else toList.add(selectedUser);
                Message m = new Message(from, toList, text, localDateTime, null);
                data.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

        //FUNCTII DE CRIPTARE SI DECRIPTARE

    public String Encrypt(String message){
        int key=5;
        String hash = "";
        char[] chars =message.toCharArray();
        for(char c: chars)
        {
            c+=key;
            hash = hash + c;
        }
        return hash;
    }

    public String Decrypt(String hashMessage){
        int key=5;
        String message = "";
        char[] chars =hashMessage.toCharArray();
        for(char c: chars)
        {
            c-=key;
            message = message + c;
        }
        return message;
    }

    @FXML
    private Button disconnectButton;

    public void disconnectButtonOnAction(ActionEvent event)
    {
        Stage stage = (Stage) disconnectButton.getScene().getWindow();
        stage.close();
    }

    public void leaveApp(ActionEvent event)
    {
        Stage stage = (Stage) disconnectButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image userImage = new Image(getClass().getResourceAsStream("/Image/userImage.png"));
        userImageView.setImage(userImage);

        fromColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("to"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("date"));
        textColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("text"));

        tableView.setItems(data);
    }

    public void openFriendsWindow(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("friendsWindow.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            FriendsController controller = fxmlLoader.getController();

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql= "select * from users where username = ? ";

            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1,usernameLabel.getText());
            ResultSet qResult = statement.executeQuery();

            if(qResult.next()) {
                Long id = qResult.getLong("id");
                String firstName = qResult.getString("first_name");
                String lastName = qResult.getString("last_name");
                String username = qResult.getString("username");
                String password = qResult.getString("password");
                User u = new User(firstName, lastName, username, password);
                u.setId(id);
                controller.initData(u);
            }


            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();

        }catch (Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }

    }

    public User findOne(Long aLong) {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql = "SELECT * from users where users.id=?";
        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, username, password);
                u.setId(id);
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openSendMessageWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sendMessageWindow.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            SendMessageController controller = fxmlLoader.getController();

            controller.initData(selectedUser);
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }

}

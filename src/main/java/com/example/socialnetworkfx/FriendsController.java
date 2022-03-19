package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.Friendship;
import com.example.socialnetworkfx.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class FriendsController implements Initializable {
    User selectedUser;

    @FXML
    private Button leaveButton;

    @FXML
    private ImageView friendsImage;

    @FXML
    private TableView<User> friendsTable;

    @FXML
    private TableColumn<User,String> usernameCell;

    ObservableList<User> data = FXCollections.observableArrayList();


    public void initData(User u){
        selectedUser = u;

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from friendships where user1_id=? or user2_id=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1,selectedUser.getId());
            statement.setLong(2,selectedUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long user1_id=resultSet.getLong("user1_id");
                Long user2_id=resultSet.getLong("user2_id");
                String date=resultSet.getString("date");
                Friendship f=new Friendship(user1_id,user2_id,date);

                if(f.getuser1ID()!=selectedUser.getId() ) {
                        User user = findOne(f.getuser1ID());
                        data.add(new User(user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getPassword()));}
                if(f.getuser2ID()!=selectedUser.getId() ) {
                        User user = findOne(f.getuser2ID());
                        data.add(new User(user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getPassword()));}
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User findOne(Long aLong)  {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from users where users.id=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User u = new User(firstName,lastName,username,password);
                u.setId(id);
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image userImage = new Image(getClass().getResourceAsStream("/Image/team.jpg"));
        friendsImage.setImage(userImage);

        usernameCell.setCellValueFactory(new PropertyValueFactory<User,String>("username"));

        friendsTable.setItems(data);
    }

    public void openRemoveFriendWindow(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("deleteFriendController.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            DeleteFriendController controller = fxmlLoader.getController();

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql= "select * from users where username = ? ";

            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1, selectedUser.getUsername());
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

    public void openSendRequestWindow(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("addFriend.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            AddFriendController controller = fxmlLoader.getController();

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql= "select * from users where username = ? ";

            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1, selectedUser.getUsername());
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




    public void openRequestsWindow(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("requestsWindow.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            FriendRequestsController controller = fxmlLoader.getController();

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql= "select * from friendrequests where to_user_id = ? ";

            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1, selectedUser.getId());
            ResultSet qResult = statement.executeQuery();

            if(qResult.next()) {
                Long id = qResult.getLong("id");
                Long from_user_id = qResult.getLong("from_user_id");
                User from_user=findOne(from_user_id);
                String status = qResult.getString("status");
                FriendRequest f = new FriendRequest(from_user,selectedUser, FriendRequestStatus.valueOf(status));
                f.setId(id);
                controller.initData(selectedUser);
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


    public void openYourRequestsWindow(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("yourRequests.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            YourRequestsController controller = fxmlLoader.getController();

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql= "select * from users where username = ? ";

            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1, selectedUser.getUsername());
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

}


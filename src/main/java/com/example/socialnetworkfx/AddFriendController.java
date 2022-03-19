package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFriendController {
    User selectedUser;

    @FXML
    private Label yourUsernameLabel;

    @FXML
    private Button leaveButton;
    @FXML
    private TextField enterUsernameTextFiled;

    @FXML
    private Label successfulLabel;
    @FXML
    private Label errorLabel;

    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        stage.close();
    }

    public void initData(User u) {
        selectedUser = u;
        yourUsernameLabel.setText(selectedUser.getUsername());
    }


    public void addFriendRequest()
    {
        String newFriendUsername=enterUsernameTextFiled.getText();
        User newFriend=findByUsername(newFriendUsername);
        User selUser=findByUsername(yourUsernameLabel.getText());

        if(newFriend!=null) {

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();
            String sql = "insert into friendrequests(from_user_id,to_user_id,status) values(?,?,?)";

            try {
                PreparedStatement ps = connectDb.prepareStatement(sql);

                ps.setLong(1, selUser.getId());
                ps.setLong(2, newFriend.getId());
                ps.setString(3, FriendRequestStatus.PENDING.toString());
                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            successfulLabel.setText("Friend request has been sent");
        }
        else errorLabel.setText("Username does not exits");
    }


    public User findByUsername(String name)  {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from users where users.username=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1,name);
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
}

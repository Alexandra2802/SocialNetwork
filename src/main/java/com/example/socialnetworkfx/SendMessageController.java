package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SendMessageController {
    @FXML
    private TextField toTextField;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    @FXML
    private Button backButton;
    @FXML
    private Label sentLabel;
    private User selectedUser;
    @FXML
    private Label yourUsernameLabel;

    public void initData(User u)
    {
        selectedUser=u;
        yourUsernameLabel.setText(selectedUser.getUsername());
    }

    public void sendButtonOnAction(ActionEvent event)
    {
        sendMessage();
    }

    public void sendMessage()
    {
        String toUsername=toTextField.getText();
        String message=messageTextField.getText();
        User toUser=findByUSername(toUsername);
        User selUser=findByUSername(yourUsernameLabel.getText());

        if(toUser!=null)
        {
            ArrayList<User> toList=new ArrayList<User>();
            toList.add(toUser);

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();
            String sql = "insert into messages(from_user_id,to_user_id,text,date,time) values(?,?,?,?,?)";
            try
            {
                PreparedStatement ps = connectDb.prepareStatement(sql);
                ps.setLong(1, selUser.getId());
                ps.setLong(2, toUser.getId());
                ps.setString(3, message);
                Date date=Date.valueOf(LocalDateTime.now().toLocalDate());
                Time time=Time.valueOf(LocalDateTime.now().toLocalTime());
                ps.setDate(4, date);
                ps.setTime(5, time);
                ps.executeUpdate();
            }catch (SQLException e)
            {
                e.printStackTrace();
            }
            sentLabel.setText("Message has been sent!");
        }
        else sentLabel.setText("User not found!");
    }


    public void goBack()
    {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public User findByUSername(String u)  {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from users where users.username=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1,u);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User user = new User(firstName,lastName,username,password);
                user.setId(id);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

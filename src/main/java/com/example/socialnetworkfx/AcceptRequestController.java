package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.FriendRequestStatus;
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
import org.controlsfx.control.action.Action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AcceptRequestController {
    @FXML
    private Button acceptRequest;
    @FXML
    private Button backButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successfulLabel;
    @FXML
    private TextField fromTextField;

    User selectedUser;

    @FXML
    private Label yourUsernameLabel;

    public void initData(User u)
    {
        selectedUser=u;
        yourUsernameLabel.setText(selectedUser.getUsername());
    }

    public void acceptRequest()
    {
        User fromUser=findByUSername(fromTextField.getText());
        User selUser=findByUSername(yourUsernameLabel.getText());
        if(fromUser!=null && selUser!=null)
        {
            String sql="update friendrequests set status=? where from_user_id=? and to_user_id=?";
            String sql2 = "insert into friendships(user1_id,user2_id,date) values(?,?,?)";
            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();
            try
            {
                PreparedStatement ps = connectDb.prepareStatement(sql);
                PreparedStatement ps2 = connectDb.prepareStatement(sql2);
                ps.setString(1, FriendRequestStatus.APPROVED.toString());
                ps.setLong(2, fromUser.getId());
                ps.setLong(3,selUser.getId());
                ps.executeUpdate();


                ps2.setLong(1, selUser.getId());
                ps2.setLong(2, fromUser.getId());
                Date date = new Date();
                SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
                String stringDate= DateFor.format(date);
                ps2.setString(3, stringDate);
                ps2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            successfulLabel.setText("Friend request accepted!");
            errorLabel.setText("");
        }else {errorLabel.setText("User not found!");successfulLabel.setText("");}


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

//    public void goBack()
//    {
//
//            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("requestsWindow.fxml"));
//                Stage registerStage = new Stage();
//                Scene scene = new Scene(fxmlLoader.load());
//
//                FriendRequestsController controller = fxmlLoader.getController();
//                User user = findByUSername(yourUsernameLabel.getText());
//                if (user != null) {
//                    controller.initData(user);
//
//                    registerStage.initStyle(StageStyle.UNDECORATED);
//                    registerStage.setScene(scene);
//                    registerStage.show();
//                }leaveButtonOnAction();
//                } catch(Exception e){
//                    e.printStackTrace();
//                    e.getCause();
//                }
//
//
//    }


    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}

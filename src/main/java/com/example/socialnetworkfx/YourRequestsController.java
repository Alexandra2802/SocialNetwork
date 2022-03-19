package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.Friendship;
import com.example.socialnetworkfx.domain.User;
import com.example.socialnetworkfx.domain.validators.RepoException;
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

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class YourRequestsController implements Initializable {
    User selectedUser;
   // Long idFromUser;

    @FXML
    private Button leaveButton;

    @FXML
    private ImageView friendsImage;

    @FXML
    private TableView<User> friendRequestsTable;

    @FXML
    private TableColumn<User,String> usernameCell;
    @FXML
    private Label yourUsernameLabel;

    ObservableList<User> data = FXCollections.observableArrayList();

    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        stage.close();
    }

    public void initData(User u){
        selectedUser = u;
        yourUsernameLabel.setText(selectedUser.getUsername());
        //idFromUser=selectedUser.getId();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from friendrequests where from_user_id=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1,selectedUser.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long to_user_id=resultSet.getLong("to_user_id");
                String status=resultSet.getString("status");

                if(status.equals("PENDING")){
                    User user=findUser(to_user_id);
                    data.add(new User(user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getPassword()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findUser(Long aLong)  {

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image userImage = new Image(getClass().getResourceAsStream("/Image/team.jpg"));
        friendsImage.setImage(userImage);

        usernameCell.setCellValueFactory(new PropertyValueFactory<User,String>("username"));

        friendRequestsTable.setItems(data);
    }

    public void deleteRequestButtonOnAction(ActionEvent event)
    {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("deleteRequest.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            DeleteRequestController controller = fxmlLoader.getController();

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


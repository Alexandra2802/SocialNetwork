package com.example.socialnetworkfx;

import com.example.socialnetworkfx.DatabaseConnection;
import com.example.socialnetworkfx.Main;
import com.example.socialnetworkfx.domain.FriendRequest;
import com.example.socialnetworkfx.domain.FriendRequestStatus;
import com.example.socialnetworkfx.domain.User;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FriendRequestsController implements Initializable {
    @FXML
    private Label yourUsernameLabel;
    @FXML
    private TableView<FriendRequest> tableView;

    @FXML
    private TableColumn<FriendRequest,String> fromColumn;

    @FXML
    private TableColumn<FriendRequest, FriendRequestStatus> statusColumn;

    @FXML
    private Button backButton;

    ObservableList<FriendRequest> data = FXCollections.observableArrayList();

    private User selectedUser;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        fromColumn.setCellValueFactory(new PropertyValueFactory<FriendRequest,String>("from"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<FriendRequest,FriendRequestStatus>("status"));
        tableView.setItems(data);
    }


    public void initData(User u)
    {
        selectedUser = u;
        yourUsernameLabel.setText(selectedUser.getUsername());

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from friendrequests where to_user_id=?";
        try
        {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setLong(1,selectedUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                String status=resultSet.getString("status");
                Long from_id=resultSet.getLong("from_user_id");
                User from=findOne(from_id);
                String fromUsername=from.getUsername();
                FriendRequest f=new FriendRequest(from,selectedUser,FriendRequestStatus.valueOf(status));
                data.add(f);
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
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public void goToRequestWindow()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("acceptRequestWindow.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            AcceptRequestController controller = fxmlLoader.getController();
            User selUser=findByUsername(yourUsernameLabel.getText());

            controller.initData(selUser);
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
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

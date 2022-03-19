package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.Friendship;
import com.example.socialnetworkfx.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

public class DeleteFriendController {
    User selectedUser;


    @FXML
    private Button leaveButton;

    @FXML
    private TextField enterUsernameTextFiled;

    @FXML
    private Label errorLabel;
    @FXML
    private Label successfulLabel;
    @FXML
    private Label yourUsernameLabel;

    public void leaveButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) leaveButton.getScene().getWindow();
        stage.close();
    }

    public void initData(User u) {
        selectedUser = u;
        yourUsernameLabel.setText(selectedUser.getUsername());
    }

    private long findIdFriendship() {

        long idUser1;
        idUser1=findIdUser1();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from friendships where user1_id=? or user2_id=?";
        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);

            long idUser2;
            idUser2=findIdUser2();

            if(idUser2!=-1) {

                statement.setLong(1, idUser1);
                statement.setLong(2, idUser2);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    successfulLabel.setText("Delete successful");

                    return resultSet.getLong("id");

                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private long findIdUser1() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql = "SELECT * from users where username=?";
        long id1;

        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1, yourUsernameLabel.getText());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id1=resultSet.getLong("id");
                return id1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private long findIdUser2() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql = "SELECT * from users where username=?";
        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);
            statement.setString(1, enterUsernameTextFiled.getText());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteButtonOnAction(ActionEvent event){
        if(enterUsernameTextFiled.getText().isBlank()){
            errorLabel.setText("Username field can not be blank");
        }
        else{

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql="delete from friendships where id=? ";
            try {
                PreparedStatement ps = connectDb.prepareStatement(sql);

                long idFriendship;
                idFriendship=findIdFriendship();
                if(idFriendship!=-1) {
                    ps.setLong(1, idFriendship);

                    ps.executeUpdate();

                }else
                    errorLabel.setText("Username does not exists");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

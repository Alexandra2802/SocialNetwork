package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteRequestController {
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

    public void deleteButtonOnAction(ActionEvent event){
        if(enterUsernameTextFiled.getText().isBlank()){
            errorLabel.setText("Username field can not be blank");
        }
        else{

            DatabaseConnection connectNow = new DatabaseConnection();
            Connection connectDb = connectNow.getConnection();

            String sql="delete from friendrequests where id=? ";
            try {
                PreparedStatement ps = connectDb.prepareStatement(sql);

                long idRequest;
                idRequest=findIdRequest();
                if(idRequest!=-1) {
                    ps.setLong(1, idRequest);

                    ps.executeUpdate();

                }else
                    errorLabel.setText("Username does not exists");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private long findIdRequest() {

        long from_user_id;
        from_user_id=findIdUser1();

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql="SELECT * from friendrequests where from_user_id=? and to_user_id=?";
        try {
            PreparedStatement statement = connectDb.prepareStatement(sql);

            long to_user_id;
            to_user_id=findIdUser2();

            if(from_user_id!=-1 && to_user_id!=-1) {

                statement.setLong(1, from_user_id);
                statement.setLong(2, to_user_id);
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

}

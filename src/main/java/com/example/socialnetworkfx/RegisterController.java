package com.example.socialnetworkfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private ImageView logoImageView1;

    @FXML
    private ImageView logoImageView2;

    @FXML
    private Button cancelButton;

    @FXML
    private Label successfulRegistretionLabel;
    @FXML
    private Label errorPasswordLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField enterPasswordFiled;
    @FXML
    private PasswordField renterPasswordFiled;

    @FXML
    private TextField enterFNTextField;

    @FXML
    private TextField enterLNTextFiled;

    @FXML
    private TextField enterUsernameTextField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image logoImage = new Image(getClass().getResourceAsStream("/Image/paw.png"));
        logoImageView1.setImage(logoImage);
        logoImageView2.setImage(logoImage);
    }

    public void closeButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        //Platform.exit();
    }

    public void registerButtonOnAction(ActionEvent event){
        if(!enterPasswordFiled.getText().equals(renterPasswordFiled.getText()) ) {
            errorPasswordLabel.setText("Password does not match. Try again.");
        }
        else {
            errorPasswordLabel.setText("");
            if(userValidation()==1)
                registrationValidation();

        }


    }

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



    public void registrationValidation(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String sql= "insert into users (first_name, last_name, username, password ) values (?, ?,?,?)";
        try{
            PreparedStatement ps = connectDb.prepareStatement(sql);

                ps.setString(1, enterFNTextField.getText());
                ps.setString(2, enterLNTextFiled.getText());
                ps.setString(3, enterUsernameTextField.getText());

                String hashPassword = Encrypt(enterPasswordFiled.getText());

                ps.setString(4, hashPassword);

                ps.executeUpdate();
                successfulRegistretionLabel.setText("Registration complete");

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public int userValidation() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String verifLogin = "select * from users where username = ? ";
        try {
            PreparedStatement statement = connectDb.prepareStatement(verifLogin);
            statement.setString(1, enterUsernameTextField.getText());
            ResultSet qResult = statement.executeQuery();

            if (qResult.next()) {
                errorLabel.setText("The username already exists. Please find another one.");
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return 1;
    }
}

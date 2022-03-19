package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private ImageView brandingImageView;

    @FXML
    private ImageView lockImageView;

    @FXML
    private TextField enterUsernameTextField;

    @FXML
    private PasswordField enterPasswordField;

    public void loginButtonOnAction(ActionEvent event){
        if(enterUsernameTextField.getText().isBlank() || enterPasswordField.getText().isBlank())
            loginMessageLabel.setText("Username and Password cannot be null. Please try again.");
        else{
            validateLogin();
        }

    }

    private void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String verifLogin= "select * from users where username = ? and password = ?";
        try{
            PreparedStatement statement = connectDb.prepareStatement(verifLogin);
            statement.setString(1,enterUsernameTextField.getText());
            //statement.setString(2,enterPasswordField.getText());
            String hashPasword  = Encrypt(enterPasswordField.getText());

            statement.setString(2,hashPasword);
            ResultSet qResult = statement.executeQuery();

            if(qResult.next()) {
                Long id=qResult.getLong("id");
                String firstName = qResult.getString("first_name");
                String lastName = qResult.getString("last_name");
                String username = qResult.getString("username");
                String password = qResult.getString("password");


                User u = new User(firstName,lastName,username,password);
                u.setId(id);
                loginMessageLabel.setText("Successful login");
                UserAccount(u);
            }
            else{
                    loginMessageLabel.setText("Something went wrong. Please try again.");
                }

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
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




    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image logoImage = new Image(getClass().getResourceAsStream("/Image/bda5dcde7bea3234a24acef443d277e2.jpg"));
        brandingImageView.setImage(logoImage);

//
//        Image lockImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/Desktop/Locked-Cloud-icon.png")));
//        lockImageView.setImage(lockImage);
    }


    public void createAccount(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(scene);
            registerStage.show();

        }catch (Exception e)
        {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void UserAccount(User u){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("userWindow.fxml"));
            Stage registerStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            UserController controller = fxmlLoader.getController();

            controller.initData(u);
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

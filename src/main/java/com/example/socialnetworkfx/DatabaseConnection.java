package com.example.socialnetworkfx;

import com.example.socialnetworkfx.domain.User;
import com.example.socialnetworkfx.domain.validators.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseConnection {
    public Connection databaseLink;
    public Connection getConnection(){
      try{
            databaseLink= DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialNetwork",
                    "postgres",
                    "Alxndr28022001");
        }catch (Exception e){
          e.printStackTrace();
          e.getCause();
      }
      return databaseLink;
    }


}

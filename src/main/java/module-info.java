module com.example.socialnetworkfx {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    //requires pgadmin.connector.java;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.rmi;

    opens com.example.socialnetworkfx to javafx.fxml;
    exports com.example.socialnetworkfx;
    exports com.example.socialnetworkfx.repository;
    opens com.example.socialnetworkfx.repository to javafx.fxml;
    exports com.example.socialnetworkfx.domain;
    opens com.example.socialnetworkfx.domain to javafx.fxml;
}
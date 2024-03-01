module com.idrovo.michat {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.eclipse.paho.client.mqttv3;
    requires com.google.gson;


    opens com.idrovo.michat to javafx.fxml;
    exports com.idrovo.michat;
    exports com.idrovo.michat.controller;
    exports com.idrovo.michat.model;
    exports com.idrovo.michat.repository;
    opens com.idrovo.michat.controller to javafx.fxml;
}
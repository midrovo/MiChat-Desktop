package com.idrovo.michat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertController {
    private Stage stage;
    private LoginController loginController;

    @FXML
    private Button btnAceptar;

    @FXML
    private Label lblAlert;

    @FXML
    void alertAction(ActionEvent event) {
        if(loginController == null)
            stage.close();
        else {
            loginController.getTxtpass().clear();
            loginController.getTxtuser().clear();
            stage.close();
        }

    }

    public void init(Stage stage1, LoginController loginController) {
        this.stage = stage1;
        this.loginController = loginController;
        camposVacios();
    }

    public void camposVacios() {
        if(loginController.getTxtuser().getText().equals("") || loginController.getTxtpass().getText().equals(""))
            lblAlert.setText("Error, no se permiten campos vacíos.");

    }

    public void initSignUp(Stage stage1, String s) {
        this.stage = stage1;
        this.lblAlert.setText(s);
    }
}
